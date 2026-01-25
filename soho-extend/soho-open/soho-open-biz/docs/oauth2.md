# OAuth2（开放平台）

基础路径：`/open/guest/oauth`

当前支持两种模式：

- 授权码模式（authorization_code）
- 客户端模式（client_credentials）

redirect_uri 校验使用 `open_app.redirect_uri_list`（严格匹配）。
该字段为英文逗号或分号分隔的 URL 列表。

示例 `redirect_uri_list`：

- `https://client.example.com/callback;https://client2.example.com/cb`
- `https://client.example.com/callback,https://client2.example.com/cb`

---

## 1）授权码模式

### 1.1 获取授权码

`GET /open/guest/oauth/authorize`

请求参数：

- `response_type`：固定为 `code`
- `client_id`：即 `app_key`
- `redirect_uri`：回调地址，必须命中白名单
- `state`：可选，原样回传
- `userToken`：Cookie 中的用户登录 token（用于获取当前授权用户）

示例：

```
GET /open/guest/oauth/authorize?response_type=code&client_id=APP_KEY&redirect_uri=https://client.example.com/callback&state=xyz
```

行为：

- 校验 app 状态与回调地址
- 校验当前用户登录状态（从 `userToken` 解析用户）
- 生成短期 `code`（默认 5 分钟有效）
- 重定向到：
  `redirect_uri?code=...&state=...`

### 1.2 使用 code 换取 access_token

`GET /open/guest/oauth/2.0`

请求参数：

- `grant_type`：`authorization_code`
- `client_id`：即 `app_key`
- `client_secret`：即 `app_secret`
- `code`：授权码
- `redirect_uri`：需与授权时一致

示例：

```
GET /open/guest/oauth/2.0?grant_type=authorization_code&client_id=APP_KEY&client_secret=APP_SECRET&code=CODE&redirect_uri=https://client.example.com/callback
```

返回（R 包装）：

- `payload.access_token`：JWT 字符串（见下方说明）
- `payload.token_type`：`Bearer`
- `payload.expires_in`：秒（由 `exp - iat` 计算）
- `payload.iat`：签发时间（毫秒时间戳）
- `payload.exp`：过期时间（毫秒时间戳）

---

## 2）客户端模式

`GET /open/guest/oauth/2.0`

请求参数：

- `grant_type`：`client_credentials`
- `client_id`：即 `app_key`
- `client_secret`：即 `app_secret`

示例：

```
GET /open/guest/oauth/2.0?grant_type=client_credentials&client_id=APP_KEY&client_secret=APP_SECRET
```

返回格式与授权码模式一致。

---

## access_token 的准确说明（基于当前代码）

`access_token` 为 JWT 字符串，由 `TokenServiceImpl` 生成；两种授权方式在 **主体（uid/权限）** 上不同：

### 授权码模式（authorization_code）

- `uid`：当前授权用户 ID（来源于 `userToken` 解析出的用户）
- `uname`：当前授权用户名
- `authorities`：`openUser`

### 客户端模式（client_credentials）

- `uid`：应用 ID（`open_app.id`）
- `uname`：应用 `app_key`
- `authorities`：`openApp`

### 两种模式共同包含的附加 claims

- `appId`：`open_app.id`
- `appKey`：`open_app.app_key`

### 过期时间

由 `TokenServiceImpl#getTokenLeaseTerm()` 计算：
`3600 * 365` 秒（约 365 小时 ≈ 15 天）。

---

## 备注

- `state` 为不透明参数，用于防止 CSRF，服务端原样回传。
- 授权码为一次性使用，兑换后立即失效。
