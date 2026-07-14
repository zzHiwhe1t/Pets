package com.petadopt.remote;

import lombok.Data;

@Data
public class AuthSession {
    private Long id;
    private String username;
    private String nickname;
    private String role;
}
