package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.RechargeRecord;
import com.gk.study.entity.User;
import com.gk.study.mapper.RechargeRecordMapper;
import com.gk.study.mapper.UserMapper;
import com.gk.study.service.RechargeRecordService;
import com.gk.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    // ******** mhy优化：
    // 充值功能实现
    @Override
    public void userRecharge(String userId, BigDecimal amount) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setAmount(user.getAmount().add(amount));
            // ******** mhy 优化：当充值或余额满99元，用户角色role自动成为会员角色
            if (user.getAmount().compareTo(new BigDecimal("99")) >= 0) {
                user.setRole(User.DemoUser + ""); // 更新用户角色为会员角色
            }
            // ********
            userMapper.updateById(user);
            // ******** mhy 优化：使用创建的充值记录
            createRechargeRecord(userId, amount, "success");
        } else {
            throw new RuntimeException("用户不存在");
        }
    }
    // ******** mhy 优化：
    // 创建充值记录
    private void createRechargeRecord(String userId, BigDecimal amount, String status) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        RechargeRecord record = new RechargeRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setCreateTime(LocalDateTime.now());
        record.setStatus(status);
        rechargeRecordMapper.insert(record);
    }

    // ******** mhy 优化：获取用户充值记录
    /*@Override
    public List<RechargeRecord> listRechargeRecords(String userId) {
        return rechargeRecordMapper.selectList(new QueryWrapper<RechargeRecord>().eq("user_id", userId));
    }*/
    @Override
    public List<RechargeRecord> listRechargeRecords(String userId) {
        QueryWrapper<RechargeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time"); // 按 createTime 降序排序
        return rechargeRecordMapper.selectList(queryWrapper);
    }
    //所有充值记录
    @Override
    public List<RechargeRecord> getRechargeList() {
        QueryWrapper<RechargeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time"); // 按 createTime 降序排序
        return rechargeRecordMapper.getList();
    }


}
