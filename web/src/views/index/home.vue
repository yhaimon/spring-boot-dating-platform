<template>
  <div class="portal">
    <Header />
    <div class="content">
      <div class="carousel-container">
        <div class="carousel-wrapper">
          <div class="carousel-title">前3男明星</div>
          <el-carousel :autoplay="true" :interval="3000" indicator-position="outside" height="100%">
            <el-carousel-item v-for="item in top3HotMaleThings" :key="item.id" @click="handleCarouselDetail(item)">
              <img :src="item.cover" alt="" class="carousel-image">
              <p>{{ item.title }}</p>
            </el-carousel-item>
          </el-carousel>
        </div>
        <div class="carousel-wrapper">
          <div class="carousel-title">前3女明星</div>
          <el-carousel :autoplay="true" :interval="3000" indicator-position="outside" height="100%">
            <el-carousel-item v-for="item in top3HotFemaleThings" :key="item.id" @click="handleCarouselDetail(item)">
              <img :src="item.cover" alt="" class="carousel-image">
              <p>{{ item.title }}</p>
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>

      <div class="content-top">
        <div class="left-search-item">
          <div class="label">分类:</div>
          <div class="c-list">
            <div :class="contentData.c === item.id ? 'c-item-selected' : 'c-item'" v-for="item in contentData.cData"
              :key="item.id" @click="clickCategory(item.id)">
              {{ item.title }}
            </div>
          </div>
        </div>
      </div>
      <div class="content-down">
        <div class="down-wrap">
          <div class="tab-view flex-view">
            <div class="tab" :class="contentData.selectTabIndex === index ? 'tab-select' : ''"
              v-for="(item, index) in contentData.tabData" :key="index" @click="selectTab(index)">
              {{ item }}
            </div>
          </div>
          <a-spin :spinning="contentData.loading" style="min-height: 200px">
            <div class="pc-thing-list flex-view">
              <div v-for="item in contentData.pageData" :key="item.id" @click="handleDetail(item)"
                class="thing-item item-column-3"><!---->
                <div class="img-view"><img :src="item.cover" /></div>
                <div class="info-view"> 
                  <h3 class="thing-name">{{ item.title.substring(0, 12) }}</h3>
                  <span>
                    <span class="a-price">{{ item.age }}</span>
                    <span class="a-price">{{ item.pv }}次浏览</span>
                  </span>
                </div>
              </div>
              <div v-if="contentData.pageData.length <= 0 && !contentData.loading" class="no-data" style="">暂无数据</div>
            </div>
          </a-spin>
          <div class="page-view" style="">
            <a-pagination v-model="contentData.page" size="small" @change="changePage" :hideOnSinglePage="true"
              :defaultPageSize="contentData.pageSize" :total="contentData.total" :showSizeChanger="false" />
          </div>
        </div>
      </div>

    </div>
    <Footer />
  </div>
</template>
<script setup>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import { listApi as listClassificationList } from '/@/api/classification';
import { listApi as listThingList } from '/@/api/thing';
// ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
import { getTop3HotGenderThingsApi } from '/@/api/thing';
import { BASE_URL } from '/@/store/constants';
import { useUserStore } from '/@/store';
// ******** 安装了兼容vue3的npm install vue3-carousel组件的配置
import { ElCarousel, ElCarouselItem } from 'element-plus';
import 'element-plus/dist/index.css';
// ********
const userStore = useUserStore();
const router = useRouter();

const contentData = reactive({
  c: -1,
  cc: '全部',
  cData: [],
  ccData: ['全部', '北京', '上海', '深圳'],

  loading: false,

  tabData: ['最新', '最热'],
  selectTabIndex: 0,
  sort: '',

  thingData: [],
  pageData: [],

  page: 1, // 分页
  total: 0,
  pageSize: 12,
});

// ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
const top3HotMaleThings = ref([]);
const top3HotFemaleThings = ref([]);
// ********

onMounted(() => {
  initFilter();
  getThingList();
  // ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
  getTop3HotGenderThings();
});
// ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
/* const getTop3HotGenderThings = () => {
  getTop3HotGenderThingsApi()
    .then((res) => {
      top3HotMaleThings.value = res.data.male;
      top3HotFemaleThings.value = res.data.female;
      //console.log("***************",res)
    })
    .catch((err) => {
      console.log(err);
    });
}; */


const getTop3HotGenderThings = () => {
  getTop3HotGenderThingsApi()
    .then((res) => {
      // 确保返回的数据中包含male和female两个字段
      if (res.data && res.data.male && res.data.female) {
        // 处理男性最热商品的图片路径
        res.data.male.forEach(item => {
          if (item.cover) {
            item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
          }
        });
        // 处理女性最热商品的图片路径
        res.data.female.forEach(item => {
          if (item.cover) {
            item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
          }
        });
        // 更新响应式数据
        top3HotMaleThings.value = res.data.male;
        top3HotFemaleThings.value = res.data.female;
      }
      console.log(res);
    })
    .catch((err) => {
      console.log(err);
    });
};


