package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("b_chat_message")
public class ChatMessage implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("sender_id")
    private Long senderId;
    @TableField("receiver_id")
    private Long receiverId;
    @TableField
    private String message;
    @TableField("send_time")
    private Date sendTime  = new Date(); // 设置默认值为当前时间;
}