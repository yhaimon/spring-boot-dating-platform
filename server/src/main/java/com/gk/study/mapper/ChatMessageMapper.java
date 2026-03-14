package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
//    List<ChatMessage> selectByUsers(Long senderId, Long receiverId);
}