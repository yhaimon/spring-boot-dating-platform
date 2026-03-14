package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Comment;
import com.gk.study.entity.RechargeRecord;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    String salt = "abcd1234";

    @Autowired
    UserService userService;

    @Value("${File.uploadPath}")
    private String uploadPath;



    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword){
        List<User> list =  userService.getUserList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }


    //返回用户信息
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String userId){
        User user =  userService.getUserDetail(userId);
        //System.out.println("找到用户detail详细信息========== " + user);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", user);
    }

    // ********mhy优化：获取用户角色
    @RequestMapping(value = "/userRole", method = RequestMethod.GET)
    public APIResponse getUserRole(String userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "用户不存在");
        }
        String role = user.getRole(); // User 类有一个 getRole() 方法来获取角色
        Map<String, String> responseData = new HashMap<>();
        responseData.put("role", role);
        //System.out.println("找到用户角色%%%%%%%: " + user.getRole());
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseData);
    }
    // UserController.java
   /* @RequestMapping(value = "/getUserRole", method = RequestMethod.GET)
    public APIResponse getUserRole(String userId) {
        User user = userService.findById(userId);
        if (user != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", user.getRole());
        } else {
            return new APIResponse(ResponeCode.FAIL, "用户不存在", null);
        }
    }*/
    // ******** mhy 优化：根据用户id查找用户基本信息(用户名，用户头像，用户角色、用户余额)
    // 根据用户ID获取用户基本信息
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/getUserBasicInfo", method = RequestMethod.GET)
    public APIResponse getUserBasicInfo(String userId) {
        try {
            Map<String, Object> userBasicInfo = userService.getUserBasicInfo(userId);
            System.out.println("找到用户信息: " + userBasicInfo);
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", userBasicInfo);
        } catch (Exception e) {
            return new APIResponse(ResponeCode.FAIL, "获取失败：" + e.getMessage());
        }
    }



    // ********

    // 后台用户登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user){
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser =  userService.getAdminUser(user);
        if(responseUser != null) {
            if(responseUser.getStatus().equals("0")){
                return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
            }else {
                return new APIResponse(ResponeCode.FAIL, "账号禁用");
            }
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户登录
    /*@RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user){
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser =  userService.getNormalUser(user);
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }*/
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin( User user) {
        // 确保前端发送的请求体被正确解析
        System.out.println("用户名为: " + user.getUsername());
        System.out.println("密码为: " + user.getPassword());
        // 加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
        System.out.println("加密的密码为: " + encryptedPassword);

        // 设置加密后的密码进行查询
        user.setPassword(encryptedPassword);
        User responseUser = userService.getNormalUser(user);
        System.out.println("找到用户信息"+responseUser);
        if (responseUser != null) {
            System.out.println("找到用户名: " + responseUser.getUsername());
            System.out.println("找到用户角色**********: " + responseUser.getRole());
            System.out.println("找到用户头像**********: " + responseUser.getAvatar());
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        } else {
            System.out.println("没有找到用户");
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }
    //******** mhy优化： 会员登录
    /*@RequestMapping(value = "/memberLogin", method = RequestMethod.POST)
    public APIResponse memberLogin( User user) {
        // 确保前端发送的请求体被正确解析
        System.out.println("用户名为: " + user.getUsername());
        System.out.println("密码为: " + user.getPassword());

        // 加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
        System.out.println("加密的密码为: " + encryptedPassword);

        // 设置加密后的密码进行查询
        user.setPassword(encryptedPassword);
        User responseUser = userService.getDemoUser(user); //获取会员用户

        if (responseUser != null) {
            System.out.println("找到用户名: " + responseUser.getUsername());
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        } else {
            System.out.println("没有找到用户");
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }*/


    // 普通用户注册
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @Transactional
    public APIResponse userRegister(User user) throws IOException {
        //判断是否为空
        if (!StringUtils.isEmpty(user.getUsername())
                && !StringUtils.isEmpty(user.getPassword())
                && !StringUtils.isEmpty(user.getRePassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            // 验证密码
            if(!user.getPassword().equals(user.getRePassword())) {
                return new APIResponse(ResponeCode.FAIL, "密码不一致");
            }
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            // 设置密码
            user.setPassword(md5Str);
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(md5Str);
            //设置头像
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            // 设置角色
            user.setRole(String.valueOf(User.NormalUser));
            // 设置状态
            user.setStatus("0");
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));
            //创建用户
            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }
    //创建
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(User user) throws IOException {

        if (!StringUtils.isEmpty(user.getUsername()) || !StringUtils.isEmpty(user.getPassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            // 设置密码
            user.setPassword(md5Str);
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(md5Str);
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));


            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }
    //删除
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            userService.deleteUser(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
    //更新
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(User user) throws IOException {
        // update不能修改密码，故置空
        user.setPassword(null);
        String avatar = saveAvatar(user);
        if(!StringUtils.isEmpty(avatar)) {
            user.avatar = avatar;
        }
        userService.updateUser(user);
        System.out.println(user);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    // ********mhy优化：检查用户ID是否存在的API
    /*
    @RequestMapping(value = "/exists", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Boolean>> userExists(@RequestParam String userId) {
        User user = userService.findById(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", user != null);
        return ResponseEntity.ok(response);
    }*/
    // 检查用户是否存在的API
    /*@RequestMapping(value = "/exists", method = RequestMethod.GET)
    public ResponseEntity<?> checkUserExists(@RequestParam String userId) {
        User user = userService.findById(userId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("exists", user != null);
        return ResponseEntity.ok(map);
    }*/

    // ********

   /* //原
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        User tmpUser =  userService.getUserDetail(user.getId());
        if(tmpUser.getRole().equals(String.valueOf(User.NormalUser))){
            // username和password不能改，故置空
            user.setUsername(null);
            user.setPassword(null);
            user.setRole(String.valueOf(User.NormalUser));
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.updateUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }
*/
    // ******** mhy优化：会员也可以修改个人信息,也可以不这样，比如若是会员，把前端的个人信息隐藏掉
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        User tmpUser =  userService.getUserDetail(user.getId());
        if(tmpUser.getRole().equals(String.valueOf(User.NormalUser))){
            // username和password不能改，故置空
            user.setUsername(null);
            user.setPassword(null);
            user.setRole(String.valueOf(User.NormalUser));
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.updateUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }
        // ******** ******** mhy优化：会员也可以修改个人信息
        if(tmpUser.getRole().equals(String.valueOf(User.DemoUser))){
            // username和password不能改，故置空
            user.setUsername(null);
            user.setPassword(null);
            //每次修改的时候把角色修改为会员
            user.setRole(String.valueOf(User.DemoUser));
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.updateUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

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
            userService.userRecharge(userId, amount);
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
            List<RechargeRecord> records = userService.listRechargeRecords(userId);
            return new APIResponse(ResponeCode.SUCCESS, "获取成功", records);
        } catch (Exception e) {
            return new APIResponse(ResponeCode.FAIL, "获取失败：" + e.getMessage());
        }
    }
    // 管理员获取所有用户充值记录
    @RequestMapping(value = "/RechargeRecordList", method = RequestMethod.GET)
    public APIResponse RechargeRecordList(){
        List<RechargeRecord> list =  userService.getRechargeList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }



    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @Transactional
    public APIResponse updatePwd(String userId, String password, String newPassword) throws IOException {
        User user =  userService.getUserDetail(userId);
        //if(user.getRole().equals(String.valueOf(User.NormalUser))) {
        // ******** 优化： 会员用户也可以修改密码
        if(user.getRole().equals(String.valueOf(User.NormalUser)) || user.getRole().equals(String.valueOf(User.DemoUser))) {
            String md5Pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(user.getPassword().equals(md5Pwd)){
                user.setPassword(DigestUtils.md5DigestAsHex((newPassword + salt).getBytes()));
                userService.updateUser(user);
            }else {
                return new APIResponse(ResponeCode.FAIL, "原密码错误");
            }
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }

    }

    public String saveAvatar(User user) throws IOException {
        MultipartFile file = user.getAvatarFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {

            // 存文件
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "avatar" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            user.avatar = newFileName;
        }
        return newFileName;
    }
}
