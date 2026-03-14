import { defineStore } from 'pinia';
import { loginApi as adminLogin } from '/@/api/user';
import { userLoginApi } from '/@/api/user';
import { setToken, clearToken } from '/@/utils/auth';
import { UserState } from './types';
import { AVATAR, USER_ROLE, USER_ID, USER_NAME, USER_TOKEN, ADMIN_USER_ID, ADMIN_USER_NAME, ADMIN_USER_TOKEN } from '/@/store/constants';

//这段代码是使用 Pinia 创建的一个 Vuex 风格的状态管理 store，用于管理用户和管理员的登录状态。
export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    user_id: undefined,
    user_name: undefined,
    user_token: undefined,
    // ********mhy优化：获取用户角色
    user_role: undefined,
    // ********mhy优化：获取用户头像
    avatar: undefined,
    admin_user_id: undefined,
    admin_user_name: undefined,
    admin_user_token: undefined,
  }),
  getters: {
    // ******** mhy优化：获取用户信息
    // Getter 示例：获取用户信息
    getUserInfo: (state) => {
      return {
        user_id: state.user_id,
        user_name: state.user_name,
        avatar: state.avatar,
        admin_user_id: state.admin_user_id,
        admin_user_name: state.admin_user_name,
      };
    },

  },
  actions: {
    // 用户登录
    async login(loginForm) {
      const result = await userLoginApi(loginForm);
      console.log('result==>', result);

      if (result.code === 0 || result.code === 200) {
        this.$patch((state) => {
          state.user_id = result.data.id;
          state.user_name = result.data.username;
          state.user_token = result.data.token;
          // ********mhy优化：获取用户角色
          state.user_role = result.data.role;
          // ********mhy优化：获取用户头像
          state.avatar = result.data.avatar,
          console.log('state==>', state);
          console.log('state.user_id==>', state.user_id);
          console.log('state.user_name==>', state.user_name);
          console.log('state.user_role==>', state.user_role);
          console.log('state.avatar==>', state.avatar);
        });

        localStorage.setItem(USER_TOKEN, result.data.token);
        localStorage.setItem(USER_NAME, result.data.username);
        localStorage.setItem(USER_ID, result.data.id);
        // ********mhy优化：获取用户角色
        localStorage.setItem(USER_ROLE, result.data.role);
        // ********mhy优化：获取用户头像
        localStorage.setItem(AVATAR, result.data.avatar);

      }

      return result;
    },
    
    // ******** mhy 优化：更新用户信息
    updateUserInfo(data) {
      if (!data) {
        console.error('更新用户信息失败，未接收到有效数据');
        return;
      }

      this.$patch((state) => {
        state.user_id = data.user_id || state.user_id;
        state.user_name = data.user_name || state.user_name;
        state.avatar = data.avatar || state.avatar;
        state.user_role = data.role || state.user_role;
      });

      // 更新 localStorage 中的数据
      localStorage.setItem(USER_ID, data.user_id);
      localStorage.setItem(USER_NAME, data.user_name);
      localStorage.setItem(USER_TOKEN, data.token);
      localStorage.setItem(USER_ROLE, data.role);
      localStorage.setItem(AVATAR, data.avatar);
    },



    // 用户登出
    async logout() {
      // await userLogout();
      this.$patch((state) => {
        localStorage.removeItem(USER_ID);
        localStorage.removeItem(USER_NAME);
        localStorage.removeItem(USER_TOKEN);
        // ********mhy优化：获取用户角色
        localStorage.removeItem(USER_ROLE);
        // ********mhy优化：获取用户头像
        localStorage.removeItem(AVATAR);

        state.user_id = undefined;
        state.user_name = undefined;
        state.user_token = undefined;
        // ********mhy优化：获取用户角色
        state.user_role = undefined;
        // ********mhy优化：获取用户头像
        state.avatar = undefined;
      });
    },

    // 管理员登录
    async adminLogin(loginForm) {
      const result = await adminLogin(loginForm);
      console.log('result==>', result);

      if (result.code === 0 || result.code === 200) {
        this.$patch((state) => {
          state.admin_user_id = result.data.id;
          state.admin_user_name = result.data.username;
          state.admin_user_token = result.data.token;
          console.log('state==>', state);
        });

        localStorage.setItem(ADMIN_USER_TOKEN, result.data.token);
        localStorage.setItem(ADMIN_USER_NAME, result.data.username);
        localStorage.setItem(ADMIN_USER_ID, result.data.id);
      }

      return result;
    },
    // 管理员登出
    async adminLogout() {
      // await userLogout();
      this.$patch((state) => {
        localStorage.removeItem(ADMIN_USER_ID);
        localStorage.removeItem(ADMIN_USER_NAME);
        localStorage.removeItem(ADMIN_USER_TOKEN);

        state.admin_user_id = undefined;
        state.admin_user_name = undefined;
        state.admin_user_token = undefined;
      });
    },


  },
});
