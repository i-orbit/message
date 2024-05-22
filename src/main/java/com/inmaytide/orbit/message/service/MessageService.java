package com.inmaytide.orbit.message.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.commons.domain.dto.result.PageResult;
import com.inmaytide.orbit.message.domain.Message;
import com.inmaytide.orbit.message.service.dto.MessageVO;
import com.inmaytide.orbit.message.service.dto.MyMessagePagingQuery;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/28
 */
public interface MessageService extends BasicService<Message> {

    void create(MessageVO message);

    /**
     * 查询指定消息发送类型的需要发送的消息列表
     */
    List<MessageVO> findUnsentMessages(MessageSendingMode sendingMode);

    PageResult<Message> findMyMessages(MyMessagePagingQuery query);

}
