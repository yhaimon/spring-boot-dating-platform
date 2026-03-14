import {get, post} from '/@/utils/http/axios';
const URL= {
    getMessages: '/api/chatMessages/getMsg',
    createMessage: '/api/chatMessages/createMessage',
   
}

// const getMessagesApi = async (userId, otherUserId) =>
//     get({url: `${URL.getMessages}/${userId}/${otherUserId}`, params: params, data: {}, headers: {}});

const getMessagesApi = async (userId, otherUserId) => {
    console.log('chatMessager.js获取聊天历史，userId:', userId, 'otherUserId:', otherUserId);

    try {
        const response = await get({ url: `${URL.getMessages}/${userId}/${otherUserId}`, params: {}, data: {}, headers: {} });
        console.log('获取到的历史消息:', response);
        return response; // 确保返回的是数组
    } catch (error) {
        console.error('chatMessager.js获取聊天历史失败:', error);
        throw error; // 抛出错误，让调用者处理
    }
};

// const createMessageApi = async (data) =>
//     post({ url: URL.createMessage, params: {}, data: data, headers: {} });

const createMessageApi = async (data) => {
    try {
        const response = await post({ url: URL.createMessage, params: {}, data: data, headers: {} });
        return response; // 返回后端的响应
    } catch (error) {
        console.error('发送消息失败:', error);
        throw error; // 抛出错误，让调用者处理
    }
};

export {getMessagesApi,createMessageApi};
