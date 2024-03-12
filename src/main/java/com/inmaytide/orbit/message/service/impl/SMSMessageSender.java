package com.inmaytide.orbit.message.service.impl;

import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.message.service.ExternalMessageSender;
import com.inmaytide.orbit.message.service.dto.MessageVO;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/3/8
 */
public class SMSMessageSender implements ExternalMessageSender {
    @Override
    public void send(MessageVO message) {

    }

    @Override
    public void send(List<MessageVO> messages) {

    }

    @Override
    public boolean support(MessageSendingMode sendingMode) {
        return sendingMode == MessageSendingMode.SMS;
    }
}
