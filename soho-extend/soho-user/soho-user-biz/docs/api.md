# soho-user-biz API 文档

说明：示例仅展示关键字段；真实返回结构为 `R<T>`，字段为 `code`/`msg`/`payload`。

## 管理端：用户信息
基础路径：`/user/admin/userInfo`

### GET /list
用户列表（分页）

请求示例：
```http
GET /user/admin/userInfo/list?username=tom&status=1&startTime=2024-01-01 00:00:00&endTime=2024-12-31 23:59:59
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "code": "U202401010001",
        "username": "tom",
        "nickname": "Tom",
        "email": "tom@example.com",
        "phone": "13800000000",
        "avatar": "https://.../avatar.png",
        "status": 1,
        "age": 20,
        "sex": 1,
        "level": 1,
        "referrerId": 0,
        "createdTime": "2024-01-01 10:00:00",
        "updatedTime": "2024-01-02 10:00:00"
      }
    ]
  }
}
```

### GET /{id}
用户详情

请求示例：
```http
GET /user/admin/userInfo/1
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 1,
    "username": "tom",
    "phone": "13800000000",
    "status": 1
  }
}
```

### POST /
新增用户

请求示例：
```http
POST /user/admin/userInfo
Content-Type: application/json

{
  "username": "tom",
  "password": "123456",
  "phone": "13800000000",
  "email": "tom@example.com"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### PUT /
修改用户

请求示例：
```http
PUT /user/admin/userInfo
Content-Type: application/json

