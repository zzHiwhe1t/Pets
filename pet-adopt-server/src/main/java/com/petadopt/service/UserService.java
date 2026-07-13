package com.petadopt.service;

import com.petadopt.dto.LoginRequest;
import com.petadopt.dto.RegisterRequest;
import com.petadopt.entity.User;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginRequest request);
    void register(RegisterRequest request);
    User profile(Long userId);
    void updateProfile(Long userId, User profile);
    void logout(Long userId);
}
