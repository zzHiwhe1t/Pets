package com.petadopt.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdoptionApplication {
    private Long id;
    private Long petId;
    private Long applicantId;
    private Long ownerId;
    private String reason;
    private String livingCondition;
    private String experience;
    private String status;
    private String reviewRemark;
    private LocalDateTime reviewTime;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String petName;
    private String petCoverImage;
    private String applicantNickname;
    private String ownerNickname;
}
