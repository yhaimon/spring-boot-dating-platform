<template>
  <div class="detail">
    <Header />
    <div class="detail-content">
      <div class="detail-content-top">
        <img class="top-left-img" :src="detailData.cover" />
        <div class="top-right">
          <div class="title">
            <div class="tag">有效会员·{{ detailData.id }}
            </div>

            <div class="right-box avatar-box flex-view">
              用户头像：
              <img v-if="uData.form && uData.form.avatar" :src="uData.form.avatar" class="avatar" />
              <img v-else :src="AvatarIcon" class="avatar" />
              用户id：{{ detailData.userId }}
              用户名：{{ uData.form.username }}
              昵称：{{ uData.form.nickname }}
            </div>

            <div class="title-value">
              真实姓名：
              {{ detailData.title }}
            </div>
          </div>
          <!-- {{ uData }} -->

          <div class="score">
            <div class="pv">{{ detailData.pv }}次浏览</div>
            <a-rate :value="detailData.rate" style="font-size: 16px" @change="clickRate" />
          </div>
          <div class="meta">性别：{{ detailData.sex }}</div>
          <div class="meta">年龄：{{ detailData.age }}</div>
          <div class="meta">身高：{{ detailData.shengao }}</div>
          <div class="meta">地区：{{ detailData.address }}</div>
          <div class="meta">职业：{{ detailData.zhiye }}</div>
          <div class="meta">车房情况：{{ detailData.chefang }}</div>
          <div class="actions">
            <button class="buy-btn" @click="handleOrder(detailData)">
              <img :src="AddIcon" />
              <span>申请约会</span>
            </button>

            <!-- 添加聊天室 -->
            <button class="buy-btn" @click="handleChat(detailData)">
              <img :src="chatInfoImg" />
              <span>在线聊天</span>

            </button>

            <div class="more">
              <div class="btn-like" @click="addToWish()">
                <img :src="LikeUpIcon" />
                <div class="label">点赞（{{ detailData.wishCount }}）</div>
              </div>
              <div class="btn-like" @click="collect()">
                <img :src="CollectUpIcon" />
                <div class="label">收藏（{{ detailData.collectCount }}）</div>
              </div>
              <div class="btn-like" @click="share()">
                <img :src="ShareIcon" />
                <div class="label">分享</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="detail-content-bottom">
        <div class="thing-content-view">
          <div class="main-content">
            <a-tabs v-model:activeKey="selectTabIndex">
              <a-tab-pane key="1" tab="简介">
                <!--简介-->
                <div class="thing-intro" :class="selectTabIndex <= 0 ? '' : 'hide'">
                  <p class="text" style=""> {{ detailData.description }}</p>
                </div>
              </a-tab-pane>
              <a-tab-pane key="2" tab="评论" force-render>
                <!--评论-->
                <div class="thing-comment" :class="selectTabIndex > 0 ? '' : 'hide'">
                  <div class="title">发表新的评论</div>
                  <div class="publish flex-view">
                    <img :src="AvatarIcon" class="mine-img" />
                    <input placeholder="说点什么..." class="content-input" ref="commentRef" />
                    <button class="send-btn" @click="sendComment()">发送</button>
                  </div>
                  <div class="tab-view flex-view">
                    <div class="count-text">共有{{ commentData.length }}条评论</div>
                    <div class="tab-box flex-view" v-if="commentData.length > 0">
                      <span :class="sortIndex === 0 ? 'tab-select' : ''" @click="sortCommentList('recent')">最新</span>
                      <div class="line"></div>
                      <span :class="sortIndex === 1 ? 'tab-select' : ''" @click="sortCommentList('hot')">热门</span>
                    </div>
                  </div>
                  <div class="comments-list">
                    <div class="comment-item" v-for="item in commentData" :key="item.id">
                      <div class="flex-item flex-view">
                        <img :src="item.cover ? item.cover : AvatarIcon" class="avator" />
                        <div class="person">
                          <div class="name">{{ item.username }}</div>
                          <div class="time">{{ item.commentTime }}</div>
                        </div>
                        <div class="float-right">
                          <span @click="like(item.id)">推荐</span>
                          <span class="num">{{ item.likeCount }}</span>
                          <!-- 编辑按钮，只有评论作者才能看到 -->
                          <span v-if="item.userId === userStore.user_id" @click="editComment(item)">编辑</span>
                          <!-- 删除按钮，只有评论的作者才能看到 -->
                          <span v-if="item.userId === userStore.user_id" @click="deleteComment(item.id)">删除</span>
                        </div>
                      </div>
                      <p class="comment-content">{{ item.content }}</p>
                      <!-- // ******** mhy优化：修改后，添加了，回复的评论ID的查询   -->
                      <!-- 优化回复和展开按钮的样式，增加明显间隔 -->
                      <div class="reply-section">
                        <button class="reply-btn" @click="toggleReplyBox(item.id)">
                          <img :src="replyIcom" class="commont-img" />
                          回复
                        </button>
                        <!-- 增加间隔，使用margin或padding -->
                        <div v-if="replyBoxId === item.id" class="reply-input">
                          <input placeholder="回复..." class="reply-content-input" v-model="replyContent" />
                          <button class="send-reply-btn" @click="sendReply(item.id)">发送</button>
                        </div>
                        <!-- 优化：只有当回复列表不为空时，才显示展开回复按钮 -->
                        <button v-if="!item.isReplyExpanded && item.replies.length > 0" class="expand-btn"
                          @click="loadReplies(item.id, item)">
                          <img :src="downIcon" class="commont-img" />
                          展开回复（{{ item.replies.length }}）
                        </button>
                        <!-- 收起回复按钮 -->
                        <button v-else-if="item.isReplyExpanded" class="collapse-btn" @click="collapseReplies(item)">
                          <img :src="upIcon" class="commont-img" />
                          收起回复（{{ item.replies.length }}）
                        </button>
                        <!-- 显示回复列表  -->
                        <div v-if="item.isReplyExpanded" class="replies-list">
                          <div class="reply-item" v-for="reply in item.replies" :key="reply.id">
                            <div class="flex-item flex-view">
                              <img :src="reply.cover ? reply.cover : AvatarIcon" class="avator" />
                              <div class="person">
                                <div class="name">{{ reply.username }}</div>
                                <div class="time">{{ reply.commentTime }}</div>
                              </div>
                              <!--  优化: 给回复里面也添加推荐效果 -->
                              <div class="float-right">
                                <span @click="likeReply(reply.id)">推荐</span>
                                <span class="num">{{ reply.likeCount }}</span>
                                <!-- 编辑按钮，只有回复作者才能看到 -->
                                <span v-if="reply.userId === userStore.user_id" @click="editReply(reply)">编辑</span>
                                <!-- 删除按钮，只有回复作者才能看到 -->
                                <span v-if="reply.userId === userStore.user_id" @click="deleteReply(reply.id)">删除</span>
                              </div>
                            </div>

                            <p class="comment-content">{{ reply.content }}</p>

                          </div>
                        </div>

                      </div>
                      <!-- ******** -->
                    </div>
                    <div class="infinite-loading-container">
                      <div class="infinite-status-prompt" style="">
                        <div slot="no-results" class="no-results">
                          <div></div>
                          <p>没有更多了</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

              </a-tab-pane>
            </a-tabs>
          </div>
          <div class="main-side">
            <div class="recommend">
              <div class="title">相关推荐</div>
              <div class="things">
                <div class="thing-item" v-for="item in recommendData" @click="handleDetail(item)">
                  <div class="img-view"> <img :src="item.cover" /></div>
                  <div class="info-view">
                    <h3 class="thing-name">{{ item.title.substring(0, 12) }}</h3>
                    <span>
                      <span class="a-price-symbol"></span>
                      <span class="a-price">{{ item.age }}</span>&nbsp;
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <div class="recommend" style="margin-top: 20px">
              <div class="title">广告</div>
              <div class="things">
                <div class="thing-item thing-item" v-for="item in adData" @click="clickAd(item)">
                  <div class="img-view"> <img :src="item.cover" /></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>
