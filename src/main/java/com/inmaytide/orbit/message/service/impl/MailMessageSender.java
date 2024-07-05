package com.inmaytide.orbit.message.service.impl;

import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.commons.constants.MessageSendingStatus;
import com.inmaytide.orbit.commons.domain.FileMetadata;
import com.inmaytide.orbit.commons.service.core.SystemPropertyService;
import com.inmaytide.orbit.commons.service.uaa.UserService;
import com.inmaytide.orbit.message.configuration.ErrorCode;
import com.inmaytide.orbit.message.domain.MessageReceiver;
import com.inmaytide.orbit.message.service.ExternalMessageSender;
import com.inmaytide.orbit.message.service.ReceiverService;
import com.inmaytide.orbit.message.service.dto.MessageVO;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/3/4
 */
@Component
public class MailMessageSender implements ExternalMessageSender {

    private final Logger log = LoggerFactory.getLogger(MailMessageSender.class);

    private final JavaMailSender mailSender;

    private final UserService userService;

    private final ReceiverService receiverService;

    private final String form;

    private final SystemPropertyService systemPropertyService;

    public MailMessageSender(JavaMailSender mailSender, UserService userService, ReceiverService receiverService, @Value("${spring.mail.username}") String form, SystemPropertyService systemPropertyService) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.receiverService = receiverService;
        this.form = form;
        this.systemPropertyService = systemPropertyService;
    }

    @Override
    public void send(MessageVO message) {
        // 消息为空或消息接收人为空
        if (message == null || CollectionUtils.isEmpty(message.getReceivers())) {
            log.warn("邮件消息{id = {}}为NULL或消息没有包含任何接收人信息", message == null ? "NULL" : message.getId());
            return;
        }
        // 消息接收人中不包含需要发送邮件的接收人信息
        List<Long> receivers = message.getReceivers().stream().filter(e -> e.getSendingMode() == MessageSendingMode.MAIL).map(MessageReceiver::getReceiver).toList();
        if (CollectionUtils.isEmpty(receivers)) {
            log.warn("邮件消息{id = {}}接收人中不包含需要发送邮件的接收人信息", message.getId());
            return;
        }
        // 验证消息接收人中邮件信息的配置情况
        Map<Long, String> emailAddresses = userService.findEmailsByIds(receivers);
        List<MessageReceiver> failedReceivers = message.getReceivers().stream()
                .filter(e -> e.getSendingMode() == MessageSendingMode.MAIL)
                .filter(e -> StringUtils.isBlank(emailAddresses.get(e.getReceiver())))
                .peek(e -> onSendFailed(e, ErrorCode.E_0x00200002.value()))
                .toList();
        // 保存因用户信息邮件地址未配置导致的消息发送失败信息
        if (!failedReceivers.isEmpty()) {
            failedReceivers.forEach(receiverService::update);
        }
        if (MapUtils.isEmpty(emailAddresses)) {
            log.warn("邮件消息{id = {}}接收人中没有用户信息中配置有邮箱地址", message.getId());
            return;
        }
        List<MessageReceiver> sendableReceivers = message.getReceivers().stream()
                .filter(e -> e.getSendingMode() == MessageSendingMode.MAIL)
                .filter(e -> StringUtils.isNotBlank(emailAddresses.get(e.getReceiver())))
                .toList();
        try {
            mailSender.send(createMailMessage(message, emailAddresses.values()));
            sendableReceivers.forEach(this::onSendSucceed);
        } catch (Exception e) {
            log.error("邮件消息{id = {}}发送失败, Cause by: ", message.getId(), e);
            sendableReceivers.forEach(r -> onSendFailed(r, "消息发送失败，请查看详细日志"));
        }
        if (!sendableReceivers.isEmpty()) {
            sendableReceivers.forEach(receiverService::update);
        }
    }

    @Override
    public void send(List<MessageVO> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            log.warn("需要发送的消息列表为空");
            return;
        }
        messages.forEach(this::send);
    }

    @Override
    public boolean support(MessageSendingMode sendingMode) {
        return sendingMode == MessageSendingMode.MAIL;
    }

    private MimeMessagePreparator createMailMessage(MessageVO message, Collection<String> receivers) {
        return m -> {
            MimeMessageHelper helper = new MimeMessageHelper(m, true, StandardCharsets.UTF_8.name());
            helper.setFrom(form);
            helper.setTo(receivers.toArray(String[]::new));
            helper.setSubject(systemPropertyService.getValue(1L, Constants.SystemPropertyKeys.SYSTEM_NAME).orElse(StringUtils.EMPTY) + "-" + message.getTitle());


            MimeMultipart multipart = new MimeMultipart("related");

            MimeBodyPart body = new MimeBodyPart();
            body.setContent(message.getContent(), "text/html;charset=utf-8");
            multipart.addBodyPart(body);

            if (CollectionUtils.isNotEmpty(message.getAttachments())) {
                for (FileMetadata metadata : message.getAttachments()) {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    try {
                        attachPart.attachFile(new File("/Users/inmaytide/Downloads/你啊活动佛为哦人家.txt"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    multipart.addBodyPart(attachPart);
                }
            }
            m.setContent(multipart);
        };
    }

    private void onSendSucceed(MessageReceiver receiver) {
        receiver.setSendingStatus(MessageSendingStatus.SUCCEED);
        receiver.setSendingStatusTime(Instant.now());
    }

    private void onSendFailed(MessageReceiver receiver, String failedReason) {
        receiver.setSendingStatus(MessageSendingStatus.FAILED);
        receiver.setSendingStatusTime(Instant.now());
        receiver.setFailedCount(receiver.getFailedCount().add(BigDecimal.ONE));
        receiver.setFailedReason(failedReason);
    }


}
