export type RoleType = '' | '*' | 'admin' | 'user';
export interface UserState {
  user_id: any;
  user_name: any;
  user_token: any;
  // ********mhy优化：获取用户角色
  user_role: any;
  avatar: any,

  admin_user_id: any;
  admin_user_name: any;
  admin_user_token: any;
}
