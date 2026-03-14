// 路由表
const constantRouterMap = [
  // ************* 前台路由 **************
  {
    path: '/',
    redirect: '/index',
  },
  {
    path: '/index',
    name: 'index',
    redirect: '/index/home',
    component: () => import('/@/views/index/index.vue'),
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('/@/views/index/login.vue'),
      },
      {
        path: 'register',
        name: 'register',
        component: () => import('/@/views/index/register.vue'),
      },
      {
        path: 'home',
        name: 'home',
        component: () => import('/@/views/index/home.vue'),
      },
      {
        path: 'recommend',
        name: 'recommend',
        component: () => import('/@/views/index/recommend.vue'),
      },
      {
        path: 'detail',
        name: 'detail',
        component: () => import('/@/views/index/detail.vue'),
      },
      {
        path: 'confirm',
        name: 'confirm',
        component: () => import('/@/views/index/confirm.vue'),
      },
      // ***添加动态广场******开始************
      {
        path: 'squareDynamics',
        name: 'squareDynamics',
        component: () => import('/@/views/index/square-dynamics.vue'),
      },

      // ***添加聊天室******开始************
      // {
      //   path: 'chatMessage',
      //   name: 'chatMessage',
      //   component: () => import('/@/views/index/chat-message.vue'),
      // },

      // {
      //   path: 'im',
      //   name: 'im',
      //   component: () => import('/@/views/index/im.vue'),
      // },
      // ******************结束************
      {
        path: 'success',
        name: 'success',
        component: () => import('/@/views/index/success.vue'),
      },
      {
        path: 'feedback',
        name: 'feedback',
        component: () => import('/@/views/index/feedback.vue'),
      },
      {
        path: 'search',
        name: 'search',
        component: () => import('/@/views/index/search.vue'),
      },

      {
        path: 'usercenter',
        name: 'usercenter',
        redirect: '/index/usercenter/userInfoEditView',
        component: () => import('/@/views/index/usercenter.vue'),
        children: [
          {
            path: 'wishThingView',
            name: 'wishThingView',
            component: () => import('/@/views/index/user/wish-thing-view.vue'),
          },
          {
            path: 'collectThingView',
            name: 'collectThingView',
            component: () => import('/@/views/index/user/collect-thing-view.vue'),
          },
          // ***添加聊天室******开始************
          {
            path: 'chatRoomView',
            name: 'chatRoomView',
            component: () => import('/@/views/index/user/chat-room-view.vue'),
          },
          // ******************结束************
          /*原
          {
            path: 'applyView',
            name: 'applyView',
            component: () => import('/@/views/index/user/apply-view.vue'),
          },  */

          // ******** mhy优化：用户的会员管理，添加两个子菜单
          {
            path: 'applyView',
            name: 'applyView',
            component: () => import('/@/views/index/user/apply-view.vue'),
          },
          {
            path: 'rechargeView',
            name: 'rechargeView',
            component: () => import('/@/views/index/user/recharge-view.vue'),
          },
          // ********
          // ******** mhy优化：个人的动态管理与发布
          {
            path: 'dynamicsView',
            name: 'dynamicsView',
            component: () => import('/@/views/index/user/dynamics-view.vue'),
          },
          {
            path: 'dynamicSend',
            name: 'dynamicSend',
            component: () => import('/@/views/index/user/dynamic-send.vue'),
          },
          /* {
            path: 'dynamicEdit',
            name: 'dynamicEdit',
            component: () => import('/@/views/index/user/dynamic-edit.vue'),
          }, */

          // ********
          {
            path: 'orderView',
            name: 'orderView',
            component: () => import('/@/views/index/user/order-view.vue'),
          },
          {
            path: 'userInfoEditView',
            name: 'userInfoEditView',
            component: () => import('/@/views/index/user/userinfo-edit-view.vue'),
          },
          {
            path: 'commentView',
            name: 'commentView',
            component: () => import('/@/views/index/user/comment-view.vue'),
          },
          {
            path: 'securityView',
            name: 'securityView',
            component: () => import('/@/views/index/user/security-view.vue'),
          },
          {
            path: 'messageView',
            name: 'messageView',
            component: () => import('/@/views/index/user/message-view.vue'),
          },
        ],
      },
    ],
  },
  {
    path: '/adminLogin',
    name: 'adminLogin',
    component: () => import('/@/views/admin/admin-login.vue'),
  },
  {
    path: '/admin',
    name: 'admin',
    redirect: '/admin/thing',
    component: () => import('/@/views/admin/main.vue'),
    children: [
      { path: 'overview', name: 'overview', component: () => import('/@/views/admin/overview.vue') },
      { path: 'order', name: 'order', component: () => import('/@/views/admin/order.vue') },
      { path: 'thing', name: 'thing', component: () => import('/@/views/admin/thing.vue') },
      { path: 'comment', name: 'comment', component: () => import('/@/views/admin/comment.vue') },
      { path: 'user', name: 'user', component: () => import('/@/views/admin/user.vue') },
      { path: 'classification', name: 'classification', component: () => import('/@/views/admin/classification.vue') },
      { path: 'ad', name: 'ad', component: () => import('/@/views/admin/ad.vue') },
      // mhy优化：添加动态管理
      { path: 'dynamic', name: 'dynamic', component: () => import('/@/views/admin/dynamic.vue') },

      //mhy优化：添加充值记录管理
      { path: 'recharge', name: 'recharge', component: () => import('/@/views/admin/recharge.vue') },

      { path: 'notice', name: 'notice', component: () => import('/@/views/admin/notice.vue') },
      { path: 'loginLog', name: 'loginLog', component: () => import('/@/views/admin/login-log.vue') },
      { path: 'opLog', name: 'opLog', component: () => import('/@/views/admin/op-log.vue') },
      { path: 'errorLog', name: 'errorLog', component: () => import('/@/views/admin/error-log.vue') },
      { path: 'feedbackList', name: 'feedbackList', component: () => import('/@/views/admin/feedbackList.vue') },
      { path: 'sysInfo', name: 'sysInfo', component: () => import('/@/views/admin/sys-info.vue') },

    ],
  },
];

export default constantRouterMap;
