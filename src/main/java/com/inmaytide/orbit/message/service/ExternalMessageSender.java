package com.inmaytide.orbit.message.service;

import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.message.service.dto.MessageVO;

import java.util.List;


/**
 * 消息发送至第三方(SMS、EMAIL等)发送器接口
 *
 * @author inmaytide
 * @since 2024/3/4
 */
public interface ExternalMessageSender {

    void send(MessageVO message);

    void send(List<MessageVO> messages);

    boolean support(MessageSendingMode sendingMode);

}
