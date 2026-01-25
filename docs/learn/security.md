鉴权
====

鉴权相关接口与工具类位于 `soho-common-security` 模块。
后台相关配置已提供统一实现，其他模块只需引入依赖并按需扩展。

当前系统主要角色：

- `admin`：后台管理
- `user`：普通用户
- `open`：开放平台用户鉴权（开放平台模块默认角色）
- `openUser`：开放平台 OAuth2 用户级授权
- `openApp`：开放平台 OAuth2 应用级授权

核心点：

- 通过 `TokenServiceImpl` 生成/解析 JWT
- 通过 `SohoAuthenticationProvider` 统一认证
- 通过 `SecurityConfig` 定义路径与角色访问规则

---

请求携带 Token
=============

默认使用 HTTP 头：

```
Authorization: Bearer <token>
```

`TokenServiceImpl` 会解析 `Authorization` 头并恢复用户信息。

---

路由与角色建议
===========

以下是建议的路径与角色映射：

- `/*/admin/**` → `admin`
- `/*/user/**` → `user`
- `/*/open/user/**` → `openUser`
- `/*/open/app/**` → `openApp`
- `/*/open/**` → `open`

说明：

- `open` 为开放平台模块默认用户鉴权角色
- `openUser` / `openApp` 用于区分 OAuth2 授权方式

---

开放平台 OAuth2 角色区分
======================

开放平台 OAuth2 分为：

- 授权码模式（authorization_code） → 用户级授权 (`openUser`)
- 客户端模式（client_credentials） → 应用级授权 (`openApp`)

Token 中包含的主体信息会随着授权模式变化（见 `docs/learn/oauth2.md`）。

---

控制器中获取认证用户
==================

```
@AuthenticationPrincipal SohoUserDetails sohoUserDetails
```

或：

```
SohoUserDetails user = tokenService.getLoginUser(request)
```

---

自定义鉴权实现（可选）
==================

如需为某个模块提供自定义鉴权（非 JWT），可实现：

- `SohoRoleAuthenticationService`

并注册为 Spring Bean，系统会自动遍历并尝试获取 `SohoUserDetails`。

---

测试中启用安全配置
===============

```
@BeforeEach
public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            // 注意：开启 security
            .apply(springSecurity())
            .build();
}

@SohoWithUser(id = 6, username = "197489090675871745", role = "chat")
```
