<template>
  <div class="content-list">
    <div class="list-title">会员管理</div>
    <a-spin :spinning="loading" style="min-height: 200px">
      <div class="list-content">
        <!-- 提示信息 -->
        <div class="Recharge-view">
          <div class="normal-message" v-if="userRole === '1'">
            尊敬的用户，您还不是会员，请先充值后填写会员信息！
            <!-- 充值按钮 -->
            <button class="recharge-btn" @click="goToRecharge">去充值</button>
          </div>
          <div class="member-message" v-else-if="userRole === '2'">
            尊敬的会员!<br />登记会员信息后，系统将自动发布您的信息在相亲广场哟！
            <!-- 充值按钮 -->
            <button class="recharge-btn" @click="goToRecharge">充值更多</button>
          </div>
        </div>

        <!-- 会员登记表单 -->
        <div class="edit-view"  v-if="userRole === '2'">
          <div class="list-title">会员信息发布</div>
          <!-- 上传相亲照片 -->
          <div class="item flex-view">
            <div class="label">相亲照片</div>
            <div class="right-box avatar-box flex-view">
              <img v-if="tData.form.avatar" :src="tData.form.avatar" class="avatar" />
              <img v-else :src="AvatarIcon" class="avatar" />
              <div class="change-tips flex-view">
                <a-upload name="file" accept="image/*" :multiple="false" :before-upload="beforeUpload"
                  class="upload-button">
                  <label>点击更换相亲照片</label>
                </a-upload>
              </div>
              <p class="tip">图片格式支持 GIF、PNG、JPEG，尺寸不小于 200 PX，小于 4 MB</p>
            </div>
          </div>
          <!-- 循环渲染表单字段 -->
          <div v-for="(group, index) in formGroups" :key="index" class="item flex-view"
            :class="{ 'three-columns': isLargeScreen, 'full-width': group.fields[0].class === 'full-width' }">
            <div v-for="field in group.fields" :key="field.key" class="column">
              <div class="label">{{ field.label }}</div>
              <div class="right-box">
                <input v-if="field.type === 'text'" v-model="tData.form[field.key]" :placeholder="field.placeholder"
                  :maxlength="field.maxlength" class="input-dom web-input" />
                <div v-else-if="field.type === 'textarea'" class="textarea-container">
                  <textarea v-model="tData.form[field.key]" :placeholder="field.placeholder"
                    :maxlength="field.maxlength" class="intro"></textarea>
                  <div class="word-count">
                    {{ wordCount }} / {{ field.maxlength }} 字
                  </div>
                </div>
                <p class="tip">{{ field.tip }}</p>
              </div>
            </div>
          </div>



          <!-- 保存按钮 -->
          <button class="save mg" @click="submit()">保存</button>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { message } from 'ant-design-vue';
import { listUserThingApi, updateApi, createApi } from '/@/api/thing';
import { listApi as listClassificationApi } from '/@/api/classification';
import { BASE_URL } from '/@/store/constants';
import { useUserStore } from '/@/store';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import { getUserRoleApi } from '/@/api/user';
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const userRole = ref(''); // 用户角色
const tData = reactive({
  form: {
    avatar: undefined,
    avatarFile: undefined,
    title: undefined,
    description: undefined,
    shengao: undefined,
    tizhong: undefined,
    zhiye: undefined,
    age: undefined,
    sex: undefined,
    chefang: undefined,
    address: undefined,
    phone: undefined,
    classificationId: undefined,
  },
});
const cData = ref([]);

const isLargeScreen = computed(() => window.innerWidth > 768); // 判断是否为大屏幕

const beforeUpload = (file) => {
  if (file.size > 2 * 1024 * 1024) {
    message.warn('图片太大，需小于2M');
    return false;
  }
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  tData.form.avatarFile = copyFile;

  // 更新 tData.form.avatar 的路径
  const reader = new FileReader();
  reader.onload = (e) => {
    tData.form.avatar = e.target.result;
  };
  reader.readAsDataURL(file);

  return false;
};

