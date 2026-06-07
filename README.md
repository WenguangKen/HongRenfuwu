# 红人营销系统

> **Influencer Marketing System** — 面向电商商家的一站式红人 CRM、样品单/转化单、易仓商品同步、佣金结算与内容资产管理平台。

| 项目 | 说明 |
|------|------|
| 系统名称 | 红人营销系统 |
| 数据库名 | `hongrenfuwu` |
| 当前版本 | 后端 user `1.0.0` · influencer/eccang `1.0.4` · ai-agent `1.0.0` · 前端 `1.0.0` |
| 最后更新 | 2026-05 |

---

## 目录

- [1. 交付说明](#1-交付说明)
- [2. 系统架构](#2-系统架构)
- [3. 技术栈](#3-技术栈)
- [4. 目录结构](#4-目录结构)
- [5. 环境要求](#5-环境要求)
- [6. 外部依赖（非本仓库）](#6-外部依赖非本仓库)
- [7. 本地开发](#7-本地开发)
- [8. 生产部署](#8-生产部署)
- [9. 服务与端口](#9-服务与端口)
- [10. API 路由（Nginx 反向代理）](#10-api-路由nginx-反向代理)
- [11. 数据库初始化](#11-数据库初始化)
- [12. 环境变量与配置](#12-环境变量与配置)
- [13. 功能模块一览](#13-功能模块一览)
- [14. 运维手册](#14-运维手册)
- [15. 交付前检查清单](#15-交付前检查清单)
- [16. 常见问题](#16-常见问题)

---

## 1. 交付说明

### 1.1 交付包是否足够？

**结论：作为「完整源码 + 部署脚本」交付，截图中的目录结构是足够的**，已覆盖：

| 类别 | 路径 | 是否必需 |
|------|------|----------|
| 用户/权限服务 | `backend-user/` | ✅ 必需 |
| 红人业务服务 | `backend-influencer/` | ✅ 必需 |
| 易仓集成服务 | `backend-eccang-integration/` | ✅ 必需（当前仅商品同步） |
| AI 智能体服务 | `backend-ai-agent/` | 可选 |
| 前端 | `frontend/`（含 `dist/` 构建产物） | ✅ 必需 |
| 数据库脚本 | `database_scripts/` | ✅ 强烈推荐 |
| 生产编排 | `docker-compose.prod.yml` | ✅ 必需 |
| 开发编排 | `docker-compose.yml` | 推荐（本地 MySQL/Redis/MinIO） |
| 全量部署 | `auto_deploy.ps1` | 推荐 |
| 单模块部署 | `deploy_frontend.ps1` / `deploy_influencer.ps1` | 可选 |

### 1.2 交付包不包含、但生产必须另行准备

| 组件 | 说明 |
|------|------|
| **MySQL 8.0** | 生产环境使用**外部数据库**（`docker-compose.prod.yml` 注释明确 MySQL 不在 Docker 内） |
| **Redis 7** | 由 `docker-compose.prod.yml` 启动容器 |
| **MinIO / 对象存储** | 内容库视频/图片存储；Nginx 中配置了 `/influencer-storage/` 代理，需单独部署 |
| **域名 / HTTPS 证书** | 生产通常在前置 Nginx 或 CDN 层终止 SSL |
| **易仓 API 凭证** | 在系统「设置 → 易仓」中配置，不入库到代码仓库 |

### 1.3 交付前务必处理（安全）

当前部署脚本（`auto_deploy.ps1` 等）内含**服务器地址与明文密码**，交付给客户前请：

1. 替换为占位符或环境变量
2. 单独提供《部署凭据说明》文档（勿与源码同包公开传播）
3. 轮换生产数据库、Redis、SSH 密码

### 1.4 已知缺口（交付时需说明）

- `docker-compose.prod.yml` 引用 `deploy/Dockerfile.deploy`，**本仓库根目录下可能缺少 `deploy/` 文件夹**；生产服务器 `/data/influencer-marketing` 上若已有该文件则不影响运行。新环境部署可改用各模块自带的 `Dockerfile`（见 `backend-*/Dockerfile`）。
- 各后端 `application.yml` 中含开发默认密码，生产应**仅通过环境变量**注入。

---

## 2. 系统架构

```
                         ┌─────────────────────────────────────┐
                         │           浏览器 / 用户              │
                         └──────────────────┬──────────────────┘
                                            │ HTTP :80
                         ┌──────────────────▼──────────────────┐
                         │   Nginx (frontend 容器)            │
                         │   · 静态 SPA (frontend/dist)       │
                         │   · 反向代理 API                    │
                         └──┬────────┬─────────┬──────────┘
                            │        │         │
              /api/*        │ /influencer-api/* │ /api/eccang/*
                            ▼        ▼         ▼
                    ┌──────────┐ ┌──────────┐ ┌──────────┐
                    │ backend- │ │ backend- │ │ backend- │
                    │   user   │ │influencer│ │  eccang  │
                    │  :8080   │ │  :8082   │ │  :8081   │
                    └────┬─────┘ └────┬─────┘ └────┬─────┘
                         │            │            │
                         └────────────┴─────┬──────┘
                                              │
                         ┌────────────────────▼────────────────────┐
                         │  MySQL 8.0 (hongrenfuwu) — 外部主机      │
                         │  Redis 7 — Docker 容器                   │
                         │  MinIO — 外部对象存储（内容库）           │
                         └─────────────────────────────────────────┘
                                              ▲
                         ┌────────────────────┴────────────────────┐
                         │  易仓 ERP API（当前仅商品同步）            │
                         └─────────────────────────────────────────┘
```

**数据流要点：**

- **商品同步**：易仓 API → `backend-eccang-integration` → `eccang_products` 表
- **订单管理**：`backend-eccang-integration` 提供订单查询/绑定接口（样品单/转化单）
- **红人业务**：`backend-influencer` 负责档案、佣金、内容、汇款等
- **认证权限**：`backend-user` 统一登录、RBAC、系统配置

---

## 3. 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 · TypeScript · Vite 7 · Ant Design Vue 4 · Pinia · ECharts · ExcelJS |
| 后端 | Java 17 · Spring Boot 3.2.5 · Spring Data JPA · Flyway（部分模块） |
| 数据库 | MySQL 8.0 · Redis 7 |
| 对象存储 | MinIO（可配置 S3 兼容存储） |
| 部署 | Docker Compose · Nginx Alpine · PowerShell 自动化脚本 |
| 外部集成 | 易仓 ERP API |

---

## 4. 目录结构

```
Hongrenguanlixitong-main/
├── backend-user/                 # 用户、角色、权限、登录、系统配置
├── backend-influencer/           # 红人档案、佣金、内容、汇款、样品/转化业务
├── backend-eccang-integration/   # 易仓商品/订单集成（当前以商品为主）
├── backend-ai-agent/             # AI 智能体服务
├── frontend/                     # Vue 前端源码 + dist 构建产物
│   ├── src/                      # 源码
│   ├── dist/                     # 生产静态文件（npm run build 输出）
│   └── default.conf              # Nginx 站点配置（API 反向代理）
├── database_scripts/             # 数据库结构与数据 SQL 备份
│   ├── hongrenfuwu(仅结构).sql
│   └── hongrenfuwu(2026-5-25数据).sql
├── docker-compose.yml            # 本地开发：MySQL + Redis + MinIO
├── docker-compose.prod.yml       # 生产：后端服务 + Redis + Nginx 前端
├── auto_deploy.ps1               # 一键：编译 → 打包 → 上传 → Docker 重启
├── deploy_frontend.ps1           # 仅部署前端
├── deploy_influencer.ps1         # 仅部署 influencer 后端
├── .dockerignore
└── README.md                     # 本文档
```

### 后端 JAR 产物映射

| 模块 | Maven artifact | 输出 JAR |
|------|----------------|----------|
| backend-user | athluna-kms-backend | `athluna-kms-backend-1.0.0-exec.jar` |
| backend-influencer | athluna-influencer | `athluna-influencer-1.0.4.jar` |
| backend-eccang-integration | athluna-eccang-integration | `athluna-eccang-integration-1.0.4.jar` |
| backend-ai-agent | athluna-ai-agent | `athluna-ai-agent-1.0.0.jar` |

部署脚本会将 JAR 统一重命名为各模块目录下的 `app.jar`。

---

## 5. 环境要求

### 5.1 开发/构建机

| 工具 | 版本要求 |
|------|----------|
| JDK | **17+**（Temurin 推荐） |
| Maven | **3.8+** |
| Node.js | **18+**（前端 build 建议 20 LTS） |
| npm | **9+** |
| Docker Desktop | 可选（本地全栈） |
| PowerShell | 5.1+（Windows 部署脚本） |

### 5.2 生产服务器

| 工具 | 说明 |
|------|------|
| Linux | 推荐 Ubuntu 22.04+ / CentOS 7+ |
| Docker | 20.10+ |
| Docker Compose | v2 |
| 磁盘 | 建议 ≥ 50GB（含日志、Redis 持久化） |
| 内存 | 建议 ≥ 8GB（4 个 Java 服务 + Redis + Nginx） |

---

## 6. 外部依赖（非本仓库）

| 服务 | 生产典型地址 | 用途 |
|------|--------------|------|
| MySQL 主库 | 如 `192.168.5.135:3306` | 业务数据 |
| MySQL 从库 | 如 `192.168.5.130:3309` | 读库（可配置同主库） |
| MinIO | 如 `192.168.5.135:9000` | 红人内容库文件 |
| 易仓 ERP | 客户提供的易仓 API 地址 | 商品/订单 |

---

## 7. 本地开发

### 7.1 启动基础设施

```powershell
# 项目根目录
docker compose up -d
```

将启动：

- MySQL `localhost:3306`（库名 `hongrenfuwu`，root 密码见 `docker-compose.yml`）
- Redis `localhost:6379`
- MinIO API `9000` / Console `9001`

### 7.2 初始化数据库

```bash
# 仅结构（推荐开发）
mysql -h 127.0.0.1 -u root -p hongrenfuwu < database_scripts/hongrenfuwu(仅结构).sql

# 或导入含生产数据的备份（体积大，慎用）
mysql -h 127.0.0.1 -u root -p hongrenfuwu < "database_scripts/hongrenfuwu(2026-5-25数据).sql"
```

### 7.3 启动后端（按需分别启动）

```powershell
cd backend-user
mvn spring-boot:run

cd backend-influencer
mvn spring-boot:run

cd backend-eccang-integration
mvn spring-boot:run

cd backend-ai-agent
mvn spring-boot:run
```

默认 profile 为 `dev`（部分模块），数据库连接见各模块 `application.yml` / `application-dev.yml`。

### 7.4 启动前端

```powershell
cd frontend
npm install
npm run dev
```

开发服务器默认 `http://localhost:5173`，Vite 会代理 API 到后端（见 `vite.config.ts`）。

---

## 8. 生产部署

### 8.1 方式一：一键全量部署（Windows 构建机 → Linux 服务器）

```powershell
# 在项目根目录执行（需配置 SSH/SCP 到目标服务器）
.\auto_deploy.ps1
```

流程：

1. `mvn clean package` 编译各后端服务
2. `npm run build` 构建前端
3. 打包为 `deploy_pkg.tar` 并 SCP 上传
4. 远程解压到 `/data/influencer-marketing`，`docker compose -f docker-compose.prod.yml up -d`

### 8.2 方式二：仅更新前端 / 单后端

```powershell
.\deploy_frontend.ps1      # 只更新 frontend/dist + Nginx
.\deploy_influencer.ps1    # 只更新 backend-influencer
```

### 8.3 方式三：手动 Docker Compose（Linux 服务器）

```bash
# 1. 编译并放置 app.jar 到各 backend-*/ 目录
# 2. 确保 frontend/dist 与 default.conf 就位
cd /data/influencer-marketing
docker compose -f docker-compose.prod.yml build
docker compose -f docker-compose.prod.yml up -d
docker compose -f docker-compose.prod.yml ps
```

### 8.4 生产目录布局（服务器 `/data/influencer-marketing`）

```
/data/influencer-marketing/
├── backend-user/app.jar
├── backend-influencer/app.jar
├── backend-eccang-integration/app.jar
├── backend-ai-agent/app.jar
├── frontend/dist/              # SPA 静态资源
├── frontend/default.conf       # Nginx 配置
├── docker-compose.prod.yml
└── redis/data/                 # Redis 持久化（自动创建）
```

---

## 9. 服务与端口

| 容器名 | 服务 | 端口 | 健康检查 |
|--------|------|------|----------|
| influencer-frontend-prod | Nginx + SPA | **80** | HTTP |
| influencer-backend-user-prod | 用户/权限 API | 8080 | `/api/actuator/health` |
| influencer-backend-eccang-prod | 易仓集成 | 8081 | 内部 |
| influencer-backend-influencer-prod | 红人业务 | 8082 | `/influencer-api/actuator/health` |
| influencer-backend-ai-agent-prod | AI 智能体 | 8085 | 内部 |
| influencer-redis-prod | Redis | 6379 | — |

> 对外通常只暴露 **80/443**；8080–8083 可在防火墙层限制为内网访问。

---

## 10. API 路由（Nginx 反向代理）

| 前端请求路径 | 转发目标 | 说明 |
|--------------|----------|------|
| `/` | 静态 `frontend/dist` | Vue SPA |
| `/api/*` | `backend-user:8080` | 登录、用户、角色、权限、系统设置 |
| `/influencer-api/*` | `backend-influencer:8082` | 红人、佣金、内容、汇款 |
| `/api/eccang/*` | `backend-eccang:8081` | 易仓商品/订单接口 |
| `/ai-agent-api/*` | `backend-ai-agent:8085` | AI 智能体对话 |
| `/influencer-storage/*` | MinIO（需按环境修改 IP） | 内容库媒体文件 |

配置源文件：`frontend/default.conf`（部署前请修改 MinIO 地址与 CORS 域名）。

---

## 11. 数据库初始化

### 11.1 脚本说明

| 文件 | 内容 | 适用场景 |
|------|------|----------|
| `hongrenfuwu(仅结构).sql` | 57 张表 DDL + 索引 | 新环境建库 |
| `hongrenfuwu(2026-5-25数据).sql` | 结构 + 业务数据 | 灾备恢复、测试环境克隆 |

### 11.2 建库命令

```sql
CREATE DATABASE IF NOT EXISTS hongrenfuwu
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

```bash
mysql -h <DB_HOST> -u root -p hongrenfuwu < database_scripts/hongrenfuwu(仅结构).sql
```

### 11.3 核心表（速查）

| 域 | 主要表 |
|----|--------|
| 红人 | `influencer`, `influencer_social_media`, `influencer_address` |
| 订单 | `orders`, `influencer_sample_order`, `influencer_conversion_order` |
| 佣金 | `influencer_commission_order`, `commission_payout`, `influencer_balance` |
| 易仓 | `eccang_stores`, `eccang_products`, `eccang_product_variants` |
| 权限 | `users`, `roles`, `permissions`, `user_roles`, `role_permissions` |
| 标签 | `system_tags`（主用） |
| 内容 | `influencer_content`, `content_storage_config` |

> 数据库存在部分历史冗余表/字段，详见内部《数据库字段统一性审查报告》；不影响当前运行。

---

## 12. 环境变量与配置

生产环境通过 `docker-compose.prod.yml` 注入：

| 变量 | 说明 | 示例 |
|------|------|------|
| `DB_HOST` | MySQL 主库地址 | `192.168.5.135` |
| `DB_PORT` | 端口 | `3306` |
| `DB_NAME` | 库名 | `hongrenfuwu` |
| `DB_USER` / `DB_PASSWORD` | 主库账号 | — |
| `DB_SLAVE_HOST` / `DB_SLAVE_PORT` | 从库 | 可与主库相同 |
| `DB_SLAVE_USER` / `DB_SLAVE_PASSWORD` | 从库账号 | — |
| `REDIS_HOST` | Redis 主机 | `redis`（Compose 服务名） |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `REDIS_PASSWORD` | Redis 密码 | 见 compose 文件 |
| `SPRING_PROFILES_ACTIVE` | Spring 环境 | `prod` |
| `ECCANG_INTEGRATION_URL` | influencer 调易仓服务 | `http://backend-eccang:8081` |
| `USER_SERVICE_URL` | influencer 调 user 服务 | `http://backend-user:8080` |

易仓 API 凭证在**系统管理界面 → 易仓设置**中配置，存入 `eccang_stores` / `system_configs` 表（加密存储）。

---

## 13. 功能模块一览

| 模块 | 路由前缀 | 主要能力 |
|------|----------|----------|
| 工作台 | `/dashboard` | 数据概览 |
| 红人列表 | `/influencer/list` | 档案 CRUD、批量操作、导出、佣金率 |
| 红人资源池 | `/influencer/pool` | 待筛选红人 |
| 样品单 | `/order/sample` | 寄样订单管理 |
| 转化单 | `/order/conversion` | 折扣码归因订单 |
| 折扣码 | `/discount/list` | 折扣绑定红人 |
| 商品 | `/product/list` | 易仓商品同步 |
| 佣金配置 | `/commission/*` | 分佣规则、佣金单、打款 |
| 汇款 | `/finance/remittance` | 红人汇款任务 |
| 内容库 | `/content/*` | 待审核 / 内容库 |
| 系统管理 | `/system/*` | 用户、角色、标签、易仓、存储 |

---

## 14. 运维手册

### 14.1 常用命令

```bash
# 查看容器状态
docker compose -f docker-compose.prod.yml ps

# 查看日志
docker compose -f docker-compose.prod.yml logs -f backend-influencer

# 重启单个服务
docker compose -f docker-compose.prod.yml restart backend-eccang

# 停止全部
docker compose -f docker-compose.prod.yml stop
```

### 14.2 订单同步

- **商品同步**：`backend-eccang-integration` 提供手动/定时商品同步
- **订单查询**：通过易仓集成服务查询样品单/转化单

### 14.3 日志位置

- 容器标准输出：`docker logs <container>`
- 部分服务配置：`/var/log/influencer-marketing/`（见 `application-prod.yml`）

### 14.4 备份建议

| 对象 | 频率 | 方式 |
|------|------|------|
| MySQL | 每日 | mysqldump / 主从复制 |
| Redis | 按需 | RDB/AOF（`redis/data` 卷） |
| MinIO | 每日 | 桶级别复制 |
| 代码/配置 | 发版时 | Git tag + 部署包归档 |

---

## 15. 交付前检查清单

- [ ] 移除/脱敏 `auto_deploy.ps1` 中的 SSH 密码与内网 IP
- [ ] 确认 `frontend/dist` 为最新 build
- [ ] 确认各后端可 `mvn clean package -DskipTests` 通过
- [ ] 提供数据库初始化脚本（`database_scripts/`）
- [ ] 提供生产环境变量清单（勿写明文密码进 README）
- [ ] 确认 `deploy/Dockerfile.deploy` 或改用各模块 `Dockerfile`
- [ ] 修改 `default.conf` 中 MinIO 地址与 CORS 域名为客户环境
- [ ] 提供初始管理员账号说明（或首次登录引导）
- [ ] 说明易仓 API 凭证配置方式
- [ ] （可选）附《数据库字段审查报告》供二期优化参考

### 建议交付物清单

```
交付包/
├── 源码（本仓库全部目录）
├── README.md（本文档）
├── 部署凭据说明.pdf（单独密送，含 DB/Redis/SSH/易仓）
├── 数据库脚本/
└── （可选）已编译 deploy_pkg.tar 或 Docker 镜像 tar
```

**可不打包以减小体积**（接收方自行安装）：

- `frontend/node_modules/`
- `**/target/`

---

## 16. 常见问题

### Q1：部署后前端 502？

检查 4 个 backend 容器是否均为 `Up`，以及 Nginx `default.conf` 中 upstream 服务名是否与 compose 一致。

### Q2：商品/订单不同步？

1. 检查 `eccang_stores` 中店铺状态为 `active`
2. 查看 `backend-eccang` 日志是否有 API 错误
3. 确认易仓 API 凭证未过期

### Q3：登录失败？

确认 `users` 表有数据；Redis 可连接；`backend-user` 健康检查通过。

### Q4：内容库视频无法播放？

检查 MinIO 是否可达；`default.conf` 中 `/influencer-storage/` 代理地址是否正确；CORS 域名是否匹配。

### Q5：本地 build 前端 TypeScript 报错？

```powershell
cd frontend
npm install
npm run build
```

需 Node 18+ 与足够内存（建议 4GB+）。

---

## 许可证与版权

本系统为标准红人营销系统。交付后使用权、二次开发权以合同约定为准。

---

## 联系方式

| 角色 | 说明 |
|------|------|
| 技术支持 | （交付时填写） |
| 运维值班 | （交付时填写） |
| 紧急联系 | （交付时填写） |

---

*文档版本：1.0 · 生成日期：2026-05-25*
