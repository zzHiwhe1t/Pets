package com.petadopt.mapper;

import com.petadopt.entity.AdoptionApplication;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AdoptionApplicationMapper {
    @Select("select * from adoption_application where pet_id=#{petId} and applicant_id=#{applicantId} and deleted=0 limit 1")
    AdoptionApplication findExisting(@Param("petId") Long petId, @Param("applicantId") Long applicantId);

    @Insert("insert into adoption_application(pet_id,applicant_id,owner_id,reason,living_condition,experience,status) values(#{petId},#{applicantId},#{ownerId},#{reason},#{livingCondition},#{experience},'PENDING')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdoptionApplication application);

    @Select("select a.*,p.name pet_name,p.cover_image pet_cover_image,u.nickname owner_nickname from adoption_application a left join pet p on a.pet_id=p.id left join user u on a.owner_id=u.id where a.applicant_id=#{userId} and a.deleted=0 order by a.create_time desc")
    List<AdoptionApplication> findSent(Long userId);

    @Select("select a.*,p.name pet_name,p.cover_image pet_cover_image,u.nickname applicant_nickname from adoption_application a left join pet p on a.pet_id=p.id left join user u on a.applicant_id=u.id where a.owner_id=#{userId} and a.deleted=0 order by a.create_time desc")
    List<AdoptionApplication> findReceived(Long userId);

    @Select("select * from adoption_application where id=#{id} and deleted=0")
    AdoptionApplication findById(Long id);

    @Update("update adoption_application set status=#{status},review_remark=#{remark},review_time=now(),update_time=now() where id=#{id} and owner_id=#{ownerId} and status='PENDING' and deleted=0")
    int review(@Param("id") Long id, @Param("ownerId") Long ownerId, @Param("status") String status, @Param("remark") String remark);

    @Update("update adoption_application set status='CANCELLED',update_time=now() where pet_id=#{petId} and id != #{approvedId} and status='PENDING' and deleted=0")
    int cancelOthers(@Param("petId") Long petId, @Param("approvedId") Long approvedId);
}