const getCDataList = () => {
  listClassificationApi({}).then((res) => {
    cData.value = res.data;
  });
};

const getUserThing = () => {
  loading.value = true;
  listUserThingApi({ userId: userStore.user_id })
    .then((res) => {
      if (res.data && res.data.length > 0) {
        tData.form = res.data[0];
      }
      if (tData.form.cover) {
        tData.form.avatar = BASE_URL + '/api/staticfiles/image/' + tData.form.cover;
      }
      loading.value = false;
    })
    .catch((err) => {
      console.log(err);
      loading.value = false;
    });
};

const getUserRole = () => {
  getUserRoleApi({ userId: userStore.user_id })
    .then((res) => {
      if (res.code === 0 || res.code === 200) {
        userRole.value = res.data.role;
      } else {
        message.error('获取用户角色失败');
      }
    })
    .catch((err) => {
      console.error(err);
      message.error('请求失败');
    });
};

//******** 去充值 ********** */
// 跳转到充值页面
const goToRecharge = () => {
  router.push('rechargeView'); // 假设充值页面的路由路径是 '/recharge'
};

const handleChat = (detailData) => {
  console.log(detailData);
  const userId = userStore.user_id;

  if (userId) {
    // 检查是否登记过
    listUserThingApi({ userId: userId })
      .then((res) => {
        console.log(res);
        if (res.data && res.data.length > 0) {
          let myThing = res.data[0];
          router.push({
            name: 'chatRoomView',
            query: {
              detailData: JSON.stringify(detailData)// 直接传递 detailData
            },
          });
        } else {
          message.warn('请先登记个人信息！');
          router.push({ name: 'applyView' });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  } else {
    message.warn('请先登录！');
  }
};

const wordCount = computed(() => {
  const description = tData.form.description || '';
  return description.length;
});

const submit = () => {
  const formData = new FormData();
  const userId = userStore.user_id;

  if (tData.form.avatarFile) {
    formData.append('imageFile', tData.form.avatarFile);
  }
  formData.append('userId', userId);
  formData.append('status', '0');

  if (tData.form.title) {
    formData.append('title', tData.form.title);
  } else {
    message.warn('姓名不能为空');
    return;
  }

  if (tData.form.sex) {
    formData.append('sex', tData.form.sex);
  } else {
    message.warn('性别不能为空');
    return;
  }

  if (tData.form.age) {
    formData.append('age', tData.form.age);
  } else {
    message.warn('年龄不能为空');
    return;
  }

  if (tData.form.description) {
    formData.append('description', tData.form.description);
  } else {
    message.warn('简介不能为空');
    return;
  }

  formData.append('shengao', tData.form.shengao || '');
  formData.append('tizhong', tData.form.tizhong || '');
  formData.append('zhiye', tData.form.zhiye || '');
  formData.append('chefang', tData.form.chefang || '');
  formData.append('address', tData.form.address || '');
  formData.append('phone', tData.form.phone || '');

  if (tData.form.id) {
    formData.append('id', tData.form.id);
    updateApi(formData)
      .then((res) => {
        message.success('保存成功');
        getUserThing();
      })
      .catch((err) => {
        console.log(err);
      });
  } else {
    createApi(formData)
      .then((res) => {
        message.success('保存成功');
        getUserThing();
      })
      .catch((err) => {
        console.log(err);
      });
  }
};

onMounted(() => {
  getCDataList();
  getUserThing();
  getUserRole();
});

// 定义表单字段分组
const formGroups = ref([
  {
    fields: [
      { label: '姓名', key: 'title', type: 'text', placeholder: '请输入姓名', maxlength: 20, tip: '支持中英文，长度不能超过 20 个字符' },
      { label: '性别', key: 'sex', type: 'text', placeholder: '请输入“男”或“女”', maxlength: 10, tip: '请输入“男”或“女”' },
      { label: '联系方式', key: 'phone', type: 'text', placeholder: '请输入', maxlength: 20, tip: '请输入有效联系方式' },
    ],
  },
  {
    fields: [
      { label: '年龄', key: 'age', type: 'text', placeholder: '请输入年龄，比如“18岁”', maxlength: 10, tip: '请输入有效年龄' },
      { label: '身高', key: 'shengao', type: 'text', placeholder: '请输入身高，比如：180cm', maxlength: 20, tip: '请输入有效身高' },
      { label: '体重', key: 'tizhong', type: 'text', placeholder: '请输入体重，比如：66kg', maxlength: 20, tip: '请输入有效体重' },
    ],
  },
  {
    fields: [
      { label: '职业', key: 'zhiye', type: 'text', placeholder: '请输入', maxlength: 20, tip: '请输入职业信息' },
      { label: '车房情况', key: 'chefang', type: 'text', placeholder: '请输入', maxlength: 20, tip: '请输入车房情况' },
      { label: '居住地', key: 'address', type: 'text', placeholder: '请输入居住地，比如：北京', maxlength: 20, tip: '请输入居住地' },
    ],
  },
  {
    fields: [
      { label: '自我介绍', key: 'description', type: 'textarea', placeholder: '请输入简介', maxlength: 230, tip: '限制230字以内' },
    ],
  },
]);
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
  display: flex;
  align-items: center;
}

.content-list {
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

  .member-message {
    background-color: #e6f7ff;
    border-left: 4px solid #1890ff;
    color: #1890ff;
    padding: 10px 20px;
    border-radius: 4px;
    font-size: 16px;
    margin-bottom: 20px;
    text-align: center;
  }

  .normal-message {
    background-color: #fffbe6;
    border-left: 4px solid #faad14;
    color: #faad14;
    padding: 10px 20px;
    border-radius: 4px;
    font-size: 16px;
    margin-bottom: 20px;
    text-align: center;
  }

  .edit-view {
    .item {
      margin: 24px 0;

      .label {
        width: 100px;
        color: #152844;
        font-weight: 600;
        font-size: 14px;
      }

      .right-box {
        flex: 1;
        position: relative;
        margin-top: 8px;
      }

      .avatar {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: 8px;
      }

      .input-dom {
        width: 100%;
        max-width: 200px;
        background: #f8fafb;
        border-radius: 4px;
        height: 40px;
        line-height: 40px;
        font-size: 14px;
        color: #152844;
        padding: 0 12px;
        margin-bottom: 5px;
      }

      .textarea-container {
        position: relative;
        width: 100%;
      }

      .intro {
        resize: none;
        background: #f8fafb;
        width: 100%;
        max-width: 700px;
        padding: 8px 12px;
        height: 130px;
        line-height: 22px;
        font-size: 14px;
        color: #152844;
        margin-bottom: 8px;
      }

      .word-count {
        position: absolute;
        bottom: 12px;
        right: 45px;
        font-size: 12px;
        color: #6f6f6f;
      }

      .tip {
        font-size: 12px;
        color: #6f6f6f;
        width: 100%;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .three-columns {
      display: flex;
      justify-content: space-between;

      .column {
        flex: 1;
        margin-right: 16px;
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

    /* 上传图片按钮悬停效果 */
    .upload-button {
      display: inline-block;
      padding: 8px 10px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s ease, transform 0.3s ease, box-shadow 0.3s ease;

      &:hover {
        background-color: #fffde8;
        transform: scale(1.05);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
      }

      &:active {
        transform: scale(0.95);
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .content-list {
    padding: 10px;

    .list-title {
      font-size: 16px;
      line-height: 24px;
    }

    .edit-view {
      .item {
        flex-direction: column;
        align-items: flex-start;

        .label {
          margin-bottom: 8px;
        }

        .right-box {
          width: 100%;
        }
      }

      .three-columns {
        flex-direction: column;
      }

      .save {
        margin-left: 0;
        margin-top: 20px;
      }
    }
  }
}
</style>