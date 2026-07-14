package com.petadopt.client;

import com.petadopt.common.ApiResult;
import com.petadopt.remote.UserSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "pet-user-service")
public interface UserClient {
    @GetMapping("/internal/users/{id}")
    ApiResult<UserSummary> summary(@PathVariable("id") Long id);

    @PostMapping("/internal/users/batch")
    ApiResult<List<UserSummary>> summaries(@RequestBody List<Long> ids);
}
