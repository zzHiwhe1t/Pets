package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.common.PageResult;
import com.petadopt.client.UserClient;
import com.petadopt.dto.PetSaveRequest;
import com.petadopt.entity.Pet;
import com.petadopt.mapper.PetImageMapper;
import com.petadopt.mapper.PetMapper;
import com.petadopt.remote.PetSummary;
import com.petadopt.remote.RemoteResult;
import com.petadopt.remote.UserSummary;
import com.petadopt.service.PetService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class PetServiceImpl implements PetService {
    private final PetMapper petMapper;
    private final PetImageMapper imageMapper;
    private final UserClient userClient;
    public PetServiceImpl(PetMapper petMapper, PetImageMapper imageMapper, UserClient userClient) {
        this.petMapper = petMapper;
        this.imageMapper = imageMapper;
        this.userClient = userClient;
    }

    public PageResult<Pet> page(Long categoryId, Long subcategoryId, String keyword, String status, Long ownerId, int page, int pageSize) {
        int safePage = Math.max(1, page); int safeSize = Math.min(50, Math.max(1, pageSize));
        List<Pet> records = petMapper.findPage(categoryId, subcategoryId, keyword, status, ownerId, (safePage - 1) * safeSize, safeSize);
        enrichOwners(records);
        return new PageResult<>(petMapper.count(categoryId, subcategoryId, keyword, status, ownerId), records);
    }

    public Pet detail(Long id) {
        Pet pet = petMapper.findById(id);
        if (pet == null) throw new BizException(404, "宠物信息不存在");
        pet.setImages(imageMapper.findUrls(id));
        enrichOwners(Collections.singletonList(pet));
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

    @Override
    public PetSummary summary(Long id) {
        Pet pet = petMapper.findById(id);
        return pet == null ? null : toSummary(pet);
    }

    @Override
    public List<PetSummary> summaries(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        List<PetSummary> result = new ArrayList<>();
        for (Pet pet : petMapper.findByIds(ids)) result.add(toSummary(pet));
        return result;
    }

    private void enrichOwners(List<Pet> pets) {
        if (pets == null || pets.isEmpty()) return;
        Set<Long> ids = new LinkedHashSet<>();
        for (Pet pet : pets) if (pet.getOwnerId() != null) ids.add(pet.getOwnerId());
        if (ids.isEmpty()) return;
        List<UserSummary> users = RemoteResult.data(userClient.summaries(new ArrayList<>(ids)), "用户服务暂时不可用");
        Map<Long, UserSummary> byId = new HashMap<>();
        for (UserSummary user : users) byId.put(user.getId(), user);
        for (Pet pet : pets) {
            UserSummary user = byId.get(pet.getOwnerId());
            if (user != null) {
                pet.setOwnerNickname(user.getNickname());
                pet.setOwnerAvatar(user.getAvatar());
                pet.setOwnerPhone(user.getPhone());
            }
        }
    }

    private PetSummary toSummary(Pet pet) {
        PetSummary summary = new PetSummary();
        summary.setId(pet.getId());
        summary.setOwnerId(pet.getOwnerId());
        summary.setName(pet.getName());
        summary.setCoverImage(pet.getCoverImage());
        summary.setStatus(pet.getStatus());
        return summary;
    }

    private Pet toPet(PetSaveRequest request) { Pet pet = new Pet(); BeanUtils.copyProperties(request, pet); return pet; }
    private void validateImages(PetSaveRequest request) { if (request.getImages() == null || request.getImages().isEmpty()) throw new BizException("请至少上传一张图片"); }
    private void saveImages(Long petId, List<String> urls) { for (int i = 0; i < urls.size(); i++) imageMapper.insert(petId, urls.get(i), i); }
}
