package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("love_dynamic")
public class Dynamic implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private String id; // 动态的唯一标识

    @TableField
    private String userId; // 发布动态的用户 ID

    @TableField
    private String title; // 动态的标题

    @TableField
    private String content; // 动态的内容

    @TableField
    private String images; // 动态的图片，以 JSON 格式存储

    @TableField
    public String likeCount; // 喜欢数

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt; // 动态的发布时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt; // 动态的最后更新时间

    // 非数据库字段，用于临时存储图片文件
    @TableField(exist = false)
    private List<MultipartFile> imagesFile;

    // 非数据库字段，用于存储图片的 Base64 数据
    @TableField(exist = false)
    private List<String> imagePreviews;

    // 非数据库字段，用于临时存储用户信息
//    @TableField(exist = false)
//    private String avatar; // 用户头像
//    @TableField(exist = false)
//    private String user_name; // 用户名

}