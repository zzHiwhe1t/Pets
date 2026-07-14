package com.petadopt.mapper;

import com.petadopt.entity.ChatMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ChatMessageMapper {
    @Insert("insert into chat_message(pet_id,sender_id,receiver_id,content,read_flag) values(#{petId},#{senderId},#{receiverId},#{content},0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);

    @Select("select * from chat_message where pet_id=#{petId} and deleted=0 and ((sender_id=#{userId} and receiver_id=#{otherId}) or (sender_id=#{otherId} and receiver_id=#{userId})) order by create_time asc")
    List<ChatMessage> findThread(@Param("petId") Long petId, @Param("userId") Long userId, @Param("otherId") Long otherId);

    @Select("select x.*,g.other_id other_id, " +
            "(select count(*) from chat_message z where z.pet_id=x.pet_id and z.sender_id=g.other_id and z.receiver_id=#{userId} and z.read_flag=0 and z.deleted=0) unread_count " +
            "from chat_message x join (select pet_id,case when sender_id=#{userId} then receiver_id else sender_id end other_id,max(id) max_id from chat_message where (sender_id=#{userId} or receiver_id=#{userId}) and deleted=0 group by pet_id,other_id) g on x.id=g.max_id " +
            "order by x.create_time desc")
    List<ChatMessage> findConversations(Long userId);

    @Update("update chat_message set read_flag=1 where pet_id=#{petId} and sender_id=#{otherId} and receiver_id=#{userId} and read_flag=0")
    int markRead(@Param("petId") Long petId, @Param("userId") Long userId, @Param("otherId") Long otherId);

    @Select("select count(*) from chat_message where receiver_id=#{userId} and read_flag=0 and deleted=0")
    int unreadCount(Long userId);
}