<script setup>
import { h, ref } from 'vue';
import { Modal, message } from 'ant-design-vue';
import { Input as AInput } from 'ant-design-vue';
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import AddIcon from '/@/assets/images/add.svg';
import WantIcon from '/@/assets/images/want-read-hover.png';
import LikeUpIcon from '/@/assets/images/icon-like-up3.svg';
import CollectUpIcon from '/@/assets/images/icon-collect-up.svg';
import ShareIcon from '/@/assets/images/share-icon.svg';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import replyIcom from '/@/assets/images/reply-icon.svg';
import downIcon from '/@/assets/images/icon-down.svg';
import upIcon from '/@/assets/images/icon-up.svg';
import chatInfoImg from '/@/assets/images/chat-info.png';

import { detailApi, listApi as listThingList, getRecommendApi, rateApi, listUserThingApi } from '/@/api/thing';
import { detailApi as userDetailApi, getUserBasicInfoApi } from '/@/api/user';
import {
  thingDeleteCommentApi,
  updateCommentApi,
  listThingCommentsApi,
  listRepliesApi,
  createApi as createCommentApi, likeApi
} from '/@/api/comment';
import { listApi as listAdApi } from '/@/api/ad';
import { wishApi } from '/@/api/thingWish';
import { collectApi } from '/@/api/thingCollect';
import { BASE_URL } from '/@/store/constants';
import { useRoute, useRouter } from 'vue-router/dist/vue-router';
import { useUserStore } from '/@/store';
import { getFormatTime } from '/@/utils';
import { createApi } from '../../api/order';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

