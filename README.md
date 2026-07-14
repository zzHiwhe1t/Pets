# 爪爪家园 · Spring Cloud 萌宠领养寄养平台

这是一个可在 Windows 本地直接运行的前后端分离微服务项目。前端继续使用 Vue 2 + Element UI，后端已由单体 Spring Boot 改造成 Spring Cloud 多模块架构，原有注册登录、宠物筛选与发布、图片上传、在线留言、领养申请和个人中心功能保持不变。

## 技术栈

- JDK 8、Maven 3.5+
- Spring Boot 2.7.18、Spring Cloud 2021.0.9
- Eureka、Spring Cloud Gateway、OpenFeign、MyBatis
- MySQL 8.0（数据库 `pet_adopt`，账号 `root`，密码 `123456`）
- Vue 2、Element UI、Vue Router、Axios

## 项目结构

```text
pets/
├─ database/pet_adopt.sql
├─ pet-adopt-server/
│  ├─ pet-cloud-common/          公共返回体、异常、网关身份上下文、内部 DTO
│  ├─ pet-cloud-eureka/          服务注册中心，端口 8761
│  ├─ pet-cloud-gateway/         API 网关与统一认证，端口 8080
│  ├─ pet-user-service/          注册、登录、用户资料，端口 8082
│  ├─ pet-pet-service/           分类、宠物、图片上传，端口 8083
│  ├─ pet-interaction-service/   领养申请、聊天消息，端口 8084
│  ├─ start-cloud.ps1            一键启动已打包的全部服务
│  └─ stop-cloud.ps1             一键停止服务
├─ pet-adopt-web/                Vue 2 前端
├─ resources/                    内置宠物图片
└─ docs/image-prompts.md         配图生成文案
```

详细模块边界和请求流程见 `pet-adopt-server/ARCHITECTURE.md`。

## 1. 导入数据库

1. 启动 MySQL 8.0，确认本机端口为 `3306`。
2. 在 Navicat 中选择“运行 SQL 文件”。
3. 执行 `database\pet_adopt.sql`。

脚本会重建 `pet_adopt` 数据库。已有正式数据时不要直接重复执行。

测试账号：`alice / 123456`、`bob / 123456`、`admin / admin123`。

## 2. 打包并启动微服务

打开 PowerShell：

```powershell
cd C:\Users\27743\Desktop\PROJECTS\pets\pet-adopt-server
mvn clean package
Set-ExecutionPolicy -Scope Process Bypass
.\start-cloud.ps1
```

启动顺序已经由脚本处理：Eureka → 用户服务/宠物服务/互动服务 → 网关。日志写入 `pet-adopt-server\logs`，进程号写入 `pet-adopt-server\.pids`。

服务启动后：

- 统一 API 地址：`http://localhost:8080/api`
- Eureka 控制台：`http://localhost:8761`
- 前端仍只访问 8080 网关，不需要修改任何接口地址

停止全部服务：

```powershell
.\stop-cloud.ps1
```

如果希望在 IDE 中观察日志，也可以依次运行各模块的 Application 类：`EurekaServerApplication`、`UserServiceApplication`、`PetServiceApplication`、`InteractionServiceApplication`、`GatewayApplication`。

## 3. 启动前端

另开一个 PowerShell：

```powershell
cd C:\Users\27743\Desktop\PROJECTS\pets\pet-adopt-web
npm install
npm run serve
```

浏览器访问 `http://localhost:8081`。Vue 开发代理仍将 `/api` 转发到 8080 网关。

## 4. 图片目录

- 内置演示图片：`C:\Users\27743\Desktop\PROJECTS\pets\resources`
- 用户新上传图片：`C:\Users\27743\Desktop\PROJECTS\pets` 根目录
- 访问地址：`http://localhost:8080/api/files/文件名`

上传服务仍使用时间戳与 UUID 防重名，所有运行时图片平铺保存，不创建分类子文件夹。

## 常见问题

- 网关提示认证服务不可用：确认 8761、8082 已启动，并在 Eureka 中看到 `PET-USER-SERVICE`。
- 页面接口 503：确认三个业务服务均已注册到 Eureka，再启动或刷新网关。
- 图片不显示：确认 `pet-pet-service/application.yml` 中的 `upload-path` 为当前项目根目录。
- 端口被占用：停止旧的单体后端；新架构固定使用 8761、8080、8082、8083、8084。
- Maven 版本不对：执行 `java -version` 与 `mvn -version`，确保 Maven 使用 JDK 8 或兼容 JDK。

## 业务状态

- 宠物：`AVAILABLE` 待领养、`IN_PROGRESS` 领养中、`ADOPTED` 已领养、`OFFLINE` 已下架。
- 申请：`PENDING` 待审核、`APPROVED` 已通过、`REJECTED` 已驳回、`CANCELLED` 已失效。
- 数据库表结构与原系统兼容，无需迁移已有数据。
