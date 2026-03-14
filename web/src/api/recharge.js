// 权限问题后期增加
import { get, post } from '/@/utils/http/axios';
// import axios from 'axios';
const URL = {
 
  // ********mhy优化：添加用户充值
  userRecharge: '/api/recharge/userRecharge',
  // ******** mhy 优化：获取用户充值记录
  listRechargeRecordsById: '/api/recharge/listRechargeRecordsById',
  // ******** mhy 优化：获取所有用户充值记录
  RechargeRecordList: '/api/recharge/RechargeRecordList',
  // ******** mhy 优化：获取用户余额
  getUserBalance: '/api/recharge/getUserBalance',
};


// ******** mhy 优化：获取用户余额
const getUserBalanceApi = async (params) => get({ url: URL.getUserBalance, params: params, data: {}, headers: {} });

// ********mhy优化：添加用户充值
const userRechargeApi = async (data) => post({ url: URL.userRecharge, data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
// ******** mhy 优化：获取某用户充值记录
const listRechargeRecordsApi = async (params) => get({ url: URL.listRechargeRecordsById, params: params, data: {}, headers: {} });
// ******** mhy 优化：获取所有用户充值记录
const RechargeRecordListApi = async (params) => get({ url: URL.RechargeRecordList, params: params, data: {}, headers: {} });



export {
  // ******** mhy 优化：获取用户余额
  getUserBalanceApi,

  // ******** mhy优化：添加用户充值
  userRechargeApi,
  // ******** mhy优化：获取某用户充值记录
  listRechargeRecordsApi,
// ******** mhy 优化：获取所有用户充值记录
  RechargeRecordListApi,

};
