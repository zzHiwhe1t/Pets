package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.common.PageResult;
import com.petadopt.dto.PetSaveRequest;
import com.petadopt.entity.Pet;
import com.petadopt.service.PetService;
import com.petadopt.util.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/pets")
public class PetController {
    private final PetService service;
    public PetController(PetService service) { this.service = service; }

    @GetMapping
    public ApiResult<PageResult<Pet>> page(@RequestParam(required=false) Long categoryId, @RequestParam(required=false) Long subcategoryId,
       @RequestParam(required=false) String keyword, @RequestParam(required=false) String status, @RequestParam(required=false) Long ownerId,
       @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="12") int pageSize) {
        return ApiResult.ok(service.page(categoryId, subcategoryId, keyword, status, ownerId, page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResult<Pet> detail(@PathVariable Long id) { return ApiResult.ok(service.detail(id)); }

    @PostMapping
    public ApiResult<Map<String, Long>> create(@Validated @RequestBody PetSaveRequest request) {
        return ApiResult.ok(Collections.singletonMap("id", service.create(UserContext.require(), request)));
    }

    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @Validated @RequestBody PetSaveRequest request) { service.update(id, UserContext.require(), request); return ApiResult.ok(); }

    @PutMapping("/{id}/status")
    public ApiResult<Void> status(@PathVariable Long id, @RequestBody Map<String,String> body) { service.updateStatus(id, UserContext.require(), body.get("status")); return ApiResult.ok(); }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) { service.delete(id, UserContext.require()); return ApiResult.ok(); }
}
