package com.petadopt.service;

import com.petadopt.dto.MessageRequest;
import com.petadopt.entity.ChatMessage;
import java.util.List;

public interface ChatService {
    Long send(Long userId, MessageRequest request);
    List<ChatMessage> thread(Long userId, Long petId, Long otherId);
    List<ChatMessage> conversations(Long userId);
    int unreadCount(Long userId);
}
