import { get, post } from '/@/utils/http/axios';

const URL = {
    //查所有动态列表
    dynamicList: '/api/dynamic/list',
    //根据id查动态列表
    dynamicListById: '/api/dynamic/getListById',
    create: '/api/dynamic/create',
    update: '/api/dynamic/update',
    userDelete: '/api/dynamic/user-delete',
    delete: '/api/dynamic/delete',
    likeDynamic: '/api/dynamic/like',
}

const listDynamicApi = async (params) =>
    get({ url: URL.dynamicList, params: params, data: {}, headers: {} });
const dynamicListByIdApi = async (params) =>
    get({ url: URL.dynamicListById, params: params, data: {}, headers: {} });

const createApi = async (data) =>
    post({
        url: URL.create,
        params: {},
        data: data,
        headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
    });
const updateDynamicApi = async (data) =>
    post({
        url: URL.update,
        data: data,
        headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
    });
//用户删除
const deleteDynamicApi = async (params) =>
    post({ url: URL.userDelete, params: params, headers: {} });
//管理员删除
const deleteApi = async (params) =>
    post({ url: URL.delete, params: params, headers: {} });

//喜欢数
const likeDynamicApi = async (params) => post({ url: URL.likeDynamic, params: params, headers: {} });


export { listDynamicApi, dynamicListByIdApi, deleteApi, likeDynamicApi, createApi, updateDynamicApi, deleteDynamicApi };