// ********
// 过滤栏
const initFilter = () => {
  contentData.cData.push({ id: -1, title: '全部' });
  listClassificationList().then((res) => {
    res.data.forEach((item) => {
      contentData.cData.push(item);
    });
  });
};

// 点击分类
const clickCategory = (id) => {
  contentData.c = id;
  getThingList();
};

// 点击分类2
const clickCC = (item) => {
  contentData.cc = item;
  getThingList();
};

// 最新|最热
const selectTab = (index) => {
  contentData.selectTabIndex = index;
  console.log(contentData.selectTabIndex);
  let sort = index === 0 ? 'recent' : 'hot';
  contentData.sort = sort;
  getThingList();
};
const handleDetail = (item) => {
  // 跳转新页面
  let text = router.resolve({ name: 'detail', query: { id: item.id } });
  window.open(text.href, '_blank');
};
// ******** mhy优化：轮播图跳转详情页detail
const handleCarouselDetail = (item) => {
  handleDetail(item);
};
// 分页事件
const changePage = (page) => {
  contentData.page = page;
  let start = (contentData.page - 1) * contentData.pageSize;
  contentData.pageData = contentData.thingData.slice(start, start + contentData.pageSize);
  console.log('第' + contentData.page + '页');
};
const getThingList = () => {
  contentData.loading = true;
  let params = {
    c: contentData.c,
    cc: contentData.cc,
    sort: contentData.sort,
  };
  listThingList(params)
    .then((res) => {
      contentData.loading = false;
      contentData.thingData = [];
      res.data.forEach((item, index) => {
        if (item.cover) {
          item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
        }
        if (item.status === '0') {
          // 0 表示上架（显示）
          contentData.thingData.push(item);
        }
      });
      console.log(res);
      contentData.total = contentData.thingData.length;
      changePage(1);
    })
    .catch((err) => {
      console.log(err);
      contentData.loading = false;
    });
};
</script>

<style scoped lang="less">
// 兼容
@media screen and (max-width: 720px) {
  .thing-item {
    width: calc((100% - 20 * 1px) / 2) !important;

    .img-view {
      height: 100px !important;
    }
  }

  .content-top {
    width: 90% !important;
  }

  .down-wrap {
    width: 90% !important;
  }
}

@primary-color: #b146e2;

.content {
  display: flex;
  flex-direction: column;
  margin: 56px auto;
  width: 100%;
}

// ******** mhy优化： 筛选处最热top1-3男or女，轮播图准备
.carousel-container {
  margin: 20px 0;
  display: flex;
  justify-content: center;
  gap: 80px;
  width: 100%;
  max-width: 1200px; /* 限制最大宽度 */
  margin-left: auto;
  margin-right: auto;
}

.carousel-wrapper {
  flex: 0 0 510px; /* 设置固定宽度 */
  height: 410px; /* 增加高度以容纳文字 */
  background-color: #eccbcb;
  border-radius: 12px;
  box-shadow: 2px 4px 8px rgba(112, 176, 244, 0.1);
  padding: 20px;
  position: relative;
  overflow: visible; /* 确保内容不会被隐藏 */
  
}

.carousel-title {
  font-size: 24px;
  margin-bottom: 10px;
  color: #f66206;
  font-weight: bold;
  text-align: center;
}

.el-carousel {
  height: 100%;
  width: 100%;
}

.el-carousel__item {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  transition: transform 0.3s ease;
  padding: 20px;
  box-sizing: border-box;
  cursor: pointer; /* 添加鼠标悬停样式 */



}

.el-carousel__item img {
  width: 100%;
  height: 280px; /* 4:3 的高度 */
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.3s ease;
  margin-bottom: 10px; /* 添加间距 */
}

.el-carousel__item:hover img {
  transform: scale(1.07); /* 悬浮时图片放大 */
}

.el-carousel__item p {
  font-size: 20px; /* 增大文字大小 */
  color: #333; /* 修改文字颜色 */
  text-align: center;
  transition: color 0.3s ease, transform 0.3s ease;
  margin: 10px 0; /* 添加上下间距 */
}

.el-carousel__item:hover p {
  color: #fb6514; /* 悬浮时文字颜色变化 */
  transform: scale(1.05); /* 悬浮时文字放大 */
}

/* 自定义轮播条的样式 */
.el-carousel__indicator {
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  border-radius: 50%;
  background-color: #f7d0d0; /* 默认颜色 */
  width: 10px;
  height: 10px;
  margin: 0 5px;
}

.el-carousel__indicator.is-active {
  background-color: #333; /* 激活状态的颜色 */
}

.el-carousel__indicator:hover {
  background-color: #666; /* 鼠标悬停颜色 */
}