let thingId = ref('');
let detailData = ref({});
let selectTabIndex = ref('1');
let userId = ref('');//当前会员详情页的id
let loading = ref(false);
let uData = reactive({//用户信息
  form: {},
});

let commentData = ref([]); // 评论数据
let recommendData = ref([]); // 推荐数据
let adData = ref([]); // 广告数据
let sortIndex = ref(0);
let order = ref('recent'); // 默认排序最新

let commentRef = ref();
// ******** mhy优化 : 评论回复
let replyBoxId = ref(null); // 当前显示回复框的评论ID
let replyContent = ref(''); // 回复内容
// ********

onMounted(() => {
  thingId.value = route.query.id.trim();
  getThingDetail();
  getRecommendThing();
  getCommentList();
  getAdList();
  //getUserInfo();//获取用户信息
});
//获取详情
const getThingDetail = () => {
  detailApi({ id: thingId.value })
    .then((res) => {
      detailData.value = res.data;
      detailData.value.cover = BASE_URL + '/api/staticfiles/image/' + detailData.value.cover;
      // 在这里打印 detailData
      //console.log("detailData=========!!!!!!", detailData.value.userId);
      // 正确赋值给 userId，保持响应性
      userId.value = detailData.value.userId; // 使用 userId.value 赋值
      console.log("userId=========!!!!!!", userId.value); // 打印 userId
      getUserInfo();
    })
    .catch((err) => {
      message.error('获取详情失败');
    });
};

//获取用户信息
const getUserInfo = () => {
  loading.value = true;
  if (!userId.value) {//检查 userId.value 是否为有效值
    console.error("userId 为空，不是有效值", userId.value);
    loading.value = false;
    return;
  } else {
    console.log("userId 为有效值", userId.value);
  }

  userDetailApi({ userId: userId.value })// 使用 userId.value 获取当前值
    .then((res) => {
      uData.form = res.data;
      if (uData.form.avatar) {
        uData.form.avatar = BASE_URL + '/api/staticfiles/avatar/' + uData.form.avatar
      }
      console.log('userInfo====tData===!!!!!!!', uData)
      loading.value = false;
    })
    .catch((err) => {
      console.log(err);
      loading.value = false;
    });
};

