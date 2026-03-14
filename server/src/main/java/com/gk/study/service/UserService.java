package com.gk.study.service;


import com.gk.study.entity.Comment;
import com.gk.study.entity.RechargeRecord;
import com.gk.study.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> getUserList(String keyword);
    User getAdminUser(User user);
    User getNormalUser(User user);
    ///******** mhy优化：添加会员登录
    User getDemoUser(User user);
    void createUser(User user);
    void deleteUser(String id);

    void updateUser(User user);
    //********mhy优化：为会员与用户id关联
    User findById(String userId);

    User getUserByToken(String token);
    User getUserByUserName(String username);

    User getUserDetail(String userId);
    // ******** mhy优化：添加充值功能
    void userRecharge(String userId, BigDecimal amount);

    // ******** mhy 优化：获取用户余额
    BigDecimal getUserBalance(String userId);
    // ******** mhy 优化：获取用户充值记录
    List<RechargeRecord> listRechargeRecords(String userId);
    List<RechargeRecord> getRechargeList();
    // ******** mhy 优化：根据用户id查找用户名(聊天备用)
    String getUsernameById(String userId);
    // ******** mhy 优化：根据用户id查找用户基本信息(用户名，用户头像，用户角色、用户余额)
    // 获取用户基本信息（用户名、头像、角色、余额）
    Map<String, Object> getUserBasicInfo(String userId);

}
