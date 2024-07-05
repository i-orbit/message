package com.inmaytide.orbit.message.api;

import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import com.inmaytide.orbit.commons.domain.dto.result.PageResult;
import com.inmaytide.orbit.message.domain.Message;
import com.inmaytide.orbit.message.service.MessageService;
import com.inmaytide.orbit.message.service.dto.MessageVO;
import com.inmaytide.orbit.message.service.dto.MyMessagePagingQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * @author inmaytide
 * @since 2024/2/27
 */
@RestController
@Tag(name = "系统消息")
@RequestMapping("/api/messages")
public class MessageResource {

    private final MessageService service;

    public MessageResource(MessageService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "创建消息")
    public void create(MessageVO message) {
        service.create(message);
    }

    @GetMapping
    @Operation(summary = "分页查询我的系统消息")
    public PageResult<Message> pagination(@ModelAttribute MyMessagePagingQuery query) {
        return service.pagination(query);
    }

}
