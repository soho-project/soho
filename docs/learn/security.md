# 鉴权与安全

鉴权相关接口与工具类位于 `soho-common-security` 模块。后台模块已提供统一实现，其他业务模块在引入依赖后可按需扩展。

## 当前系统角色

- `admin`：后台管理
- `user`：普通用户
- `open`：开放平台默认角色
- `openUser`：开放平台 OAuth2 用户级授权
- `openApp`：开放平台 OAuth2 应用级授权

## 核心组件

- `TokenServiceImpl`：生成与解析 JWT
- `SohoAuthenticationProvider`：统一认证入口
- `SecurityConfig`：定义路径与角色访问规则

## 请求携带 Token

默认通过 HTTP Header 传递：

```http
Authorization: Bearer <token>
```

系统会从 `Authorization` 头中解析用户信息。

## 路由与角色建议

推荐的路径与角色映射如下：

- `/*/admin/**` → `admin`
- `/*/user/**` → `user`
- `/*/open/user/**` → `openUser`
- `/*/open/app/**` → `openApp`
- `/*/open/**` → `open`

说明：

- `open` 用于开放平台模块级访问控制
- `openUser` / `openApp` 用于区分 OAuth2 授权主体

## OAuth2 角色区分

开放平台 OAuth2 支持两类主体：

- 授权码模式 `authorization_code` → `openUser`
- 客户端模式 `client_credentials` → `openApp`

相关说明见 [oauth2.md](oauth2.md)。

## 控制器中获取当前用户

方式一，直接注入：

```java
@AuthenticationPrincipal SohoUserDetails sohoUserDetails
```

方式二，通过 `TokenServiceImpl` 获取：

```java
SohoUserDetails user = tokenService.getLoginUser(request);
```

## 自定义鉴权实现

如果某个模块不使用默认 JWT 方案，可实现以下接口：

- `SohoRoleAuthenticationService`

注册为 Spring Bean 后，系统会自动尝试解析当前请求并恢复 `SohoUserDetails`。

## 测试中启用安全配置

```java
@BeforeEach
public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
}

@SohoWithUser(id = 6, username = "197489090675871745", role = "chat")
```

## 维护建议

- 路径级鉴权只能解决角色隔离，不能替代资源归属校验。
- 用户态接口建议同时校验当前登录用户与数据归属关系。
- 对内部接口、云端接口与开放接口，建议补充网关或服务间鉴权，而不是只依赖路径规则。
