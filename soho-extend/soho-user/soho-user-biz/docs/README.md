# soho-user-biz 文档

本模块负责会员/用户相关的基础能力，包含用户信息、登录注册、短信验证码、三方认证（OAuth）等。

## 模块结构
- `controller`
  - `UserInfoController`：后台管理端用户信息 CRUD。
  - `UserOauthController`：后台管理端用户三方认证 CRUD + Excel 导入/导出。
  - `guest/UserAuthController`：游客态登录/注册/短信/验证码。
  - `guest/GuestUserOauthController`：游客态三方认证登录。
  - `user/UserUserInfoController`：用户态资料、头像、密码、手机号修改。
  - `user/UserUserOauthController`：用户态三方认证详情。
- `service`
  - `UserInfoService`：用户信息基础服务。
  - `UserOauthService`：三方认证登录服务。
  - `UserCertificationService`：实名认证相关服务（当前模块无控制器入口）。
  - `UserSmsService`：短信验证码发送与校验。
- `domain`：`user_info` / `user_oauth` / `user_certification` 实体。
- `enums`：用户状态、三方类型、认证状态。
- `config`：用户模块配置项初始化与读取。

## 配置项（UserSysConfig）
配置由 `AdminConfigApiService` 初始化并读取：
- 组名：`user`
- `user_default_avatar`：用户默认头像地址（默认 `https://soho-oss.oss-cn-shenzhen.aliyuncs.com/avatar/default.png`）
- `user_auto_realname`：是否自动实名认证（默认 `false`）
- `performance_root_user_id`：推荐层级根用户 ID（默认 `1`）
- `user_login_dev`：是否开启登录开发模式（默认 `false`）

## 领域模型

### UserInfo（`user_info`）
关键字段：
- `id`（Long）：ID
- `code`（String）：唯一标识
- `username` / `nickname`
- `email` / `phone`
- `password` / `payPassword`
- `avatar`
- `status`（0 禁用 / 1 活跃）
- `age` / `sex`（0 女 / 1 男）/ `level`
- `referrerId`：推荐人 ID
- `createdTime` / `updatedTime`

### UserOauth（`user_oauth`）
- `id`
- `openId` / `unionId`
- `uid`：关联用户 ID
- `type`：三方类型（见 `UserOauthEnums.Type`）
- `createdTime` / `updatedTime`

### UserCertification（`user_certification`）
- `id` / `userId`
- `cardId` / `name`
- `status`（见 `UserCertificationEnums.Status`）
- `cardFrontImg` / `cardBackImg` / `video`
- `issuingLocation` / `cardAddress` / `periodOfValidity`
- `createdTime` / `updatedTime`

## 枚举
- `UserInfoEnums.Status`：`NORMAL(1)` / `DISABLED(0)`
- `UserOauthEnums.Type`：`GITEE(1)` / `WECHAT_MINI_PROGRAM(2)` / `WECHAT_OFFICIAL_ACCOUNT(3)`
- `UserCertificationEnums.Status`：`TO_BE_CERTIFIED(0)` / `PENDING(10)` / `CERTIFIED(20)`

## API 概览
返回结果统一为 `R<T>`。

### 管理端：用户信息
基础路径：`/user/admin/userInfo`

- `GET /list`：用户列表（分页）
  - 查询参数：`UserInfo` 字段（id/username/email/phone/password/avatar/status/age/sex/updatedTime）
  - 时间范围：`BetweenCreatedTimeRequest` 的 `startTime`/`endTime`
- `GET /{id}`：详情
- `POST /`：新增（自动写入 `createdTime` / `updatedTime`）
- `PUT /`：修改（更新 `updatedTime`）
- `DELETE /{ids}`：批量删除
- `GET /options`：返回 `Map<id, username>`
- `GET /queryLevelOptions`：字典 `user-info-level`

### 管理端：用户三方认证
基础路径：`/user/admin/userOauth`

- `GET /list`：列表（分页）
  - 查询参数：`UserOauth` 字段（id/openId/unionId/uid/type/updatedTime）
  - 时间范围：`BetweenCreatedTimeRequest` 的 `startTime`/`endTime`
- `GET /{id}`：详情
- `POST /`：新增
- `PUT /`：修改
- `DELETE /{ids}`：批量删除
- `GET /exportExcel`：导出 Excel
- `POST /importExcel`：导入 Excel（`file` 表单上传）

### 游客态：会员鉴权
基础路径：`/guest/user/auth`

- `POST /login`：用户名/手机/邮箱 + 密码登录
  - 请求体：`UserLoginVo`
  - 逻辑：用户名、手机号、邮箱三选一匹配
  - 若配置 `admin.login.captcha.enable=true`，会校验图形验证码
  - 注意：密码校验允许固定字符串 `dfa54f$#%@!$dfa55` 直接通过
- `POST /mobileLogin`：手机号 + 短信验证码登录
  - 请求体：`UserLoginVo`，`username` 作为手机号使用
- `POST /sendSms`：发送短信验证码
  - 请求体：`SendNewPhoneSmsRequest`（phone）
- `POST /captcha`：获取图形验证码（直接返回图片）
- `POST /register`：注册用户
  - 请求体：`UserRegisterVo`
  - 逻辑：手机号/用户名唯一校验；用户名为空时用 `P{phone}`
  - 备注：无论是否开发模式，都会执行短信验证码校验

### 游客态：三方认证登录
基础路径：`/user/guest/userOauth`

- `POST /login`：三方认证登录
  - 请求体：`UserOauthLoginRequest`（code）
  - 当前实现：`loginWithCode` 内部使用随机 `openId/unionId` 和默认 `WECHAT_MINI_PROGRAM` 类型（存在 TODO）

### 用户态：用户信息
基础路径：`/user/user/userInfo`

- `GET /`：获取当前用户信息
- `PUT /`：更新用户信息（仅允许更新头像、昵称、邮箱、年龄、性别）
- `GET /queryLevelOptions`：字典 `user-info-level`
- `POST /uploadAvatar`：头像上传（`avatar` 文件，需 image 类型）
- `PUT /changePassword`：修改密码
  - 请求体：`ChangePasswordRequest`
  - 逻辑：新密码与确认密码一致；旧密码校验为可选
- `POST /sendNewPhoneSms`：给当前手机号发送验证码
- `PUT /changePhone`：修改手机号
  - 请求体：`ChangePhoneRequest`
  - 校验：原手机号验证码必填，新手机号验证码可选
- `POST /sendSms`：发送认证验证码（基于当前用户）

### 用户态：三方认证详情
基础路径：`/user/user/userOauth`

- `GET /{id}`：获取指定三方认证详情

## 短信验证码策略
- 发送频率限制：同一用户/手机号 60 秒内不可重复发送。
- 验证码校验成功后会清理对应 Redis Key。
- Key 前缀：
  - 用户维度：`user_send_sms_lasttime{userId}` / `user_send_sms_code{userId}`
  - 手机维度：`phone_send_sms_lasttime{phone}` / `phone_send_sms_code{phone}`

## 安全与行为说明
- 登录允许固定字符串 `dfa54f$#%@!$dfa55` 作为万能密码（请确认是否需要保留）。
- `UserOauthServiceImpl.loginWithCode` 为临时实现，未对接真实三方平台。
- 用户注册时验证码校验不可跳过（即便开启登录开发模式）。

## 依赖与外部服务
- `AdminConfigApiService` / `AdminDictApiService`：系统配置与字典。
- `SmsApiService`：短信发送。
- `TokenServiceImpl`：登录 token 生成。
- `UploadUtils`：头像上传。

