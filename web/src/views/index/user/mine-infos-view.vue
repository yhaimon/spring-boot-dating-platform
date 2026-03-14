<template>
  <div class="mine-infos-view">
    <div class="info-box flex-view">
      <img :src="avatarUrl.length > 0 ? avatarUrl : defaultAvatar" class="avatar-img" />
      <div class="name-box">
        <h2 class="nick">{{ userStore.user_name }}</h2>
        <div class="age">
          <span>欢迎回来</span>
          <span class="give-point"></span>
        </div>
      </div>
    </div>
    <div class="counts-view">
      <div class="counts flex-view">
        <div class="fans-box flex-item" @click="clickMenu('collectThingView')">
          <div class="text">收藏</div>
          <div class="num">{{ collectCount }}</div>
        </div>
        <div class="split-line"> </div>
        <div class="follow-box flex-item" @click="clickMenu('wishThingView')">
          <div class="text">点赞</div>
          <div class="num">{{ wishCount }}</div>
        </div>
      </div>
    </div>
    <div class="setting-box">
      <div class="title">个人设置</div>
      <div class="list">
        <div class="mine-item flex-view" @click="clickMenu('userInfoEditView')">
          <img :src="basicInformationIMG" alt="个人资料" />
          <span>个人信息</span>
        </div>
        <!-- 原
         <div class="mine-item flex-view" @click="clickMenu('applyView')">
          <img :src="SettingIconImage" alt="信息登记" />
          <span>会员管理</span>
        </div> -->
        <!-- ******** mhy优化：会员管理的下拉菜单 -->
        <!-- 会员管理的下拉菜单 -->
        <div class="mine-item flex-view" @click="toggleDropDownMenu">
          <img :src="MemberManagementImg" alt="会员管理" />
          <span>会员管理</span>
          <img :src="DropdownMenuImg" alt="下拉" class="down-arrow" />
        </div>
        <transition name="fade">
          <div v-show="isDropDownMenuOpen" class="drop-down-menu">
            <div class="menu-item mine-item-min " @click="clickMenu('rechargeView')">
              <img :src="MemberRechargeImg" alt="会员充值" />
              <span>会员充值</span>
            </div>
            <div class="menu-item mine-item-min" @click="clickMenu('applyView')">
              <img :src="MemberInfoImg" alt="会员信息发布" />
              <span>会员信息发布</span>
            </div>
          </div>
        </transition>


        <!-- ******** -->
        
        <div class="mine-item flex-view" @click="clickMenu('orderView')">
          <img :src="AppointmentManagementImg" alt="我的约会" />
          <span>约会管理</span>
        </div>
        <div class="mine-item flex-view" @click="clickMenu('commentView')">
          <img :src="CommentIconImg" alt="评论管理" />
          <span>评论管理</span>
        </div>
        <!-- ******** mhy优化：添加动态发布功能 -->
        <div class="mine-item flex-view" @click="clickMenu('dynamicsView')">
          <img :src="dynamicsIconIMG" alt="动态管理与发布" />
          <span>我的动态</span>
        </div>
        <!-- ******** mhy优化：添加聊天功能 -->
        <div class="mine-item flex-view" @click="clickMenu('chatRoomView')">
          <img :src="chatInfoIconIMG" alt="聊天" />
          <span>在线聊天</span>
        </div>
     

        <div class="mine-item flex-view" @click="clickMenu('securityView')">
          <img :src="SafeIconImage" alt="账号安全" />
          <span>账号安全</span>
        </div>
        <div class="mine-item flex-view" @click="clickMenu('messageView')">
          <img :src="MessageIconImage" alt="系统通知" />
          <span>系统通知</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import defaultAvatar from '/@/assets/images/avatar.jpg';
import MyOrderImg from '/@/assets/images/order-icon.svg';
import CommentIconImg from '/@/assets/images/comment-management-icon.svg';
import AddressIconImage from '/@/assets/images/order-address-icon.svg';
import PointIconImage from '/@/assets/images/order-point-icon.svg';
import SettingIconImage from '/@/assets/images/setting-icon.svg';

