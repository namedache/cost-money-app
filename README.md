# CostMoney 记账App

个人记账应用，帮你轻松记录每一笔收支，掌控消费趋势。

## 技术栈

| 层 | 技术 |
|---|------|
| 前端 | Vue 3 + Pinia + Vue Router + ECharts + Vite |
| 后端 | Spring Boot 3.2 + Spring Cloud Gateway + Spring Security + JPA + MySQL |
| 认证 | JWT (JJWT) |
| 数据库迁移 | Flyway |
| API 文档 | SpringDoc OpenAPI (Swagger) |

## 功能

- 用户注册 / 登录（JWT 认证）
- 收支记录增删改查，支持日期筛选
- 分类管理（系统预设 + 自定义分类）
- 多账户管理（现金、银行卡等）
- 月度预算设置与超支提醒
- 快捷记账（一键记录常用消费）
- 自动记账规则
- 收支统计与趋势图表（ECharts）
- 账单 CSV 导出

## 项目结构

```
costMoneyApp-front/       # 前端 (Vue 3)
├── src/
│   ├── views/            # 页面组件
│   ├── components/       # 通用组件
│   ├── stores/           # Pinia 状态管理
│   ├── router/           # 路由配置
│   └── utils/            # 工具函数
└── vite.config.js

costMoneyApp-backend/     # 后端 (Maven 多模块微服务)
├── pom.xml               # Maven 父项目
├── costmoney-common/     # 通用配置、JWT、异常处理
├── costmoney-gateway/    # API 网关，默认端口 8080
├── costmoney-auth/       # 用户认证，默认端口 8081
├── costmoney-ledger/     # 账本、账户、导出，默认端口 8082
├── costmoney-reference/  # 分类、快捷记账、自动规则，默认端口 8083
├── costmoney-budget/     # 预算，默认端口 8084
├── costmoney-scheduler/  # 定时任务，默认端口 8085
└── costmoney-admin/      # 管理接口，默认端口 8086
```

## 快速开始

### 环境要求

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.9+

### 1. 数据库

```sql
CREATE DATABASE costmoney_auth DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE costmoney_ledger DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE costmoney_reference DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE costmoney_budget DEFAULT CHARACTER SET utf8mb4;
```

### 2. 后端

```bash
cd costMoneyApp-backend

# 配置环境变量（或直接修改各服务的 application.yml）
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key

# 首次构建公共模块和各服务
mvn clean install

# 分别启动服务。建议每个命令开一个终端窗口。
mvn -pl costmoney-auth spring-boot:run
mvn -pl costmoney-ledger spring-boot:run
mvn -pl costmoney-reference spring-boot:run
mvn -pl costmoney-budget spring-boot:run
mvn -pl costmoney-scheduler spring-boot:run
mvn -pl costmoney-admin spring-boot:run
mvn -pl costmoney-gateway spring-boot:run
```

网关启动在 http://localhost:8080，前端请求统一经过网关转发到各服务。

各服务的具体端口见项目结构说明。

### 3. 前端

```bash
cd costMoneyApp-front

npm install
npm run dev
```

前端启动在 http://localhost:5173

## 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_URL` | 单个服务的 MySQL 连接地址 | 各服务 `application.yml` 中配置的数据库 |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `root` |
| `JWT_SECRET` | JWT 签名密钥 | 内置默认值 |
| `JWT_EXPIRATION` | Token 有效期（毫秒） | `86400000`（24小时） |
| `SERVER_PORT` | 单个服务端口 | 各服务 `application.yml` 中配置的端口 |

## License

MIT
