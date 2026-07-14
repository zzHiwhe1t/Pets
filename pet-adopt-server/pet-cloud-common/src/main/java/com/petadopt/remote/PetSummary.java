package com.petadopt.remote;

import lombok.Data;

@Data
public class PetSummary {
    private Long id;
    private Long ownerId;
    private String name;
    private String coverImage;
    private String status;
}