{
  "id": 1,
  "nickname": "Tom",
  "status": 1
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### DELETE /{ids}
删除用户

请求示例：
```http
DELETE /user/admin/userInfo/1,2,3
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### GET /options
用户选项（id -> username）

请求示例：
```http
GET /user/admin/userInfo/options
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "1": "tom",
    "2": "lucy"
  }
}
```

### GET /queryLevelOptions
用户等级字典

请求示例：
```http
GET /user/admin/userInfo/queryLevelOptions
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": [
    {"key": 1, "value": "普通"},
    {"key": 2, "value": "VIP"}
  ]
}
```

---

## 管理端：用户三方认证
基础路径：`/user/admin/userOauth`

### GET /list
三方认证列表（分页）

请求示例：
```http
GET /user/admin/userOauth/list?uid=1&type=2
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "total": 1,
    "list": [
      {
        "id": 10,
        "openId": "openid-xxx",
        "unionId": "union-xxx",
        "uid": 1,
        "type": 2,
        "createdTime": "2024-01-01 10:00:00",
        "updatedTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### GET /{id}
三方认证详情

请求示例：
```http
GET /user/admin/userOauth/10
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 10,
    "uid": 1,
    "type": 2,
    "openId": "openid-xxx"
  }
}
```

### POST /
新增三方认证

请求示例：
```http
POST /user/admin/userOauth
Content-Type: application/json

{
  "uid": 1,
  "type": 2,
  "openId": "openid-xxx",
  "unionId": "union-xxx"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### PUT /
修改三方认证

请求示例：
```http
PUT /user/admin/userOauth
Content-Type: application/json

{
  "id": 10,
  "unionId": "union-new"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### DELETE /{ids}
删除三方认证

请求示例：
```http
DELETE /user/admin/userOauth/10
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### GET /exportExcel
导出 Excel

请求示例：
```http
GET /user/admin/userOauth/exportExcel?uid=1&type=2
```

返回示例：
```json
[
  {
    "id": 10,
    "uid": 1,
    "type": 2,
    "openId": "openid-xxx",
    "unionId": "union-xxx"
  }
]
```

### POST /importExcel
导入 Excel

请求示例：
```http
POST /user/admin/userOauth/importExcel
Content-Type: multipart/form-data

file=@/path/user_oauth.xlsx
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": null
}
```

---

## 管理端：三方认证类型
基础路径：`/user/admin/userOauthType`

### GET /list
三方认证类型列表（分页）

请求示例：
```http
GET /user/admin/userOauthType/list?name=GitHub&status=1
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "name": "github",
        "title": "GitHub",
        "logo": "https://.../github.png",
        "clientId": "xxx",
        "clientSecret": "yyy",
        "status": 1,
        "adapter": 9,
        "createdTime": "2024-01-01 10:00:00",
        "updatedTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### GET /{id}
三方认证类型详情

请求示例：
```http
GET /user/admin/userOauthType/1
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 1,
    "name": "github",
    "title": "GitHub",
    "status": 1,
    "adapter": 9
  }
}
```

### POST /
新增三方认证类型

请求示例：
```http
POST /user/admin/userOauthType
Content-Type: application/json

{
  "name": "github",
  "title": "GitHub",
  "logo": "https://.../github.png",
  "clientId": "xxx",
  "clientSecret": "yyy",
  "status": 1,
  "adapter": 9
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### PUT /
修改三方认证类型

请求示例：
```http
PUT /user/admin/userOauthType
Content-Type: application/json

{
  "id": 1,
  "title": "GitHub (new)"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### DELETE /{ids}
删除三方认证类型

请求示例：
```http
DELETE /user/admin/userOauthType/1,2,3
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### GET /exportExcel
导出 Excel

请求示例：
```http
GET /user/admin/userOauthType/exportExcel?status=1
```

返回示例：
```json
[
  {
    "id": 1,
    "name": "github",
    "title": "GitHub",
    "status": 1,
    "adapter": 9
  }
]
```

### POST /importExcel
导入 Excel

请求示例：
```http
POST /user/admin/userOauthType/importExcel
Content-Type: multipart/form-data

file=@/path/user_oauth_type.xlsx
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": null
}
```

---

## 游客态：会员鉴权
基础路径：`/guest/user/auth`

### POST /login
用户名/手机号/邮箱 + 密码登录

请求示例：
```http
POST /guest/user/auth/login
Content-Type: application/json

{
  "username": "tom",
  "password": "123456",
  "captcha": "ab12"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "token": "xxx",
    "iat": "1700000000000",
    "exp": "1731536000000"
  }
}
```

### POST /mobileLogin
手机号 + 短信验证码登录（`username` 传手机号）

请求示例：
```http
POST /guest/user/auth/mobileLogin
Content-Type: application/json

{
  "username": "13800000000",
  "captcha": "123456"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "token": "xxx",
    "iat": "1700000000000",
    "exp": "1731536000000"
  }
}
```

### POST /sendSms
发送短信验证码

请求示例：
```http
POST /guest/user/auth/sendSms
Content-Type: application/json

{
  "phone": "13800000000"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": null
}
```

### POST /captcha
获取图形验证码（返回图片流）

请求示例：
```http
POST /guest/user/auth/captcha
```

返回示例：
```
<binary image>
```

### POST /register
用户注册

请求示例：
```http
POST /guest/user/auth/register
Content-Type: application/json

{
  "username": "tom",
  "nickname": "Tom",
  "password": "123456",
  "email": "tom@example.com",
  "phone": "13800000000",
  "verifyCode": "123456",
  "inviteCode": 0
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 1,
    "username": "tom",
    "phone": "13800000000"
  }
}
```

---

## 游客态：三方认证登录
基础路径：`/user/guest/userOauth`

### POST /login
三方认证登录

请求示例：
```http
POST /user/guest/userOauth/login
Content-Type: application/json

