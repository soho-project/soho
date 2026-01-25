# OAuth2（系统级说明）

本文档描述系统内 OAuth2 的使用方式与角色区分，适用于开放平台对接与权限设计。

## 1. 支持的授权方式

- 授权码模式（authorization_code）：用户级授权，代表具体用户
- 客户端模式（client_credentials）：应用级授权，代表应用自身

## 2. 角色区分与权限

系统内使用不同的角色标识：

- `openUser`：用户级（授权码模式）
- `openApp`：应用级（客户端模式）

在网关/安全配置中可按路径区分权限，例如：

- `/*/open/user/**` → `openUser`
- `/*/open/app/**` → `openApp`
- `/*/open/**` → `open`（兼容旧接口或公共接口）

## 3. 授权码模式流程

1）用户在前端登录后，携带 `userToken` Cookie 发起授权：

```
GET /open/guest/oauth/authorize?response_type=code&client_id=APP_KEY&redirect_uri=CALLBACK&state=STATE
```

2）服务端校验：

- userToken 是否有效
- 应用状态
- redirect_uri 是否在白名单

3）生成 code（默认 5 分钟有效），重定向回：

```
CALLBACK?code=...&state=...
```

4）客户端使用 code 换取 token：

```
GET /open/guest/oauth/2.0?grant_type=authorization_code&client_id=APP_KEY&client_secret=APP_SECRET&code=CODE&redirect_uri=CALLBACK
```

## 4. 客户端模式流程

应用直接使用 app_key/app_secret 换取 token：

```
GET /open/guest/oauth/2.0?grant_type=client_credentials&client_id=APP_KEY&client_secret=APP_SECRET
```

## 5. access_token 说明

access_token 为 JWT。根据授权方式，主体信息不同：

### 授权码模式（authorization_code）

- `uid`：当前授权用户 ID
- `uname`：当前授权用户名
- `authorities`：`openUser`

### 客户端模式（client_credentials）

- `uid`：应用 ID（open_app.id）
- `uname`：应用 app_key
- `authorities`：`openApp`

### 共同的附加 claims

- `appId`：open_app.id
- `appKey`：open_app.app_key

## 6. redirect_uri 白名单

字段：`open_app.redirect_uri_list`

格式：英文逗号或分号分隔

示例：

- `https://client.example.com/callback;https://client2.example.com/cb`
- `https://client.example.com/callback,https://client2.example.com/cb`

仅允许严格匹配。

## 7. 安全建议

- `state` 使用随机不可预测值，用于 CSRF 防护
- 授权码一次性使用，兑换后立即失效
- 对用户级接口严格要求 `openUser` 权限
- 对应用级接口严格要求 `openApp` 权限
