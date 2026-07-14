package com.petadopt.service;

import com.petadopt.dto.LoginRequest;
import com.petadopt.dto.RegisterRequest;
import com.petadopt.entity.User;
import com.petadopt.remote.AuthSession;
import com.petadopt.remote.UserSummary;
import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginRequest request);
    void register(RegisterRequest request);
    User profile(Long userId);
    void updateProfile(Long userId, User profile);
    void logout(Long userId);
    AuthSession resolveToken(String token);
    UserSummary summary(Long userId);
    List<UserSummary> summaries(List<Long> userIds);
}
