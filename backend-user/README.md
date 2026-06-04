# 红人营销系统 - 用户权限后端服务

## 📋 项目概述

红人营销系统用户权限后端服务，基于 Spring Boot 3.x，提供用户管理、角色管理、权限管理等核心功能。

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 配置说明

1. **数据库配置**

   编辑 `src/main/resources/application-dev.yml`：

   ```yaml
   spring:
     datasource:
       master:
         url: jdbc:mysql://localhost:3306/hongrenfuwu
         username: root
         password: ${DB_PASSWORD}
       slave:
         url: jdbc:mysql://localhost:3306/hongrenfuwu
         username: root
         password: ${DB_PASSWORD}
   ```

2. **Redis配置**

   ```yaml
   spring:
     redis:
       host: localhost
       port: 6379
       password: ${REDIS_PASSWORD}
   ```

3. **JWT配置**

   ```yaml
   jwt:
     secret: ${JWT_SECRET}  # 建议使用环境变量
     expiration: 14400  # 4小时
   ```

4. **AES加密配置**

   ```yaml
   encryption:
     aes-key: ${AES_KEY}  # 32字节密钥，建议使用环境变量
   ```

### 启动步骤

1. **创建数据库**

   ```bash
   mysql -u root -p
   CREATE DATABASE hongrenfuwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **执行数据库迁移**

   Flyway 会在应用启动时自动执行迁移脚本，或手动执行：

   ```bash
   mysql -u root -p hongrenfuwu < mysql/schema/99_init_all.sql
   mysql -u root -p hongrenfuwu < mysql/seed/99_init_all.sql
   ```

3. **启动应用**

   ```bash
   mvn spring-boot:run
   ```

   或

   ```bash
   mvn clean package
   java -jar target/athluna-kms-1.0.0.jar
   ```

## 📚 API文档

详细的API接口文档请参考：[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

### Swagger文档

启动应用后，访问：`http://localhost:8080/swagger-ui.html`

## 🔧 功能模块

### 1. 认证模块 (`auth`)

- 用户登录（邮箱+密码+验证码）
- 用户登出
- JWT Token生成和验证
- 单点登录（SSO）
- 会话管理
- 限流保护

### 2. 用户管理模块 (`user`)

- 用户CRUD操作
- 用户状态管理
- 密码修改
- 用户操作日志

### 3. 角色管理模块 (`role`)

- 角色CRUD操作
- 角色权限分配
- 角色状态管理

### 4. 权限管理模块 (`permission`)

- 权限查询
- 用户权限聚合
- 权限缓存

### 5. 系统管理模块 (`system`)

- 数据库配置管理（主库/从库）
- 数据库连接测试

## 🔐 安全特性

### 数据加密

- **邮箱**: AES-256-GCM加密 + SHA256哈希（快速查找）
- **电话**: AES-256-GCM加密
- **密码**: BCrypt哈希（不可还原）
- **验证码**: SHA256哈希存储
- **数据库密码**: AES-256-GCM加密

### 单点登录（SSO）

- 每个用户只能有一个活跃会话
- 新登录会自动失效旧会话
- 数据库层强制约束（生成列 + 唯一键）

### 限流保护

- IP限流：每分钟最多5次登录尝试
- 邮箱限流：每小时最多10次登录尝试

### 会话管理

- Token有效期：4小时
- 会话超时：4小时未活动自动登出
- 定时任务每5分钟检查超时会话

## 📊 数据库设计

### 核心表结构

- `users`: 用户表
- `roles`: 角色表
- `permissions`: 权限表
- `user_roles`: 用户角色关联表
- `role_permissions`: 角色权限关联表
- `user_sessions`: 用户会话表
- `captcha_records`: 验证码记录表
- `user_logs`: 用户操作日志表（按月分区）
- `login_attempts`: 登录尝试记录表（按月分区）
- `database_configs`: 数据库配置表

### 分区策略

- `user_logs` 和 `login_attempts` 表按月分区
- 分区维护脚本：`mysql/ops/partition_maintenance.sql`
- 建议每月1号执行分区维护存储过程

## 🔄 权限字典同步

### 同步脚本

1. **SQL脚本**: `mysql/ops/sync_permissions.sql`
   - 手动执行，检查前端和数据库权限差异
   - 支持差异报告和同步操作

2. **Node.js脚本**: `scripts/sync_permissions_from_frontend.js`
   - 自动从前端 `permissions.ts` 同步权限定义
   - 支持差异检查和自动同步

### 使用方式

```bash
# 1. 检查差异
node scripts/sync_permissions_from_frontend.js

# 2. 自动同步（需要设置环境变量）
AUTO_SYNC=true node scripts/sync_permissions_from_frontend.js
```

## 📝 开发规范

### 代码结构

```
backend/
├── src/main/java/com/athlunakms/
│   ├── common/          # 公共模块
│   │   ├── config/      # 配置类
│   │   ├── constant/    # 常量
│   │   ├── dto/         # 数据传输对象
│   │   ├── exception/   # 异常处理
│   │   ├── security/    # 安全相关
│   │   └── util/        # 工具类
│   ├── auth/            # 认证模块
│   ├── user/            # 用户模块
│   ├── role/            # 角色模块
│   ├── permission/      # 权限模块
│   └── system/          # 系统管理模块
└── src/main/resources/
    ├── application.yml
    └── db/migration/    # Flyway迁移脚本
```

### 命名规范

- Controller: `XxxController`
- Service: `XxxService`
- Repository: `XxxRepository`
- Entity: `Xxx`
- DTO: `XxxRequest`, `XxxResponse`

### API版本

- 所有API路径以 `/api/v1.0` 开头
- 未来版本向后兼容

## 🧪 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 📦 部署

### Docker部署

```bash
# 构建镜像
docker build -t influencer-user:1.0.0 .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e DB_PASSWORD=xxx \
  -e REDIS_PASSWORD=xxx \
  -e JWT_SECRET=xxx \
  -e AES_KEY=xxx \
  influencer-user:1.0.0
```

### 生产环境配置

1. 使用环境变量管理敏感信息
2. 启用HTTPS
3. 配置CORS白名单
4. 启用日志审计
5. 配置监控和告警

## 📄 许可证

Apache 2.0

## 👥 维护团队

红人营销系统开发团队
