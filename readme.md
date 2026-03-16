

> 
> 
> 
> 
> ## 项目简介
> 
>         经这10多年发展，现婚恋市场有一大批“网红媒婆”用直播的方式推销婚姻和爱情吸引了大量单身群体。而这种模式明显存在局限性。地域的限制和线下的排队拥挤导致许多用户难以有高效的婚恋服务。直播的方式对内向的人和小众观众不友好。用户投入时间和金钱也可能无法得到服务，且存在网络暴力的风险。为此，本研究设计了一个基于Spring Boot框架的婚恋平台来解决用户需求。
>         平台用Vue.js技术结合ES6语法、Vue-Router路由和Vuex状态管理构建了前端界面，实现跨终端适配与组件化开发功能，达到界面高效交互与用户体验优化的效果；后端用Spring Boot微服务框架和Java语言，实现MyBatis数据库操作，并用WebSocket技术实现在线聊天的实时性，达到多模块协同，满足婚恋平台核心功能的高效运行需求。
>         平台用智能推荐算法技术分析用户行为与偏好数据，实现准确匹配相亲功能，达到提升用户匹配效率的效果；用3级权限分级设计（普通用户、管理员、会员）角色，实现差异化服务功能达到平台服务标准化；用动态内容发布、在线聊天、线下约会模块，实现全场景婚恋服务功能，达到用户多样化需求。用测试数据，实现增加用户注册转化率和活跃度，达到系统技术可行性和用户体验优化价值。
> 
> ## 演示地址
> 
> http://localhost:8080/
> 
> ## 主要功能

