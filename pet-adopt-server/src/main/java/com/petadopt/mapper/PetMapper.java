package com.petadopt.mapper;

import com.petadopt.entity.Pet;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PetMapper {
    String BASE = " from pet p left join pet_category c on p.category_id=c.id left join pet_category s on p.subcategory_id=s.id left join user u on p.owner_id=u.id where p.deleted=0 ";

    @Select({"<script>",
        "select p.*,c.name category_name,s.name subcategory_name,u.nickname owner_nickname,u.avatar owner_avatar ", BASE,
        "<if test='categoryId!=null'> and p.category_id=#{categoryId}</if>",
        "<if test='subcategoryId!=null'> and p.subcategory_id=#{subcategoryId}</if>",
        "<if test='status!=null and status!=\"\"'> and p.status=#{status}</if>",
        "<if test='ownerId!=null'> and p.owner_id=#{ownerId}</if>",
        "<if test='keyword!=null and keyword!=\"\"'> and (p.name like concat('%',#{keyword},'%') or p.breed like concat('%',#{keyword},'%'))</if>",
        " order by p.create_time desc limit #{offset},#{pageSize}",
        "</script>"})
    List<Pet> findPage(@Param("categoryId") Long categoryId, @Param("subcategoryId") Long subcategoryId,
                       @Param("keyword") String keyword, @Param("status") String status,
                       @Param("ownerId") Long ownerId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select({"<script>", "select count(*) ", BASE,
        "<if test='categoryId!=null'> and p.category_id=#{categoryId}</if>",
        "<if test='subcategoryId!=null'> and p.subcategory_id=#{subcategoryId}</if>",
        "<if test='status!=null and status!=\"\"'> and p.status=#{status}</if>",
        "<if test='ownerId!=null'> and p.owner_id=#{ownerId}</if>",
        "<if test='keyword!=null and keyword!=\"\"'> and (p.name like concat('%',#{keyword},'%') or p.breed like concat('%',#{keyword},'%'))</if>",
        "</script>"})
    long count(@Param("categoryId") Long categoryId, @Param("subcategoryId") Long subcategoryId,
               @Param("keyword") String keyword, @Param("status") String status, @Param("ownerId") Long ownerId);

    @Select("select p.*,c.name category_name,s.name subcategory_name,u.nickname owner_nickname,u.avatar owner_avatar,u.phone owner_phone " +
            "from pet p left join pet_category c on p.category_id=c.id left join pet_category s on p.subcategory_id=s.id " +
            "left join user u on p.owner_id=u.id where p.id=#{id} and p.deleted=0")
    Pet findById(Long id);

    @Insert("insert into pet(owner_id,category_id,subcategory_id,name,breed,age_months,gender,weight,personality,vaccine_status,deworm_status,sterilization_status,health_status,feeding_notes,owner_message,cover_image,status) " +
            "values(#{ownerId},#{categoryId},#{subcategoryId},#{name},#{breed},#{ageMonths},#{gender},#{weight},#{personality},#{vaccineStatus},#{dewormStatus},#{sterilizationStatus},#{healthStatus},#{feedingNotes},#{ownerMessage},#{coverImage},'AVAILABLE')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Pet pet);

    @Update("update pet set category_id=#{categoryId},subcategory_id=#{subcategoryId},name=#{name},breed=#{breed},age_months=#{ageMonths},gender=#{gender},weight=#{weight},personality=#{personality},vaccine_status=#{vaccineStatus},deworm_status=#{dewormStatus},sterilization_status=#{sterilizationStatus},health_status=#{healthStatus},feeding_notes=#{feedingNotes},owner_message=#{ownerMessage},cover_image=#{coverImage},update_time=now() where id=#{id} and owner_id=#{ownerId} and deleted=0")
    int update(Pet pet);

    @Update("update pet set status=#{status},update_time=now() where id=#{id} and owner_id=#{ownerId} and deleted=0")
    int updateStatus(@Param("id") Long id, @Param("ownerId") Long ownerId, @Param("status") String status);

    @Update("update pet set deleted=1,update_time=now() where id=#{id} and owner_id=#{ownerId} and deleted=0")
    int logicalDelete(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @Update("update pet set view_count=view_count+1 where id=#{id}")
    int increaseView(Long id);
}
