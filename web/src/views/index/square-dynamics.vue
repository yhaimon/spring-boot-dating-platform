<template>
  <div class="container">
    <Header />
    <!-- 添加一个按钮，点击后切换动态发布组件的显示状态 -->
    <button @click="toggleDynamicSend" class="publish-button">
      {{ showDynamicSend ? '收起' : '去发布' }} <!-- 根据状态动态切换按钮文本 -->
    </button>

    <!-- 动态发布组件，根据 showDynamicSend 的值动态显示 -->
    <dynamic-send v-if="showDynamicSend" @postSubmitted="fetchPosts"
      @close="toggleDynamicSend" /><!-- 点击关闭按钮时调用 toggleDynamicSend 方法 -->

    <!-- 动态列表区域 -->
    <h2>动态广场</h2>
    <div class="dynamic-list">
      <!-- 如果没有动态，显示提示信息 -->
      <div v-if="posts.length === 0" class="no-dynamics">
        快来发布你的第一条动态吧！
      </div>
      <!-- 如果有动态，显示动态列表 -->
      <div v-for="(post, index) in posts" :key="index" class="post">
        <!-- 动态框框 -->
        <div class="post-box">
          <!-- 用户信息整体 -->
          <div class="user-info-container">
            <!-- 用户头像 -->
            <div class="user-avatar">
              <img :src="getAvatarUrl(post.avatar)" alt="用户头像" class="avatar-image" />
            </div>
            <!-- 用户姓名和时间 -->
            <div class="user-details">
              <div class="user-name">{{ post.user_name }}</div>
              <div class="post-time">{{ post.updatedAt }}</div>
            </div>

            <!-- 添加更多按钮 -->
            <div class="more-button-container" v-if="post.userId === userId" @mouseenter="showMoreOptions = index"
              @mouseleave="showMoreOptions = null">
              <button class="more-button">
                更多
              </button>
              <!-- 更多操作弹出层 -->
              <div v-if="showMoreOptions === index" class="more-options">
                <button @click="editPost(post, index)" class="option-button">编辑</button>
                <button @click="deletePost(post)" class="option-button">删除</button>
              </div>
            </div>
          </div>
          <!-- 动态标题 -->
          <!-- <div class="title"># {{ post.title }}</div> -->
          <!-- 动态内容 -->
          <!-- <div class="content">{{ post.content }}</div> -->
          <!-- 动态图片 --><!-- 点击图片时调用 openImage 方法 --><!-- 鼠标悬停时调用 hoverImage 方法 --><!-- 鼠标移出时调用 resetHover 方法 -->
          <!-- <div v-if="post.images" class="image-list">            
            <img v-for="(img, imgIndex) in post.images" :key="imgIndex" :src="img" alt="动态图片" class="post-image"
              @click="openImage(img)" @mouseover="hoverImage(imgIndex)" @mouseout="resetHover" />
          </div> -->

          <!-- 动态标题 -->
          <div v-if="!isEditing[index]" class="title"># {{ post.title }}</div>
          <input v-else v-model="post.title" type="text" class="edit-title" placeholder="请输入标题" />
          <!-- 动态内容 -->
          <div v-if="!isEditing[index]" class="content">{{ post.content }}</div>
          <textarea v-else v-model="post.content" class="edit-content" placeholder="请输入内容"></textarea>
          <!-- 动态图片 -->
          <div v-if="!isEditing[index] && post.images" class="image-list">
            <img v-for="(img, imgIndex) in post.images" :key="imgIndex" :src="getPostImgUrl(img)" alt="动态图片" class="post-image"
              @click="openImage(getPostImgUrl(img))" @mouseover="hoverImage(imgIndex)" @mouseout="resetHover" />
          </div>
          <div v-else class="image-upload-container">
            <!-- 遍历post.images，显示已上传的图片 -->
            <div v-for="(image, imgIndex) in post.images" :key="imgIndex" class="image-upload">
              <img :src="image.preview ||  getPostImgUrl(image)" alt="预览" class="image-preview" />

              <!-- 删除按钮，用于移除已上传的图片 -->
              <button type="button" @click="removeImage(post, imgIndex)" class="remove-button">删除</button>
            </div>
            <!-- 上传按钮 -->
            <div class="upload-button" @click="triggerUpload(post)">
              <img :src="AddIcon" alt="上传" class="upload-icon" />
            </div>
            <!-- 实际的文件输入框 -->
            <input type="file" :id="`file-input-${post.id}`" @change="handleImageUpload(post, $event)" multiple
              style="display: none" />
          </div>

          <!-- 动态点赞、评论操作按钮 -->
          <div class="post-actions">
            <button class="action-button" @click="likeDynamic(post)">
              <img :src="LikeUpIcon" alt="点赞" class="action-icon" />
              <span class="num">{{ post.likeCount }}</span>
              <!-- <span class="num">1</span> -->
            </button>
            <button class="action-button" @click="toggleCommentBox(index)">
              <img :src="replyIcon" alt="评论" class="action-icon" />
            </button>
          </div>

          <!-- 评论区域 -->
          <div v-if="showCommentBox === index" class="comment-section">
            <div class="comment-input">
              <input placeholder="说点什么..." class="comment-input-field" v-model="commentContent" />
              <button class="comment-send-btn" @click="sendComment(post.id)">
                发送
              </button>
            </div>
            <div class="comments-list">
              <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
                <div class="comment-user-info">
                  <img :src="comment.avatar ? comment.avatar : AvatarIcon" class="comment-avatar" />
                  <div class="comment-name">{{ comment.username }}</div>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
              </div>
            </div>
          </div>

        </div>

        <!-- 编辑状态下的保存和取消按钮 -->
        <div v-if="isEditing[index]" class="edit-buttons">
          <button @click="saveEdit(post, index)">保存</button>
          <button @click="cancelEdit(post, index)">取消</button>
        </div>
      </div>
    </div>
    <Footer />

    <!-- 放大图片的模态框 -->
    <div v-if="showImage" class="image-modal" @click="closeImage">
      <img :src="currentImage" alt="放大图片" class="modal-image" />
    </div>
  </div>
