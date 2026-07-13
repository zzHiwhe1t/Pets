package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.dto.MessageRequest;
import com.petadopt.entity.ChatMessage;
import com.petadopt.entity.Pet;
import com.petadopt.mapper.ChatMessageMapper;
import com.petadopt.mapper.PetMapper;
import com.petadopt.mapper.UserMapper;
import com.petadopt.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatMessageMapper mapper;
    private final PetMapper petMapper;
    private final UserMapper userMapper;
    public ChatServiceImpl(ChatMessageMapper mapper, PetMapper petMapper, UserMapper userMapper) { this.mapper = mapper; this.petMapper = petMapper; this.userMapper = userMapper; }

    public Long send(Long userId, MessageRequest request) {
        Pet pet = petMapper.findById(request.getPetId());
        if (pet == null || userMapper.findSafeById(request.getReceiverId()) == null) throw new BizException("会话对象不存在");
        if (userId.equals(request.getReceiverId())) throw new BizException("不能给自己发送消息");
        ChatMessage message = new ChatMessage();
        message.setPetId(request.getPetId()); message.setSenderId(userId); message.setReceiverId(request.getReceiverId()); message.setContent(request.getContent().trim());
        mapper.insert(message); return message.getId();
    }

    @Transactional
    public List<ChatMessage> thread(Long userId, Long petId, Long otherId) {
        List<ChatMessage> list = mapper.findThread(petId, userId, otherId); mapper.markRead(petId, userId, otherId); return list;
    }
    public List<ChatMessage> conversations(Long userId) { return mapper.findConversations(userId); }
    public int unreadCount(Long userId) { return mapper.unreadCount(userId); }
}
