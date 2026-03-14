import { get, post } from '/@/utils/http/axios';

const URL = {
    list: '/api/comment/list',
    createDetailComment: '/api/comment/create-detailComment',
    delete: '/api/comment/delete',
    listThingComments: '/api/comment/listThing-detailComments',
    listUserComments: '/api/comment/listUserComments',
    // ******** mhy优化： 在回复列表中再添加回复列表
    listReplies: '/api/comment/listReplies',
    // ******** mhy优化： 给自身评论的用户显示删除按钮
    thingDeleteComment: '/api/comment/thingDeleteComment',
    // ******** mhy优化： 给自身评论的用户显示编辑功能
    updateComment: '/api/comment/update',
    // ******** mhy优化：动态广场评论
    createDynamicComment: '/api/comment/create-dynamicComment',
    listThingDynamicComments: '/api/comment/listThing-dynamicComments',
    // ********
    like: '/api/comment/like'
}

const listApi = async (params) => get({ url: URL.list, params: params, data: {}, headers: {} });
const createApi = async (data) => post({
    url: URL.createDetailComment,
    params: {},
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
});
//删除（支持批量删除）
const deleteApi = async (params) => post({ url: URL.delete, params: params, headers: {} });
const listThingCommentsApi = async (params) => get({ url: URL.listThingComments, params: params, data: {}, headers: {} });
// ******** mhy优化： 在回复列表中再添加回复列表
const listRepliesApi = async (params) => get({ url: URL.listReplies, params: params, data: {}, headers: {} });
// ******** mhy优化： 给自身评论的用户显示删除功能
const thingDeleteCommentApi = async (params) => post({ url: URL.thingDeleteComment, params: params, headers: {} });
// ******** mhy优化： 给自身评论的用户显示编辑功能
const updateCommentApi = async (params) => post({ url: URL.updateComment, params: params, headers: {} });

// 动态广场评论：
const createDetailCommentApi = async (data) => post({
    url: URL.createDetailComment,
    params: {},
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
});
const listDynamicCommentsApi = async (params) => get({ url: URL.listThingDynamicComments, params: params, data: {}, headers: {} });

// ********

const listUserCommentsApi = async (params) => get({ url: URL.listUserComments, params: params, data: {}, headers: {} });
const likeApi = async (params) => post({ url: URL.like, params: params, headers: {} });

export {
    createDetailCommentApi,
    listDynamicCommentsApi,
    listRepliesApi,
    thingDeleteCommentApi,
    updateCommentApi,
    listApi, createApi, deleteApi, listThingCommentsApi, listUserCommentsApi, likeApi
};
