package com.petadopt.entity;

import lombok.Data;
import java.util.List;

@Data
public class PetCategory {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private String image;
    private Integer sort;
    private List<PetCategory> children;
}
