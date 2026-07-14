package com.petadopt.mapper;

import com.petadopt.entity.PetCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("select id,parent_id,name,code,image,sort from pet_category where deleted=0 order by parent_id,sort,id")
    List<PetCategory> findAll();

    @Select("select id,parent_id,name,code,image,sort from pet_category where id=#{id} and deleted=0")
    PetCategory findById(Long id);
}
