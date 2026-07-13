package com.petadopt.entity;

import lombok.Data;

@Data
public class PetImage {
    private Long id;
    private Long petId;
    private String imageUrl;
    private Integer sort;
}