{
  "code": "wx-code-xxx"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "token": "xxx",
    "iat": "1700000000000",
    "exp": "1731536000000"
  }
}
```

### GET /login
三方认证登录（code 作为 query 参数）

请求示例：
```http
GET /user/guest/userOauth/login?code=wx-code-xxx
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "token": "xxx",
    "iat": "1700000000000",
    "exp": "1731536000000"
  }
}
```

### GET /render/{type}
跳转到第三方授权页

请求示例：
```http
GET /user/guest/userOauth/render/9
```

返回示例：
```
302 Redirect -> 第三方授权地址
```

### GET /resolveOauth/{type}
返回第三方授权地址（可携带 `callbackUrl`）

请求示例：
```http
GET /user/guest/userOauth/resolveOauth/9?callbackUrl=https://example.com/callback
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": "https://third-party/authorize?..."
}
```

### GET /callback/{type}
第三方回调

请求示例：
```http
GET /user/guest/userOauth/callback/9?code=xxx&state=yyy
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "token": "xxx",
    "iat": "1700000000000",
    "exp": "1731536000000"
  }
}
```

---

## 游客态：三方认证类型
基础路径：`/user/guest/userOauthType`

### GET /list
可用三方认证类型列表（仅 `ACTIVE`）

请求示例：
```http
GET /user/guest/userOauthType/list
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "total": 1,
    "list": [
      {
        "id": 1,
        "name": "github",
        "title": "GitHub",
        "logo": "https://.../github.png",
        "status": 1,
        "adapter": 9
      }
    ]
  }
}
```

### GET /{id}
三方认证类型详情

请求示例：
```http
GET /user/guest/userOauthType/1
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 1,
    "name": "github",
    "title": "GitHub",
    "logo": "https://.../github.png",
    "status": 1,
    "adapter": 9
  }
}
```

---

## 用户态：用户信息
基础路径：`/user/user/userInfo`

### GET /
获取当前用户信息

请求示例：
```http
GET /user/user/userInfo
Authorization: Bearer <token>
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 1,
    "username": "tom",
    "nickname": "Tom",
    "phone": "13800000000",
    "avatar": "https://.../avatar.png"
  }
}
```

### PUT /
更新用户信息

请求示例：
```http
PUT /user/user/userInfo
Authorization: Bearer <token>
Content-Type: application/json

{
  "nickname": "Tom",
  "email": "tom@example.com",
  "age": 20,
  "sex": 1
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 1,
    "nickname": "Tom",
    "email": "tom@example.com"
  }
}
```

### GET /queryLevelOptions
用户等级字典

请求示例：
```http
GET /user/user/userInfo/queryLevelOptions
Authorization: Bearer <token>
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": [
    {"key": 1, "value": "普通"},
    {"key": 2, "value": "VIP"}
  ]
}
```

### POST /uploadAvatar
头像上传

请求示例：
```http
POST /user/user/userInfo/uploadAvatar
Authorization: Bearer <token>
Content-Type: multipart/form-data

avatar=@/path/avatar.png
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": "https://.../user/avatar/xxx.png"
}
```

### PUT /changePassword
修改密码

请求示例：
```http
PUT /user/user/userInfo/changePassword
Authorization: Bearer <token>
Content-Type: application/json

{
  "oldPassword": "old123",
  "newPassword": "new123",
  "confirmPassword": "new123"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### POST /sendNewPhoneSms
发送原手机号验证码

请求示例：
```http
POST /user/user/userInfo/sendNewPhoneSms
Authorization: Bearer <token>
Content-Type: application/json

{
  "phone": "13800000000"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": null
}
```

### PUT /changePhone
修改手机号

请求示例：
```http
PUT /user/user/userInfo/changePhone
Authorization: Bearer <token>
Content-Type: application/json

{
  "newPhone": "13900000000",
  "newPhoneCaptcha": "654321",
  "captcha": "123456"
}
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": true
}
```

### POST /sendSms
发送认证验证码

请求示例：
```http
POST /user/user/userInfo/sendSms
Authorization: Bearer <token>
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": null
}
```

---

## 用户态：三方认证详情
基础路径：`/user/user/userOauth`

### GET /{id}
获取当前用户三方认证详情

请求示例：
```http
GET /user/user/userOauth/10
Authorization: Bearer <token>
```

返回示例：
```json
{
  "code": 0,
  "msg": "success",
  "payload": {
    "id": 10,
    "uid": 1,
    "type": 2,
    "openId": "openid-xxx"
  }
}
```
