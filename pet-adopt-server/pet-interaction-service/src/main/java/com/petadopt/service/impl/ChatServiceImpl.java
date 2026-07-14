package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.client.PetClient;
import com.petadopt.client.UserClient;
import com.petadopt.dto.MessageRequest;
import com.petadopt.entity.ChatMessage;
import com.petadopt.mapper.ChatMessageMapper;
import com.petadopt.remote.PetSummary;
import com.petadopt.remote.RemoteResult;
import com.petadopt.remote.UserSummary;
import com.petadopt.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatMessageMapper mapper;
    private final PetClient petClient;
    private final UserClient userClient;
    public ChatServiceImpl(ChatMessageMapper mapper, PetClient petClient, UserClient userClient) {
        this.mapper = mapper;
        this.petClient = petClient;
        this.userClient = userClient;
    }

    public Long send(Long userId, MessageRequest request) {
        PetSummary pet = RemoteResult.data(petClient.summary(request.getPetId()), "宠物服务暂时不可用");
        UserSummary receiver = RemoteResult.data(userClient.summary(request.getReceiverId()), "用户服务暂时不可用");
        if (pet == null || receiver == null) throw new BizException("会话对象不存在");
        if (userId.equals(request.getReceiverId())) throw new BizException("不能给自己发送消息");
        ChatMessage message = new ChatMessage();
        message.setPetId(request.getPetId()); message.setSenderId(userId); message.setReceiverId(request.getReceiverId()); message.setContent(request.getContent().trim());
        mapper.insert(message); return message.getId();
    }

    @Transactional
    public List<ChatMessage> thread(Long userId, Long petId, Long otherId) {
        List<ChatMessage> list = mapper.findThread(petId, userId, otherId);
        enrichThread(list);
        mapper.markRead(petId, userId, otherId);
        return list;
    }
    public List<ChatMessage> conversations(Long userId) {
        List<ChatMessage> list = mapper.findConversations(userId);
        enrichConversations(list);
        return list;
    }
    public int unreadCount(Long userId) { return mapper.unreadCount(userId); }

    private void enrichThread(List<ChatMessage> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> ids = new LinkedHashSet<>();
        for (ChatMessage message : list) ids.add(message.getSenderId());
        Map<Long, UserSummary> users = userMap(ids);
        for (ChatMessage message : list) {
            UserSummary sender = users.get(message.getSenderId());
            if (sender != null) {
                message.setSenderNickname(sender.getNickname());
                message.setSenderAvatar(sender.getAvatar());
            }
        }
    }

    private void enrichConversations(List<ChatMessage> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> userIds = new LinkedHashSet<>();
        Set<Long> petIds = new LinkedHashSet<>();
        for (ChatMessage message : list) {
            userIds.add(message.getOtherId());
            petIds.add(message.getPetId());
        }
        Map<Long, UserSummary> users = userMap(userIds);
        List<PetSummary> pets = RemoteResult.data(petClient.summaries(new ArrayList<>(petIds)), "宠物服务暂时不可用");
        Map<Long, PetSummary> petMap = new HashMap<>();
        for (PetSummary pet : pets) petMap.put(pet.getId(), pet);
        for (ChatMessage message : list) {
            UserSummary other = users.get(message.getOtherId());
            PetSummary pet = petMap.get(message.getPetId());
            if (other != null) {
                message.setSenderNickname(other.getNickname());
                message.setSenderAvatar(other.getAvatar());
            }
            if (pet != null) {
                message.setPetName(pet.getName());
                message.setPetCoverImage(pet.getCoverImage());
            }
        }
    }

    private Map<Long, UserSummary> userMap(Set<Long> ids) {
        List<UserSummary> users = RemoteResult.data(userClient.summaries(new ArrayList<>(ids)), "用户服务暂时不可用");
        Map<Long, UserSummary> map = new HashMap<>();
        for (UserSummary user : users) map.put(user.getId(), user);
        return map;
    }
}
