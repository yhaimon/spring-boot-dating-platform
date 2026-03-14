package com.gk.study.service;


import com.gk.study.entity.RechargeRecord;
import com.gk.study.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RechargeRecordService {

    // ******** mhy优化：添加充值功能
    void userRecharge(String userId, BigDecimal amount);
     // ******** mhy 优化：获取用户充值记录
    List<RechargeRecord> listRechargeRecords(String userId);
    // 管理员获取所有用户充值记录
    List<RechargeRecord> getRechargeList();


}
