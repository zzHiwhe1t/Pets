package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.dto.MessageRequest;
import com.petadopt.entity.ChatMessage;
import com.petadopt.service.ChatService;
import com.petadopt.util.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService service;
    public ChatController(ChatService service) { this.service = service; }
    @PostMapping("/messages") public ApiResult<Map<String,Long>> send(@Validated @RequestBody MessageRequest request) { return ApiResult.ok(Collections.singletonMap("id", service.send(UserContext.require(), request))); }
    @GetMapping("/messages") public ApiResult<List<ChatMessage>> thread(@RequestParam Long petId, @RequestParam Long otherId) { return ApiResult.ok(service.thread(UserContext.require(), petId, otherId)); }
    @GetMapping("/conversations") public ApiResult<List<ChatMessage>> conversations() { return ApiResult.ok(service.conversations(UserContext.require())); }
    @GetMapping("/unread-count") public ApiResult<Map<String,Integer>> unread() { return ApiResult.ok(Collections.singletonMap("count", service.unreadCount(UserContext.require()))); }
}
