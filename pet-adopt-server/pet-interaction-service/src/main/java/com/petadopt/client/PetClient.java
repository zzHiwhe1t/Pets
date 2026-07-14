package com.petadopt.client;

import com.petadopt.common.ApiResult;
import com.petadopt.remote.PetStatusCommand;
import com.petadopt.remote.PetSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "pet-pet-service")
public interface PetClient {
    @GetMapping("/internal/pets/{id}")
    ApiResult<PetSummary> summary(@PathVariable("id") Long id);

    @PostMapping("/internal/pets/batch")
    ApiResult<List<PetSummary>> summaries(@RequestBody List<Long> ids);

    @PutMapping("/internal/pets/{id}/status")
    ApiResult<Void> updateStatus(@PathVariable("id") Long id, @RequestBody PetStatusCommand command);
}
