package com.petadopt.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Pet {
    private Long id;
    private Long ownerId;
    private Long categoryId;
    private Long subcategoryId;
    private String name;
    private String breed;
    private Integer ageMonths;
    private String gender;
    private BigDecimal weight;
    private String personality;
    private String vaccineStatus;
    private String dewormStatus;
    private String sterilizationStatus;
    private String healthStatus;
    private String feedingNotes;
    private String ownerMessage;
    private String coverImage;
    private String status;
    private Integer viewCount;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String categoryName;
    private String subcategoryName;
    private String ownerNickname;
    private String ownerAvatar;
    private String ownerPhone;
    private List<String> images;
}
