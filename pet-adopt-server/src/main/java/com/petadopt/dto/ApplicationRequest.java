package com.petadopt.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ApplicationRequest {
    @NotNull private Long petId;
    @NotBlank private String reason;
    @NotBlank private String livingCondition;
    private String experience;
}
