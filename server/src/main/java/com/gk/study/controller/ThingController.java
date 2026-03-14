package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.*;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.RecordService;
import com.gk.study.service.ThingService;
import com.gk.study.service.UserService;
import com.gk.study.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/thing")
public class ThingController {

    private final static Logger logger = LoggerFactory.getLogger(ThingController.class);

    @Autowired
    ThingService service;

    @Autowired
    RecordService recordService;
    //********mhy优化：
    @Autowired
    UserService userService; // 一个UserService来处理用户相关的业务逻辑

    @Value("${File.uploadPath}")
    private String uploadPath;
    //查询表单
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String c, String cc){
        List<Thing> list =  service.getThingList(keyword, sort, c, cc);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }
    //详情页
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(HttpServletRequest request, String id){
        Thing thing =  service.getThingById(id);


        // ------------------保存浏览记录--------------------
        String ip = IpUtils.getIpAddr(request);
        Record record = recordService.getRecord(thing.getId(), ip);
        if(record != null){
            record.setScore(record.getScore() + 1);
            recordService.updateRecord(record);
        }else {
            Record entity = new Record();
            entity.setThingId(thing.getId());
            entity.setIp(ip);
            entity.setScore(1);
            recordService.createRecord(entity);
        }
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", thing);
    }

    // 推荐接口
    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public APIResponse recommend(HttpServletRequest request){
        // 获取ip列表
        List<String> ips = recordService.getRecordIpList();
        List<UserCF> users = new ArrayList<>();
        for(String ip : ips){
            // 获取ip对于的会员
            List<Record> recordList = recordService.getRecordListByIp(ip);
//            System.out.println(recordList);
            UserCF userCF = new UserCF(ip);
            for(Record record: recordList){
                userCF.set(record.thingId, record.score);
            }
            users.add(userCF);
        }



        List<Thing> thingList;

        if(users.size() <= 1){
            // 1个用户不满足协同推荐条件
            thingList = service.getDefaultThingList();
        }else {
            Recommend recommend = new Recommend();
            String currentIp = IpUtils.getIpAddr(request);
            List<RecEntity> recommendList = recommend.recommend(currentIp, users);
            List<Long> thingIdList = recommendList.stream().map(A -> A.thingId).collect(Collectors.toList());
            if(thingIdList.size() > 0){
                thingList = service.getThingListByThingIds(thingIdList);
                if(thingList == null || thingList.size() < 1){
                    // 如推荐量太少，则走默认
                    thingList = service.getDefaultThingList();
                }
            }else {
                thingList = service.getDefaultThingList();
            }
        }


        return new APIResponse(ResponeCode.SUCCESS, "查询成功", thingList);
    }

    // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备（根据点赞量最高wish_count)
    // 获取最热的男前3个和女前3个
    @RequestMapping(value = "/top3-hot-gender", method = RequestMethod.GET)
    public APIResponse getTop3HotGenderThings() {
        List<Thing> top3HotMaleThings = service.getTop3HotMaleThings();
        List<Thing> top3HotFemaleThings = service.getTop3HotFemaleThings();

        Map<String, List<Thing>> result = new HashMap<>();
        result.put("male", top3HotMaleThings);
        result.put("female", top3HotFemaleThings);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", result);
    }


    ///想一想，判断是否充值，若充值了就可创建会员
    //创建会员
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Thing thing) throws IOException {
        String url = saveThing(thing);
        if(!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }

        service.createThing(thing);
        // ******** mhy添加代码：
        // 新增会员后，更新关联用户的角色为会员用户（假设角色2代表会员用户）
        if (thing.getUserId() != null) {
            User user = userService.findById(thing.getUserId());
            if (user != null) {
                user.setRole(User.DemoUser + ""); // 更新用户角色为会员
                userService.updateUser(user);
            } else {
                logger.warn("未找到ID为{}的用户", thing.getUserId());
            }
        } else {
            logger.warn("创建会员时，用户ID为空");
        }
        // ********
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /*原代码
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteThing(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }*/
    //******* mhy优化：删除的同时，修改用户表role角色为1（普通用户）
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        System.out.println("ids===" + ids);
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteThingAndUpdateUserRole(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功并更新用户角色");
    }

    //更新会员
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Thing thing) throws IOException {
        System.out.println(thing);
        String url = saveThing(thing);
        if(!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }
        service.updateThing(thing);
        // ******** mhy添加代码：
        // 修改会员也进行更新用户角色为会员（角色DemoUser=2代表会员用户）
        if (thing.getUserId() != null) {
            User user = userService.findById(thing.getUserId());
            if (user != null) {
                user.setRole(User.DemoUser + ""); // 更新用户角色为会员
                userService.updateUser(user);
            } else {
                logger.warn("未找到ID为{}的用户", thing.getUserId());
            }
        } else {
            logger.warn("创建会员时，用户ID为空");
        }
        // ********

        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    // 评分
    @RequestMapping(value = "/rate", method = RequestMethod.POST)
    @Transactional
    public APIResponse rate(String thingId, int rate) throws IOException {
        Thing thing = service.getThingById(thingId);
        thing.rate = String.valueOf((Integer.parseInt(thing.rate) + rate)/2);
        service.updateThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "成功");
    }

    public String saveThing(Thing thing) throws IOException {
        // 从 Thing 对象中获取上传的图片文件
        MultipartFile file = thing.getImageFile();
        String newFileName = null;

        // 检查文件是否不为空且不是空文件
        if (file != null && !file.isEmpty()) {
            // 获取上传文件的原始文件名
            String oldFileName = file.getOriginalFilename();
            // 生成一个随机字符串，用于防止文件名冲突
            String randomStr = UUID.randomUUID().toString();
            // 生成新的文件名，保留原始文件的扩展名
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            // 构建文件的保存路径，将文件保存到 uploadPath/image/ 目录下
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            // 创建目标文件对象
            File destFile = new File(filePath);

            // 如果目标文件的父目录不存在，则创建父目录
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            // 将上传的文件保存到目标路径
            file.transferTo(destFile);
        }

        // 如果生成了新的文件名，则将新的文件名设置到 Thing 对象的 cover 属性中
        if (!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }

        // 返回生成的新文件名
        return newFileName;
    }

    @RequestMapping(value = "/listUserThing", method = RequestMethod.GET)
    public APIResponse listUserThing(String userId){
        List<Thing> list =  service.getUserThing(userId);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 根据用户ID获取会员信息
    @RequestMapping(value = "/getMemberInfoByUserId", method = RequestMethod.GET)
    public APIResponse getMemberInfo(String userId) {
        Thing thing = service.getThingByUserId(userId);
        if (thing != null) {
            Map<String, String> memberInfo = new HashMap<>();
            memberInfo.put("receiverName", thing.getTitle());
            memberInfo.put("receiverPhone", thing.getPhone());
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", memberInfo);
        } else {
            return new APIResponse(ResponeCode.FAIL, "未找到会员信息", null);
        }
    }


}
