package com.petadopt.service.impl;

import com.petadopt.common.BizException;
import com.petadopt.client.PetClient;
import com.petadopt.client.UserClient;
import com.petadopt.dto.ApplicationRequest;
import com.petadopt.entity.AdoptionApplication;
import com.petadopt.mapper.AdoptionApplicationMapper;
import com.petadopt.remote.PetStatusCommand;
import com.petadopt.remote.PetSummary;
import com.petadopt.remote.RemoteResult;
import com.petadopt.remote.UserSummary;
import com.petadopt.service.AdoptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AdoptionServiceImpl implements AdoptionService {
    private final AdoptionApplicationMapper mapper;
    private final PetClient petClient;
    private final UserClient userClient;
    public AdoptionServiceImpl(AdoptionApplicationMapper mapper, PetClient petClient, UserClient userClient) {
        this.mapper = mapper;
        this.petClient = petClient;
        this.userClient = userClient;
    }

    @Transactional
    public Long apply(Long userId, ApplicationRequest request) {
        PetSummary pet = RemoteResult.data(petClient.summary(request.getPetId()), "宠物服务暂时不可用");
        if (pet == null || !"AVAILABLE".equals(pet.getStatus())) throw new BizException("该宠物当前不可申请领养");
        if (pet.getOwnerId().equals(userId)) throw new BizException("不能申请领养自己发布的宠物");
        if (mapper.findExisting(request.getPetId(), userId) != null) throw new BizException("你已提交过领养申请");
        AdoptionApplication app = new AdoptionApplication();
        app.setPetId(request.getPetId()); app.setApplicantId(userId); app.setOwnerId(pet.getOwnerId());
        app.setReason(request.getReason()); app.setLivingCondition(request.getLivingCondition()); app.setExperience(request.getExperience());
        mapper.insert(app);
        // 首次提交申请后进入“领养中”，避免同一时间重复申请；驳回后恢复待领养。
        changePetStatus(pet.getId(), pet.getOwnerId(), "IN_PROGRESS");
        return app.getId();
    }

    public List<AdoptionApplication> sent(Long userId) {
        List<AdoptionApplication> list = mapper.findSent(userId);
        enrich(list);
        return list;
    }
    public List<AdoptionApplication> received(Long userId) {
        List<AdoptionApplication> list = mapper.findReceived(userId);
        enrich(list);
        return list;
    }

    @Transactional
    public void review(Long userId, Long id, String status, String remark) {
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) throw new BizException("审核状态无效");
        AdoptionApplication app = mapper.findById(id);
        if (app == null || !app.getOwnerId().equals(userId)) throw new BizException(403, "无权审核该申请");
        if (mapper.review(id, userId, status, remark) == 0) throw new BizException("该申请已处理");
        if ("APPROVED".equals(status)) {
            changePetStatus(app.getPetId(), userId, "ADOPTED");
            mapper.cancelOthers(app.getPetId(), id);
        } else {
            changePetStatus(app.getPetId(), userId, "AVAILABLE");
        }
    }

    private void changePetStatus(Long petId, Long ownerId, String status) {
        PetStatusCommand command = new PetStatusCommand();
        command.setOwnerId(ownerId);
        command.setStatus(status);
        RemoteResult.data(petClient.updateStatus(petId, command), "宠物服务暂时不可用");
    }

    private void enrich(List<AdoptionApplication> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> petIds = new LinkedHashSet<>();
        Set<Long> userIds = new LinkedHashSet<>();
        for (AdoptionApplication item : list) {
            petIds.add(item.getPetId());
            userIds.add(item.getOwnerId());
            userIds.add(item.getApplicantId());
        }
        List<PetSummary> pets = RemoteResult.data(petClient.summaries(new ArrayList<>(petIds)), "宠物服务暂时不可用");
        List<UserSummary> users = RemoteResult.data(userClient.summaries(new ArrayList<>(userIds)), "用户服务暂时不可用");
        Map<Long, PetSummary> petMap = new HashMap<>();
        Map<Long, UserSummary> userMap = new HashMap<>();
        for (PetSummary pet : pets) petMap.put(pet.getId(), pet);
        for (UserSummary user : users) userMap.put(user.getId(), user);
        for (AdoptionApplication item : list) {
            PetSummary pet = petMap.get(item.getPetId());
            UserSummary owner = userMap.get(item.getOwnerId());
            UserSummary applicant = userMap.get(item.getApplicantId());
            if (pet != null) {
                item.setPetName(pet.getName());
                item.setPetCoverImage(pet.getCoverImage());
            }
            if (owner != null) item.setOwnerNickname(owner.getNickname());
            if (applicant != null) item.setApplicantNickname(applicant.getNickname());
        }
    }
}
