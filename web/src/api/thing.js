// 权限问题后期增加
import { get, post } from '/@/utils/http/axios';
// import axios from 'axios';
const URL = {
  list: '/api/thing/list',
  create: '/api/thing/create',
  update: '/api/thing/update',
  delete: '/api/thing/delete',
  detail: '/api/thing/detail',
  listUserThing: '/api/thing/listUserThing',
  recommend: '/api/thing/recommend',
  rate: '/api/thing/rate',
    // ******** mhy优化：设置查询用户是否存在的api
    checkUserIdExists: '/api/user/exists',
    // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
    getTop3HotGenderThings: '/api/thing/top3-hot-gender',
    //根据用户id 查找会员信息
    getMemberInfoByUserId: 'api/thing/getMemberInfoByUserId'
    
};

const listApi = async (params) => get({ url: URL.list, params: params, data: {}, headers: {} });
const listUserThingApi = async (params) => get({ url: URL.listUserThing, params: params, data: {}, headers: {} });
const createApi = async (data) =>
  post({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const updateApi = async (data) => post({ url: URL.update, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const deleteApi = async (params) => post({ url: URL.delete, params: params, headers: {} });
const detailApi = async (params) => get({ url: URL.detail, params: params, headers: {} });

const getRecommendApi = async (params) => get({ url: URL.recommend, params: params, headers: {} });
const rateApi = async (params) => post({ url: URL.rate, params: params, headers: {} });
// ******** mhy优化：设置查询用户是否存在的api
const checkUserIdExistsApi = async (params) => get({ url: URL.checkUserIdExists, params: params, data: {}, headers: {} });
// ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
const getTop3HotGenderThingsApi  = async (params) => get({ url: URL.getTop3HotGenderThings, params: params, data: {}, headers: {} });

//根据用户id 查找会员信息
const getMemberInfoByUserIdApi = async (params) => get({ url: URL.getMemberInfoByUserId, params: params, data: {}, headers: {} });

export {  
  // ******** mhy优化：设置查询用户是否存在的api
  checkUserIdExistsApi,
  // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
  getTop3HotGenderThingsApi,
  //根据用户id 查找会员信息
  getMemberInfoByUserIdApi,
  listApi, createApi, updateApi, deleteApi, detailApi, listUserThingApi, getRecommendApi, rateApi };
