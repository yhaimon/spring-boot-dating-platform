<template>
    <div class="chat-container">
        <div class="user-list">
            <div class="user-list-header">
                <img :src="OnlineUsersIconIMG" alt="聊天" class="online-img" />
                <span>在线用户</span>
                <span class="hint">（点击气泡开始聊天）</span>
            </div>
            <div class="user-item" v-for="user in users" :key="user" @click="selectChatUser(user)">
                <!-- <img :src="`{{user.avatar }}`" alt="Avatar" class="user-avatar" /> -->
                <!--没用 <img :src="BASE_URL + '/api/staticfiles/avatar/' + user.avatar" alt="Avatar" class="user-avatar" /> -->
                <img :src="getAvatarUrl(user.avatar)" alt="Avatar" class="user-avatar" />
                <span>（{{ user.user_id }}）</span>
                <span>{{ user.user_name }}</span>
                <i class="el-icon-chat-dot-round"><img :src="IconDotIMG1" alt="聊天" class="online-img" /></i>
                <span class="chatting-indicator" v-if="user.user_id === chatUser.user_id">
                    chatting...
                </span>
            </div>
        </div>
        <div class="chat-box">
            <!-- <div class="chat-header"> -->
            <!-- 动态绑定 chat-header 的背景色 -->
            <div class="chat-header" :style="{ backgroundColor: chatUser.user_id ? '#bddef7' : '#cccbcb' }">
                <img :src="IconDotIMG" alt="在线" class="online-img" v-if="chatUser.user_id" />
                <span v-if="!chatUser.user_id" class="not-chat">请选择聊天对象！</span>
                <span v-if="chatUser.user_id">与 {{ chatUser.user_name }} 聊天</span>
            </div>
            <div class="chat-content" v-html="content"
                :style="{ backgroundColor: chatUser.user_id ? '#f2feff' : '#e4e1e1' }"></div>
            <div class="chat-input">
                <textarea v-model="text" placeholder="输入消息..."></textarea>
                <button @click="send" :style="{ backgroundColor: chatUser.user_id ? '#409eff' : '#b8b7b7' }">发送</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { io } from "socket.io-client";
import { useUserStore } from "/@/store";
import { ref, onMounted, onUnmounted } from "vue";
// import { ElMessage } from "element-plus";
import { message } from "ant-design-vue";
import AvatarIcon from "/@/assets/images/avatar.jpg";
// import { getUserBasicInfoApi } from '/@/api/user';
import { BASE_URL } from "/@/store/constants";
import OnlineUsersIconIMG from "/@/assets/images/online-users-icon.svg";
import IconDotIMG from "/@/assets/images/icon_dot-green.svg";
import IconDotIMG1 from "/@/assets/images/icon_dot-blue.svg";
import IconDotIMG2 from "/@/assets/images/icon_dot2.svg";
import { detailApi } from '/@/api/user';
// const avatarUrl = ref(AvatarIcon);
const userStore = useUserStore();
const user = userStore.getUserInfo; // 我有一个方法从 store 中获取用户信息
const users = ref([]); // 在线用户列表
const chatUser = ref(""); // 当前聊天对象
const text = ref(""); // 输入的聊天内容
const content = ref(""); // 聊天内容的HTML
const loading = ref(true);

// 初始化 socket 连接
const socket = io("http://localhost:9090", {
    query: {
        user_name: user.user_name,
        user_id: user.user_id, // 添加用户 ID
        avatar: user.avatar // 添加用户头像
    },
    reconnection: true, // 启用自动重连
    reconnectionDelay: 5000, // 重连间隔时间（毫秒）
    reconnectionAttempts: 3 // 最大重连尝试次数
});

// 监听连接事件
onMounted(() => {
    socket.on("connect", () => {
        console.log("已连接到服务器");
    });

    //这种方式获取的头像，还是老头像，不是最新头像 
    // socket.on("updateUsers", (data) => {
    //     users.value = data.filter((u) => u.user_id !== user.user_id);
    //     //users.value = data; // 更新在线用户列表
    //     console.log("在线用户列表更新:", users.value);
    //     // 打印每个用户的 ID、用户名和头像
    //     users.value.forEach(u => {
    //         console.log(`用户ID: ${u.user_id}, 用户名: ${u.user_name}, 头像: ${u.avatar}`);
    //     });        
    // });

    socket.on("updateUsers", async (data) => {
  users.value = await Promise.all(data.filter(u => u.user_id !== user.user_id).map(async (u) => {
    const userResponse = await detailApi({ userId: u.user_id });
    if (userResponse.code === 200 && userResponse.data) {
      console.log("detailApi 返回的数据:", userResponse.data); // 打印返回的数据
      return {
        user_id: userResponse.data.id, // 后端返回的字段是 id
        user_name: userResponse.data.username, // 后端返回的字段是 username
        avatar: userResponse.data.avatar,
        nickname: userResponse.data.nickname,
      };
    }
    return u;
  }));
  console.log("在线用户列表更新:", users.value);
});

 

    //前端接收消息
    socket.on("getMessage", (data) => {
        //if (data.from === chatUser.value) {
        // 检查消息是否来自当前聊天对象
        if (data.from === chatUser.value.user_id) {
            // 如果是当前聊天对象，显示消息
            createContent(data.from, null, data.text);
        } else {
            // 如果不是当前聊天对象，显示通知
            message(`${data.from} 发来了一条新消息`);
            //ElMessage.info(`${data.from} 发来了一条新消息`);
        }
    });

    socket.on("disconnect", () => {
        console.log("已断开连接");
    });

    socket.on("connect_error", (error) => {
        console.error("连接失败:", error);
        message.error("连接失败，请稍后重试");
        // ElMessage.error("连接失败，请稍后重试");
    });
    // getUserBasicInfo();
});

