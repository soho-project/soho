# OAuth2 授权码模式（开发者对接）

本文档面向第三方应用开发者，介绍开放平台 OAuth2 **授权码模式**（authorization_code）的对接流程与注意事项。

基础路径：`/open/guest/oauth`

## 1. 适用场景

- **用户级授权**：需要代表具体用户访问开放平台资源。
- **前后端分离**：前端发起授权，后端安全地换取 `access_token`。

## 2. 前置条件

- 已在开放平台创建应用，获取 `app_key` / `app_secret`。
- 在应用中配置 `redirect_uri` 白名单（`open_app.redirect_uri_list`）：
  - 以英文逗号或分号分隔（也支持空格分隔）。
  - 必须 **严格匹配**（全量 URL 完全一致）。
  - 示例：
    - `https://client.example.com/callback;https://client2.example.com/cb`
    - `https://client.example.com/callback,https://client2.example.com/cb`
- 用户已登录系统，浏览器携带 `userToken` Cookie。

## 3. 授权码模式流程

1. 前端引导用户访问授权地址（`/authorize`）。
2. 服务端校验用户登录态与 `redirect_uri` 白名单。
3. 成功后重定向到 `redirect_uri`，并附带 `code` 与 `state`。
4. 应用后端使用 `code` + `app_secret` 换取 `access_token`。

## 4. 获取授权码

`GET /open/guest/oauth/authorize`

### 请求参数

- `response_type`：固定为 `code`（可缺省，默认 `code`）
- `client_id`：应用 `app_key`（也可用 `app_key` 参数名）
- `redirect_uri`：回调地址，必须命中白名单
- `state`：可选，推荐随机字符串，防 CSRF

### 示例

```http
GET /open/guest/oauth/authorize?response_type=code&client_id=APP_KEY&redirect_uri=https://client.example.com/callback&state=xyz
```

### 成功响应

服务端 **302** 重定向：

```
https://client.example.com/callback?code=CODE&state=xyz
```

### 失败响应（重定向）

若能解析 `redirect_uri`，会以重定向方式返回错误：

```
https://client.example.com/callback?error=access_denied&state=xyz
```

常见 `error`：

- `access_denied`：用户未登录或应用不可用
- `unsupported_response_type`：不支持的 `response_type`
- `invalid_client`：`client_id` 无效
- `invalid_redirect_uri`：回调地址未在白名单

> 若缺少 `redirect_uri`，服务端直接返回 `400 Bad Request`。

## 5. 使用 code 换取 access_token

`GET /open/guest/oauth/2.0`

### 请求参数

- `grant_type`：`authorization_code`
- `client_id`：应用 `app_key`（也可用 `app_key`）
- `client_secret`：应用 `app_secret`（也可用 `app_secret`）
- `code`：授权码
- `redirect_uri`：可选，若传入需与授权时一致

### 示例

```http
GET /open/guest/oauth/2.0?grant_type=authorization_code&client_id=APP_KEY&client_secret=APP_SECRET&code=CODE&redirect_uri=https://client.example.com/callback
```

### 成功返回（R 包装）

```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "access_token": "jwt",
    "token_type": "Bearer",
    "expires_in": "1296000",
    "iat": "1700000000000",
    "exp": "1701296000000"
  }
}
```

### 行为说明

- 授权码有效期 **5 分钟**，且 **一次性使用**。
- `redirect_uri` 若传入，必须与授权时一致，否则视为无效。

## 6. access_token 说明

`access_token` 为 JWT，授权码模式下包含以下主体信息：

- `authorities`：`openUser`
- `uid`：当前授权用户 ID
- `uname`：当前授权用户名
- 额外 claims：
  - `appId`：应用 ID（`open_app.id`）
  - `appKey`：应用 `app_key`

## 7. 错误码说明（/2.0 接口）

接口返回 `R.error`，常见错误码：

- `6100`（MISSING_THE_NECESSARY_REQUEST）：缺少必要参数
- `6002`（PARAM_ERROR_CODE）：参数错误（如 code 失效、密钥错误、redirect_uri 不一致）

## 8. 安全建议

- `state` 使用随机不可预测值，防止 CSRF。
- `client_secret` 只能在服务端使用，禁止放在前端页面。
- 回调地址务必使用 HTTPS。
- `code` 只能使用一次，换取失败需重新授权。
