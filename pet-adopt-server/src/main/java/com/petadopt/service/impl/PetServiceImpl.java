package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.common.PageResult;
import com.petadopt.dto.PetSaveRequest;
import com.petadopt.entity.Pet;
import com.petadopt.mapper.PetImageMapper;
import com.petadopt.mapper.PetMapper;
import com.petadopt.service.PetService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PetServiceImpl implements PetService {
    private final PetMapper petMapper;
    private final PetImageMapper imageMapper;
    public PetServiceImpl(PetMapper petMapper, PetImageMapper imageMapper) { this.petMapper = petMapper; this.imageMapper = imageMapper; }

    public PageResult<Pet> page(Long categoryId, Long subcategoryId, String keyword, String status, Long ownerId, int page, int pageSize) {
        int safePage = Math.max(1, page); int safeSize = Math.min(50, Math.max(1, pageSize));
        List<Pet> records = petMapper.findPage(categoryId, subcategoryId, keyword, status, ownerId, (safePage - 1) * safeSize, safeSize);
        return new PageResult<>(petMapper.count(categoryId, subcategoryId, keyword, status, ownerId), records);
    }

    public Pet detail(Long id) {
        Pet pet = petMapper.findById(id);
        if (pet == null) throw new BizException(404, "宠物信息不存在");
        pet.setImages(imageMapper.findUrls(id));
        petMapper.increaseView(id);
        return pet;
    }

    @Transactional
    public Long create(Long ownerId, PetSaveRequest request) {
        validateImages(request);
        Pet pet = toPet(request); pet.setOwnerId(ownerId); pet.setCoverImage(request.getImages().get(0));
        petMapper.insert(pet); saveImages(pet.getId(), request.getImages()); return pet.getId();
    }

    @Transactional
    public void update(Long id, Long ownerId, PetSaveRequest request) {
        validateImages(request);
        Pet pet = toPet(request); pet.setId(id); pet.setOwnerId(ownerId); pet.setCoverImage(request.getImages().get(0));
        if (petMapper.update(pet) == 0) throw new BizException(403, "无权修改该宠物");
        imageMapper.deleteByPetId(id); saveImages(id, request.getImages());
    }

    public void updateStatus(Long id, Long ownerId, String status) {
        if (!java.util.Arrays.asList("AVAILABLE", "IN_PROGRESS", "ADOPTED", "OFFLINE").contains(status)) throw new BizException("宠物状态无效");
        if (petMapper.updateStatus(id, ownerId, status) == 0) throw new BizException(403, "无权操作该宠物");
    }

    public void delete(Long id, Long ownerId) {
        if (petMapper.logicalDelete(id, ownerId) == 0) throw new BizException(403, "无权删除该宠物");
    }

    private Pet toPet(PetSaveRequest request) { Pet pet = new Pet(); BeanUtils.copyProperties(request, pet); return pet; }
    private void validateImages(PetSaveRequest request) { if (request.getImages() == null || request.getImages().isEmpty()) throw new BizException("请至少上传一张图片"); }
    private void saveImages(Long petId, List<String> urls) { for (int i = 0; i < urls.size(); i++) imageMapper.insert(petId, urls.get(i), i); }
}
