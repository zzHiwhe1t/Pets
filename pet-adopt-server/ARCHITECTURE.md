# 后端微服务架构说明

## 模块边界

| 模块 | 端口 | 数据归属 | 职责 |
|---|---:|---|---|
| pet-cloud-eureka | 8761 | 无 | 服务注册与发现 |
| pet-cloud-gateway | 8080 | 无 | `/api` 统一入口、跨域、Token 校验、路由 |
| pet-user-service | 8082 | `user` | 注册登录、Token、个人资料、用户摘要 |
| pet-pet-service | 8083 | `pet_category`、`pet`、`pet_image` | 分类、宠物 CRUD、状态、图片上传与读取 |
| pet-interaction-service | 8084 | `adoption_application`、`chat_message` | 领养申请审核、会话、未读消息 |
| pet-cloud-common | 无 | 无 | 统一返回体、异常、身份上下文和内部传输对象 |

为兼容原项目和本地部署，三个业务服务暂时连接同一个 `pet_adopt` 数据库，但每个服务只访问自己负责的表。跨领域数据不再使用 SQL Join，而是通过 OpenFeign 调用对方内部接口补齐。

## 请求流程

```text
Vue 前端 :8081
      │ /api + Bearer Token
      ▼
Spring Cloud Gateway :8080
      ├─ 调用用户服务解析 Token
      ├─ 注入 X-User-Id / X-User-Role / X-User-Name
      └─ 通过 Eureka 将请求路由到业务服务
             ├─ 用户服务 :8082
             ├─ 宠物服务 :8083 ──Feign──► 用户服务
             └─ 互动服务 :8084 ──Feign──► 用户服务、宠物服务
```

前端 API 路径和返回格式全部保持原样，因此 Vue 工程无需因微服务拆分而修改。

## 网关路由

- `/api/auth/**`、`/api/users/**` → 用户服务
- `/api/categories/**`、`/api/pets/**`、`/api/upload`、`/api/files/**` → 宠物服务
- `/api/applications/**`、`/api/chat/**` → 互动服务

登录、注册、分类、宠物公开查询和图片读取允许匿名访问。发布、修改、聊天、领养申请和个人资料接口统一由网关校验 Token。

## 一致性策略

本项目定位为 Windows 本地教学与作品展示环境，不额外引入 Redis、RabbitMQ、Kafka、Docker 或分布式事务组件。领养申请与宠物状态更新采用同步 Feign 调用：远程失败时接口立即报错，本地事务回滚，部署和调试成本更低。

如果后续扩展到生产环境，可在不改变前端 API 的前提下增加配置中心、熔断限流、分布式事务、链路追踪和独立数据库实例。
