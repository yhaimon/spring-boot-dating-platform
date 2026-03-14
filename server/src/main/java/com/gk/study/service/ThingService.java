package com.gk.study.service;


import com.gk.study.entity.Thing;

import java.util.List;

public interface ThingService {
    List<Thing> getThingList(String keyword, String sort, String c, String cc);
    void createThing(Thing thing);
    void deleteThing(String id);

    void updateThing(Thing thing);

    Thing getThingById(String id);

    void addWishCount(String thingId);

    void addCollectCount(String thingId);

    List<Thing> getUserThing(String userId);

    List<Thing> getThingListByThingIds(List<Long> thingIdList);

    List<Thing> getDefaultThingList();

    //******* mhy优化：删除的同时，修改用户表role角色为1（普通用户）
    void deleteThingAndUpdateUserRole(String id);
    // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备（根据点赞量最高wish_count)
    List<Thing> getTop3HotMaleThings();
    List<Thing> getTop3HotFemaleThings();
    //根据用户id 查找会员信息
    Thing getThingByUserId(String userId);

}