//点赞
const addToWish = () => {
  let userId = userStore.user_id;
  if (userId) {
    wishApi({ thingId: thingId.value, userId: userId })
      .then((res) => {
        message.success(res.msg);
        getThingDetail();
      })
      .catch((err) => {
        console.log('操作失败');
      });
  } else {
    message.warn('请先登录');
  }
};
//收藏
const collect = () => {
  let userId = userStore.user_id;
  if (userId) {
    collectApi({ thingId: thingId.value, userId: userId })
      .then((res) => {
        message.success(res.msg);
        getThingDetail();
      })
      .catch((err) => {
        console.log('收藏失败');
      });
  } else {
    message.warn('请先登录');
  }
};
//分享
const share = () => {
  let content = '分享一个非常好玩的网站 ' + window.location.href;
  let shareHref = 'http://service.weibo.com/share/share.php?title=' + content;
  window.open(shareHref);
};

const handleOrder = (detailData) => {
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
            name: 'confirm',
            query: {
              fromId: myThing.id,
              id: detailData.id,
              title: detailData.title,
              cover: detailData.cover,
              sex: detailData.sex,
              age: detailData.age,
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
//******** 添加聊天室 ********** */
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

//----------- 评论
const getRecommendThing = () => {
  getRecommendApi({})
    .then((res) => {
      res.data.forEach((item, index) => {
        if (item.cover) {
          item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
        }
      });
      console.log(res);
      recommendData.value = res.data.slice(0, 6);
    })
    .catch((err) => {
      console.log(err);
    });
};

const handleDetail = (item) => {
  // 跳转新页面
  let text = router.resolve({ name: 'detail', query: { id: item.id } });
  window.open(text.href, '_blank');
};
const clickRate = (num) => {
  console.log('num:' + num);
  rateApi({ thingId: thingId.value, rate: num })
    .then((res) => {
      message.success('操作成功！');
      getThingDetail();
    })
    .catch((err) => {
      console.log(err);
    });
};
const clickAd = (item) => {
  window.open(item.link, '_blank');
};
//发送评论
const sendComment = () => {
  console.log(commentRef.value);
  let text = commentRef.value.value.trim();
  console.log(text);
  if (text.length <= 0) {
    return;
  }
  commentRef.value.value = '';
  let userId = userStore.user_id;
  if (userId) {
    createCommentApi({ content: text, thingId: thingId.value, userId: userId })
      .then((res) => {
        getCommentList();
        message.success('评论成功！');
      })
      .catch((err) => {
        console.log(err);
        message.error('评论失败，请稍后再试。');
      });
  } else {
    message.warn('请先登录！');
    router.push({ name: 'login' });
  }
};
//推荐
const like = (commentId) => {
  likeApi({ id: commentId })
    .then((res) => {
      getCommentList();
    })
    .catch((err) => {
      console.log(err);
    });
};
//用户自身删除评论
const deleteComment = (commentId) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这条评论吗？',
    onOk: () => {
      return thingDeleteCommentApi({ id: commentId })
        .then((res) => {
          message.success('删除成功');
          getCommentList(); // 刷新评论列表
        })
        .catch((err) => {
          message.error('删除失败，请稍后再试。');
        });
    },
    onCancel: () => {
      console.log('取消删除');
    }
  });
};
// 用户自身修改
const editComment = (comment) => {
  const editContent = ref(comment.content); // 创建响应式变量

  Modal.confirm({
    title: '编辑评论',
    content: () => {
      return h(AInput, {
        value: editContent.value, // 使用响应式变量
        onInput: (e) => editContent.value = e.target.value, // 更新响应式变量
        placeholder: '请输入新的评论内容'
      });
    },
    onOk: () => {
      return updateCommentApi({ id: comment.id, content: editContent.value }) // 使用响应式变量
        .then((res) => {
          message.success('更新成功');
          getCommentList(); // 刷新评论列表
        })
        .catch((err) => {
          message.error('更新失败，请稍后再试。');
        });
    },
    onCancel: () => {
      console.log('取消编辑');
    }
  });
};

/* //原 
 const getCommentList = () => {
  listThingCommentsApi({ thingId: thingId.value, order: order.value })
    .then((res) => {
      res.data.forEach((item) => {
        item.commentTime = getFormatTime(item.commentTime, true);
        if (item.cover) {
          item.cover = BASE_URL + '/api/staticfiles/avatar/' + item.cover;
        }
      });
      commentData.value = res.data;
    })
    .catch((err) => {
      console.log(err);
    });
};
 */

