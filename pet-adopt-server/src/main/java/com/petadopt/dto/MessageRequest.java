package com.petadopt.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageRequest {
    @NotNull private Long petId;
    @NotNull private Long receiverId;
    @NotBlank private String content;
}
