## 图书管理系统（library-web_2）

一个前后端分离的图书管理系统示例：后端基于 Spring Boot 3 + MyBatis-Plus + JWT，前端基于 Vue 3 + Vite + Element Plus。支持用户注册/登录、图书管理、借阅记录、仪表盘统计等功能。

### 功能特性
- **用户认证**: 注册、登录，基于 JWT 的无状态认证
- **权限控制**: Spring Security 保护除认证接口外的所有 API
- **图书管理**: 图书增删改查
- **借阅管理**: 借书/还书、借阅记录
- **用户管理**: 用户信息、密码修改
- **可视化看板**: 仪表盘统计展示（ECharts）

### 技术栈
- **后端**: Spring Boot 3.5.x, Spring Security, MyBatis-Plus, Druid, JJWT, Lombok, MySQL
- **前端**: Vue 3, Vite 7, Vue Router 4, Pinia, Element Plus, ECharts
- **运行环境**:
  - Java 17+
  - Node.js 20.19+ 或 22.12+
  - MySQL 8+

### 目录结构
```text
library-web_2/
├─ src/main/java/com/kyouko/libraryweb/        # 后端源码
│  ├─ controller/                              # 控制器（REST API）
│  ├─ service/                                 # 业务服务
│  ├─ mapper/                                  # Mapper 接口
│  ├─ dto/                                     # 数据传输对象
│  ├─ security/                                # 安全 & JWT 过滤器
│  └─ common|config|util|entity|domain         # 公共、配置、工具、领域
├─ src/main/resources/                         # 配置 & MyBatis XML
│  └─ application.properties                    # 数据源等配置
├─ library-ui/                                 # 前端工程（Vue 3 + Vite）
│  ├─ src/views|components|stores|router|api   # 页面/组件/状态/路由/API
│  └─ vite.config.js                           # Vite 配置
└─ pom.xml                                     # Maven 配置
```

## 快速开始

### 1) 准备环境
- 安装 Java 17+
- 安装 Node.js 20.19+（或 22.12+）
- 安装 MySQL 8+

### 2) 初始化数据库
1. 创建数据库（默认名为 `library`）：
   ```sql
   CREATE DATABASE IF NOT EXISTS library DEFAULT CHARACTER SET utf8mb4;
   ```
2. 配置数据库连接（见 `src/main/resources/application.properties`），默认如下：
   ```properties
   spring.application.name=library-web
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.url=jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2b8
   spring.datasource.username=root
   spring.datasource.password=root
   logging.level.root=INFO
   ```

> 说明：项目使用 MyBatis-Plus + XML 映射，需在 MySQL 中准备相应表结构后再启动。若你已有建表 SQL，请导入至 `library` 数据库；如需参考，请查看 `entity` 与 `resources/.../mapper/*.xml`。

### 3) 启动后端（Spring Boot）
在项目根目录执行：
```bash
./mvnw spring-boot:run
```
或打包后运行：
```bash
./mvnw -DskipTests package
java -jar target/library-web_2-0.0.1-SNAPSHOT.jar
```
默认后端服务地址：`http://localhost:8080`

### 4) 启动前端（Vue 3 + Vite）
进入前端目录并启动：
```bash
cd library-ui
pnpm install   # 或 npm install / yarn
pnpm dev       # 或 npm run dev / yarn dev
```
默认前端地址：`http://localhost:5173`

> 前端已在 `library-ui/src/utils/request.js` 中将 `baseURL` 指向 `http://localhost:8080`。后端 `CORS` 已允许 `http://localhost:5173` 访问（见 `SecurityConfig#corsConfigurationSource`）。

## 配置与约定

### 认证与调用约定
- 认证接口放行：`/api/auth/**`
- 其余接口需要携带 JWT：
  - 请求头：`Authorization: Bearer <token>`
- 登录/注册接口（示例）：
  - `POST /api/auth/register`
  - `POST /api/auth/login` → 返回 `{ token, userInfo }`

### 前端接口基址
`library-ui/src/utils/request.js`（开发环境默认）：
```js
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000,
  headers: { 'Content-Type': 'application/json' }
})
```
生产环境建议通过环境变量（如 `.env.production`）配置 `VITE_API_BASE_URL`，并在 `request.js` 中按环境读取。

### 跨域（CORS）
后端已允许 `http://localhost:5173`（Vite 默认端口）：
```java
configuration.setAllowedOrigins(List.of("http://localhost:5173"));
```
如需更改端口或部署到域名，请同步修改后端 CORS 配置与前端 `baseURL`。

## 常见问题（FAQ）
- **403/未登录被拦截**：
  - 确认已在本地存储 token，并在请求头携带 `Authorization: Bearer <token>`。
  - token 过期会被重定向到登录页，重新登录即可。
- **CORS 报错**：
  - 确认前端端口与后端 CORS 配置一致（5173 ↔ 5173）。
  - 如有自定义端口/域名，请同步修改两端配置。
- **数据库连接失败**：
  - 检查 `application.properties` 的 URL/账号/密码是否匹配本地 MySQL。
  - 确保数据库 `library` 已创建且表结构已就绪。
- **Node 版本不兼容**：
  - 项目要求 Node 20.19+ 或 22.12+（见 `library-ui/package.json#engines`）。


## 版权与许可
本项目仅用于学习与示例，若用于生产环境，请根据实际业务完善鉴权、日志、异常、审计与安全策略。