</template>

<script setup>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import AddIcon from "/@/assets/images/icon-add.svg";
import AvatarIcon from "/@/assets/images/avatar.jpg";
import LikeDownIcon from '/@/assets/images/icon-like-down.svg';
import LikeUpIcon from '/@/assets/images/icon-like-up2.svg';
import replyIcon from '/@/assets/images/reply-icon.svg';
import { ref, onMounted } from "vue";
import { message } from 'ant-design-vue';
import { listDynamicApi, createApi, likeDynamicApi, deleteDynamicApi } from '/@/api/dynamic';
import { dynamicListByIdApi, updateDynamicApi } from '/@/api/dynamic';

import { BASE_URL } from '/@/store/constants';
import { useUserStore } from '/@/store';
import { detailApi } from '/@/api/user';
import { useRouter } from 'vue-router';
import DynamicSend from './user/dynamic-send.vue'; // 引入动态发布组件
import {
  thingDeleteCommentApi,
  updateCommentApi,
  listDynamicCommentsApi,
  listRepliesApi,
  createDetailCommentApi as createCommentApi, likeApi
} from '/@/api/comment';


const router = useRouter();
const userStore = useUserStore();
// 从 userStore 中获取当前用户的 userId
const userId = userStore.user_id;

const posts = ref([]);
const showMoreOptions = ref(null); // 用于控制更多操作的显示
const showDynamicSend = ref(false); // 控制动态发布组件的显示与隐藏

const isEditing = ref([]); // 用于记录每个动态是否处于编辑状态

const showImage = ref(false); // 控制放大图片的显示状态
const currentImage = ref(''); // 当前放大的图片
const hoveredImageIndex = ref(null); // 当前悬停的图片索引

const showCommentBox = ref(null); // 用于控制评论框的显示
const commentContent = ref(''); // 评论内容
let thingId = ref('');

// 切换动态发布组件的显示状态
const toggleDynamicSend = () => {
  showDynamicSend.value = !showDynamicSend.value; // 切换布尔值
};