> * 用户（包含普通用户和会员用户功能）
>
> * **注册与登录**：新用户需通过注册流程创建账户，并通过登录验证后才能享受网站服务。
>
>  ![用户注册](..\java_lover\images\lover-user\婚恋平台-注册页面.png)
>
>  ![用户登录](..\java_lover\images\lover-user\婚恋平台-登录-用户页面.png)
>
> * **相亲浏览**：首页展示会员列表，用户可以根据最新或最热的标准筛选感兴趣的对象。
>
>  ![首页](..\java_lover\images\lover-user\婚恋平台-首页页面.png)
>
> * **热门推荐**：采用协同过滤算法为用户提供个性化推荐，展示最受欢迎的会员。
>
>  ![热门推荐](D:\MyProject\javaProject\java_lover\images\lover-user\婚恋平台-推荐页面.png)
>
> * **个人中心**：用户可以在此更新个人信息、修改密码、管理收藏夹和点赞记录。
>
>  ![个人中心](..\java_lover\images\lover-user\婚恋平台-个人中心页面.png)
>
> * **约会管理**：用户可以查看自己的约会安排，接收约会邀请，并管理约会状态。
>
>   包含申请约会，发布约会请求：
>
>   ![相亲详情](..\java_lover\images\lover-user\婚恋平台-相亲详情页面.png)
>
> ![约会申请](..\java_lover\images\lover-user\婚恋平台-发起约会页面.png)
> ![约会管理](..\java_lover\images\lover-user\婚恋平台-管理约会页面.png)
>
> * **意见提交**：用户界面包含意见反馈入口，方便用户随时提交建议或问题。
>   ![意见反馈](..\java_lover\images\lover-user\婚恋平台-意见反馈页面.png)
>
> * **模糊搜索**：顶部搜索栏支持模糊查询会员信息，提升查找效率。
>   ![模糊查询](..\java_lover\images\lover-user\婚恋平台-模糊查询页面.png)
>
> * **会员评论**：在会员详情页下方，用户可以发表评论，分享看法或互动交流。
>   ![会员评论](..\java_lover\images\lover-user\婚恋平台-会员评论页面.png)
>   包含回复功能：
>   ![会员回复](..\java_lover\images\lover-user\婚恋平台-会员回复.png)
>
> * **动态发布**：用户可以上传图片和文字动态，还能对已发布的动态进行修改、删除或点赞操作。
>![动态管理](..\java_lover\images\lover-user\婚恋平台-动态管理-发布页面.png)
>   包含动态浏览点赞，删除修改。
>   ![动态编辑](..\java_lover\images\lover-user\婚恋平台-动态编辑页面.png)
>   ![动态保存](..\java_lover\images\lover-user\婚恋平台-动态删除页面.png)
>
> * **在线聊天**：提供实时聊天功能，促进用户之间的即时通讯和互动。
>   ![在线聊天-接收消息](..\java_lover\images\lover-user\婚恋平台-在线聊天-接收消息页面.png)
>   ![在线聊天-发送消息](..\java_lover\images\lover-user\婚恋平台-在线聊天-发送消息页面.png)
>
> * **充值管理**：用户可通过充值成为会员，并根据需要续费以维持会员特权。
>   ![会员充值](..\java_lover\images\lover-user\婚恋平台-会员管理-充值页面.png)
>
> * 
>   ![会员充值记录](..\java_lover\images\lover-user\婚恋平台-会员管理-充值记录页面.png)
>
> * 会员相亲信息发布
>   ![会员发布相亲](..\java_lover\images\lover-user\婚恋平台-会员管理-相亲发布页面.png)
>
> * **广告浏览**：系统支持在详情页面右侧浏览广告，管理员可灵活控制广告内容和展示时间。
>   ![广告浏览](..\java_lover\images\lover-user\婚恋平台-广告浏览页面.png)
>
> * 管理员
>  ![管理员登录](..\java_lover\images\lover-admin\婚恋平台-登录-管理员页面.png)
>  
> * **会员管理**：系统支持录入、修改及查询会员的基本信息（如姓名、性别、年龄等），并附带备注功能以记录额外信息。
> ![会员管理](..\java_lover\images\lover-admin\婚恋平台-会员管理页面.png)
>
> ![会员管理-编辑](..\java_lover\images\lover-admin\婚恋平台-会员管理-编辑页面.png)
>
> * **会员分类**：管理员可以定义和管理不同类型的会员，每种类型有独特的名称和其他属性。
>
> ![会员分类](..\java_lover\images\lover-admin\婚恋平台-会员分类页面.png)
>
> * **评论管理**：提供工具用于浏览和管理网站上的所有评论，确保内容健康且符合规定。
>
>  ![评论管理](..\java_lover\images\lover-admin\婚恋平台-会员评论管理页面.png)
>
> * **约会管理：**管理用户的约会情况。
>
> ![约会管理](..\java_lover\images\lover-admin\婚恋平台-会员约会管理页面.png)
>
> * **动态管理：**管理用户的动态，对不合格动态进行管控。
>* ![动态管理](..\java_lover\images\lover-admin\婚恋平台-动态管理页面.png)
> * **用户管理**：允许管理员新增、编辑或删除用户资料，便于维护用户数据库的准确性和完整性。
>
>  ![用户管理](..\java_lover\images\lover-admin\婚恋平台-用户管理页面.png)
>
>    
>
> * **统计分析**：基于会员活动数据和用户参与情况生成统计报告，帮助管理员全面了解系统运行状态。
>
>  ![统计分析](..\java_lover\images\lover-admin\婚恋平台-统计分析页面.png)
>
> * **广告发布**：管理员可在系统中发布公告，确保所有用户都能及时接收到重要通知。
>
>  ![广告管理](..\java_lover\images\lover-admin\婚恋平台-广告发布页面.png)
>
> * **意见反馈**：提供一个专门模块供用户提交反馈意见，管理员可以通过后台查看并处理这些意见。
>
>  ![接收意见反馈](..\java_lover\images\lover-admin\婚恋平台-意见反馈管理页面.png)
>
> * **系统信息**：管理员能够查看系统的详细信息，包括服务器配置、内存使用情况、CPU性能等关键指标。
>
> ![系统配置](..\java_lover\images\lover-admin\婚恋平台-系统配置页面.png)
>
> 
>
> ## 开发环境
>
> * 后端： Java 8 + Springboot
> * 前端： Javascript + Vue
> * 数据库：MySQL 8.0
> * 开发平台：IDEA + vscode
> * 运行环境：Windows 10/11
>
> ## 关键技术
>
> * 前端技术栈 ES6、vue、vuex、vue-router、vue-cli、axios、antd
> * 后端技术栈 Java、Springboot、mybatis、WebSocket
>
> ## 运行步骤
>
> ### 软件准备
>
> 1. IDEA 2024
> 2. MySQL 8.0 
> 3. Node-v18.20.2
>
> ### 后端运行步骤
>
> (1) 使用IDEA打开server目录，设置项目的jdk，同步maven依赖。
>
> (2) 创建数据库，创建SQL如下：
>    CREATE DATABASE IF NOT EXISTS java_db[your dbname] DEFAULT CHARSET utf8 COLLATE utf8_general_ci
>
> (3) 恢复数据库数据。在mysql下依次执行如下命令：
>    mysql> use xxx(数据库名);
>    mysql> source D:/xxx/xxx/xxx.sql;
>
> (4) 配置数据库。在application.yml中配置您的数据库账号密码
>
>     BASE_LOCATION: D:\ProgramData\IdeaProjects\java_lover  
>
>     DB_NAME: [db_name]
>    spring:
>      datasource:
>        username: root
>        password: [your password]
>        url: jdbc:mysql://localhost:3306/${DB_NAME}?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
>        driver-class-name: com.mysql.cj.jdbc.Driver
>        type: com.alibaba.druid.pool.DruidDataSource
>
> (5) 启动服务。点击IDEA的运行按钮。
>
> ### 前端运行步骤
>
> (1) 使用VS Code打开前端代码web，安装依赖，cd到web目录，执行:
>    npm install
>
> ```bash
> # 1. 进入前端目录
> cd 前端目录
> 
> # 2. 一次性装回当时 exact 版本
> npm install
> 
> # 3. 确认安装结果
> npm list socket.io-client socket.io
> # 应看到
> # ├─ socket.io-client@4.8.1
> # └─ socket.io@4.8.1
> ```
>
> ​	（包括我的websocke需要的前端包
> ​		注：保留前端页面的 `package.json`，执行 `npm install` 即可 100% 还原当时能跑的环境，不会再因为“下错包”而报错。）
> (2) 实现私聊功能步骤：
>
>     1. 在前端页面中，打开终端cd到这个目录下cd src/services
>         PS D:\ProgramData\IdeaProjects\java_lover\web>cd src/services
>                                                                                             2. 运行node服务器node server.js
>
>         PS D:\ProgramData\IdeaProjects\java_lover\web\src\services> node server.js
>
> 
>
> (3) 再打开一个新的终端，运行项目 npm run dev
>
>  PS D:\ProgramData\IdeaProjects\java_lover\web>npm run dev
>
> 
>
> 然后访问前端地址。即可
>
> 
>
> ## 说明：
>
> **1. 数据库的版本要求：**
>
> mysql 5.7及以上版本即可
>
> **2. 项目代码的结构**
>
> server目录是后端代码，web目录是前端代码。
>
> **3. 需要的技术知识**
>
> 答：需要Java编程知识、springboot框架知识、vue编程知识
>
> **4. 后台管理的默认账号密码是？**
>
> 用户密码统一为：123
>
> 管理员密码是：admin123/123
>
> 
>
> 
