package com.inmaytide.orbit.message.service.dto;

import com.inmaytide.orbit.message.domain.Message;
import com.inmaytide.orbit.message.domain.MessageReceiver;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/29
 */
public class MessageVO extends Message {

    private List<MessageReceiver> receivers;

    public MessageVO() {
    }

    public MessageVO(Message message, List<MessageReceiver> receivers) {
        BeanUtils.copyProperties(message, this);
        this.receivers = receivers;
    }

    public List<MessageReceiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<MessageReceiver> receivers) {
        this.receivers = receivers;
    }
}
