# 药品有效期管理小程序

基于 SpringBoot + 微信小程序的家庭药品有效期管理系统，支持药品录入、效期提醒、AI 用药问答等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | SpringBoot 2.7.18、MyBatis-Plus 3.5、MySQL 8.0、Redis (可选) |
| 前端 | 微信小程序原生框架 |
| 文档 | Knife4j (Swagger) |
| 工具 | Lombok、Hutool、Jackson |
| AI | 阿里云百炼 DashScope API |

## 功能概览

- 微信登录 - 小程序一键登录，自动注册
- 药品管理 - 手动录入 / 扫码录入 / 增删改查 / 数量变更
- 效期监控 - 自动计算状态（正常/临期/过期/已用完），定时更新
- 消息提醒 - 临期/过期自动推送通知，滑动删除
- 数据统计 - 首页仪表盘、效期分布图表、近期待办
- AI 助手 - 接入阿里云大模型，药品问题智能问答

## 项目结构

```
demo1/
├── pom.xml                          # Maven 配置
├── schema.sql                       # 数据库建表脚本
├── README.md
├── miniprogram/                     # 微信小程序前端
│   ├── app.js / app.json / app.wxss
│   ├── pages/
│   │   ├── home/                    # 首页仪表盘
│   │   ├── medicine/
│   │   │   ├── list/                # 药品列表
│   │   │   ├── add/                 # 新增/编辑药品
│   │   │   └── detail/              # 药品详情
│   │   ├── notification/            # 消息通知
│   │   ├── chat/                    # AI 聊天
│   │   └── profile/                 # 个人中心
│   └── utils/
│       ├── api.js                   # 接口封装
│       └── util.js                  # 工具函数
└── src/main/java/com/medicine/demo1/
    ├── Demo1Application.java        # 启动类
    ├── config/                      # 配置类
    ├── controller/                  # 接口层
    ├── service/                     # 业务层
    ├── mapper/                      # MyBatis Mapper
    ├── entity/                      # 数据库实体
    ├── dto/                         # 数据传输对象
    ├── common/                      # 通用类
    ├── task/                        # 定时任务
    └── ai/                          # AI 聊天
```

 快速开始

1. 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0
- 微信开发者工具

2. 数据库初始化

```bash
mysql -u root -p < schema.sql
```

3. 修改配置

编辑 `src/main/resources/application.yaml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medicine_db?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

4. 启动后端

```bash
cd demo1
mvn spring-boot:run
```

启动后访问：
- 后端地址：`http://localhost:8899`
- API 文档：`http://localhost:8899/doc.html`

5. 启动前端

1. 打开 微信开发者工具
2. 导入项目，选择 `miniprogram/` 目录
3. 填写你的小程序 AppID（或使用测试号）
4. 开发者工具 → 详情 → 本地设置 → 勾选「不校验合法域名」
5. 点击「编译」

API 接口

 用户

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/login` | 微信登录 |
| GET | `/api/user/info` | 获取用户信息 |
| PUT | `/api/user/info` | 更新用户信息 |

药品

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/medicine` | 新增药品 |
| GET | `/api/medicine/list` | 药品列表（分页+筛选） |
| GET | `/api/medicine/{id}` | 药品详情 |
| PUT | `/api/medicine/{id}` | 更新药品 |
| DELETE | `/api/medicine/{id}` | 删除药品（软删除） |
| PUT | `/api/medicine/{id}/quantity` | 数量变更 |
| POST | `/api/medicine/scan` | 扫码查询 |

统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics/dashboard` | 首页仪表盘 |
| GET | `/api/statistics/expiry-distribution` | 效期分布 |

通知

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/notification/list` | 通知列表 |
| PUT | `/api/notification/{id}/read` | 标记已读 |
| DELETE | `/api/notification/{id}` | 删除通知 |
| GET | `/api/notification/unread-count` | 未读数量 |

AI

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/ai/chat` | AI 对话 |

定时任务

| 时间 | 任务 |
|------|------|
| 每日 00:30 | 更新药品效期状态 |
| 每日 09:00 | 推送临期提醒 |
| 每日 09:30 | 推送过期提醒 |

数据库表

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| medicine | 药品表 |
| medicine_usage_log | 药品使用记录表 |
| notification_log | 消息推送记录表 |

药品状态说明

| 状态值 | 含义 | 触发条件 |
|:------:|------|----------|
| 0 | 正常 | 有效期 > 30天 |
| 1 | 临期 | 有效期 ≤ 30天 |
| 2 | 过期 | 已超过有效期 |
| 3 | 已用完 | 数量 ≤ 0 |
