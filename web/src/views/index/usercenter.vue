<template>
  <div class="user">
    <Header />

    <div class="user-content">
      <div class="user-content-left">
        <MineInfosView />
      </div>
      <div class="user-content-right">
        <router-view />
      </div>
    </div>

    <Footer />
  </div>
</template>

<script>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import MineInfosView from '/@/views/index/user/mine-infos-view.vue';

export default {
  components: {
    MineInfosView,
    Footer,
    Header,
  },
  data() {
    return {
      collapsed: false,
    };
  },
  mounted() {
    this.handleResize();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      const screenWidth = window.innerWidth;
      const nav = document.querySelector('.user-content-left');

      if (nav) {
        if (screenWidth >= 960) {
          // 大屏幕，使用 sticky 定位
          nav.style.position = 'sticky';
          nav.style.top = '90px';
          nav.style.height = 'calc(105vh - 100px)';
          nav.style.boxShadow = '0 2px 4px rgba(0, 0, 0, 0.1)';
          nav.style.marginBottom = '0';
          nav.style.width = '25%'; // 恢复默认宽度
        } else {
          // 小屏幕，取消固定限制
          nav.style.position = 'static';
          nav.style.height = 'auto';
          nav.style.boxShadow = 'none';
          nav.style.marginBottom = '20px';
          nav.style.width = '100%'; // 设置宽度为 100%
          nav.style.minWidth = '200px'; // 设置最小宽度
        }
      }
    },
  },
};
</script>

<style scoped lang="less">
.user {
  display: block;
}

.user-content {
  display: flex;
  flex-direction: row; // 默认水平布局
  flex-wrap: wrap;
  width: 80%;
  margin: 40px auto;
  gap: 32px;

  .user-content-left {
    //background-color: rgb(252, 246, 236);
    width: 25%;
    position: sticky; // 使用 sticky 定位
    top: 90px; // 确保在 Header 下方
    height: calc(105vh - 100px); // 高度为视口高度减去 Header 和 Footer 的高度
    overflow-y: auto; // 如果内容超出高度，允许滚动
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); // 添加阴影以增强视觉效果
    min-width: 200px; // 设置最小宽度，确保内容不会重叠
  }

  .user-content-right {
    flex: 1;
    padding-right: 20px;
    margin-top: 30px;
  }
}

// 小屏幕设备（< 960px）
@media screen and (max-width: 960px) {
  .user-content {
    flex-direction: column; // 垂直布局
    width: 90%;
    margin: 0 auto; // 调整外边距
  }

  .user-content-left {
    width: 100%; // 导航组件占满整个宽度
    position: static; // 取消固定限制
    height: auto; // 自动高度
    box-shadow: none; // 移除阴影
    margin-bottom: 20px; // 添加间距
    margin-top: 90px; // 添加间距
    min-width: 200px; // 设置最小宽度，确保内容不会重叠
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); // 添加阴影以增强视觉效果
  }

  .user-content-right {
    padding-top: 16px; // 添加一些间距
  }
}
</style>