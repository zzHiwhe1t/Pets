package com.petadopt.mapper;

import com.petadopt.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where username=#{username} and deleted=0 limit 1")
    User findByUsername(String username);

    @Select("select id,username,nickname,phone,email,avatar,role,create_time from user where id=#{id} and deleted=0")
    User findSafeById(Long id);

    @Select("select id from user where token=#{token} and deleted=0 limit 1")
    Long findIdByToken(String token);

    @Insert("insert into user(username,password,nickname,phone,role) values(#{username},#{password},#{nickname},#{phone},'USER')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("update user set token=#{token},update_time=now() where id=#{id}")
    int updateToken(@Param("id") Long id, @Param("token") String token);

    @Update("update user set nickname=#{nickname},phone=#{phone},email=#{email},avatar=#{avatar},update_time=now() where id=#{id}")
    int updateProfile(User user);
}
