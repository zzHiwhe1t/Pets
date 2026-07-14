package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.dto.ApplicationRequest;
import com.petadopt.entity.AdoptionApplication;
import com.petadopt.service.AdoptionService;
import com.petadopt.util.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/applications")
public class AdoptionController {
    private final AdoptionService service;
    public AdoptionController(AdoptionService service) { this.service = service; }

    @PostMapping
    public ApiResult<Map<String,Long>> apply(@Validated @RequestBody ApplicationRequest request) { return ApiResult.ok(Collections.singletonMap("id", service.apply(UserContext.require(), request))); }
    @GetMapping("/sent") public ApiResult<List<AdoptionApplication>> sent() { return ApiResult.ok(service.sent(UserContext.require())); }
    @GetMapping("/received") public ApiResult<List<AdoptionApplication>> received() { return ApiResult.ok(service.received(UserContext.require())); }
    @PutMapping("/{id}/review") public ApiResult<Void> review(@PathVariable Long id, @RequestBody Map<String,String> body) { service.review(UserContext.require(), id, body.get("status"), body.get("remark")); return ApiResult.ok(); }
}
