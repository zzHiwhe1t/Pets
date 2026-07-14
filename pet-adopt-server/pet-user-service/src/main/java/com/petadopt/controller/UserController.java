package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.entity.User;
import com.petadopt.service.UserService;
import com.petadopt.util.UserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @GetMapping("/me")
    public ApiResult<User> me() { return ApiResult.ok(service.profile(UserContext.require())); }

    @PutMapping("/me")
    public ApiResult<Void> update(@RequestBody User profile) { service.updateProfile(UserContext.require(), profile); return ApiResult.ok(); }
}
