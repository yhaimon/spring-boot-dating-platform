const express = require('express');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
//方法1：只允许一个前端端口
/* const io = socketIo(server, {
    cors: {
        origin: "http://localhost:8080", // 允许的前端域名
        methods: ["GET", "POST"]
    }
}); */
//方法2：只允许特定端口号访问
const io = socketIo(server, {
    cors: {
        // 使用正则表达式匹配多个前端端口
        origin: (origin, callback) => {
            // 允许的前端域名和端口列表
            const allowedOrigins = [
                "http://localhost:8080",
                "http://localhost:8081",
                // 可以继续添加更多端口
            ];

            // 检查请求的来源是否在允许列表中
            if (allowedOrigins.includes(origin)) {
                // 如果允许，返回 null（表示允许）和状态码 200
                callback(null, true);
            } else {
                // 如果不允许，返回错误信息和状态码 403
                callback(new Error("Not allowed by CORS"));
            }
        },
        methods: ["GET", "POST"], // 允许的 HTTP 方法
    }
});


//const onlineUsers = new Set(); // 用于存储在线用户

const onlineUsers = []; // 用于存储在线用户信息

// 监听 WebSocket 连接
io.on('connection', (socket) => {
    console.log('用户已连接', socket.id);
    /*// 获取用户信息
    const user_id = socket.handshake.query.user_id;
    onlineUsers.add(user_id); // 添加到在线用户列表
    console.log('在线用户:', Array.from(onlineUsers));
    // 广播在线用户列表
    const usersArray = Array.from(onlineUsers).map(userName => ({ user_name: userName }));
    io.emit('updateUsers', Array.from(onlineUsers));
        */
    
    // 获取用户信息
    const user_id = socket.handshake.query.user_id;
    const user_name = socket.handshake.query.user_name;
    const avatar = socket.handshake.query.avatar;
    // 创建用户对象,,,为每个用户存储 socket.id
    const user = { user_id, user_name, avatar ,socketId: socket.id};

    // 添加到在线用户列表
    onlineUsers.push(user);
    console.log('在线用户:', onlineUsers);
    // 广播在线用户列表
    io.emit('updateUsers', onlineUsers);


    // 监听客户端发送的消息
    socket.on('sendMessage', (data) => {
        console.log('!!!!!!!!!收到消息:', data);        
       // io.emit('message', data); // 广播消息给所有客户端
       const receiver = onlineUsers.find(u => u.user_id === data.to); // 查找接收者的socket.id
        if(receiver){
             // 只向特定用户发送消息
            //io.to(receiver.socketId).emit('getMessage', data); // 只向特定用户发送消息
            // 只向特定用户发送消息，并添加from字段
            io.to(receiver.socketId).emit('getMessage', { from: socket.handshake.query.user_id, text: data.text });
            console.log(`！！！！！消息已发送至用户 ${receiver.user_id}`);
        }else {
            console.error(`！！！！！未找到用户 ${data.to}`);
        }
    });

    // 监听客户端断开连接
    /*  socket.on('disconnect', () => {
         console.log('用户已断开连接', socket.id);
         onlineUsers.delete(user_name); // 从在线用户列表中移除
         console.log('在线用户:', Array.from(onlineUsers));
         const usersArray = Array.from(onlineUsers).map(userName => ({ user_name: userName }));
         io.emit('updateUsers', Array.from(onlineUsers)); // 广播更新后的在线用户列表
     }); */
    // 监听客户端断开连接
    socket.on('disconnect', () => {
        console.log('用户已断开连接', socket.id);
        // 从在线用户列表中移除
        onlineUsers.splice(onlineUsers.findIndex(u => u.user_name === user_name), 1);
        console.log('在线用户:', onlineUsers);
        // 广播更新后的在线用户列表
        io.emit('updateUsers', onlineUsers);
    });

});

// 启动服务器
const PORT = 9090;
server.listen(PORT, () => {
    console.log(`服务器运行在 http://localhost:${PORT}`);
});



//方法3：允许任意前端端口访问（适用于开发阶段）
/* const io = socketIo(server, {
    cors: {
        // 使用正则表达式匹配动态端口
        origin: (origin, callback) => {
            // 允许的前端域名和端口范围
            const allowedOrigins = [
                /^http:\/\/localhost:\d+$/, // 匹配 http://localhost:任意端口
            ];

            // 检查请求的来源是否匹配正则表达式
            if (allowedOrigins.some(regex => regex.test(origin))) {
                // 如果允许，返回 null（表示允许）和状态码 200
                callback(null, true);
            } else {
                // 如果不允许，返回错误信息和状态码 403
                callback(new Error("Not allowed by CORS"));
            }
        },
        methods: ["GET", "POST"], // 允许的 HTTP 方法
    }
}); */

