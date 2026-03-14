package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Comment;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Comment> list =  service.getCommentList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }


    // 所有评论
   /* // 原
    @RequestMapping(value = "/listThingComments", method = RequestMethod.GET)
    public APIResponse listThingComments(String thingId, String order){
        List<Comment> list =  service.getThingCommentList(thingId, order);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }*/
    // 优化：只显示一级评论，回复在一级评论框里面（不支持分页）
    /*@RequestMapping(value = "/listThingComments", method = RequestMethod.GET)
    public APIResponse listThingComments(String thingId, String order){
        // 获取所有评论（包括回复）
        List<Comment> list =  service.getThingCommentList(thingId, order);
        // 优化：有一个 setReplies 方法来设置回复列表
        for (Comment comment : list) {
            List<Comment> replies = service.getReplyList(comment.getId());
            comment.setReplies(replies);
        }
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }*/
    //这是过滤detailComments后的评论和回复内容
    @RequestMapping(value = "/listThing-detailComments", method = RequestMethod.GET)
    public APIResponse listThingComments(String thingId, String order) {
        // 获取所有评论（包括回复）
        List<Comment> list = service.getThingCommentList(thingId, order);
        // 过滤出 commentType 为 DetailComment 的评论
        list = list.stream()
                .filter(comment -> comment.getCommentType() != null &&
                        Integer.parseInt(comment.getCommentType()) == Comment.DetailComment)
                .collect(Collectors.toList());
        // 优化：为每个一级评论设置回复列表
        for (Comment comment : list) {
            List<Comment> replies = service.getReplyList(comment.getId());
            // 同样过滤回复中的评论类型为 DetailComment
            replies = replies.stream()
                    .filter(reply -> reply.getCommentType() != null &&
                            Integer.parseInt(reply.getCommentType()) == Comment.DetailComment)
                    .collect(Collectors.toList());
            comment.setReplies(replies);
        }
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    //这是过滤dynamicComments后的评论和回复内容
    @RequestMapping(value = "/listThing-dynamicComments", method = RequestMethod.GET)
    public APIResponse listThing_dynamicComments(String thingId, String order) {
        // 获取所有评论（包括回复）
        List<Comment> list = service.getThingCommentList(thingId, order);

        // 过滤出 commentType 为 DynamicComment 的评论
        list = list.stream()
                .filter(comment -> comment.getCommentType() != null &&
                        Integer.parseInt(comment.getCommentType()) == Comment.DynamicComment)
                .collect(Collectors.toList());

        // 优化：为每个一级评论设置回复列表
        for (Comment comment : list) {
            List<Comment> replies = service.getReplyList(comment.getId());
            // 同样过滤回复中的评论类型为 DynamicComment
            replies = replies.stream()
                    .filter(reply -> reply.getCommentType() != null &&
                            Integer.parseInt(reply.getCommentType()) == Comment.DynamicComment)
                    .collect(Collectors.toList());
            comment.setReplies(replies);
        }

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }





    // 某用户的所有评论
    @RequestMapping(value = "/listUserComments", method = RequestMethod.GET)
    public APIResponse listUserComments(String userId){
        List<Comment> list =  service.getUserCommentList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }
    // ******** mhy优化： 创建回复
    @RequestMapping(value = "/listReplies", method = RequestMethod.GET)
    public APIResponse listReplies(Long replyToId) {
        List<Comment> list = service.getReplyList(replyToId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // ********


    /*
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Comment comment) throws IOException {
        service.createComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }*/
    //优化：创建时，comment类型为detail的
    @RequestMapping(value = "/create-detailComment", method = RequestMethod.POST)
    @Transactional
    public APIResponse create_detailComment(Comment comment) throws IOException {
        // 如果是回复，则设置 commentType 为 DetailComment
        comment.setCommentType(String.valueOf(Comment.DetailComment)); // 设置为 "1"
        service.createComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }
    //优化：创建时，comment类型为detail的
    @RequestMapping(value = "/create-dynamicComment", method = RequestMethod.POST)
    @Transactional
    public APIResponse create_dynamicComment(Comment comment) throws IOException {
        // 如果是回复，则设置 commentType 为 DetailComment
        comment.setCommentType(String.valueOf(Comment.DynamicComment)); // 设置为 "2"
        service.createComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }


    // ******** mhy优化：用户删除自己的评论
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/thingDeleteComment", method = RequestMethod.POST)
    public APIResponse thingDeleteComment(String id){
        System.out.println("id===" + id);
            service.deleteComment(id);
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }
    // 管理员删除
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteComment(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }



    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Comment comment) throws IOException {
        service.updateComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @Transactional
    public APIResponse like(String id) throws IOException {
        Comment commentBean = service.getCommentDetail(id);
        int likeCount = Integer.parseInt(commentBean.getLikeCount()) + 1;
        commentBean.setLikeCount(String.valueOf(likeCount));
        service.updateComment(commentBean);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}
