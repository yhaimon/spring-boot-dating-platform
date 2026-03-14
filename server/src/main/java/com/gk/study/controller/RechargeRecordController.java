package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.RechargeRecord;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.RechargeRecordService;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recharge")
public class RechargeRecordController {

    @Autowired
    UserService userService;
    @Autowired
    RechargeRecordService rechargeService;
    // ******** mhy 优化：
    // 获取用户余额接口
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/getUserBalance", method = RequestMethod.GET)
    public APIResponse getUserBalance(String userId) {
        try {
            BigDecimal balance = userService.getUserBalance(userId);
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", balance);
        } catch (Exception e) {
            return new APIResponse(ResponeCode.FAIL, "获取失败：" + e.getMessage());
        }
    }
    // ******** mhy 优化：
    // 添加充值接口
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/userRecharge", method = RequestMethod.POST)
    @Transactional
    public APIResponse recharge(String userId, BigDecimal amount) {
        //System.out.println("用户id："+userId+"\t用户金额："+amount);
        try {
            rechargeService.userRecharge(userId, amount);
            return new APIResponse(ResponeCode.SUCCESS, "充值成功");
            //充值记录在UserserviceImpl实现层
        } catch (Exception e) {
            return new APIResponse(ResponeCode.FAIL, "充值失败：" + e.getMessage());
        }
    }
    // ******** mhy 优化：
    // 获取某用户充值记录
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/listRechargeRecordsById", method = RequestMethod.GET)
    public APIResponse listRechargeRecords(String userId) {
        try {
            List<RechargeRecord> records = rechargeService.listRechargeRecords(userId);
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", records);
        } catch (Exception e) {
            return new APIResponse(ResponeCode.FAIL, "获取失败：" + e.getMessage());
        }
    }
    // 管理员获取所有用户充值记录
    @RequestMapping(value = "/RechargeRecordList", method = RequestMethod.GET)
    public APIResponse RechargeRecordList(){
        List<RechargeRecord> list =  rechargeService.getRechargeList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }


}