import SafeIconImage from '/@/assets/images/setting-safe-icon.svg';
import PushIconImage from '/@/assets/images/setting-push-icon.svg';
import MessageIconImage from '/@/assets/images/setting-msg-icon.svg';
// ********矢量图添加在这
import MemberManagementImg from '/@/assets/images/member-management-icon.svg';
import DropdownMenuImg from '/@/assets/images/dropdown-menu-icon.svg';
import MemberInfoImg from '/@/assets/images/member-info-icon.svg';
import MemberRechargeImg from '/@/assets/images/member-recharge-icon.svg';
import AppointmentManagementImg from '/@/assets/images/appointment-management-icon.svg';
import basicInformationIMG from '/@/assets/images/basic-information-icon.svg';
import chatInfoIconIMG from '/@/assets/images/chat-info-icon.svg';
import dynamicsIconIMG from '/@/assets/images/dynamics-icon.svg';
import { BASE_URL } from '/@/store/constants';
import { detailApi } from '/@/api/user';

import { userCollectListApi } from '/@/api/thingCollect';
import { userWishListApi } from '/@/api/thingWish';
import { useUserStore } from '/@/store';

const userStore = useUserStore();
const router = useRouter();
// ******** mhy优化： 会员管理下拉菜单
const isDropDownMenuOpen = ref(false);
// ******** 
let collectCount = ref(0);
let wishCount = ref(0);
let avatarUrl = ref('');

onMounted(() => {
  getCollectThingList();
  getWishThingList();
  getUserInfo();
});

const getUserInfo = () => {
  let userId = userStore.user_id;
  detailApi({ userId: userId })
    .then((res) => {
      if (res.data.avatar) {
        avatarUrl.value = BASE_URL + '/api/staticfiles/avatar/' + res.data.avatar;
      }
    })
    .catch((err) => {
      console.log(err);
      loading.value = false;
    });
};

/*原
 const clickMenu = (name) => {
  router.push({ name: name });
}; */
// ******** mhy优化：会员管理菜单
const toggleDropDownMenu = () => {
  isDropDownMenuOpen.value = !isDropDownMenuOpen.value;
};

//改
const clickMenu = (name) => {
  router.push({ name: name });
  isDropDownMenuOpen.value = false; // 点击菜单项后关闭下拉菜单
};
// ********

const getCollectThingList = () => {
  let userId = userStore.user_id;
  userCollectListApi({ userId: userId })
    .then((res) => {
      collectCount.value = res.data.length;
    })
    .catch((err) => {
      console.log(err.msg);
    });
};

const getWishThingList = () => {
  let userId = userStore.user_id;
  userWishListApi({ userId: userId })
    .then((res) => {
      wishCount.value = res.data.length;
    })
    .catch((err) => {
      console.log(err.msg);
    });
};
</script>

