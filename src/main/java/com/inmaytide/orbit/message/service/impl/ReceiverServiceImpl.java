package com.inmaytide.orbit.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.commons.constants.MessageReadingStatus;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.commons.constants.MessageSendingStatus;
import com.inmaytide.orbit.message.configuration.ApplicationProperties;
import com.inmaytide.orbit.message.configuration.ErrorCode;
import com.inmaytide.orbit.message.domain.MessageReceiver;
import com.inmaytide.orbit.message.mapper.MessageReceiverMapper;
import com.inmaytide.orbit.message.service.ReceiverService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/2/29
 */
@Service
public class ReceiverServiceImpl implements ReceiverService {

    private final ApplicationProperties properties;

    private final MessageReceiverMapper baseMapper;

    public ReceiverServiceImpl(ApplicationProperties properties, MessageReceiverMapper baseMapper) {
        this.properties = properties;
        this.baseMapper = baseMapper;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createBatch(List<MessageReceiver> receivers) {
        if (CollectionUtils.isEmpty(receivers)
                || receivers.stream().anyMatch(e -> e.getMessageId() == null || e.getReceiver() == null || e.getSendingMode() == null)) {
            throw new BadRequestException(ErrorCode.E_0x00200001);
        }
        receivers.forEach(e -> {
            e.setSendingStatus(MessageSendingStatus.UNSENT);
            e.setSendingStatusTime(Instant.now());
            e.setReadingStatus(MessageReadingStatus.UNREAD);
            e.setReadingStatusTime(Instant.now());
            // 系统内部消息不需要定时程序调用第三方接口发送，直接置为发送成功
            if (e.getSendingMode() == MessageSendingMode.SYSTEM) {
                e.setSendingStatus(MessageSendingStatus.SUCCEED);
            }
            // 邮箱消息和短信消息无法获取到已读回执，默认视为已读
            if (e.getSendingMode() == MessageSendingMode.MAIL || e.getSendingMode() == MessageSendingMode.SMS) {
                e.setReadingStatus(MessageReadingStatus.READED);
            }
            baseMapper.insert(e);
        });
    }

    @Override
    public Map<Long, List<MessageReceiver>> findByMessages(List<Long> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MessageReceiver::getMessageId, messageIds);
        return baseMapper.selectList(wrapper).stream().collect(Collectors.groupingBy(MessageReceiver::getMessageId, Collectors.toList()));
    }

    @Override
    public Map<Long, List<MessageReceiver>> findUnsentByMessages(MessageSendingMode sendingMode, List<Long> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<MessageReceiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MessageReceiver::getMessageId, messageIds);
        wrapper.and(w -> {
            w.eq(MessageReceiver::getSendingStatus, MessageSendingStatus.UNSENT)
                    .or(ww -> {
                        ww.eq(MessageReceiver::getSendingStatus, MessageSendingStatus.FAILED);
                        ww.lt(MessageReceiver::getFailedCount, properties.getMaximumNumberOfExternalMessageResends());
                    });
        });
        return baseMapper.selectList(wrapper).stream().collect(Collectors.groupingBy(MessageReceiver::getMessageId, Collectors.toList()));
    }

    @Override
    public BaseMapper<MessageReceiver> getBaseMapper() {
        return baseMapper;
    }
}
