package com.petadopt.remote;

import lombok.Data;

@Data
public class PetStatusCommand {
    private Long ownerId;
    private String status;
}