const fetchPosts = async () => {
  try {
    const response = await listDynamicApi({ userId: userId });
    if (response.code === 200) {
      posts.value = await Promise.all(response.data.map(async (post) => {
        if (post.images) {
         // 确保 post.images 是一个数组
         if (typeof post.images === 'string') {
            //post.images = post.images.split(',').map(img => `${BASE_URL}/api/staticfiles/dynamic/${img}`);
            post.images = post.images.split(',').map(img => `${img}`);
          }
        }
        // 格式化发布时间        
        post.createdAt = new Date(post.createdAt).toLocaleString();
        post.updatedAt = new Date(post.updatedAt).toLocaleString();
        //post.userId 找对应id的头像等信息
        const userResponse = await detailApi({ userId: post.userId });
        if (userResponse.code === 200 && userResponse.data) {
          post.avatar = userResponse.data.avatar || AvatarIcon;
          post.user_name = userResponse.data.username || '匿名用户';
        } else {
          post.avatar = AvatarIcon;
          post.user_name = '匿名用户';
        }
        // 初始化评论列表
        post.comments = [];
        return post;
      }));
      // 获取每个动态的评论列表
      for (const post of posts.value) {
        const commentsResponse = await listDynamicCommentsApi({ dynamicId: post.id });
        if (commentsResponse.code === 200) {

          post.comments = commentsResponse.data;
        }
      }


    } else {
      message.error("获取动态列表失败：" + response.message);
    }
  } catch (error) {
    console.error("获取动态列表失败", error);
    message.error("获取动态列表失败：" + error.message);
  }
};

onMounted(() => {
  fetchPosts();
});

const getAvatarUrl = (avatar) => {
  return `${BASE_URL}/api/staticfiles/avatar/${avatar}`;
};
const getPostImgUrl = (postImg) => {
  return `${BASE_URL}/api/staticfiles/dynamic/${postImg}`;
};

const toggleMoreOptions = (index) => {
  showMoreOptions.value = showMoreOptions.value === index ? null : index;
};

const deletePost = async (post) => {
  try {
    const response = await deleteDynamicApi({ id: post.id });
    if (response.code === 200) {
      message.success("动态删除成功");
      await fetchPosts();
    } else {
      message.error("动态删除失败：" + response.message);
    }
  } catch (error) {
    console.error("动态删除失败", error);
    message.error("动态删除失败：" + error.message);
  }
};



// 编辑图片
const editPost = (post, index) => {
  isEditing.value[index] = true; // 设置当前动态为编辑状态
  showMoreOptions.value = null; // 关闭更多操作弹出层
  console.log("编辑动态", post);
};


// 打开图片
const openImage = (img) => {
  currentImage.value = img; // 设置当前放大的图片
  showImage.value = true; // 显示放大图片
};

// 关闭图片
const closeImage = () => {
  showImage.value = false; // 隐藏放大图片
};

// 鼠标悬停图片
const hoverImage = (imgIndex) => {
  hoveredImageIndex.value = imgIndex; // 设置当前悬停的图片索引
};

// 鼠标移出图片
const resetHover = () => {
  hoveredImageIndex.value = null; // 重置悬停的图片索引
};



//编辑后，保存动态
const saveEdit = async (post, index) => {
  try {
    const formData = new FormData();
    formData.append("id", post.id);
    formData.append("title", post.title);
    formData.append("content", post.content);
    // 处理上传图片
    if (post.images) {
      post.images.forEach((img) => {
        if (img.file) { // 新
          formData.append("imagesFile", img.file);
        } else { // 旧
          formData.append("imagesUrl", img); // 后端支持接收图片路径
        }
      });
    }
    // 处理被删除的图片路径
    if (removedImages.value.length > 0) {
      removedImages.value.forEach((img) => {
        formData.append("removedImages", img); // 将被删除的图片路径传递到后端
      });
    }

    const response = await updateDynamicApi(formData);
    if (response.code === 200) {
      message.success("动态更新成功");
      isEditing.value[index] = false; // 退出编辑状态
      removedImages.value = []; // 清空被删除的图片路径数组

      // 更新当前动态的图片路径
      if (response.data && response.data.images) {
        //post.images = response.data.images.split(',').map(img => `${BASE_URL}/api/staticfiles/dynamic/${img}`);
        post.images = response.data.images.split(',').map(img => `${img}`);
        console.log("更新后的图片路径:", post.images);
      }
      // 更新当前动态的更新时间
      if (response.data && response.data.updatedAt) {
        post.updatedAt = new Date(response.data.updatedAt).toLocaleString();
        console.log("更新后的时间:", post.updatedAt);
      }

    } else {
      message.error("动态更新失败：" + response.message);
    }
  } catch (error) {
    console.error("动态更新失败", error);
    message.error("动态更新失败：" + error.message);
  }
};

