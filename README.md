# 爪爪家园 · 萌宠领养寄养平台

一套本地可运行的前后端分离项目：Vue 2 + Element UI 前端、Spring Boot + MyBatis 后端、MySQL 8 数据库。图片通过后端从 `C:\Users\27743\Desktop\pets` 根目录读取和上传，所有新上传文件均使用时间戳与 UUID 命名并平铺存放。

## 目录说明

```text
pets/
├─ database/pet_adopt.sql       MySQL 一键初始化脚本
├─ pet-adopt-server/            Spring Boot 后端
├─ pet-adopt-web/               Vue 2 前端
├─ resources/                   项目内置宠物图片（程序直接读取）
├─ docs/image-prompts.md        8 大类及全部细分类配图文案
└─ 时间戳_UUID.图片后缀        用户运行时上传的图片
```

## 1. 准备环境

- JDK 8（本项目编译目标为 Java 8；JDK 8～17 均可构建）
- Maven 3.5+
- Node.js 16 或 18（不建议使用过新的 Node.js 版本运行旧版 Vue CLI）
- MySQL 8.0
- Navicat（可选，也可使用 MySQL 命令行）

确认 MySQL 本机端口为 `3306`，账号为 `root`，密码为 `123456`。如果本机配置不同，请同时修改 `pet-adopt-server/src/main/resources/application.yml`。

## 2. 导入数据库

1. 打开 Navicat，连接本机 MySQL。
2. 选择“运行 SQL 文件”。
3. 选择 `database\pet_adopt.sql` 并运行。
4. 脚本会自动删除并重建 `pet_adopt` 数据库，请勿在已有正式数据库上直接执行。

测试账号：

- 发布人：`alice / 123456`
- 领养人：`bob / 123456`
- 管理员预留账号：`admin / admin123`

## 3. 放置图片素材

项目自带的演示宠物图片统一放在 `\pets\resources`，不再在项目根目录重复保存。后端会同时读取该素材目录和固定上传目录。

用户通过页面新上传的头像、宠物照片仍会按时间戳与 UUID 命名，直接写入 `\pets` 根目录；这些是运行数据，不是内置素材副本。后续依据 `docs\image-prompts.md` 补充内置演示图时，请保存到 `resources`。

## 4. 启动后端

打开 PowerShell：

```powershell
cd \pets\pet-adopt-server
mvn spring-boot:run
```

看到 `Started PetAdoptApplication` 后，后端地址为 `http://localhost:8080/api`。上传接口会自动创建目标目录（若目录不存在），上传后图片可通过 `http://localhost:8080/api/files/文件名` 访问。

如需先打包：

```powershell
mvn clean package -DskipTests
java -jar target\pet-adopt-server-1.0.0.jar
```

## 5. 启动前端

另开一个 PowerShell：

```powershell
cd C:\Users\27743\Desktop\pets\pet-adopt-web
npm install
npm run serve
```

浏览器访问 `http://localhost:8081`。开发代理会自动把 `/api` 请求转发到 `8080` 后端。

生产构建命令：

```powershell
npm run build
```

构建结果位于 `pet-adopt-web\dist`。

## 6. 推荐体验顺序

1. 使用 `alice / 123456` 登录，进入个人中心查看发布、收到的申请与消息。
2. 退出后使用 `bob / 123456` 登录，筛选宠物、进入详情、联系主人并提交申请。
3. 再次以 Alice 登录，在“收到的申请”中通过或驳回；通过后宠物自动变为“已领养”，同一宠物的其他待审核申请自动失效。
4. 在“发布寄养”中上传多图，第一张会自动成为封面；图片会直接写入固定根目录。

## 常见问题

- 页面提示网络连接失败：先确认 MySQL 与后端均已启动，且 8080 端口未被占用。
- 登录提示用户名或密码错误：重新执行 SQL 初始化脚本，或确认没有修改测试账号数据。
- 图片不显示：确认文件位于 `\pets` 根目录，而不是只放在 `resources` 中。
- Maven 使用 JDK 版本不对：执行 `java -version` 与 `mvn -version`，确保 Maven 指向可用 JDK。
- Node.js 运行 Vue CLI 报兼容错误：切换至 Node.js 18 LTS，删除 `node_modules` 后重新执行 `npm install`。

## 关键业务状态

- 宠物：`AVAILABLE` 待领养、`IN_PROGRESS` 领养中、`ADOPTED` 已领养、`OFFLINE` 已下架。
- 申请：`PENDING` 待审核、`APPROVED` 已通过、`REJECTED` 已驳回、`CANCELLED` 已失效。
- 删除均为数据库逻辑删除；图片文件不会随宠物记录删除，避免误删用户本地素材。
