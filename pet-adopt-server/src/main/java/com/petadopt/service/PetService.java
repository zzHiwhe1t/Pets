package com.petadopt.service;

import com.petadopt.common.PageResult;
import com.petadopt.dto.PetSaveRequest;
import com.petadopt.entity.Pet;

public interface PetService {
    PageResult<Pet> page(Long categoryId, Long subcategoryId, String keyword, String status, Long ownerId, int page, int pageSize);
    Pet detail(Long id);
    Long create(Long ownerId, PetSaveRequest request);
    void update(Long id, Long ownerId, PetSaveRequest request);
    void updateStatus(Long id, Long ownerId, String status);
    void delete(Long id, Long ownerId);
}
