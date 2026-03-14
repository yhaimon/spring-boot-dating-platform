<template>
  <div class="content-list">
    <div class="list-title">会员充值</div>
    <a-spin :spinning="loading" style="min-height: 200px">
      <div class="list-content">
        <!--******** mhy优化： 用户余额显示 -->
        <div class="list-title-min">用户余额</div>
        <a-card class="balance-card">
          <div class="balance-info">
            <!-- <img src="/@/assets/images/money-icon.svg" alt="余额" class="balance-icon" /> -->
            <div class="balance-amount">
              当前余额：<span class="amount">{{ userBalance }}</span> 元
            </div>
          </div>
        </a-card>

        <!--******** mhy优化：添加充值 -->
        <div class="list-title-min">会员充值</div>
        <div class="Recharge-view">
          <!--********mhy优化 添加提示信息:       来一个提示信息，需要到thingController后端拿到user表中的role角色，若是会员欢迎会员，若不是会员，提示请充值的前端显示-->
          <!-- 根据用户角色显示提示信息 -->
          <div class="normal-message" v-if="userRole === '1'">
            尊敬的用户，您还不是会员，请充值！<br/>成为会员只需99元哦！
          </div>
          <div class="member-message" v-else-if="userRole === '2'">
            尊敬的用户，您已是会员，可续充！
            <!-- 去发布按钮 待优化！！！！！，添加失败 -->
            <!-- <a-button type="primary" @click="goToApplyView">去发布</a-button> -->
          </div>
          <!--******** mhy优化：充值按钮 -->
          <div class="Recharge-function">
            <!-- 充值输入框 -->
            <input type="number" v-model="amount" :min="1" placeholder="请输入充值金额" class="styled-input" />
            <!-- 充值按钮 -->
            <a-button type="primary" @click="handleRecharge">充值</a-button>
            <!-- 充值结果提示 -->
            <div v-if="rechargeResult === 'success'" class="recharge-success">
              充值成功！
            </div>
            <div v-else-if="rechargeResult === 'fail'" class="recharge-fail">
              充值失败！
            </div>
          </div>
          <!--******** mhy优化：充值记录 -->
          <div class="list-title-min">充值记录</div>
          <!-- 充值记录表格 -->
          <a-table 
          :columns="columns" 
          :data-source="rechargeRecords" 
          row-key="id"
          :pagination="pagination"
          @change="handleTableChange"
          >
           <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'amount'">
                {{ record.amount }} 元
              </template>
              <template v-if="column.dataIndex === 'createTime'">
                <!-- 优化时间：{{ record.createTime }} 该为  xxxx-xx-xx 时:分:秒 格式-->
                {{ formatDate(record.createTime) }}
              </template>
              <template v-if="column.dataIndex === 'status'">
                <span :class="record.status === 'success' ? 'status-success' : 'status-fail'">
                  {{ record.status === 'success' ? '成功' : '失败' }}
                </span>
              </template>
            </template>
          </a-table>
        </div>
        <!--******** mhy优化：添加会员充值弹窗 -->
        <a-modal v-model:visible="rechargeModalVisible" title="会员充值" @ok="handleRecharge">
          <p>点击支付将模拟充值成功。</p>
        </a-modal>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Modal, message } from 'ant-design-vue';
import { useRouter } from 'vue-router';// 引入 useRouter
import { listUserThingApi, updateApi, createApi } from '/@/api/thing';
import { listApi as listClassificationApi } from '/@/api/classification';
import { BASE_URL } from '/@/store/constants';
import { useUserStore } from '/@/store';
//******** mhy优化：获取userRole用户角色、userRechargeApi用户充值、listRechargeRecordsApi充值记录
import { getUserRoleApi, userRechargeApi, getUserBalanceApi, listRechargeRecordsApi } from '/@/api/user';

const router = useRouter(); // 使用 useRouter 获取 router 实例
const userStore = useUserStore();

let loading = ref(false);
// ******** mhy优化：获取userRole
let userRole = ref('');// 新增一个变量来存储用户角色
// ******** mhy优化：充值功能
const amount = ref(0); // 充值金额
const rechargeResult = ref(''); // 充值结果
// ******** mhy 优化：获取用户充值记录
const rechargeRecords = ref([]);
// ******** mhy 优化：获取用户余额
const userBalance = ref(0);

const currentPage = ref(1); // 当前页码
const pageSize = ref(6); // 每页显示条数
let cData = ref([]);

const columns = [
  {
    title: '充值金额',
    dataIndex: 'amount',
    key: 'amount',
  },
  {
    title: '充值时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '充值状态',
    dataIndex: 'status',
    key: 'status',
  },
];

onMounted(() => {
  getCDataList();
  // ********mhy优化：获取用户角色
  getUserRole();
  // ******** mhy 优化：获取用户余额
  fetchUserBalance();
  // ******** mhy 优化：获取用户充值记录
  fetchRechargeRecords();
});