// 发送消息
const send = () => {
    if (!chatUser.value) {
        message.warn("请选择聊天对象");
        return;
    }
    if (!text.value) {
        message.warn("请输入内容");
        return;
    }

    socket.emit("sendMessage", {
        //to: chatUser.value,
        to: chatUser.value.user_id,// 确保这里传递的是用户ID
        text: text.value,
    });

    createContent(null, user.user_id, text.value);// 确保发送给正确的用户ID
    text.value = "";
};

// 创建聊天内容的HTML// <img :src="avatarUrl" style="object-fit: cover;" /> ---错误写法<img :src="BASE_URL + '/api/staticfiles/avatar/' + user.avatar" style="object-fit: cover;" />
const createContent = (remoteUser, nowUser, text) => {
    let html;
    if (nowUser) {// 当前用户发送的消息
        html = `
            <div class="message-item self">
              <div class="bubble left">${text}</div>
              <div class="avatar"> 
                <img src="${getAvatarUrl(user.avatar)}" alt="Self Avatar" />            
              </div>
            </div>
          `;
    } else if (remoteUser) {// 对方发送的消息
        const remoteUserInfo = users.value.find(u => u.user_id === remoteUser);
        const avatarUrl = remoteUserInfo ? getAvatarUrl(remoteUserInfo.avatar) : "";
        html = `
            <div class="message-item other">
              <div class="avatar">               
                <img src="${avatarUrl}" alt="Other Avatar" />
              </div>
              <div class="bubble right">${text}</div>
            </div>
          `;
    }
    content.value += html;
    // document.getElementById('messages').innerHTML += html;

};

// 选择聊天对象
const selectChatUser = (user) => {
    //chatUser.value = user_name;
    chatUser.value = user; // 确保 chatUser 是一个对象

};
// 获取用户信息
// const getUserBasicInfo = async () => {
//     try {
//         let userId = userStore.user_id;
//         if (userId) {
//             const res = await getUserBasicInfoApi({ userId: userId });
//             if (res.data.avatar) {
//                 avatarUrl.value = BASE_URL + '/api/staticfiles/avatar/' + res.data.avatar;
//             }
//         }
//     } catch (err) {
//         console.error(err);
//     } finally {
//         loading.value = false;
//     }
// };

// 获取头像 URL
const getAvatarUrl = (avatar) => {
    return `${BASE_URL}/api/staticfiles/avatar/${avatar}`;
};

// 在组件卸载时关闭 socket 连接
onUnmounted(() => {
    socket.disconnect();
});
</script>
<style scoped lang="less">
.chat-container {

    width: 100%;
    margin: auto;
    max-width: 1200px;
    background-color: #ffffff;
    display: flex;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

    .user-list {
        width: 30%;
        min-height: 600px;
        color: #333;
        background-color: #fff;
        border-radius: 8px 0 0 8px;
        overflow-y: auto;

        .user-list-header {
            padding: 10px;
            border-bottom: 1px solid #ccc;
            font-size: 16px;
            font-weight: bold;

            .online-img {
                height: 35px;
                width: 35px;
            }

            .hint {
                font-size: 12px;
                color: #999;
            }
        }

        .user-item {
            padding: 10px;
            border-bottom: 1px solid #f0f0f0;
            display: flex;
            align-items: center;
            cursor: pointer;

            &:last-child {
                border-bottom: none;
            }

            .user-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                margin-right: 10px;
                object-fit: cover;
            }

            .el-icon-chat-dot-round {
                margin-left: auto;
                cursor: pointer;
                color: #409eff;

                .online-img {
                    height: 20px;
                    width: 20px;
                }
            }

            .chatting-indicator {
                font-size: 12px;
                color: limegreen;
                margin-left: 5px;
            }
        }
    }

    .chat-box {
        width: 70%;
        background-color: #fff;
        border-radius: 0 8px 8px 0;
        overflow: hidden;

        .chat-header {
            text-align: center;
            line-height: 50px;
            font-size: 18px;
            font-weight: bold;
            background-color: #bddef7;
            border-bottom: 1px solid #a9d4fc;

            .online-img {
                height: 25px;
                width: 25px;
            }

            .not-chat {
                color: rgb(255, 92, 92);
            }
        }

        .chat-content {
            height: 400px;
            overflow-y: auto;
            padding: 10px;
            background-color: #f2feff;

            :deep(.message-item) {
                display: flex;
                margin: 10px 10px;

                .avatar {
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                    overflow: hidden;
                    margin: 0 10px;

                    img {
                        width: 100% !important;
                        height: 100% !important;
                        object-fit: cover;
                        /* 确保图片按比例缩放并覆盖容器 */
                        /* 确保图片按比例缩放 */
                    }
                }

                .bubble {
                    max-width: 60%;
                    padding: 5px 10px;
                    border-radius: 10px;
                    font-size: 14px;
                    color: white;
                    margin: 0 10px;

                    &.left {
                        background-color: #007bff;
                    }

                    &.right {
                        background-color: #28a745;
                    }
                }

                &.self {
                    justify-content: flex-end;
                }

                &.other {
                    justify-content: flex-start;
                }
            }
        }

        .chat-input {
            padding: 10px;
            display: flex;
            border-top: 1px solid #ccc;

            textarea {
                flex: 1;
                height: 100px;
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                resize: none;
                outline: none;
            }

            button {
                margin-left: 10px;
                padding: 5px 15px;
                background-color: #409eff;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;

                &:hover {
                    background-color: #357ae8;
                }
            }
        }
    }
}
</style>