@media (max-width: 768px) {
  .carousel-container {
    flex-direction: column;
  }
  .carousel-wrapper {
    width: 100%;
    height: 600px;
  }
  .el-carousel__item img {
    height: 380px;
  }
  .el-carousel__item p {
    font-size: 16px;
  }
}

@media (max-width: 320px) {
  .carousel-wrapper {
    height: 300px;
  }
  .el-carousel__item img {
    height: 250px;
  }
  .el-carousel__item p {
    font-size: 14px;
  }
}
// ********


.content-top {
  width: 80%;
  margin: 8px auto;

  .left-search-item {
    overflow: hidden;
    margin: 16px 0;
    display: flex;
    flex-direction: row;

    .label {
      color: #212121;
      font-weight: 600;
      font-size: 18px;
      margin-right: 12px;
      width: 50px;
      line-height: 27px;
    }

    .c-list {
      flex: 1;
      display: flex;
      flex-wrap: wrap;
      flex-direction: row;
      align-items: center;

      .c-item {
        display: inline-block;
        cursor: pointer;
        margin: 2px 12px;
        color: #212121;
        font-size: 18px;
      }

      .c-item:hover {
        color: #555555;
        background-color: rgb(244, 195, 160);
      }

      .c-item-selected {
        display: inline-block;
        cursor: pointer;
        margin: 2px 10px;
        font-weight: bold;
        font-size: 20px;
        color: #e92020;
      }
    }
  }
}

.flex-view {
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  //justify-content: space-between;
  display: flex;
}

.content-down {
  background: rgb(246, 243, 229);

  min-height: 375px;

  .down-wrap {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    width: 80%;
    margin: 0 auto;

    .flex-view {
      display: flex;
    }

    .tab-view {
      display: flex;
      flex-direction: row;
      gap: 8px;
      margin-top: 20px;
      margin-bottom: 20px;

      .tab {
        height: 24px;
        line-height: 24px;
        padding: 0 12px;
        text-align: center;
        display: inline-block;
        font-size: 14px;
        color: #545c63;
        cursor: pointer;
      }

      .tab-select {
        font-size: 18px;
        border-radius: 12px;
        background-color: @primary-color;
        color: #fff;
      }
    }

    .pc-thing-list {
      -ms-flex-wrap: wrap;
      flex-wrap: wrap;
      gap: 20px;

      .thing-item {
        width: calc((100% - 20 * 3px) / 4);
        position: relative;
        height: fit-content;
        overflow: hidden;
        cursor: pointer;

        /* 
          .img-view {
            width: 100%;
            aspect-ratio: 4/3;

            img {
              height: 100%;
              width: 100%;
              border-radius: 8px;
              margin: 0 auto;
              background-size: cover;
              object-fit: cover;
            }
          } */
        .img-view {
          width: 100%;
          aspect-ratio: 4/3;
          overflow: hidden;
          /* 防止图片溢出 */
          transition: transform 0.3s ease;
          /* 平滑过渡效果 */

          img {
            height: 100%;
            width: 100%;
            border-radius: 8px;
            margin: 0 auto;
            background-size: cover;
            object-fit: cover;
            transition: transform 0.3s ease;
            /* 平滑过渡效果 */
          }

          &:hover img {
            /* 鼠标悬停时放大图片 */
            transform: scale(1.1);
            /* 放大10% */
          }
        }

        .info-view {
          //background: #f6f9fb;
          overflow: hidden;
          padding: 0 0px;

          .thing-name {
            line-height: 20px;
            margin-top: 12px;
            font-size: 18px;
            color: #0f1111;
            font-weight: 500;
            // 优化：
            transition: color 0.3s ease, text-shadow 0.3s ease, opacity 0.3s ease;

            /* 平滑过渡效果 */
            &:hover {
              /* 鼠标悬停时的效果 */
              color: #9309d3;
              /* 改变文字颜色 */
              text-shadow: 0 0 5px rgba(153, 5, 5, 0.9);
              /* 添加文字阴影 */
              opacity: 0.8;
              /* 改变文字透明度 */
            }
          }

          .price {
            color: #ff7b31;
            font-size: 14px;
            line-height: 20px;
            margin-top: 4px;
            overflow: hidden;
            white-space: nowrap;
          }

          .translators {
            color: #6f6f6f;
            font-size: 12px;
            line-height: 14px;
            margin-top: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }


        }
      }

      .no-data {
        height: 200px;
        line-height: 200px;
        text-align: center;
        width: 100%;
        font-size: 16px;
        color: #152844;
      }
    }

    .page-view {
      font-size: 15px;
      width: 100%;
      text-align: center;
      margin-top: 48px;
    }
  }
}


.a-price {
  color: #0f1111;
  font-size: 14px;
  margin-right: 10px;
}
</style>
