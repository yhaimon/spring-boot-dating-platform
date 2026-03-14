package com.gk.study.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.entity.Comment;
import com.gk.study.entity.Dynamic;
import com.gk.study.entity.User;
import com.gk.study.mapper.DynamicMapper;
import com.gk.study.mapper.UserMapper;
import com.gk.study.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class DynamicServiceImpl implements DynamicService {
    @Autowired
    private DynamicMapper dynamicMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public List<Dynamic> getDynamicList() {
        return dynamicMapper.selectList(null);
    }
    //查询所有人的动态
    @Override
    public List<Dynamic> getDynamicList(String keyword, String sort, String userId) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        // 搜索
        //queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);
//        // 只查询当前用户的动态
//        queryWrapper.eq("user_id", userId);
        // 按照 createdAt 字段降序排列，最新的动态在最上面
        queryWrapper.orderByDesc("created_at");
        List<Dynamic> dynamics = dynamicMapper.selectList(queryWrapper);
        if (StringUtils.hasText(userId)) {
            queryWrapper.eq("user_id", userId);
        }
        // 填充用户信息
//        for (Dynamic dynamic : dynamics) {
//            User user = userMapper.selectById(dynamic.getUserId());
//            if (user != null) {
//                dynamic.setAvatar(user.getAvatar());
//                dynamic.setUser_name(user.getUsername());
//            }
//        }
        return dynamics;
    }
    // 根据id查找动态列表
    @Override
    public List<Dynamic> getDynamicListById(String userId) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("created_at");
        return dynamicMapper.selectList(queryWrapper);
    }

    @Override
    public void createDynamic(Dynamic dynamic) {
        //dynamic.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        dynamic.setCreatedAt(new Date());
        dynamic.setUpdatedAt(new Date());
        dynamicMapper.insert(dynamic);
    }

    @Override
    public void updateDynamic(Dynamic dynamic) {
        dynamic.setUpdatedAt(new Date());//设置更新时间
        dynamicMapper.updateById(dynamic);
    }

    @Override
    public void deleteDynamic(String id) {
        dynamicMapper.deleteById(id);
    }

//    @Override
//    public Dynamic getDynamicDetail(String id) {
//        return dynamicMapper.selectById(id);
//    }
@Override
public Dynamic getDynamicDetail(String id) {
    Dynamic dynamic = dynamicMapper.selectById(id);
    if (dynamic == null) {
        System.out.println("未找到动态ID: " + id); // 打印日志
    }
    return dynamic;
}



}