// ******** mhy优化： 在评论列表中再添加回复列表
const getCommentList = () => {
  return listThingCommentsApi({ thingId: thingId.value, order: order.value })
    .then((res) => {
      res.data.forEach((item) => {
        item.commentTime = getFormatTime(item.commentTime, true);
        if (item.cover) {
          item.cover = BASE_URL + '/api/staticfiles/avatar/' + item.cover;
        }
        //item.replies = []; // 初始化回复列表
        // ******** mhy优化：初始化回复列表
        item.replies = item.replies || []; // 初始化回复列表
        item.replies.forEach((reply) => {
          reply.commentTime = getFormatTime(reply.commentTime, true);
          if (reply.cover) {
            reply.cover = BASE_URL + '/api/staticfiles/avatar/' + reply.cover;
          }
        });
        item.isReplyExpanded = false; // 初始化回复展开状态
      });
      //commentData.value = res.data;
      commentData.value = res.data.filter(item => item.replyToId === null); // 只保留顶级评论
      // 过滤出 commentType 为 DetailComment 的评论
      /* commentData.value = res.data.filter(item =>
        item.replyToId === null && item.commentType === '1' //  DetailComment 的值为 '1'
      ); */
    })
    .catch((err) => {
      console.log('获取评论失败:', err);
    });
};
// ******** mhy优化：显示评论回复
const toggleReplyBox = (commentId) => {
  if (replyBoxId.value === commentId) {
    replyBoxId.value = null;
  } else {
    replyBoxId.value = commentId;
    replyContent.value = '';
  }
};
const loadReplies = (commentId, item) => {
  if (item.isReplyExpanded) {
    item.isReplyExpanded = false;
  } else {
    listRepliesApi({ replyToId: commentId })
      .then((repliesRes) => {
        //******优化：回复的头像显示和时间正确
        repliesRes.data.forEach((reply) => {
          console.log('Original time:', reply.commentTime); // 输出原始时间
          if (reply.cover) {
            reply.cover = BASE_URL + '/api/staticfiles/avatar/' + reply.cover;
          }
          reply.commentTime = getFormatTime(reply.commentTime, true); // 格式化时间
          console.log('Formatted time:', reply.commentTime); // 输出格式化后的时间
        });
        // ******
        item.replies = repliesRes.data;
        item.isReplyExpanded = true;
      })
      .catch((err) => {
        console.log(err);
      });
  }
};

// ******** mhy优化：收起评论回复
const collapseReplies = (item) => {
  item.isReplyExpanded = false;
};
//发送回复
const sendReply = (commentId) => {
  let text = replyContent.value.trim();
  if (text.length <= 0) {
    return;
  }
  let userId = userStore.user_id;
  if (userId) {
    createCommentApi({ content: text, thingId: thingId.value, userId: userId, replyToId: commentId })
      .then((res) => {
        /* // 发送回复成功后，自动展开回复列表
        commentData.value.forEach((comment) => {
          if (comment.id === commentId) {
            comment.isReplyExpanded = true; // 展开回复列表
          }
        });         
        getCommentList();// 获取评论列表 */

        // 发送回复成功后，刷新评论列表
        getCommentList().then(() => {
          // 刷新后，再次将对应的评论的 isReplyExpanded 设置为 true
          commentData.value.forEach((comment) => {
            if (comment.id === commentId) {
              comment.isReplyExpanded = true; // 展开回复列表
            }
          });
        });
        replyBoxId.value = null; // 关闭回复框
        message.success('回复成功！');
      })
      .catch((err) => {
        console.log(err);
        message.error('回复失败，请稍后再试。');
      });
  } else {
    message.warn('请先登录！');
    router.push({ name: 'login' });
  }
};

// 回复里的推荐
const likeReply = (replyId) => {
  likeApi({ id: replyId })
    .then((res) => {
      // 更新回复的点赞数
      commentData.value.forEach((comment) => {
        comment.replies.forEach((reply) => {
          if (reply.id === replyId) {
            reply.likeCount = String(Number(reply.likeCount) + 1); // 更新点赞数
          }
        });
      });
      message.success('推荐成功！');
    })
    .catch((err) => {
      console.log(err);
      message.error('推荐失败，请稍后再试。');
    });
};

