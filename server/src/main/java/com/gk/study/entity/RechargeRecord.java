package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("love_recharge_record")
public class RechargeRecord implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    //******** mhy优化：用户id的String改为Long类型********
    @TableField
    public String userId;//充值记录
    @TableField
    private BigDecimal amount;
    @TableField
    private LocalDateTime createTime;
    @TableField
    private String status;
}