const getCDataList = () => {
  listClassificationApi({}).then((res) => {
    cData.value = res.data;
  });
};


// ********mhy优化：获取用户角色

const getUserRole = () => {
  const userId = userStore.user_id;
  getUserRoleApi({ userId: userId })
    .then((res) => {
      if (res.code === 0 || res.code === 200) {
        //res.data= {role: '1'}
        // console.log(res.data,"**************res.data=",typeof res.data)
        userRole.value = res.data.role;
        //console.log(userRole._value,"**************userRole.value=",typeof userRole._value)
      } else {
        message.error('获取用户角色失败');
      }
    })
    .catch((err) => {
      console.error(err);
      message.error('请求失败');
    });
};


/* const goToApplyView = () => {
  // 使用 Vue Router 的编程式导航跳转到 applyView 页面
  router.push('/applyView');
}; */


// ********mhy优化：添加充值
const fetchUserBalance = () => {
  getUserBalanceApi({ userId: userStore.user_id })
    .then((res) => {
      if (res.code === 200) {
        userBalance.value = res.data;
      } else {
        message.error('获取余额失败：' + res.msg);
      }
    })
    .catch((err) => {
      message.error('获取余额失败：' + (err.response?.data?.msg || err.message || '网络错误'));
    });
};

// ********mhy优化：添加充值记录
const fetchRechargeRecords = () => {
  listRechargeRecordsApi({ userId: userStore.user_id })
    .then((res) => {
      if (res.code === 200) {
        rechargeRecords.value = res.data;
      } else {
        message.error('获取充值记录失败：' + res.msg);
      }
    })
    .catch((err) => {
      message.error('获取充值记录失败：' + (err.response?.data?.msg || err.message || '网络错误'));
    });
};
// ********mhy优化：充值功能
const handleRecharge = () => {
  if (amount.value <= 0) {
    message.error('请输入有效的充值金额');
    return;
  }

  Modal.confirm({
    title: '充值确认',
    content: `您确定要充值 ${amount.value} 元吗？`,
    onOk: () => {
      userRechargeApi({ userId: userStore.user_id, amount: amount.value })
        .then((res) => {
          // 检查响应数据是否存在
          if (res.code === 200) {
            rechargeResult.value = 'success';
            message.success('充值成功！');
            fetchUserBalance(); // 充值成功后更新余额
            fetchRechargeRecords(); // 充值成功后更新充值记录
            getUserRole();//充值后更新用户角色
            amount.value = ''; // 充值成功后重置输入框
          } else {
            rechargeResult.value = 'fail';
            message.error('充值失败：' + res.msg);
          }
        })
        .catch((err) => {
          rechargeResult.value = 'fail';
          message.error('充值失败：' + (err.response?.data?.msg || err.message || '网络错误'));
        });
    },
    onCancel: () => {
      message.info('已取消充值');
    },
  });
};

// ******** mhy优化：设置 时间格式（例如 2025-01-10T01:32:43.564Z），可在前端将其转换为 xxxx-xx-xx 时:分:秒 的格式
const formatDate = (dateString) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};


// ******** mhy优化：跳转到 applyView 页面（待优化）
const goToApplyView = () => {
router.push('/applyView').catch((error) => {
if (error.name !== 'NavigationDuplicated') {
console.error(error);
message.error('跳转失败：' + error.message);
}
});
};


// 计算属性，用于获取当前页的充值记录
const currentPageRechargeRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return rechargeRecords.value.slice(start, end);
});

// 分页配置
const pagination = computed(() => ({
  current: currentPage.value,
  pageSize: pageSize.value,
  total: rechargeRecords.value.length,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '30', '40'],
  showTotal: (total) => `共 ${total} 条数据`,
}));

// 处理表格变化，如分页、排序等
const handleTableChange = (pagination, filters, sorter) => {
  currentPage.value = pagination.current;
  pageSize.value = pagination.pageSize;
};
</script>

<style scoped lang="less">
input,
textarea {
  border-style: none;
  outline: none;
  margin: 0;
  padding: 0;
}

.flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.content-list {
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  background: #ffffff;
  padding: 20px;

  .list-title {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    line-height: 48px;
    height: 48px;
    margin-bottom: 4px;
    border-bottom: 1px solid #cedce4;
  }

  .list-title-min {
    color: #4e5b6f;
    font-weight: 600;
    font-size: 15px;
    line-height: 48px;
    height: 48px;
    margin-bottom: 4px;
    border-bottom: 1px solid #cedce4;
  }

  /* ******** mhy优化：用户余额样式 */
  .balance-card {
    width: 300px;
    margin-bottom: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    padding: 5px;
    background-color: #fff9f4;
  }

  .balance-info {
    display: flex;
    align-items: center;
  }

  .balance-icon {
    width: 32px;
    height: 32px;
    margin-right: 12px;
  }

  .balance-amount {
    font-size: 18px;
    font-weight: bold;
    color: #333;
  }

  .amount {
    font-size: 24px;
    color: #ff5722;
    /* 使用渐变色或其他醒目的颜色 */
    background: linear-gradient(to right, #ff5722, #ff9800);
    -webkit-background-clip: text;
    color: transparent;
  }

  /* 添加一些简单的动画效果 */
  .balance-card {
    transition: transform 0.3s ease;
  }

  .balance-card:hover {
    transform: translateY(-5px);
  }

  /* ********mhy优化：会员提示信息样式 */
  .member-message {
    background-color: #e6f7ff;
    /* 轻柔的蓝色背景 */
    border-left: 4px solid #1890ff;
    /* 蓝色边框 */
    color: #1890ff;
    /* 蓝色文字 */
    padding: 10px 20px;
    /* 内边距 */
    border-radius: 4px;
    /* 圆角边框 */
    font-size: 16px;
    /* 字体大小 */
    margin-bottom: 20px;
    /* 底部外边距 */
    text-align: center;
    /* 文字居中 */
  }

  /* 普通用户提示信息样式 */
  .normal-message {
    background-color: #fffbe6;
    /* 轻柔的黄色背景 */
    border-left: 4px solid #faad14;
    /* 黄色边框 */
    color: #faad14;
    /* 黄色文字 */
    padding: 10px 20px;
    /* 内边距 */
    border-radius: 4px;
    /* 圆角边框 */
    font-size: 16px;
    /* 字体大小 */
    margin-bottom: 20px;
    /* 底部外边距 */
    text-align: center;
    /* 文字居中 */
  }


  //充值框
  .styled-input {
    width: 200px;
    padding: 5px;
    margin-right: 5px;
    font-size: 16px;
    border: 2px solid #ccc;
    border-radius: 5px;
    box-sizing: border-box;
    outline: none;
    transition: border-color 0.3s ease, box-shadow 0.3s ease;
  }

  .styled-input:focus {
    border-color: #007bff;
    box-shadow: 0 0 0 .2rem rgba(0, 123, 255, .25);
  }

  .styled-input::-webkit-input-placeholder {
    color: #aaa;
    font-style: italic;
  }

  .styled-input:-moz-placeholder {
    color: #aaa;
    font-style: italic;
  }

  .styled-input::-moz-placeholder {
    color: #aaa;
    font-style: italic;
  }

  .styled-input:-ms-input-placeholder {
    color: #aaa;
    font-style: italic;
  }

  //充值提示信息
  /* 充值成功提示样式 */
  .recharge-success {
    background-color: #e6ffed;
    border-left: 4px solid #52c41a;
    color: #52c41a;
    padding: 10px 20px;
    border-radius: 4px;
    font-size: 16px;
    margin-top: 20px;
    text-align: center;
  }

  /* 充值失败提示样式 */
  .recharge-fail {
    background-color: #fff2e8;
    border-left: 4px solid #ff4d4f;
    color: #ff4d4f;
    padding: 10px 20px;
    border-radius: 4px;
    font-size: 16px;
    margin-top: 20px;
    text-align: center;
  }


  // ********
  .edit-view {
    .item {
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      margin: 24px 0;

      .label {
        width: 100px;
        color: #152844;
        font-weight: 600;
        font-size: 14px;
      }

      .right-box {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
      }

      .avatar {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        margin-right: 16px;
        object-fit: cover;
      }

      .change-tips {
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        -ms-flex-wrap: wrap;
        flex-wrap: wrap;
      }

      label {
        color: #4684e2;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
        cursor: pointer;
        width: 100px;
        display: block;
      }

      .tip {
        color: #6f6f6f;
        font-size: 14px;
        height: 22px;
        line-height: 22px;
        margin: 0;
        width: 100%;
      }

      .right-box {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
      }

      .input-dom {
        width: 400px;
      }

      .input-dom {
        background: #f8fafb;
        border-radius: 4px;
        height: 40px;
        line-height: 40px;
        font-size: 14px;
        color: #152844;
        padding: 0 12px;
      }

      .tip {
        font-size: 12px;
        line-height: 16px;
        color: #6f6f6f;
        height: 16px;
        margin-top: 4px;
      }

      .intro {
        resize: none;
        background: #8ec1da;
        width: 100%;
        padding: 8px 12px;
        height: 82px;
        line-height: 22px;
        font-size: 14px;
        color: #152844;
      }
    }

    .save {
      background: #4684e2;
      border-radius: 32px;
      width: 96px;
      height: 32px;
      line-height: 32px;
      font-size: 14px;
      color: #fff;
      border: none;
      outline: none;
      cursor: pointer;
    }

    .mg {
      margin-left: 100px;
    }
  }
}
</style>
