package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.remote.PetStatusCommand;
import com.petadopt.remote.PetSummary;
import com.petadopt.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/pets")
public class InternalPetController {
    private final PetService petService;

    public InternalPetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/{id}")
    public ApiResult<PetSummary> summary(@PathVariable Long id) {
        PetSummary pet = petService.summary(id);
        return pet == null ? new ApiResult<>(404, "宠物信息不存在", null) : ApiResult.ok(pet);
    }

    @PostMapping("/batch")
    public ApiResult<List<PetSummary>> summaries(@RequestBody List<Long> ids) {
        return ApiResult.ok(petService.summaries(ids));
    }

    @PutMapping("/{id}/status")
    public ApiResult<Void> updateStatus(@PathVariable Long id, @RequestBody PetStatusCommand command) {
        petService.updateStatus(id, command.getOwnerId(), command.getStatus());
        return ApiResult.ok();
    }
}
