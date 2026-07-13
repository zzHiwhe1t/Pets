package com.petadopt.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PetSaveRequest {
    @NotNull private Long categoryId;
    @NotNull private Long subcategoryId;
    @NotBlank private String name;
    @NotBlank private String breed;
    @NotNull private Integer ageMonths;
    @NotBlank private String gender;
    private BigDecimal weight;
    private String personality;
    private String vaccineStatus;
    private String dewormStatus;
    private String sterilizationStatus;
    @NotBlank private String healthStatus;
    @NotBlank private String feedingNotes;
    private String ownerMessage;
    @NotNull private List<String> images;
}
