# AI 会员卡实现逻辑说明

## 1. 目标与范围

本文档梳理 `soho-ai-biz` 中会员卡相关实现，覆盖：

- 会员卡模板管理
- 用户会员卡发放与选择
- 兑换码生成与兑换
- 生效会员卡判定
- `by_request` 模式下的请求配额限制（Redis 滑动窗口）
- 会员卡与 AI 调用计费联动逻辑

不包含：

- 订单购买会员卡流程（当前模块未实现完整订单链路）
- `by_token` 模式的 token 周限额执行（字段已预留，逻辑未落地）

---

## 2. 数据模型

### 2.1 会员卡模板 `ai_member_card`

关键字段：

- `card_type`: 卡类型（`monthly/quarterly/yearly`）
- `limit_mode`: 限制模式（`by_request/by_token`）
- `validity_days`: 有效天数
- `sale_price`: 销售价格（用于按卡自动定价扣款）
- `rate_limit_5h` / `rate_limit_7d`: 请求次数阈值
- `rate_limit_5h_enabled` / `rate_limit_7d_enabled`: 对应窗口是否启用
- `rate_limit_window_5h` / `rate_limit_window_7d`: 窗口大小
- `weekly_*_token_limit`: token 周限额（当前仅存储，未执行）
- `status`: 0 禁用，1 启用

### 2.2 用户会员卡 `ai_user_member_card`

关键字段：

- `user_id`: 用户 ID
- `member_card_id`: 关联模板 ID
- `no`: 会员卡号（用户端展示）
- `status`: 0 未激活，1 生效中，2 已过期（状态变更目前依赖业务写入，不是定时任务自动转）
- `priority`: 优先级（值越大越优先）
- `is_selected`: 用户显式选择标记
- `start_time/end_time`: 生效时间区间
- `source`: 来源（例如 `admin`、`redeem_code`）
- `biz_no`: 业务单号（兑换码场景存兑换码）

### 2.3 兑换码 `ai_member_card_redeem_code`

关键字段：

- `redeem_code`: 唯一兑换码
- `status`: 0 未使用，1 已使用，2 已禁用
- `used_by_user_id`: 使用人
- `user_member_card_id`: 兑换后生成的用户会员卡 ID
- `expire_time`: 兑换码过期时间

---

## 3. 接口分层

## 3.1 管理端接口

- 会员卡模板：`/ai/admin/memberCard`
  - `GET /list`、`GET /{id}`、`POST`、`PUT`、`DELETE /{ids}`
- 用户会员卡：`/ai/admin/userMemberCard`
  - `GET /list`、`GET /{id}`、`POST`、`PUT`、`DELETE /{ids}`
  - `POST /grant`：后台发放用户会员卡
- 兑换码：`/ai/admin/memberCardRedeemCode`
  - `GET /list`
  - `POST /batchGenerate`
  - `GET /exportExcel`

## 3.2 用户端接口

- 路径前缀：`/ai/user/memberCard`
- `GET /list`: 我的会员卡列表
- `GET /current`: 当前生效会员卡
- `PUT /select/{userCardId}`: 选择某张会员卡
- `POST /redeem`: 兑换码兑换并激活会员卡

---

## 4. 核心流程

## 4.1 后台发放会员卡（`POST /ai/admin/userMemberCard/grant`）

1. 校验模板存在。
2. 计算有效期：
   - `startTime` 为空则用当前时间；
   - `endTime` 为空则 `start + validityDays`（默认 30 天）；
   - 要求 `end > start`。
3. 组装 `AiUserMemberCard` 并保存。

说明：该接口不会自动将同用户其他卡 `is_selected` 清零，因此可出现多张卡都为已选；真正生效顺序由查询排序决定（见 4.3）。

## 4.2 兑换码兑换（`POST /ai/user/memberCard/redeem`）

实现位于 `AiMemberCardRedeemCodeServiceImpl#redeem`，关键步骤：

1. 参数校验（登录、兑换码非空）。
2. 兑换码标准化：`trim + upperCase`。
3. 查兑换码并校验：
   - 必须存在；
   - `status == 0`；
   - 未过期。
