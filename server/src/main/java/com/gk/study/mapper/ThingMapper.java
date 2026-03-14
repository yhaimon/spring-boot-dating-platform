package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Thing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ThingMapper extends BaseMapper<Thing> {
    //*******mhy优化
    boolean isUserAlreadyRegisteredAsMember(String userId);
//    List<Thing> getList();
//    boolean update(Thing thing);
    // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
    List<Thing> getTop3HotMaleThings();
    List<Thing> getTop3HotFemaleThings();
}
