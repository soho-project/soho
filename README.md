# SOHO 后台管理系统

<div align="center">

[![star](https://gitee.com/fang/soho/badge/star.svg?theme=dark)](https://gitee.com/fang/soho/stargazers)
[![fork](https://gitee.com/fang/soho/badge/fork.svg?theme=dark)](https://gitee.com/fang/soho/members)

**一款基于 Spring Boot、MyBatis-Plus 和 Spring Security 的现代化、高扩展性的后台管理系统脚手架。**

</div>

SOHO Admin 旨在提供一个简单易用、功能丰富的开发基础，帮助开发者快速搭建稳定可靠的企业级后台服务。项目集成了用户权限管理、动态菜单、代码生成、多渠道文件上传等一系列常用功能，并提供了清晰的模块化结构，支持从单体到微服务的平滑演进。

---

## 🚀 快速开始 (Quick Start)

请遵循以下步骤在你的本地环境启动项目。

### 1. 环境准备 (Prerequisites)

-   **Java 11**: 确保你的 JDK 版本为 11。
-   **Maven**: 用于项目构建和依赖管理。
-   **MySQL**: 推荐使用 5.7 或更高版本。
-   **Redis**: 用于缓存和会话管理。

### 2. 配置 (Configuration)

1.  **数据库配置**:
    -   在你的本地 MySQL 中创建一个新的数据库，例如 `soho_admin`。
    -   找到并导入最新的数据库脚本文件来初始化表结构。脚本位于 `docs/databases/` 目录下，例如 `soho_admin-2026_01_14_17_17_52.sql`。
    -   打开核心配置文件 `soho-admin-web/src/main/resources/application.yml`。
    -   修改 `spring.datasource.datasource.master`下的 `url`, `username`, 和 `password`，使其指向你本地的数据库。

2.  **Redis 配置**:
    -   在同一个 `application.yml` 文件中，找到 `spring.redis` 部分。
    -   修改 `host` 和 `port`，使其指向你本地的 Redis 服务器。

### 3. 构建 (Build)

在项目根目录下，使用 Maven 执行 clean install 命令来构建整个项目。

```bash
mvn clean install
```

### 4. 运行 (Run)

项目有多种运行方式：

-   **通过 IDE**: 找到 `soho-admin-web` 模块中的 `work.soho.web.AdminApplication` 类，直接运行它的 `main` 方法。
-   **通过命令行**: 
    ```bash
    java -jar soho-admin-web/target/soho-admin-web-1.0-SNAPSHOT.jar
    ```

成功启动后，你可以在浏览器中访问 `http://localhost:6677`。

---

## ✨ 功能列表 (Features)

-   **用户管理**: 后台用户配置管理。
-   **菜单管理**: 前后端菜单资源管理，用于权限控制和导航。
-   **角色管理**: 精细化的角色与权限控制。
-   **系统配置**: 动态系统配置，即时生效。
-   **字典管理**: 方便前端获取业务字典数据。
-   **日志系统**: 包括登录日志、操作日志，增强系统可追溯性和安全性。
-   **定时任务**: 在线配置、管理定时任务，支持在线测试。
-   **代码生成**: 基于 Groovy 的自定义代码生成器，快速生成 CRUD 代码。
-   **审批流**: 动态配置审批流程，满足企业 OA 需求。
-   **统一文件上传**: 支持多渠道存储（本地、OSS、S3等），通过引用计数节约存储空间。
-   **微服务支持**: 天然的模块化设计，可通过 API 桥接，轻松解耦为微服务架构。
-   **开放平台**: 集成开放平台功能，方便构建对外 API。

---

## 🛠️ 技术栈 (Tech Stack)

-   **后端**: Spring Boot, Spring Security, MyBatis-Plus, Redisson, Log4j2, Maven
-   **数据库**: MySQL, Redis
-   **前端 (分离部署)**: 提供了多种前端实现，仓库地址见[在线预览](#-在线预览-live-demos)部分。

---

## 📚 文档 (Documentation)

-   **[在线文档](https://docs.soho.work/)**: 官方的公开文档网站。
-   **[本地技术文档](docs/learn/main.md)**: 项目仓库内更偏向开发侧的详细技术文档。

---

## 👀 在线预览 (Live Demos)

-   [React (Ant Design 5)](https://adminv2.demo.soho.work/login/) - (账号: admin / 123456)
-   [Vue (Ant Design Vue)](https://admin-vue.demo.soho.work/login/) - (账号: admin / 123456)
-   [开放平台](https://open.demo.soho.work/) - (账号: P15873164076 / 123456)
-   [旧版预览](https://www.soho.work/) - (已停止更新) (账号: admin / 123456)

---

## 🤝 贡献 (Contributing)

我们欢迎任何形式的贡献！

1.  **Fork** 本仓库。
2.  创建你的功能分支 (`git checkout -b feature/AmazingFeature`)。
3.  提交你的代码 (`git commit -m 'Add some AmazingFeature'`)。
4.  在提交前，请务必运行代码格式化命令以保证风格统一：
    ```bash
    mvn spring-javaformat:apply
    ```
5.  **Push** 到你的分支 (`git push origin feature/AmazingFeature`)。
6.  创建一个 **Pull Request**。

---

## 💬 联系我们 (Contact Us)

-   **QQ群**: 569407926
-   <img src="docs/images/qq.jpg" alt="QQ群" width="200">