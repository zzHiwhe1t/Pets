package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.entity.PetCategory;
import com.petadopt.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }
    @GetMapping("/tree")
    public ApiResult<List<PetCategory>> tree() { return ApiResult.ok(service.tree()); }
}
