package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.dto.LoginRequest;
import com.petadopt.dto.RegisterRequest;
import com.petadopt.entity.User;
import com.petadopt.mapper.UserMapper;
import com.petadopt.service.UserService;
import com.petadopt.util.PasswordUtil;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
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

    public User profile(Long userId) { return userMapper.findSafeById(userId); }

    public void updateProfile(Long userId, User profile) {
        profile.setId(userId);
        userMapper.updateProfile(profile);
    }

    public void logout(Long userId) { userMapper.updateToken(userId, null); }
}
