package com.inmaytide.orbit.message.api;

import com.inmaytide.orbit.message.domain.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author inmaytide
 * @since 2024/2/27
 */
@RestController
@RequestMapping("/api/messages")
public class MessageResource {

    @PostMapping
    public void create(Message message) {

    }

}