<style scoped lang="less">
.flex-view {
  display: flex;
  ///
  align-items: center;
  cursor: pointer;
  padding: 10px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.flex-view:hover {
  background-color: #f5f5f5;
}

.mine-infos-view {
  border: 1px solid #cedce4;
  height: fit-content;

  .info-box {
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    padding: 16px 16px 0;
    overflow: hidden;
  }

  .avatar-img {
    width: 48px;
    height: 48px;
    margin-right: 16px;
    border-radius: 50%;
    object-fit: cover;
  }

  .name-box {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    overflow: hidden;

    .nick {
      color: #152844;
      font-weight: 600;
      font-size: 18px;
      line-height: 24px;
      height: 24px;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin: 0;
      overflow: hidden;
    }

    .age {
      font-size: 12px;
      color: #6f6f6f;
      height: 16px;
      line-height: 16px;
      margin-top: 8px;
    }

    .give-point {
      color: #4684e2;
      cursor: pointer;
      float: right;
    }
  }

  .counts-view {
    border: none;
    padding: 16px;
  }

  .counts {
    margin-top: 12px;
    text-align: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;

    .flex-item {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      cursor: pointer;
    }

    .text {
      height: 16px;
      line-height: 16px;
      color: #6f6f6f;
    }

    .num {
      height: 18px;
      line-height: 18px;
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      margin-top: 4px;
    }

    .split-line {
      width: 1px;
      height: 24px;
      background: #dae6f9;
    }
  }

  .intro-box {
    border-top: 1px solid #cedce4;
    padding: 16px;

    .title {
      color: #6f6f6f;
      font-size: 12px;
      line-height: 16px;
    }

    .intro-content {
      color: #152844;
      font-size: 14px;
      line-height: 20px;
      overflow: hidden;
      text-overflow: ellipsis;
      margin: 8px 0;
    }
  }

  .create-box {
    cursor: pointer;
    border-top: 1px solid #cedce4;
    padding: 16px;

    .title {
      position: relative;
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      line-height: 18px;
      height: 18px;
    }

    .counts {
      margin-top: 12px;
      text-align: center;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;

      .flex-item {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
        cursor: pointer;
      }

      .split-line {
        width: 1px;
        height: 24px;
        background: #dae6f9;
      }
    }
  }

  .order-box {
    border-top: 1px solid #cedce4;
    padding: 16px;

    .title {
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      line-height: 18px;
      height: 18px;
      margin-bottom: 8px;
    }

    .list {
      padding-left: 16px;

      .mine-item {
        border-top: 1px solid #649ec5;
        cursor: pointer;
        height: 48px;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;

        img {
          width: 24px;
          margin-right: 8px;
          height: 24px;
        }

        span {
          color: #152844;
          font-size: 14px;
        }
      }


      .mine-item:first-child {
        border: none;
      }
    }
  }

  .setting-box {
    border-top: 1px solid #cedce4;
    padding: 16px;

    .title {
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      line-height: 18px;
      height: 18px;
      margin-bottom: 8px;
    }

    .list {
      padding-left: 16px;
    }

    .mine-item {
      border-top: 1px solid #80bee2;
      cursor: pointer;
      height: 48px;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;

      img {
        width: 24px;
        margin-right: 8px;
        height: 24px;
      }

      span {
        color: #152844;
        font-size: 14px;
      }

      // ******** mhy优化：添加会员管理下拉菜单的样式 
      .drop-down-menu {
        position: absolute;
        background-color: #fff;
        border: 1px solid #ddd;
        border-radius: 4px;
        padding: 8px 0;
        margin-top: 8px;
        z-index: 10;

        /////
        width: 100%;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        opacity: 0;
        visibility: hidden;
        transition: opacity 0.3s ease, visibility 0.3s ease;
      }

      .drop-down-menu.visible {
        opacity: 1;
        visibility: visible;
      }

      .menu-item {
        padding: 8px 16px;
        cursor: pointer;
        transition: background-color 0.3s ease;

        &:hover {
          background-color: #e6abab;
        }
      }

      .menu-item.active {
        background-color: #e0e0e0;
      }

      .menu-item img {
        width: 16px;
        height: 16px;
        margin-right: 8px;
      }

      .down-arrow {
        margin-left: 8px;
      }

      /* 过渡动画 */
      .fade-enter-active,
      .fade-leave-active {
        transition: opacity 0.3s ease;
      }

      .fade-enter-from,
      .fade-leave-to {
        opacity: 0;
      }

      // ********

    }

    // ********mhy二级菜单样式
    .mine-item-min {
      border-top: 1px dashed #9fceea;
      cursor: pointer;
      height: 30px;
      margin: 0 15px 0px 30px;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;

      img {
        width: 16px;
        margin-right: 8px;
        height: 16px;
      }

      span {
        color: #152844;
        font-size: 12px;
      }
      &:hover {
          background-color: #f5f5f5;
        }
    }

    .mine-item:first-child {
      border: none;
    }
  }
}</style>
