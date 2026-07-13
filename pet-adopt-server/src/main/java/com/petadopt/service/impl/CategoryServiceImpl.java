package com.petadopt.service.impl;

import com.petadopt.entity.PetCategory;
import com.petadopt.mapper.CategoryMapper;
import com.petadopt.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper mapper;
    public CategoryServiceImpl(CategoryMapper mapper) { this.mapper = mapper; }

    public List<PetCategory> tree() {
        List<PetCategory> all = mapper.findAll();
        Map<Long, PetCategory> map = new LinkedHashMap<>();
        List<PetCategory> roots = new ArrayList<>();
        for (PetCategory item : all) { item.setChildren(new ArrayList<>()); map.put(item.getId(), item); }
        for (PetCategory item : all) {
            if (item.getParentId() == 0) roots.add(item);
            else if (map.containsKey(item.getParentId())) map.get(item.getParentId()).getChildren().add(item);
        }
        return roots;
    }
}
