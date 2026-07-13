package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.dto.ApplicationRequest;
import com.petadopt.entity.AdoptionApplication;
import com.petadopt.entity.Pet;
import com.petadopt.mapper.AdoptionApplicationMapper;
import com.petadopt.mapper.PetMapper;
import com.petadopt.service.AdoptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdoptionServiceImpl implements AdoptionService {
    private final AdoptionApplicationMapper mapper;
    private final PetMapper petMapper;
    public AdoptionServiceImpl(AdoptionApplicationMapper mapper, PetMapper petMapper) { this.mapper = mapper; this.petMapper = petMapper; }

    public Long apply(Long userId, ApplicationRequest request) {
        Pet pet = petMapper.findById(request.getPetId());
        if (pet == null || !"AVAILABLE".equals(pet.getStatus())) throw new BizException("该宠物当前不可申请领养");
        if (pet.getOwnerId().equals(userId)) throw new BizException("不能申请领养自己发布的宠物");
        if (mapper.findExisting(request.getPetId(), userId) != null) throw new BizException("你已提交过领养申请");
        AdoptionApplication app = new AdoptionApplication();
        app.setPetId(request.getPetId()); app.setApplicantId(userId); app.setOwnerId(pet.getOwnerId());
        app.setReason(request.getReason()); app.setLivingCondition(request.getLivingCondition()); app.setExperience(request.getExperience());
        mapper.insert(app);
        // 首次提交申请后进入“领养中”，避免同一时间重复申请；驳回后恢复待领养。
        petMapper.updateStatus(pet.getId(), pet.getOwnerId(), "IN_PROGRESS");
        return app.getId();
    }

    public List<AdoptionApplication> sent(Long userId) { return mapper.findSent(userId); }
    public List<AdoptionApplication> received(Long userId) { return mapper.findReceived(userId); }

    @Transactional
    public void review(Long userId, Long id, String status, String remark) {
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) throw new BizException("审核状态无效");
        AdoptionApplication app = mapper.findById(id);
        if (app == null || !app.getOwnerId().equals(userId)) throw new BizException(403, "无权审核该申请");
        if (mapper.review(id, userId, status, remark) == 0) throw new BizException("该申请已处理");
        if ("APPROVED".equals(status)) {
            petMapper.updateStatus(app.getPetId(), userId, "ADOPTED");
            mapper.cancelOthers(app.getPetId(), id);
        } else {
            petMapper.updateStatus(app.getPetId(), userId, "AVAILABLE");
        }
    }
}
