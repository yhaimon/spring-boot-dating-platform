package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ChatMessage;
import com.gk.study.mapper.ChatMessageMapper;
import com.gk.study.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/chatMessages")
@Controller
public class ChatMessageController {


    @Autowired
    private ChatMessageService chatMessageService;
@PostMapping
@RequestMapping(value = "/createMessage", method = RequestMethod.POST)
public ResponseEntity<List<ChatMessage>> saveMessage(@RequestBody ChatMessage message) {
    try {
        chatMessageService.save(message);
        return ResponseEntity.ok(Collections.singletonList(message));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(null);
    }
}

/*
    @GetMapping("/{userId}/{otherUserId}")
    public List<ChatMessage> getMessages(@PathVariable Long userId, @PathVariable Long otherUserId) {
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sender_id", userId).eq("receiver_id", otherUserId)
                .or().eq("sender_id", otherUserId).eq("receiver_id", userId);
        return chatMessageService.list(queryWrapper);
    }
*/
@RequestMapping(value = "/getMsg/{userId}/{otherUserId}", method = RequestMethod.POST)
@CrossOrigin(origins = "http://localhost:8080")
public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long userId, @PathVariable Long otherUserId) {
    QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("sender_id", userId).eq("receiver_id", otherUserId)
            .or().eq("sender_id", otherUserId).eq("receiver_id", userId);
    List<ChatMessage> messages = chatMessageService.list(queryWrapper);
    return ResponseEntity.ok(messages);
}

}