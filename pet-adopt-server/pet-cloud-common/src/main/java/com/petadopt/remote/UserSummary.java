package com.petadopt.remote;

import lombok.Data;

@Data
public class UserSummary {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String role;
}
