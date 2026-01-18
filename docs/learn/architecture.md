# SOHO-Admin 项目架构说明

本文档旨在阐明 SOHO-Admin 项目的整体架构、设计理念和数据流，以帮助开发人员快速理解项目结构。

## 1. 设计理念

项目采用分层、模块化的架构，遵循“高内聚、低耦合”的原则，旨在实现一个既能作为强大单体应用快速开发，又具备向微服务平滑演进能力的后台管理系统。

核心设计思想包括：

- **模块化 (Modularity)**: 将不同功能（如核心后台管理、扩展业务、通用能力）拆分为独立的 Maven 模块，方便独立开发、测试和维护。
- **关注点分离 (Separation of Concerns)**:
    - **前后端分离**: 项目是纯后端服务，通过 RESTful API 与前端应用（Vue/React）交互。
    - **接口与实现分离**: 在业务模块中，普遍采用 `-api` 和 `-biz` 的子模块结构。`-api` 模块定义服务接口（Interfaces）和数据传输对象（DTOs），`-biz` 模块则负责具体的业务逻辑实现。这使得模块间的依赖关系更加清晰。
- **可扩展性 (Extensibility)**: `soho-extend` 目录下的所有模块都是可插拔的业务功能扩展，可以根据实际需求轻松添加或移除，而不会影响核心系统的稳定性。
- **微服务就绪 (Microservice-Ready)**: 通过 `soho-common-cloud` 和 `soho-admin-cloud-bridge` 等模块，预置了服务网关、云安全等微服务场景下的通用能力，为未来从单体架构迁移到微服务架构铺平了道路。

## 2. 核心模块职责

```mermaid
graph TD
    subgraph "外部请求 (External Requests)"
        direction LR
        User[用户/客户端]
        Frontend[前端应用 (Vue/React)]
    end

    subgraph "应用与展现层 (Application & Presentation)"
        direction TB
        WebApp[soho-admin-web<br>主应用/Web入口]
        CloudGateway[soho-common-cloud-gateway<br>微服务网关 (可选)]
    end

    subgraph "业务逻辑层 (Business Logic)"
        subgraph "核心业务 (soho-admin)"
            AdminBiz[soho-admin-biz<br>业务逻辑实现]
            AdminApi[soho-admin-api<br>业务接口定义]
        end

        subgraph "扩展功能 (soho-extend)"
            direction LR
            ExtendContent[soho-content]
            ExtendShop[soho-shop]
            ExtendOther[...]
        end
    end

    subgraph "通用组件层 (Common Components)"
        subgraph "soho-common"
            CommonCore[soho-common-core<br>核心工具类]
            CommonData[soho-common-data<br>数据访问基类]
            CommonSecurity[soho-common-security<br>安全认证]
            CommonDB[soho-common-database<br>数据库配置]
        end
    end
    
    subgraph "持久化层 (Persistence)"
        Database[(MySQL/数据库)]
    end

    %% 定义关系
    User --> Frontend
    Frontend -- "HTTP/S" --> WebApp
    Frontend -- "路由到微服务" --> CloudGateway
    CloudGateway -- "API Bridge" --> AdminBiz
    
    WebApp --> AdminBiz
    
    WebApp -- "按需装配" --> ExtendContent
    WebApp -- "按需装配" --> ExtendShop
    WebApp -- "按需装配" --> ExtendOther

    AdminBiz -- "实现" --> AdminApi
    ExtendContent -- "依赖" --> AdminApi
    ExtendShop -- "依赖" --> AdminApi
    
    AdminBiz -- "使用" --> CommonCore
    AdminBiz -- "使用" --> CommonData
    AdminBiz -- "使用" --> CommonSecurity
    
    CommonData -- "依赖" --> CommonDB
    CommonDB -- "连接" --> Database
```

- **`soho-admin-web`**:
  - **职责**: 项目的启动器和主入口。这是一个 Spring Boot 应用，它打包了所有需要的业务模块 (`-biz`) 和通用模块，并通过 Controller 对外暴露 RESTful API。
  - **角色**: 聚合器和Web服务器。

- **`soho-admin`**:
  - `soho-admin-api`: 定义核心后台管理的接口和 DTO。
  - `soho-admin-biz`: 实现核心后台管理的业务逻辑，如用户管理、角色权限、菜单管理等。

- **`soho-common`**:
  - **职责**: 存放整个项目可复用的通用代码和配置。
  - `soho-common-core`: 定义了如 `Result` 返回对象、全局异常处理、常量等基础工具。
  - `soho-common-database`: 封装了 Mybatis-Plus、数据源、事务等数据库相关配置。
  - `soho-common-security`: 封装了 Spring Security、JWT 生成与校验等安全相关逻辑。
  - `...` 其他通用模块。

- **`soho-extend`**:
  - **职责**: 提供非核心但常用的业务功能。每个子目录都是一个独立的功能单元，如内容管理 (`soho-content`)、商城 (`soho-shop`) 等。这些模块通常依赖 `soho-admin-api` 和 `soho-common`。

- **`soho-common-cloud`**:
  - **职责**: 为微服务架构提供支持。包含网关 (`-gateway`)、云安全 (`-security`) 等模块，在单体模式下通常不直接使用。

## 3. 数据流（典型请求）

以一个需要认证的 API 请求为例（例如：获取用户信息）：

1.  **请求发起**: 前端应用（或客户端）携带 Token（通常在 `Authorization` Header 中），向 `soho-admin-web` 发起 HTTP 请求。
2.  **安全过滤**: 请求首先被 `soho-common-security` 中的 Spring Security 过滤器链拦截。JWT 过滤器会解析 Token，验证其有效性，并将用户信息存入安全上下文（SecurityContext）。
3.  **控制器接收**: 请求到达 `soho-admin-web` 中对应的 `Controller`。
4.  **业务处理**:
    - `Controller` 调用 `soho-admin-biz` 中定义的 `Service` 方法。
    - `Service` 方法执行业务逻辑，例如查询数据库。它会通过 `soho-common-data` 中定义的 `Mapper` 或 `Repository` 接口与数据库交互。
5.  **数据返回**:
    - `Service` 将从数据库获取的 `Entity` 转换为 `DTO`（在 `soho-admin-api` 中定义）。
    - 最终通过 `Controller` 将 DTO 包装在 `soho-common-core` 定义的全局 `Result` 对象中，序列化为 JSON 格式返回给前端。
6.  **异常处理**: 如果在任何步骤发生异常，`soho-common-core` 中的全局异常处理器会捕获异常，并返回统一格式的错误信息。
