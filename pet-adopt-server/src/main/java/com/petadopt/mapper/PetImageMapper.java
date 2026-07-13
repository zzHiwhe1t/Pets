package com.petadopt.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PetImageMapper {
    @Select("select image_url from pet_image where pet_id=#{petId} and deleted=0 order by sort,id")
    List<String> findUrls(Long petId);

    @Insert("insert into pet_image(pet_id,image_url,sort) values(#{petId},#{url},#{sort})")
    int insert(@Param("petId") Long petId, @Param("url") String url, @Param("sort") int sort);

    @Update("update pet_image set deleted=1 where pet_id=#{petId}")
    int deleteByPetId(Long petId);
}
