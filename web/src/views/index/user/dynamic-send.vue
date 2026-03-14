<template>
    <div class="container">
      <!-- 发布动态的标题 -->
      <h2>发布动态</h2>
      <!-- 表单区域，用于提交动态 -->
      <form @submit.prevent="submitPost" class="post-form">
        <!-- 标题输入框 -->
        <div class="form-group">
          <label for="title">标题</label>
          <input type="text" id="title" v-model="post.title" required />
        </div>
        <!-- 内容输入框 -->
        <div class="form-group">
          <label for="content">内容</label>
          <textarea id="content" v-model="post.content" required></textarea>
        </div>
        <!-- 图片上传区域 -->
        <div class="form-group">
          <label>上传图片</label>
          <div class="image-upload-container">
            <!-- 遍历post.images，显示已上传的图片 -->
            <div v-for="(image, index) in post.images" :key="index" class="image-upload">
              <img :src="image.preview" alt="预览" class="image-preview" />
              <!-- 删除按钮，用于移除已上传的图片 -->
              <button type="button" @click="removeImage(index)" class="remove-button">删除</button>
            </div>
            <!-- 上传按钮 -->
            <div class="upload-button" @click="triggerUpload">
              <img :src="AddIcon" alt="上传" class="upload-icon" />
            </div>
            <!-- 实际的文件输入框 -->
            <!-- <input type="file" id="image" @change="handleImageUpload" multiple style="display: none" /> -->
            <input type="file" :id="fileInputId" @change="handleImageUpload" multiple style="display: none" />        </div>
        </div>
        <!-- 提交按钮 -->
        <button type="submit">发布</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import { message } from 'ant-design-vue';
  import { createApi } from '/@/api/dynamic';
  import { useUserStore } from '/@/store';
  import AddIcon from "/@/assets/images/icon-add.svg";
  

  const emit = defineEmits(); // 定义 emit
  const userStore = useUserStore();
  const post = ref({
    title: "",
    content: "",
    images: [], // 存储多张图片的信息
  });
  
// 动态生成文件输入框的 id，确保唯一性
// 动态生成文件输入框的 id，确保唯一性
const fileInputId = ref('file-input-' + Math.random().toString(36).substr(2, 9));

  const handleImageUpload = (event) => {
    const files = event.target.files;
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const reader = new FileReader();
      reader.onload = (e) => {
        post.value.images.push({
          file: file, // 存储文件对象
          preview: e.target.result, // 存储图片预览的 Base64 数据
        });
      };
      reader.readAsDataURL(file);
    }
    // 重置文件输入框，以便可以重新选择图片
  event.target.value = null;
  };
  
/*   const triggerUpload = () => {
    document.getElementById("image").click();
  }; */
  // 触发文件输入框的点击事件
  const triggerUpload = () => {
    const fileInput = document.getElementById(fileInputId.value);
    if (fileInput) {
        fileInput.click();
    }
};
  
  const removeImage = (index) => {
    post.value.images.splice(index, 1);
  };
  
  const submitPost = async () => {
    const formData = new FormData();
    formData.append("title", post.value.title);
    formData.append("content", post.value.content);
    formData.append("userId", userStore.user_id); // 添加用户 ID 到表单数据中
    // 添加图片文件
    post.value.images.forEach((img) => {
      formData.append("imagesFile", img.file);
    });
  
    try {
      const response = await createApi(formData);
      if (response.code === 200) { // 后端返回的代码为200表示成功
        message.success("动态发布成功");
        emit('postSubmitted'); // 触发事件
        resetForm();
      } else {
        message.error("动态发布失败：" + response.message);
      }
    } catch (error) {
      console.error("动态发布失败", error);
      message.error("动态发布失败：" + error.message);
    }
  };
  
  const resetForm = () => {
    post.value.title = "";
    post.value.content = "";
    post.value.images = [];
  };
  </script>
  
  <style lang="less" scoped>
  .container {
    width: 80%;
    margin: 20px auto;
    padding: 20px;
    background-color: #fff;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  }
  
  .post-form {
    width: 80%;
    margin: auto;
    margin-bottom: 40px;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
  }
  
  .form-group input,
  .form-group textarea {
    width: 100%;
    padding: 8px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  
  button {
    padding: 10px 20px;
    font-size: 16px;
    background-color: #007bff;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #0056b3;
  }
  
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