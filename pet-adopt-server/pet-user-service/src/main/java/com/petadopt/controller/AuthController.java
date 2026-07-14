package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.dto.LoginRequest;
import com.petadopt.dto.RegisterRequest;
import com.petadopt.service.UserService;
import com.petadopt.util.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService service;
    public AuthController(UserService service) { this.service = service; }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) { return ApiResult.ok(service.login(request)); }

    @PostMapping("/register")
    public ApiResult<Void> register(@Validated @RequestBody RegisterRequest request) { service.register(request); return ApiResult.ok(); }

    @PostMapping("/logout")
    public ApiResult<Void> logout() { service.logout(UserContext.require()); return ApiResult.ok(); }
}
