// 权限问题后期增加
import { get, post } from '/@/utils/http/axios';
// import axios from 'axios';
const URL = {
  login: '/api/user/login',
  userList: '/api/user/list',
  detail: '/api/user/detail',
  // ********mhy优化：获取用户角色
  getUserRole: '/api/user/userRole',
  // ********mhy优化：添加用户充值
  userRecharge: '/api/user/userRecharge',
  // ******** mhy 优化：获取用户充值记录
  listRechargeRecordsById: '/api/user/listRechargeRecordsById',
  // ******** mhy 优化：获取所有用户充值记录
  RechargeRecordList: '/api/user/RechargeRecordList',
  // ******** mhy 优化：获取用户余额
  getUserBalance: '/api/user/getUserBalance',
  // ******** mhy 优化：根据用户id查找用户基本信息(用户名，用户头像，用户角色、用户余额)
  getUserBasicInfo: '/api/user/getUserBasicInfo',
  create: '/api/user/create',
  update: '/api/user/update',
  delete: '/api/user/delete',
  userLogin: '/api/user/userLogin',
  userRegister: '/api/user/userRegister',
  updateUserPwd: '/api/user/updatePwd',
  updateUserInfo: '/api/user/updateUserInfo',

};

const loginApi = async (data) => post({ url: URL.login, data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const listApi = async (params) => get({ url: URL.userList, params: params, data: {}, headers: {} });
const detailApi = async (params) => get({ url: URL.detail, params: params, data: {}, headers: {} });
// ********mhy优化：获取用户角色
const getUserRoleApi = async (params) => get({ url: URL.getUserRole, params: params, data: {}, headers: {} });
// ******** mhy 优化：获取用户余额
const getUserBalanceApi = async (params) => get({ url: URL.getUserBalance, params: params, data: {}, headers: {} });
// ******** mhy 优化：根据用户id查找用户基本信息(用户名，用户头像，用户角色、用户余额)
const getUserBasicInfoApi = async (params) => get({ url: URL.getUserBasicInfo, params: params, data: {}, headers: {} });
// ********mhy优化：添加用户充值
const userRechargeApi = async (data) => post({ url: URL.userRecharge, data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
// ******** mhy 优化：获取某用户充值记录
const listRechargeRecordsApi = async (params) => get({ url: URL.listRechargeRecordsById, params: params, data: {}, headers: {} });
// ******** mhy 优化：获取所有用户充值记录
const RechargeRecordListApi = async (params) => get({ url: URL.RechargeRecordList, params: params, data: {}, headers: {} });

const createApi = async (data) =>
  post({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const updateApi = async (data) => post({ url: URL.update, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const deleteApi = async (params) => post({ url: URL.delete, params: params, headers: {} });
const userLoginApi = async (data) => post({ url: URL.userLogin, data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const userRegisterApi = async (data) =>
  post({ url: URL.userRegister, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const updateUserPwdApi = async (params) => post({ url: URL.updateUserPwd, params: params });
const updateUserInfoApi = async (data) =>
  post({ url: URL.updateUserInfo, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

export {
  loginApi,
  listApi,
  // ********mhy优化：获取用户角色
  getUserRoleApi,
  // ******** mhy 优化：获取用户余额
  getUserBalanceApi,
  // ******** mhy 优化：根据用户id查找用户基本信息(用户名，用户头像，用户角色、用户余额)
  getUserBasicInfoApi,
  // ******** mhy优化：添加用户充值
  userRechargeApi,
  // ******** mhy优化：获取某用户充值记录
  listRechargeRecordsApi,
// ******** mhy 优化：获取所有用户充值记录
  RechargeRecordListApi,
  detailApi,
  createApi,
  updateApi,
  deleteApi,
  userLoginApi,
  userRegisterApi,
  updateUserPwdApi,
  updateUserInfoApi,
};
