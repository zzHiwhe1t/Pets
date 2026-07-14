package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.dto.LoginRequest;
import com.petadopt.dto.RegisterRequest;
import com.petadopt.entity.User;
import com.petadopt.mapper.UserMapper;
import com.petadopt.remote.AuthSession;
import com.petadopt.remote.UserSummary;
import com.petadopt.service.UserService;
import com.petadopt.util.PasswordUtil;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    public UserServiceImpl(UserMapper userMapper) { this.userMapper = userMapper; }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(PasswordUtil.sha256(request.getPassword()))) {
            throw new BizException("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        userMapper.updateToken(user.getId(), token);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("user", userMapper.findSafeById(user.getId()));
        return result;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) throw new BizException("用户名已存在");
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.sha256(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        userMapper.insert(user);
    }

    public User profile(Long userId) {
        User user = userMapper.findSafeById(userId);
        if (user == null) throw new BizException(404, "用户不存在");
        return user;
    }

    public void updateProfile(Long userId, User profile) {
        profile.setId(userId);
        if (userMapper.updateProfile(profile) == 0) throw new BizException(404, "用户不存在");
    }

    public void logout(Long userId) { userMapper.updateToken(userId, null); }

    @Override
    public AuthSession resolveToken(String token) {
        if (token == null || token.trim().isEmpty()) return null;
        User user = userMapper.findByToken(token.trim());
        if (user == null) return null;
        AuthSession session = new AuthSession();
        session.setId(user.getId());
        session.setUsername(user.getUsername());
        session.setNickname(user.getNickname());
        session.setRole(user.getRole());
        return session;
    }

    @Override
    public UserSummary summary(Long userId) {
        User user = userMapper.findSafeById(userId);
        return user == null ? null : toSummary(user);
    }

    @Override
    public List<UserSummary> summaries(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyList();
        List<UserSummary> result = new ArrayList<>();
        for (User user : userMapper.findSafeByIds(userIds)) result.add(toSummary(user));
        return result;
    }

    private UserSummary toSummary(User user) {
        UserSummary summary = new UserSummary();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setNickname(user.getNickname());
        summary.setPhone(user.getPhone());
        summary.setEmail(user.getEmail());
        summary.setAvatar(user.getAvatar());
        summary.setRole(user.getRole());
        return summary;
    }
}
