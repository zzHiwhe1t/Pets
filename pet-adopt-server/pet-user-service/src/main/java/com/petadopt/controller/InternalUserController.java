package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.remote.AuthSession;
import com.petadopt.remote.UserSummary;
import com.petadopt.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal")
public class InternalUserController {
    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth/resolve")
    public ApiResult<AuthSession> resolve(@RequestParam String token) {
        AuthSession session = userService.resolveToken(token);
        return session == null ? new ApiResult<>(401, "登录已过期", null) : ApiResult.ok(session);
    }

    @GetMapping("/users/{id}")
    public ApiResult<UserSummary> summary(@PathVariable Long id) {
        UserSummary user = userService.summary(id);
        return user == null ? new ApiResult<>(404, "用户不存在", null) : ApiResult.ok(user);
    }

    @PostMapping("/users/batch")
    public ApiResult<List<UserSummary>> summaries(@RequestBody List<Long> ids) {
        return ApiResult.ok(userService.summaries(ids));
    }
}