// 用户自身删除回复
const deleteReply = (replyId) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这条回复吗？',
    onOk: () => {
      return thingDeleteCommentApi({ id: replyId })
        .then((res) => {
          message.success('删除成功');
          //getCommentList(); // 刷新评论列表
          // 刷新评论列表后，确保回复列表保持展开
          getCommentList().then(() => {
            const comment = commentData.value.find(comment => comment.replies.some(r => r.id === replyId));
            if (comment) {
              comment.isReplyExpanded = true; // 展开回复列表
            }
          });
        })
        .catch((err) => {
          message.error('删除失败，请稍后再试。');
        });
    },
    onCancel: () => {
      console.log('取消删除');
    }
  });
};

// 回复里用户自身的编辑
const editReply = (reply) => {
  const editContent = ref(reply.content); // 创建响应式变量

  Modal.confirm({
    title: '编辑回复',
    content: () => {
      return h(AInput, {
        value: editContent.value, // 使用响应式变量
        onInput: (e) => editContent.value = e.target.value, // 更新响应式变量
        placeholder: '请输入新的回复内容'
      });
    },
    onOk: () => {
      return updateCommentApi({ id: reply.id, content: editContent.value }) // 使用响应式变量
        .then((res) => {
          message.success('更新成功');
          //getCommentList(); // 刷新评论列表
          // 刷新评论列表后，确保回复列表保持展开
          getCommentList().then(() => {
            const comment = commentData.value.find(comment => comment.replies.some(r => r.id === reply.id));
            if (comment) {
              comment.isReplyExpanded = true; // 展开回复列表
            }
          });
        })
        .catch((err) => {
          message.error('更新失败，请稍后再试。');
        });
    },
    onCancel: () => {
      console.log('取消编辑');
    }
  });
};
// ********

const getAdList = () => {
  listAdApi({})
    .then((res) => {
      res.data.forEach((item, index) => {
        if (item.image) {
          item.cover = BASE_URL + '/api/staticfiles/image/' + item.image;
        }
      });
      adData.value = res.data;
    })
    .catch((err) => {
      console.log(err);
    });
};
const sortCommentList = (sortType) => {
  if (sortType === 'recent') {
    sortIndex.value = 0;
  } else {
    sortIndex.value = 1;
  }
  order.value = sortType;
  getCommentList();
};

</script>
<style scoped lang="less">
// 兼容
@media screen and (max-width: 720px) {
  .detail-content {
    width: 90% !important;
  }

  .top-left-img {
    width: 99% !important;
  }

  .main-side {
    width: 100% !important;
  }

  .more {
    display: none !important;
  }
}

.detail {
  background: #ebf7f8;
  padding-top: 100px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  width: 80%;
  margin: 0 auto;

  .detail-content-top {
    padding: 20px;
    box-sizing: border-box;
    background: #fff;
    border-radius: 2px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 20px;

    .top-left-img {
      width: 350px;
      aspect-ratio: 4/3;
      object-fit: cover;
      border-radius: 8px;
    }

    .top-right {
      display: flex;
      flex-direction: column;
      flex: 1;
      gap: 4px;

      .title {
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 10px;

        .tag {
          display: inline-block;
          padding: 0 6px;
          border-radius: 2px;
          font-size: 12px;
          font-weight: bold;
          height: 20px;
          line-height: 20px;
          color: #fff;
          background: #f64242;
        }

        .title-value {
          font-size: 30px;
          color: #111;
          font-weight: bold;
        }
        //用户头像布局
        .right-box {
          -webkit-box-flex: 1;
          -ms-flex: 1;
          //flex: 1;
        }

        .avatar {
          width: 64px;
          height: 64px;
          border-radius: 50%;
          margin-right: 16px;
          object-fit: cover;
        }

      }

      .score {
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 16px;

        .pv {
          font-size: 12px;
          color: #555;
        }
      }

      .price {
        font-size: 14px;
        color: #333;
      }

      .meta {
        font-size: 14px;
        color: #333;
      }

      .actions {
        margin-top: auto;
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 16px;

        .buy-btn {
          background: #4684e2;
          cursor: pointer;
          display: block;
          border-radius: 4px;
          text-align: center;
          color: #fff;
          font-size: 14px;
          height: 36px;
          line-height: 36px;
          width: 110px;
          outline: none;
          border: none;
          margin-right: auto;
        }

        .more {
          display: flex;
          flex-direction: row;
          gap: 16px;

          .btn-like {
            white-space: nowrap;
            display: flex;
            flex-direction: row;
            cursor: pointer;

            img {
              width: 20px;
              height: 20px;
              object-fit: fill;
            }

            .label {
              font-size: 14px;
              color: #777;
            }
          }
        }
      }
    }
  }
}

