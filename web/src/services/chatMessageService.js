






// services/chatMessageService.js
const mongoose = require('mongoose');
const ChatMessage = require('../models/ChatMessage'); // 确保你有一个 ChatMessage 模型

// 获取消息历史记录
const getMessages = async (senderId, receiverId) => {
    try {
        const messages = await ChatMessage.find({
            $or: [
                { senderId: senderId, receiverId: receiverId },
                { senderId: receiverId, receiverId: senderId }
            ]
        }).sort({ timestamp: 1 }); // 按时间戳排序
        return messages;
    } catch (error) {
        console.error('获取消息历史失败:', error);
        throw error;
    }
};

module.exports = {
    getMessages
};