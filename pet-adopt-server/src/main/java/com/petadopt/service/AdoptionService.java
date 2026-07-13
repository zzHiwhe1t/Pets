package com.petadopt.service;

import com.petadopt.dto.ApplicationRequest;
import com.petadopt.entity.AdoptionApplication;
import java.util.List;

public interface AdoptionService {
    Long apply(Long userId, ApplicationRequest request);
    List<AdoptionApplication> sent(Long userId);
    List<AdoptionApplication> received(Long userId);
    void review(Long userId, Long id, String status, String remark);
}
