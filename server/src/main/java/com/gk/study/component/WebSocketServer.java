package com.gk.study.component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private static final Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();
    private SocketIOServer server;

    @PostConstruct
    public void init() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9090);

        server = new SocketIOServer(config);

        // 监听 "sendMessage" 事件
        server.addEventListener("sendMessage", JSONObject.class, new DataListener<JSONObject>() {
            @Override
            public void onData(SocketIOClient client, JSONObject data, AckRequest ackRequest) {
                // 使用 getStr 方法获取 JSON 字段
                //String toUsername = data.getStr("to");
                String toUserId = data.getStr("to"); // 注意这里是 user_id 而不是 username
                String text = data.getStr("text");

                log.info("收到消息: {}", data);

                // 获取目标用户的客户端
                //SocketIOClient toClient = clientMap.get(toUsername);
                // 获取目标用户的客户端，现在是基于 user_id 查找
                SocketIOClient toClient = clientMap.get(toUserId);
                if (toClient != null) {
                    // 构造消息并发送
                    JSONObject message = new JSONObject();
                    //message.set("from", client.getHandshakeData().getSingleUrlParam("user_name"));
                    message.set("from", client.getHandshakeData().getSingleUrlParam("user_id"));
                    message.set("text", text);
                    toClient.sendEvent("getMessage", message); // 确保事件名称与前端监听的一致
                } else {
                    //log.warn("未找到用户: {}", toUsername);
                    log.warn("未找到用户: {}", toUserId);
                }
            }
        });

        // 连接监听器，监听客户端连接
        /*server.addConnectListener(client -> {
            String username = client.getHandshakeData().getSingleUrlParam("user_name");
            String userId = client.getHandshakeData().getSingleUrlParam("user_id"); // 获取用户 ID
            String avatar = client.getHandshakeData().getSingleUrlParam("avatar"); // 获取用户头像

            // 这里是使用 了username,user_id ,avatar作为唯一键存储客户端,XXXXXXX是错误的写法
            clientMap.put(username, client);
            clientMap.put(avatar, client);
            log.info("用户 id：{},用户名：{},用户头像：{} 连接成功",userId, username,avatar);
            log.info("你看当前在线用户有: {}个", clientMap.keySet());
            // 构造在线用户列表并广播
            JSONObject users = new JSONObject();
            JSONArray userArray = new JSONArray();
            clientMap.keySet().forEach(u -> {
                JSONObject user = new JSONObject();
                user.set("user_name", u);
                user.set("user_id", u); // 添加用户 ID
                user.set("avatar", u); // 添加用户头像
                userArray.put(user);
            });
            users.set("users", userArray);
            server.getBroadcastOperations().sendEvent("updateUsers", users);
        });
*/
        // 优化：连接监听器，监听客户端连接
        server.addConnectListener(client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("user_id"); // 获取用户 ID
            String username = client.getHandshakeData().getSingleUrlParam("user_name");//获取用户名
            String avatar = client.getHandshakeData().getSingleUrlParam("avatar"); // 获取用户头像

            // 仅使用 user_id 作为唯一键存储客户端
            clientMap.put(userId, client);
            log.info("用户 id：{}, 用户名：{}, 用户头像：{} 连接成功", userId, username, avatar);

            // 构造在线用户列表并广播
            JSONObject users = new JSONObject();
            JSONArray userArray = new JSONArray();
            for (Map.Entry<String, SocketIOClient> entry : clientMap.entrySet()) {
                JSONObject user = new JSONObject();
                user.set("user_id", entry.getKey());//user_id 作为唯一键存储
                user.set("user_name", client.getHandshakeData().getSingleUrlParam("user_name")); // 使用正确的数据填充
                user.set("avatar", client.getHandshakeData().getSingleUrlParam("avatar"));
                userArray.put(user);
            }
            users.set("users", userArray);
            server.getBroadcastOperations().sendEvent("updateUsers", users);
        });

        // 监听客户端断开连接XXXXXXX(这里是以username作为断开连接，是错误的)
        /*server.addDisconnectListener(client -> {
            String username = clientMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(client))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
            if (username != null) {
                clientMap.remove(username);
                log.info("用户 {} 断开连接", username);
                log.info("当前在线用户: {}", clientMap.keySet());
            }
        });*/

        // ********优化：断开连接监听器，并且更新用户列表（在处理断开连接时，要基于user_id进行操作）

        server.addDisconnectListener(client -> {
            String disconnectedUserId = clientMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(client))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
            if (disconnectedUserId != null) {
                //根据userId来
                clientMap.remove(disconnectedUserId);
                log.info("用户 {} 断开连接", disconnectedUserId);

                // 更新在线用户列表
                JSONObject users = new JSONObject();
                JSONArray userArray = new JSONArray();
                for (Map.Entry<String, SocketIOClient> entry : clientMap.entrySet()) {
                    JSONObject user = new JSONObject();
                    user.set("user_id", entry.getKey());
                    user.set("user_name", entry.getValue().getHandshakeData().getSingleUrlParam("user_name"));
                    user.set("avatar", entry.getValue().getHandshakeData().getSingleUrlParam("avatar"));
                    userArray.put(user);
                }
                users.set("users", userArray);
                server.getBroadcastOperations().sendEvent("updateUsers", users);
            }
        });


        server.start();
    }

    @PreDestroy
    public void stop() {
        server.stop();
    }
}