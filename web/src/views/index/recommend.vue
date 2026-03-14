<template>
  <div class="portal">
    <Header />
    <div class="content">
      <a-spin :spinning="contentData.loading" style="min-height: 200px">
        <div class="pc-thing-list flex-view">
          <div v-for="item in contentData.pageData" :key="item.id" @click="handleDetail(item)" class="thing-item item-column-3"
            ><!---->
            <div class="img-view"><img :src="item.cover" /></div>
            <div class="info-view">
              <h3 class="thing-name">{{ item.title.substring(0, 12) }}</h3>
              <span>
                <span class="a-price-symbol"></span>
                <span class="a-price">{{ item.age }}</span>&nbsp;
              </span>
            </div>
          </div>
          <div v-if="contentData.pageData.length <= 0 && !contentData.loading" class="no-data" style="">暂无数据</div>
        </div>
      </a-spin>
      <div class="page-view" style="">
        <a-pagination
          v-model="contentData.page"
          size="small"
          @change="changePage"
          :hideOnSinglePage="true"
          :defaultPageSize="contentData.pageSize"
          :total="contentData.total"
          :showSizeChanger="false"
        />
      </div>
    </div>
    <Footer />
  </div>
</template>
<script setup>
  import Header from '/@/views/index/components/header.vue';
  import Footer from '/@/views/index/components/footer.vue';
  import { getRecommendApi } from '/@/api/thing';
  import { BASE_URL } from '/@/store/constants';
  import { useUserStore } from '/@/store';

  const userStore = useUserStore();
  const router = useRouter();

  const contentData = reactive({
    loading: false,

    thingData: [],
    pageData: [],

    page: 1,
    total: 0,
    pageSize: 12,
  });

  onMounted(() => {
    getThingList({});
  });

  const handleDetail = (item) => {
    // 跳转新页面
    let text = router.resolve({ name: 'detail', query: { id: item.id } });
    window.open(text.href, '_blank');
  };
  // 分页事件
  const changePage = (page) => {
    contentData.page = page;
    let start = (contentData.page - 1) * contentData.pageSize;
    contentData.pageData = contentData.thingData.slice(start, start + contentData.pageSize);
    console.log('第' + contentData.page + '页');
  };
  const getThingList = (data) => {
    contentData.loading = true;
    getRecommendApi({},{})
      .then((res) => {
        contentData.loading = false;
        contentData.thingData = [];
        res.data.forEach((item, index) => {
          if (item.cover) {
            item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
          }
          if (item.status === '0') {
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
    .content {
      width: 90% !important;
    }
  }

  .content {
    display: flex;
    flex-direction: column;
    margin: 56px auto 56px;
    padding-top: 30px;
    width: 80%;
    min-height: 500px;
    //background: linear-gradient(135deg, #fff0f0, #ffffff); // 渐变背景，体现热门感
    
    box-shadow: 0 20px 40px rgba(248, 80, 50, 0.695); // 鲜艳的阴影
    padding: 20px;

    .pc-thing-list {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;

      .thing-item {
        width: calc((90% - 20 * 3px) / 4);
        margin:auto;
        position: relative;
        height: fit-content;
        overflow: hidden;
        cursor: pointer;
        border-radius: 15px; // 圆角
        background: white; // 白色背景
        box-shadow: 0 4px 8px rgba(255, 99, 71, 0.2); // 鲜艳的阴影
        transition: transform 0.3s ease, box-shadow 0.3s ease; // 过渡动画

        &:hover {
          transform: scale(1.05); // 悬浮放大
          box-shadow: 0 8px 16px rgba(255, 99, 71, 0.866); // 悬浮阴影
        }

        .img-view {
          width: 100%;
          aspect-ratio: 4/3;
          overflow: hidden;
          //border-radius: 15px 15px 0 0; // 图片圆角
          position: relative;

          img {
            height: 100%;
            width: 100%;
            //border-radius: 8px;
            margin: 0 auto;
            background-size: cover;
            object-fit: cover;
            transition: transform 0.3s ease; // 图片过渡动画
          }

          &:hover img {
            transform: scale(1.1); // 图片悬浮放大
          }

          // 热门标签
          &::before {
            content: "Hot";
            position: absolute;
            top: 10px;
            right: 10px;
            background: #fd949c; // 鲜艳的红色
            color: white;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
            z-index: 1;
          }
        }

        .info-view {
          padding: 16px;
          background: white;
          background: linear-gradient(to bottom, rgba(252, 236, 236, 0) 0%, rgba(255, 180, 180, 0.387) 100%);
          border-radius: 0 0 15px 15px; // 底部圆角

          .thing-name {
            line-height: 20px;
            margin-top: 12px;
            font-size: 16px;
            color: #f97f89 !important; // 鲜艳的红色
            font-weight: 600; // 加粗字体
            transition: color 0.3s ease; // 字体颜色过渡
          }

          .price {
            color: #fed6bf;
            font-size: 14px;
            line-height: 20px;
            margin-top: 4px;
            overflow: hidden;
            white-space: nowrap;
            transition: color 0.3s ease; // 文字颜色过渡
          }

          .translators {
            color: #6f6f6f;
            font-size: 12px;
            line-height: 14px;
            margin-top: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            transition: color 0.3s ease; // 文字颜色过渡
          }

          &:hover {
            background: linear-gradient(to bottom, rgba(252, 236, 236, 0) 0%, rgb(255, 169, 169) 100%);
            .thing-name {
              color: #d97af6; // 悬停时文字颜色变化
            }
            .price {
              color: #e8b6f7; // 悬停时文字颜色变化
            }
            .translators {
              color: #0f1111; // 悬停时文字颜色变化
            }
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
  }

  .page-view {
    display: flex;
    justify-content: center;
    margin-top: 20px;

    .ant-pagination {
      .ant-pagination-item {
        border-radius: 50%; // 分页按钮圆角
        transition: background 0.3s ease, color 0.8s ease; // 过渡动画

        &:hover {
          background: #fffdfd; // 鲜艳的红色
          color: white; // 悬浮字体颜色
        }

        &.ant-pagination-item-active {
          background: #fff0f1; // 鲜艳的红色
          color: white; // 选中字体颜色
        }
      }
    }
  }

  .flex-view {
    display: flex;
  }
</style>