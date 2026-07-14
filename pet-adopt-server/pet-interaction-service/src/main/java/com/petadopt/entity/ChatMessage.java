package com.petadopt.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long petId;
    private Long senderId;
    private Long receiverId;
    private Long otherId;
    private String content;
    private Integer readFlag;
    private Integer deleted;
    private LocalDateTime createTime;
    private String senderNickname;
    private String receiverNickname;
    private String senderAvatar;
    private String petName;
    private String petCoverImage;
    private Integer unreadCount;
    private String lastContent;
}