.thing-content-view {
  margin-top: 40px;
  padding-bottom: 50px;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 30px;
}

.main-content {
  flex: 1;
  min-width: 300px;
  padding: 0 16px;

  background: #fff;
  border-radius: 2px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

  .text {
    color: #484848;
    font-size: 16px;
    line-height: 26px;
    padding-left: 12px;
    margin: 11px 0;
    white-space: pre-wrap;
  }

  .thing-comment {
    .title {
      font-weight: 600;
      font-size: 14px;
      line-height: 22px;
      height: 22px;
      color: #152844;
      margin: 24px 0 12px;
    }

    .publish {
      align-items: center;
      background-color: #f7d8d8;
      /* 优化评论框背景色 */
      border: 1px solid #c5e0f0;
      /* 优化评论框边框 */
      padding: 10px;
      /* 优化评论框内边距 */
      border-radius: 8px;
      /* 优化评论框圆角 */

      .mine-img {
        -webkit-box-flex: 0;
        -ms-flex: 0 0 40px;
        flex: 0 0 40px;
        margin-right: 12px;
        border-radius: 50%;
        width: 40px;
        height: 40px;
      }

      .content-input {
        flex: 1;
        background: #f6f9fb;
        border-radius: 4px;
        min-width: 100px;
        height: 32px;
        line-height: 32px;
        color: #484848;
        padding: 5px 12px;
        white-space: nowrap;
        outline: none;
        border: 0px;
      }

      .send-btn {
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

    .tab-view {
      -webkit-box-pack: justify;
      -ms-flex-pack: justify;
      justify-content: space-between;
      font-size: 14px;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      margin: 24px 0;

      .count-text {
        color: #484848;
        float: left;
      }

      .tab-box {
        color: #5f77a6;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;

        .tab-select {
          color: #152844;
        }

        span {
          cursor: pointer;
        }
      }

      .line {
        width: 1px;
        height: 12px;
        margin: 0 12px;
        background: #cedce4;
      }
    }

    .comments-list {
      .comment-item {
        border-bottom: 1px solid #8dc9ed;
        /* 优化评论项之间的分隔线 */
        padding: 16px 15px;
        /* 优化评论项的上下内边距 */
        background-color: #faf7dd;
        /* 优化评论项背景色 */
        border-radius: 8px;
        /* 优化评论项圆角 */
        margin-bottom: 16px;
        /* 优化评论项之间的间隔 */

        .flex-item {
          -webkit-box-align: center;
          -ms-flex-align: center;
          align-items: center;
          padding-top: 16px;

          .avator {
            -webkit-box-flex: 0;
            -ms-flex: 0 0 40px;
            flex: 0 0 40px;
            width: 40px;
            height: 40px;
            margin-right: 12px;
            border-radius: 20px;
            cursor: pointer;
          }

          .person {
            -webkit-box-flex: 1;
            -ms-flex: 1;
            flex: 1;
          }

          .name {
            color: #152844;
            font-weight: 600;
            font-size: 14px;
            line-height: 22px;
            height: 22px;
            cursor: pointer;
          }

          .time {
            color: #5f77a6;
            font-size: 12px;
            line-height: 16px;
            height: 16px;
            margin-top: 2px;
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

        .comment-content {
          margin-top: 8px;
          color: #484848;
          font-size: 14px;
          line-height: 22px;
          padding-bottom: 16px;
          border-bottom: 1px dashed #cedce4;
          margin-left: 52px;
          overflow: hidden;
          word-break: break-word;
        }

        .reply-section {
          margin-left: 12px;
          margin-right: 12px;
          margin-top: 12px;
          /* 增加回复区域与评论内容的间隔 */

          .reply-btn,
          .expand-btn {
            background: #f6f9fb;
            border-radius: 4px;
            color: #4684e2;
            font-size: 14px;
            text-align: center;
            height: 32px;
            line-height: 32px;
            outline: none;
            border: 0px;
            cursor: pointer;
            margin-bottom: 16px;
            /* 增加按钮之间的间隔 */
            margin: 0 10px;

            .commont-img {
              width: 18px;
              height: 18px;
            }
          }

          //收起回复
          .collapse-btn {
            background: #f6f9fb;
            border-radius: 4px;
            color: #4684e2;
            font-size: 14px;
            text-align: center;
            height: 32px;
            line-height: 32px;
            outline: none;
            border: 0px;
            cursor: pointer;
            margin-bottom: 16px;
            /* 增加按钮之间的间隔 */
            margin: 0 10px;

            .commont-img {
              width: 18px;
              height: 18px;
            }
          }

          .reply-input {
            display: flex;
            margin-top: 8px;

            .reply-content-input {
              flex: 1;
              background: #f6f9fb;
              border-radius: 4px;
              min-width: 100px;
              height: 32px;
              line-height: 32px;
              color: #484848;
              padding: 5px 12px;
              white-space: nowrap;
              outline: none;
              border: 0px;
            }

            .send-reply-btn {
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

          .replies-list {
            margin-left: 20px;
            background: #f6f9fb;
            border-radius: 4px;
            padding: 10px;
            margin-top: 8px;

            .reply-item {
              .flex-item {
                -webkit-box-align: center;
                -ms-flex-align: center;
                align-items: center;
                padding-top: 16px;

                .avator {
                  -webkit-box-flex: 0;
                  -ms-flex: 0 0 32px;
                  flex: 0 0 32px;
                  width: 32px;
                  height: 32px;
                  margin-right: 8px;
                  border-radius: 16px;
                  cursor: pointer;
                }

                .person {
                  -webkit-box-flex: 1;
                  -ms-flex: 1;
                  flex: 1;
                }

                .name {
                  color: #152844;
                  font-weight: 600;
                  font-size: 14px;
                  line-height: 22px;
                  height: 22px;
                  cursor: pointer;
                }

                .time {
                  color: #5f77a6;
                  font-size: 10px;
                  line-height: 14px;
                  height: 14px;
                  margin-top: 2px;
                }
              }

              .comment-content {
                margin-top: 8px;
                color: #484848;
                font-size: 12px;
                line-height: 18px;
                padding-bottom: 16px;
                border-bottom: 1px dashed #cedce4;
                margin-left: 40px;
                overflow: hidden;
                word-break: break-word;
              }
            }
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
    }
  }
}


.main-side {
  width: 300px;

  .recommend {
    background: #fff;
    border-radius: 2px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

    .title {
      font-weight: 600;
      font-size: 16px;
      height: 48px;
      line-height: 48px;
      padding-left: 16px;
      color: #152844;

      border-bottom: 1px solid #eee;
    }

    .things {
      display: flex;
      flex-direction: column;
      gap: 16px;
      padding: 20px 0;

      .thing-item {
        width: 90%;
        margin: 0px auto;
        position: relative;
        cursor: pointer;

        .img-view {
          width: 100%;

          img {
            border-radius: 8px;
            width: 100%;
            aspect-ratio: 5/3;
            background-size: cover;
            object-fit: cover;
          }
        }

        .info-view {
          //background: #f6f9fb;
          overflow: hidden;

          .thing-name {
            margin-top: 12px;
            color: #0f1111;
            font-weight: bold;
          }

          .price {
            color: #ff7b31;
            font-size: 20px;
            line-height: 20px;
            margin-top: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }
    }
  }
}

.flex-view {
  display: flex;
}
</style>