package com.inmaytide.orbit.message.timer;

import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.commons.metrics.JobAdapter;
import com.inmaytide.orbit.message.service.ExternalMessageSender;
import com.inmaytide.orbit.message.service.MessageService;
import com.inmaytide.orbit.message.service.dto.MessageVO;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/3/6
 */
public class MailMessageSender implements JobAdapter {

    private final Logger log = LoggerFactory.getLogger(MailMessageSender.class);

    private final static MessageSendingMode SUPPORTED_SENDING_MODE = MessageSendingMode.MAIL;

    @Resource
    private List<ExternalMessageSender> senders;

    @Resource
    private MessageService messageService;

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public String getName() {
        return "sender_mail-message";
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<MessageVO> messages = messageService.findUnsentMessages(SUPPORTED_SENDING_MODE);
        senders.stream()
                .filter(e -> e.support(SUPPORTED_SENDING_MODE))
                .findFirst()
                .ifPresentOrElse(
                        s -> s.send(messages),
                        () -> log.error("没有找到支持消息推送模式为: {} 的消息推送器", SUPPORTED_SENDING_MODE)
                );
    }
}
