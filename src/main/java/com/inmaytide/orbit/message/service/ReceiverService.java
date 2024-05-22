package com.inmaytide.orbit.message.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.message.domain.MessageReceiver;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/2/29
 */
public interface ReceiverService extends BasicService<MessageReceiver> {

    void createBatch(List<MessageReceiver> receivers);

    Map<Long, List<MessageReceiver>> findByMessages(List<Long> messageIds);

    Map<Long, List<MessageReceiver>> findUnsentByMessages(MessageSendingMode sendingMode, List<Long> messageIds);

}
