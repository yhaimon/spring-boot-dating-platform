package com.gk.study.service;


import com.gk.study.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentList();
    void createComment(Comment comment);

    // ******** mhy优化：根据ReplyToId查询回复列表
    List<Comment> getReplyList(Long replyToId);

    void deleteComment(String id);
    void updateComment(Comment comment);
    Comment getCommentDetail(String id);

    List<Comment> getThingCommentList(String thingId, String order);

    List<Comment> getUserCommentList(String userId);
}
