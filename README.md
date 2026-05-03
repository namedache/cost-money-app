# CostMoney 记账App

个人记账应用，帮你轻松记录每一笔收支，掌控消费趋势。

## 技术栈

| 层 | 技术 |
|---|------|
| 前端 | Vue 3 + Pinia + Vue Router + ECharts + Vite |
| 后端 | Spring Boot 3.2 + Spring Security + JPA + MySQL |
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
costMoneyApp/             # 前端 (Vue 3)
├── src/
│   ├── views/            # 页面组件
│   ├── components/       # 通用组件
│   ├── stores/           # Pinia 状态管理
│   ├── router/           # 路由配置
│   └── utils/            # 工具函数
└── vite.config.js

costMoneyApp-backend/     # 后端 (Spring Boot)
├── src/main/java/
│   └── com/example/costmoneyapp/
│       ├── config/       # 安全、JWT、CORS 配置
│       ├── controller/   # REST 控制器
│       ├── service/      # 业务逻辑
│       ├── entity/       # JPA 实体
│       ├── repository/   # 数据访问层
│       └── dto/          # 数据传输对象
├── src/main/resources/
│   ├── db/migration/     # Flyway 迁移脚本
│   └── application.yml
└── pom.xml
```

## 快速开始

### 环境要求

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.9+

### 1. 数据库

```sql
CREATE DATABASE cost_money_app DEFAULT CHARACTER SET utf8mb4;
```

### 2. 后端

```bash
cd costMoneyApp-backend

# 配置环境变量（或直接修改 application.yml）
export DB_URL=jdbc:mysql://localhost:3306/cost_money_app?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key

mvn spring-boot:run
```

后端启动在 http://localhost:8080

API 文档：http://localhost:8080/swagger-ui.html

### 3. 前端

```bash
cd costMoneyApp

npm install
npm run dev
```

前端启动在 http://localhost:5173

## 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_URL` | MySQL 连接地址 | `jdbc:mysql://localhost:3306/cost_money_app?...` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `root` |
| `JWT_SECRET` | JWT 签名密钥 | 内置默认值 |
| `JWT_EXPIRATION` | Token 有效期（毫秒） | `86400000`（24小时） |
| `SERVER_PORT` | 后端端口 | `8080` |

## License

MIT