const cancelEdit = (post, index) => {
  fetchPosts(); // 重新获取动态列表，恢复原始状态
  isEditing.value[index] = false; // 退出编辑状态
};

const removedImages = ref([]); // 用于记录被删除的图片路径

const removeImage = (post, imgIndex) => {
  //post.images.splice(imgIndex, 1); //删除编辑的图片
  const removedImg = post.images.splice(imgIndex, 1); // 删除图片并获取被删除的图片路径
  removedImages.value.push(removedImg[0]); // 将被删除的图片路径加入数组
};

const triggerUpload = (post) => {
  const fileInput = document.getElementById(`file-input-${post.id}`);
  if (fileInput) {
    fileInput.click();
  }
};

const handleImageUpload = (post, event) => {
  const files = event.target.files;
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader();
    reader.onload = (e) => {
      post.images.push({
        file: file, //文件对象
        preview: e.target.result, // 这里是图片的 Base64 预览
      });
    };
    reader.readAsDataURL(file);
  }
  event.target.value = null;
};

// 动态里的点赞
/* const likeDynamic2 = (post) => {
  console.log("点赞的动态ID:", post.id); // 打印动态ID
  likeDynamicApi({ id: post.id })
    .then((res) => {
      fetchPosts();//刷新动态
    })
    .catch((err) => {
      console.log(err);
    });
}; */

const likeDynamic = (post) => {
  //console.log("点赞的动态ID:", post.id); // 打印动态ID
  likeDynamicApi({ id: post.id })
    .then((res) => {
      if (res.code === 200) {
        const postIndex = posts.value.findIndex(p => p.id === post.id);
        if (postIndex !== -1) {
          //posts.value[postIndex].likeCount += 1; // 直接更新点赞数
          // 确保点赞数是数字类型,并直接更新点赞数
          posts.value[postIndex].likeCount = Number(posts.value[postIndex].likeCount) + 1;
        }
        message.success("点赞成功");
      } else {
        message.error("点赞失败：" + res.message);
      }
    })
    .catch((err) => {
      console.error("点赞失败", err);
      message.error("点赞失败：" + err.message);
    });
};


//评论
const toggleCommentBox = (index) => {
  showCommentBox.value = showCommentBox.value === index ? null : index;
};
const sendComment = async (dynamicId) => {
  const content = commentContent.value.trim();
  if (content.length === 0) {
    message.warn("评论内容不能为空！");
    return;
  }
  try {
    const response = await createCommentApi({ dynamicId, content, userId });
    if (response.code === 200) {
      message.success("评论成功");
      commentContent.value = ''; // 清空输入框
      await fetchPosts(); // 刷新动态列表
    } else {
      message.error("评论失败：" + response.message);
    }
  } catch (error) {
    console.error("评论失败", error);
    message.error("评论失败：" + error.message);
  }
};


</script>

<style lang="less" scoped>
.container {
  max-width: 85%;
  margin: 20px auto;
  padding: 20px;
  background-color: #faf7e4;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.dynamic-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  /* 设置为两列 */
  gap: 20px;
  /* 列间距 */
}

.no-dynamics {
  text-align: center;
  font-size: 18px;
  color: #666;
  margin-top: 20px;
}

.post {
  border-bottom: 1px solid #ddd;
  padding-bottom: 20px;
  margin-bottom: 20px;
}

.post-box {
  width: 90%;
  background-color: #ffffff;
  padding: 15px;
  margin: auto;
  border-radius: 8px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
  position: relative;
}

