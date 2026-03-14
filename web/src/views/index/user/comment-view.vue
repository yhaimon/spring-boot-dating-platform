<template>
  <div class="content-list">
    <div class="list-title">评论记录</div>
    <div class="list-content">
      <div class="comment-view">
        <a-spin :spinning="loading" style="min-height: 200px">
          <div class="comment-list">
            <!-- <div class="comment-item flex-view" v-for="item in commentData"> -->
            <!-- ******** mhy优化： 使用v-for循环显示当前页的数据 -->
            <div class="comment-item flex-view" v-for="item in currentPageData" :key="item.id">
              <div class="infos">
                <div class="name flex-view">
                  <h3  @click="handleClickTitle(item)">评论对象：{{ item.title }}</h3>
                  <div class="float-right">
                    <span @click="handleClickTitle(item)">查看详情</span>
                  </div>
                </div>
                <div class="time">{{ item.commentTime }}</div>
                <div class="content">评论内容：{{ item.content }}</div>
              </div>
            </div>
            <template v-if="!commentData || commentData.length <= 0">
              <a-empty style="width: 100%; margin-top: 200px" />
            </template>
          </div>
        </a-spin>
      </div>

      <!-- 添加分页组件 -->
      <!-- :show-size-changer="true"确保显示每页显示条数的下拉菜单 --><!-- @showSizeChange="onShowSizeChange" 添加每页显示条数变化的处理函数 -->
      <a-pagination v-model:current="currentPage" :total="commentData.length" :page-size="pageSize"
        :show-size-changer="true" @change="onPageChange" @showSizeChange="onShowSizeChange"
        style="margin: 20px 0; text-align: center;" />
    </div>
  </div>
</template>

<script setup>

import { useUserStore } from '/@/store';

import { listUserCommentsApi } from '/@/api/comment';
import { BASE_URL } from '/@/store/constants';
import { getFormatTime } from '/@/utils';
import { ref, computed, onMounted } from 'vue';
import message from 'ant-design-vue/es/message';
import { useRouter } from 'vue-router'; // 确保从vue-router中导入
const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const commentData = ref([]); // 存储所有评论数据
const currentPage = ref(1); // 当前页码
const pageSize = ref(4); // 每页显示的数据条数



// ******** mhy优化： 计算属性，用于获取当前页的数据
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return commentData.value.slice(start, end);
});

onMounted(() => {
  getCommentList();
});


const handleClickTitle = (record) => {
  let text = router.resolve({ name: 'detail', query: { id: record.thingId } });
  window.open(text.href, '_blank');
};

const getCommentList = () => {
  loading.value = true;
  let userId = userStore.user_id;
  listUserCommentsApi({ userId: userId })
    .then((res) => {
      res.data.forEach((item) => {
        item.cover = BASE_URL + item.cover;
        item.commentTime = getFormatTime(item.commentTime, true);
      });
      commentData.value = res.data;// 存储所有评论数据
      loading.value = false;
    })
    .catch((err) => {
      message.error(err.msg || '失败');
      loading.value = false;
    });
};

// 处理页码变化
const onPageChange = (page) => {
  currentPage.value = page; // 更新当前页码
};

// 处理每页显示条数变化
const onShowSizeChange = (current, size) => {
  currentPage.value = current; // 更新当前页码
  pageSize.value = size; // 更新每页显示条数
};

</script>
<style scoped lang="less">
.flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.content-list {
  flex: 1;
  //
  padding: 20px;
  background-color: #f8efe5;
  border-radius: 8px;
  box-shadow: 0 3px 5px rgba(3, 3, 3, 0.1);

  //
  .list-title {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    //line-height: 24px;
    height: 48px;
    margin-bottom: 4px;
    border-bottom: 1px solid #96e5f3;
  }
}

.comment-view {
  overflow: hidden;

  .comment-list {
    margin: 8px auto;
  }

  .comment-item {
    padding: 15px 20px;
    //
    background-color: #fff;
    border: 1px solid #eed5d5;
    border-radius: 8px;
    margin-bottom: 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

    //
    .avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      margin-right: 8px;
    }

    .infos {
      position: relative;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
    }

    .name {
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      cursor: pointer;

    }

    h3 {
      color: #152844;
      font-weight: 600;
      font-size: 14px;
      margin: 0;
    }

    .traingle {
      width: 0;
      height: 0;
      border-left: 6px solid #c3c9d5;
      border-right: 0;
      border-top: 4px solid transparent;
      border-bottom: 4px solid transparent;
      margin: 0 12px;
    }

    .time {
      color: #5f77a6;
      font-size: 12px;
      line-height: 16px;
      height: 16px;
      margin: 2px 0 8px;
    }

    .content {
      color: #484848;
      font-size: 14px;
      line-height: 22px;
      padding-right: 30px;
    }

    .float-right {
      color: #4684e2;
      font-size: 14px;
      float: right;

      span {
        margin-left: 19px;
        cursor: pointer;
      }

      .num {
        color: #152844;
        margin-left: 6px;
        cursor: auto;
      }
    }
  }
}
</style>
