package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Thing;
import com.gk.study.entity.User;
import com.gk.study.mapper.ThingMapper;
import com.gk.study.mapper.UserMapper;
import com.gk.study.service.ThingService;
import com.gk.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {
    @Autowired
    ThingMapper mapper;

    @Autowired
    UserMapper userMapper;

    // ***** mhy优化
    private static final Logger logger = LoggerFactory.getLogger(ThingServiceImpl.class);
    // *******

    @Override
    public List<Thing> getThingList(String keyword, String sort, String c, String cc) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        // 搜索
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);
        // 排序
        if (StringUtils.isNotBlank(sort)) {
            if (sort.equals("recent")) {
                queryWrapper.orderBy(true, false, "create_time");
            } else if (sort.equals("hot") || sort.equals("recommend")) {
                queryWrapper.orderBy(true, false, "pv");
            }
        }else {
            queryWrapper.orderBy(true, false, "create_time");
        }
        // 根据分类筛选
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq(true, "classification_id", c);
        }
        // 根据分类2筛选
        if (StringUtils.isNotBlank(cc) && !cc.equals("全部")) {
            queryWrapper.eq(true, "location", cc);
        }
        List<Thing> things = mapper.selectList(queryWrapper);
        return things;
    }
    // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备（根据点赞量最高wish_count)
    @Override
    public List<Thing> getTop3HotMaleThings() {
        return mapper.getTop3HotMaleThings();
    }
    // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备（根据点赞量最高wish_count)
    @Override
    public List<Thing> getTop3HotFemaleThings() {
        return mapper.getTop3HotFemaleThings();
    }
    // ********

    @Override
    public void createThing(Thing thing) {
        System.out.println(thing);
        thing.setCreateTime(String.valueOf(System.currentTimeMillis()));

        if (thing.getPv() == null) {
            thing.setPv("0");
        }
        if (thing.getScore() == null) {
            thing.setScore("0");
        }
        if (thing.getWishCount() == null) {
            thing.setWishCount("0");
        }
        mapper.insert(thing);

        //********mhy优化：添加代码
        // 更新用户角色为会员
        /*if (thing.getUserId() != null) {
            User user = userService.findById(thing.getUserId());
            if (user != null) {
                user.setRole(User.DemoUser + ""); // 设置为会员用户
                userService.updateUser(user);
            }else {
                System.out.println("无此用户");
            }
        }
        */
        //二次优化
        /*if (thing.getUserId() != null) {
            User user = userService.findById(thing.getUserId());
            if (user != null) {
                logger.info("将用户角色更新为用户ID为{}的会员用户:", thing.getUserId());
                user.setRole(User.DemoUser + ""); // 设置为会员用户
                userService.updateUser(user);
            }else {
                logger.warn("没有为用户id {}找到这样的用户", thing.getUserId());
                System.out.println("无此用户");
            }
        }*/
        
        
        
    }


    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);

    }
    //******* mhy优化：删除的同时，修改用户表role角色为1（普通用户）
    @Override
    public void deleteThingAndUpdateUserRole(String id) {
        // 删除 thing 记录
        Thing thing = mapper.selectById(Long.valueOf(id));
        if (thing != null) {
            // 根据 userId 更新用户角色
            User user = userMapper.selectById(thing.getUserId());
            if (user != null) {
                user.setRole(String.valueOf(User.NormalUser));
                userMapper.updateById(user);
            }
            // 删除 thing 记录
            mapper.deleteById(Long.valueOf(id));
        }
    }


    //*******

    @Override
    public void updateThing(Thing thing) {

        mapper.updateById(thing);
        //********mhy优化：添加代码
        // 修改会员也进行更新用户角色为会员
        /*if (thing.getUserId() != null) {
            User user = userService.findById(thing.getUserId());
            if (user != null) {
                user.setRole(User.DemoUser + ""); // 设置为会员用户
                userService.updateUser(user);
            }else {
                System.out.println("无此用户");
            }
        }*/
        // ********
    }

    @Override
    public Thing getThingById(String id) {
        Thing thing = mapper.selectById(id);
        thing.setPv(String.valueOf(Integer.parseInt(thing.getPv()) + 1));
        mapper.updateById(thing);

        return thing;
    }

    // 心愿数加1
    @Override
    public void addWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setWishCount(String.valueOf(Integer.parseInt(thing.getWishCount()) + 1));
        mapper.updateById(thing);
    }

    // 收藏数加1
    @Override
    public void addCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setCollectCount(String.valueOf(Integer.parseInt(thing.getCollectCount()) + 1));
        mapper.updateById(thing);
    }

    @Override
    public List<Thing> getUserThing(String userId) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return mapper.selectList(queryWrapper);
    }

    @Override
    public List<Thing> getThingListByThingIds(List<Long> thingIdList) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", thingIdList);
        return mapper.selectList(queryWrapper);
    }

    @Override
    public List<Thing> getDefaultThingList() {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("pv");
        return mapper.selectList(queryWrapper);
    }

    //根据用户id 查找会员信息
    @Override
    public Thing getThingByUserId(String userId) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }

}