.user-info-container {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;

  .avatar-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.user-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.title {
  font-size: 14px;
  font-weight: bold;
  color: #007bff;
  margin-bottom: 10px;
}

.content {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 20px;
}

.image-list {
  margin-top: 5px;

  img {
    max-width: 100px;
    max-height: 100px;
    border-radius: 8px;    
    margin-right: 10px;
    margin-bottom: 5px;
    transition: transform 0.3s ease;
    /* 添加悬停效果 */
    cursor: pointer;
    /* 鼠标指针样式 */
  }

  img:hover {
    transform: scale(1.1);
    /* 鼠标悬停时放大图片 */
  }
}

/* 动态操作按钮 */
.post-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;

  .action-button {
    background: none;
    border: none;
    cursor: pointer;
    margin-left: 10px;

    .action-icon {
      width: 24px;
      height: 24px;
    }
  }


  // 评论样式
  .comment-section {
    margin-top: 10px;
    background-color: #f6f9fb;
    padding: 10px;
    border-radius: 8px;
  }

  .comment-input {
    display: flex;
    align-items: center;
    margin-bottom: 10px;

    .comment-input-field {
      flex: 1;
      background: #ffffff;
      border-radius: 4px;
      min-width: 100px;
      height: 32px;
      line-height: 32px;
      color: #484848;
      padding: 5px 12px;
      white-space: nowrap;
      outline: none;
      border: 1px solid #ccc;
    }

    .comment-send-btn {
      margin-left: 10px;
      width: 80px;
      background: #4684e2;
      border-radius: 4px;
      color: #fff;
      font-size: 14px;
      text-align: center;
      height: 32px;
      line-height: 32px;
      outline: none;
      border: 0px;
      cursor: pointer;
    }
  }

  .comments-list {
    .comment-item {
      display: flex;
      align-items: flex-start;
      margin-bottom: 10px;

      .comment-user-info {
        display: flex;
        align-items: center;
        margin-right: 10px;

        .comment-avatar {
          width: 32px;
          height: 32px;
          border-radius: 16px;
        }

        .comment-name {
          margin-left: 5px;
          font-weight: 600;
          font-size: 14px;
          color: #152844;
        }
      }

      .comment-content {
        flex: 1;
        font-size: 14px;
        color: #484848;
      }
    }
  }

}

/* 新增更多按钮和操作样式 */
.more-button-container {
  position: absolute;
  right: 15px;
  top: 15px;
  display: inline-block;
  /* 使容器能够包裹子元素 */
}

.more-button {
  padding: 5px 10px;
  font-size: 14px;
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.more-button:hover {
  background-color: #e0e0e0;
}

.more-options {
  position: absolute;
  right: 0;
  /* 子菜单靠右对齐 */
  top: 100%;
  /* 子菜单在按钮下方 */
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 10;
  padding: 5px;
  display: flex;
  flex-direction: column;
}

.option-button {
  padding: 5px 10px;
  width: 60px;
  font-size: 14px;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 5px;
  transition: background-color 0.3s;
}

.option-button:hover {
  background-color: #e0e0e0;
}

/* 添加发布按钮样式 */
.publish-button {
  display: block;
  margin: 20px auto;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.publish-button:hover {
  background-color: #0056b3;
}

/* 放大图片的模态框样式 */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-image {
  max-width: 80%;
  max-height: 80%;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}


/* 添加编辑动态样式 */
.edit-title,
.edit-content {
  width: 100%;
  border: 1px solid #ccc;
  padding: 5px;
  margin-bottom: 5px;
  box-sizing: border-box;
}

.edit-buttons button {
  margin-left: 35px;
  padding: 5px 10px;
  font-size: 14px;
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;

}
/* 上传图片样式 */
.image-upload-container {
  display: flex;
  flex-wrap: wrap;

  .image-upload {
    position: relative;
    margin-right: 10px;

    .image-preview {
      width: 100px;
      height: 100px;
      object-fit: cover;
      border-radius: 8px;
    }

    .remove-button {
      position: absolute;
      top: -10px;
      right: -10px;
      padding: 5px;
      background-color: red;
      color: white;
      border: none;
      border-radius: 50%;
      cursor: pointer;
    }
  }

  .upload-button {
    width: 100px;
    height: 100px;
    background-color: #f2f2f2;
    border: 1px dashed #ccc;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    border-radius: 8px;
    position: relative;

    .upload-icon {
      width: 40px;
      height: 40px;
      object-fit: contain;
      z-index: 1;
    }
  }
}
</style>