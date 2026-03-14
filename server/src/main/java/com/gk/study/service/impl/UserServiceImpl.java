package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Comment;
import com.gk.study.entity.RechargeRecord;
import com.gk.study.mapper.RechargeRecordMapper;
import com.gk.study.service.UserService;
import com.gk.study.entity.User;
import com.gk.study.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    @Override
    public List<User> getUserList(String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(keyword)){
            // like查询
            queryWrapper.like("username", keyword);
        }
        queryWrapper.orderBy(true, false, "create_time");
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public User getAdminUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.gt("role", "2"); // 大于1 //********mhy优化：修改角色role为大于2，才是管理员账号
        return userMapper.selectOne(queryWrapper);
    }

    /*原
    @Override
    public User getNormalUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.eq("role", "1");
        return userMapper.selectOne(queryWrapper);
    }*/

    // ******** mhy优化：用户登录，修改用户角色小于3
    @Override
    public User getNormalUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.lt("role", "3");//******** mhy优化：用户登录，修改用户角色小于3

        return userMapper.selectOne(queryWrapper);
    }


    // ******** mhy优化： 添加会员用户，这样会员也可以登录成功
    @Override
    public User getDemoUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.eq("role", "2");
        return userMapper.selectOne(queryWrapper);
    }


    @Override
    public void createUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("token", token);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByUserName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserDetail(String userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }
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

    // ******** mhy 优化：获取用户余额
    @Override
    public BigDecimal getUserBalance(String userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            return user.getAmount();
        } else {
            throw new RuntimeException("用户不存在");
        }
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
        return rechargeRecordMapper.getList();
    }

    // ******** mhy 优化：根据用户id查找用户名(聊天备用)
    public String getUsernameById(String userId) {
        User user = userMapper.selectById(userId); // 假设使用 MyBatis-Plus 的 Mapper
        return user != null ? user.getUsername() : null;
    }
    // ******** mhy 优化：根据用户id查找用户基本信息(用户名，用户头像，用户角色、用户余额)
    // 获取用户基本信息（用户名、头像、角色、余额）
    @Override
    public Map<String, Object> getUserBasicInfo(String userId) {
        User user = this.findById(userId); // 调用已有的 findById 方法获取用户信息
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("username", user.getUsername());
        basicInfo.put("avatar", user.getAvatar());
        basicInfo.put("role", user.getRole());
        basicInfo.put("balance", user.getAmount()); // 用户余额

        return basicInfo;
    }

    //********mhy优化：添加查找用户
    @Override
    public User findById(String userId) {
        return userMapper.selectById(userId);
    }








}