4. 校验模板存在且启用。
5. 乐观锁占用兑换码（`where id=? and status=0` 更新到 `status=1`）。
6. 生成用户会员卡：
   - `status=1`
   - `is_selected=true`
   - `source=redeem_code`
   - `biz_no=兑换码`
   - 有效期 `now ~ now + validityDays`
7. 先把该用户所有卡 `is_selected=false`，再保存新卡为 `true`。
8. 回填兑换码 `user_member_card_id`。

并发语义：第 5 步保证同一码只会成功兑换一次。

## 4.3 当前生效会员卡判定

实现位于 `AiUserMemberCardServiceImpl#resolveActiveMemberCard` 与 `#currentUserCard`：

过滤条件：

- `user_id = 当前用户`
- `status = 1`
- `start_time <= now <= end_time`

排序规则（高到低）：

1. `is_selected`
2. `priority`
3. `end_time`
4. `id`

命中第一条后，再校验其模板存在且启用，最终返回生效卡。

## 4.4 用户主动选择会员卡

实现位于 `AiUserMemberCardServiceImpl#selectUserCard`：

1. 先校验目标卡属于当前用户，且在有效期内且 `status=1`。
2. 将该用户所有卡 `is_selected=false`。
3. 将目标卡置 `is_selected=true`。

---

## 5. 配额限制（`by_request`）

## 5.1 生效条件

`AiMemberRequestLimitServiceImpl#evaluate` 仅在以下条件成立时启用会员请求配额：

- 存在生效会员卡；
- `limit_mode = by_request`；
- 用户和 `userCardId` 均有效。

否则返回 `nonMember`（按非会员计费/不限额路径）。

## 5.2 限流窗口与默认值

- 5 小时窗口默认：
  - 窗口大小 `5h`
  - 阈值 `100`
- 7 天窗口默认：
  - 窗口大小 `7d`
  - 阈值 `300`

窗口和阈值可由会员卡模板字段覆盖。

## 5.3 Redis 结构

使用 `ZSET` 滑动窗口计数：

- 7 天 key: `rate:ai:member:7d:user:{userId}:userCard:{cardId}`
- 5 小时 key: `rate:ai:member:5h:user:{userId}:userCard:{cardId}`

成员值：`{timestampMillis}:{requestId}`  
score：`timestampMillis`

每次 evaluate 会先清理窗口外记录，再 count。
每次 consume 会新增一条记录并设置 key 过期时间。

---

## 6. 与 AI 计费联动

联动发生在：

- `AiOpenApiServiceImpl#buildBillingPlan`（OpenAI 兼容 API）
- `AiUserWebChatServiceImpl#buildBillingPlan`（站内聊天）

核心规则：

1. 先做会员判定 `evaluate(...)`。
2. 如果 `isMemberByRequest == true && isOverLimit == false`：
   - `billingEnabled = false`（本次不扣费）
3. 否则走常规扣费逻辑（钱包预扣校验 + 实际扣费）。
4. 调用成功后，如果可消费会员配额，则 `consumeIfNeeded(...)` 记账到 Redis。

结论：当前会员权益是“按请求次数免单”，超额后自动回退到普通付费。

---

## 7. 现状与边界

1. `by_token` 模式尚未执行  
当前 `evaluate` 仅处理 `by_request`，`weekly_*_token_limit` 字段未接入实际扣费/拦截流程。

2. 状态维护依赖业务写入  
`ai_user_member_card.status` 没有看到统一定时任务自动从“生效中”切换为“已过期”。

3. 管理端直接发放接口允许多卡同时 `is_selected=true`  
用户端 `select` 和兑换流程会主动清理，但后台手工发放不会自动互斥。

4. 兑换码状态 2（禁用）  
字段有定义，但当前主要流程只有未使用(0)和已使用(1)流转。

---

## 8. 建议补强（可选）

1. 落地 `by_token` 限额执行（按周累计 prompt/completion/total token）。
2. 增加用户卡状态巡检任务（自动过期、数据修复）。
3. 为后台发放接口增加“互斥选择”选项（可配置是否清空其他 `is_selected`）。
4. 兑换码增加“禁用”管理接口和审计日志。
