package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.ChatMessage;
import com.gk.study.mapper.ChatMessageMapper;
import com.gk.study.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
//    @Autowired
//    private ChatMessageMapper mapper;
//
//    @Override
//    public List<ChatMessage> getMessagesByUsers(Long senderId, Long receiverId) {
//        return mapper.selectByUsers(senderId, receiverId);
//    }
//
//    @Override
//    public List<ChatMessage> list(QueryWrapper<ChatMessage> queryWrapper) {
//        return Collections.emptyList();
//    }
//
//    @Override
//    public void saveMessage(ChatMessage message) {
//        mapper.insert(message);
//    }
}