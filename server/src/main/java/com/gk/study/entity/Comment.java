package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("love_comment")
public class Comment implements Serializable {

    public static final int DetailComment = 1; // 详情评论
    public static final int DynamicComment = 2;  // 动态评论

    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    @TableField
    public String content; // 评论内容
    @TableField
    public String commentTime; // 评论时间
    @TableField
    public String likeCount; // 喜欢数
    @TableField
    public String userId;
    @TableField(exist = false)
    public String username; // 用户名
    @TableField
    public String thingId;
    @TableField(exist = false)
    public String title; // 名称
    @TableField(exist = false)
    public String cover; // 封面

    // ******** mhy优化：回复的评论ID
    @TableField
    private Long replyToId; // 回复的评论ID
    @TableField(exist = false)
    private List<Comment> replies; // 回复列表

    // 新增字段：评论类型
    @TableField
    public String commentType; // 评论类型（例如：1 或 2）

}
