package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Dynamic;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.DynamicService;
import com.gk.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/dynamic") // 定义动态接口的基础路径
public class DynamicController {

    @Autowired
    DynamicService dynamicService; // 注入动态服务层，用于处理业务逻辑

    @Autowired
    UserService userService; // 注入用户服务层，用于处理用户相关业务逻辑

    @Value("${File.uploadPath}") // 从配置文件中获取文件上传路径
    private String uploadPath;
//    @Value("${File.baseUrl}") // 新增配置项，用于返回图片的访问路径
//    private String baseUrl;
    /**
     * 查询所有人的动态列表
     * @param keyword 关键字，用于搜索动态标题或内容
     * @param sort 排序方式
     * @param userId 用户ID，用于筛选特定用户的动态
     * @return 返回动态列表的API响应
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String userId) {
        List<Dynamic> list = dynamicService.getDynamicList(keyword, sort, userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }
    /**
     * 根据用户ID查询动态列表
     * @param userId 用户ID
     * @return 返回动态列表的API响应
     */
    @RequestMapping(value = "/getListById", method = RequestMethod.GET)
    public APIResponse getListById(@RequestParam String userId) {
        List<Dynamic> list = dynamicService.getDynamicListById(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建动态
     * @param dynamic 动态对象，包含动态的标题、内容、图片等信息
     * @return 返回创建结果的API响应
     * @throws IOException 如果文件上传过程中出现异常
     */
   // @Access(level = AccessLevel.DEMO) // 限制只有会员级别及以上才能创建动态
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional // 保证事务性操作
    public APIResponse create(Dynamic dynamic) throws IOException {
        String imagesUrl = saveImages(dynamic.getImagesFile());
        if (!StringUtils.isEmpty(imagesUrl)) {
            dynamic.setImages(imagesUrl); // 将图片路径设置到动态对象中
        }
        dynamicService.createDynamic(dynamic);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 保存动态图片
     * @param imagesFile 图片文件列表
     * @return 返回保存后的图片路径
     * @throws IOException 如果文件保存过程中出现异常
     */
    public String saveImages(List<MultipartFile> imagesFile) throws IOException {
        // 检查文件列表是否为空或为 null
        if (imagesFile == null || imagesFile.isEmpty()) {
            return null; // 若无图返回 null
        }
        // 初始化 StringBuilder 用于拼接图片文件名
        StringBuilder imagesUrl = new StringBuilder();
        for (MultipartFile file : imagesFile) {// 遍历上传的图片文件列表
            if (file != null && !file.isEmpty()) {// 是否不为空且不是空文件
                // 获取上传文件的原始文件名
                String oldFileName = file.getOriginalFilename();
                // 生成一个随机字符串，用于防止文件名冲突
                String randomStr = UUID.randomUUID().toString();
                // 生成新的文件名，保留原始文件的扩展名
                String newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
                // 构建文件的保存路径，将文件保存到 uploadPath/dynamic/ 目录下
                String filePath = uploadPath + File.separator + "dynamic" + File.separator + newFileName;
                // 创建目标文件对象
                File destFile = new File(filePath);
                // 若目标文件的父目录不存在，则创建父目录
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                // 将上传的文件保存到目标路径
                file.transferTo(destFile);
                // 将新的文件名拼接到 StringBuilder 中，并添加一个逗号分隔符
                imagesUrl.append(newFileName).append(",");
            }
        }
        // 如果 StringBuilder 不为空，去掉最后一个多余的逗号
        if (imagesUrl.length() > 0) {
            imagesUrl.deleteCharAt(imagesUrl.length() - 1);
        }
        // 返回拼接后的文件名字符串
        return imagesUrl.toString();
    }
    /**
     * 更新动态
     * @param id 根据id更新动态
     * @return 返回更新结果的API响应
     */
    @Access(level = AccessLevel.DEMO)
    @RequestMapping(value = "/update", method = RequestMethod.POST)//PUT一般用于更新,但是使用这个会报前后的地址错误的问题，所以我使用post
    @Transactional
    public APIResponse update(@RequestParam("id") String id,
                              @RequestParam(value = "title", required = false) String title,
                              @RequestParam(value = "content", required = false) String content,
                              @RequestParam(value = "imagesFile", required = false) List<MultipartFile> imagesFile,
                              @RequestParam(value = "imagesUrl", required = false) List<String> imagesUrl,
                              @RequestParam(value = "removedImages", required = false) List<String> removedImages
                              ) throws IOException {
        Dynamic dynamic = dynamicService.getDynamicDetail(id);
        if (dynamic == null) {
            return new APIResponse(ResponeCode.FAIL, "动态不存在");
        }

        // 更新标题和内容
        if (title != null) dynamic.setTitle(title);
        if (content != null) dynamic.setContent(content);

        // 初始化图片路径列表
        List<String> imagePaths = new ArrayList<>();
        if (dynamic.getImages() != null && !dynamic.getImages().isEmpty()) {
            imagePaths.addAll(Arrays.asList(dynamic.getImages().split(",")));
        }

        // 删除被删除的图片路径
        if (removedImages != null && !removedImages.isEmpty()) {
            imagePaths.removeAll(removedImages);
        }

        // 处理新上传的图片
        if (imagesFile != null && !imagesFile.isEmpty()) {
            String newImagesUrl = saveImages(imagesFile);
            if (!StringUtils.isEmpty(newImagesUrl)) {
                imagePaths.addAll(Arrays.asList(newImagesUrl.split(",")));
            }
        }

        // 如果有旧的图片路径需要保留
        if (imagesUrl != null && !imagesUrl.isEmpty()) {
            imagePaths.addAll(imagesUrl);
        }
        // 去重：确保图片路径不会重复
        imagePaths = imagePaths.stream().distinct().collect(Collectors.toList());

        // 更新动态的图片路径
        dynamic.setImages(String.join(",", imagePaths));

        dynamicService.updateDynamic(dynamic);
        // 返回更新后的动态信息
        return new APIResponse(ResponeCode.SUCCESS, "更新成功!!!",dynamic);
    }

    /**
     * 用户删除动态
     * @param id 动态的唯一标识
     * @return 返回删除结果的API响应
     */
    //@Access(level = AccessLevel.DEMO)
    @RequestMapping(value = "/user-delete", method = RequestMethod.POST)
    @Transactional
    public APIResponse userDelete(String id) {
        // 批量删除
       // String[] arr = ids.split(",");
       // for (String id : arr) {
            dynamicService.deleteDynamic(id);
        //}
        return new APIResponse(ResponeCode.SUCCESS, "删除动态成功");
    }
    // 管理员删除
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            dynamicService.deleteDynamic(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }



    //喜欢数
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @Transactional
    public APIResponse like(String id) throws IOException {
        System.out.println("接收到的动态ID: " + id); // 打印日志
        Dynamic dynamicBean =dynamicService.getDynamicDetail(id);
        System.out.println("找到用户动态信息==========like " + dynamicBean);
        if (dynamicBean == null) {
            return new APIResponse(ResponeCode.FAIL, "动态不存在");
        }
        int likeCount = Integer.parseInt(dynamicBean.getLikeCount()) + 1;
        dynamicBean.setLikeCount(String.valueOf(likeCount));
        dynamicService.updateDynamic(dynamicBean);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }
}