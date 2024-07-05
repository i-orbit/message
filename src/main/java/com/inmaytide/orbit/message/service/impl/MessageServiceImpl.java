package com.inmaytide.orbit.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.message.configuration.ApplicationProperties;
import com.inmaytide.orbit.message.configuration.ErrorCode;
import com.inmaytide.orbit.message.domain.Message;
import com.inmaytide.orbit.message.domain.MessageReceiver;
import com.inmaytide.orbit.message.mapper.MessageMapper;
import com.inmaytide.orbit.message.service.MessageService;
import com.inmaytide.orbit.message.service.ReceiverService;
import com.inmaytide.orbit.message.service.dto.MessageVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.inmaytide.orbit.commons.constants.MessageSendingStatus.FAILED;
import static com.inmaytide.orbit.commons.constants.MessageSendingStatus.UNSENT;

/**
 * @author inmaytide
 * @since 2024/2/28
 */
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper baseMapper;

    private final ApplicationProperties properties;

    private final ReceiverService receiverService;

    public MessageServiceImpl(MessageMapper baseMapper, ApplicationProperties properties, ReceiverService receiverService) {
        this.baseMapper = baseMapper;
        this.properties = properties;
        this.receiverService = receiverService;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void create(MessageVO message) {
        if (CollectionUtils.isEmpty(message.getReceivers())) {
            throw new BadRequestException(ErrorCode.E_0x00200001);
        }
        baseMapper.insert(message);
        receiverService.createBatch(message.getReceivers().stream().peek(e -> e.setMessageId(message.getId())).collect(Collectors.toList()));
    }

    @Override
    public List<MessageVO> findUnsentMessages(MessageSendingMode sendingMode) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        String subSQL = "select message_id from message_receiver where sending_mode = {0} and (sending_status = {1} or (sending_status = {2} and failed_count < {3}))";
        wrapper.apply(subSQL, sendingMode.name(), UNSENT, FAILED, properties.getMaximumNumberOfExternalMessageResends());
        List<Message> messages = baseMapper.selectList(wrapper);
        Map<Long, List<MessageReceiver>> receivers = receiverService.findUnsentByMessages(sendingMode, messages.stream().map(Entity::getId).collect(Collectors.toList()));
        return messages.stream().map(e -> new MessageVO(e, receivers.getOrDefault(e.getId(), Collections.emptyList()))).collect(Collectors.toList());
    }

    @Override
    public MessageMapper getBaseMapper() {
        return baseMapper;
    }
}
