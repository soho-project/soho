# 自定义二维码支付轮询与上报对接文档

## 1. 文档目标

本文档用于说明自定义二维码支付方式的完整对接流程，覆盖以下两个核心接口：

- 支付服务器长轮询拉取新支付单
- 支付完成后调用上报接口通知后台

适用支付驱动：

- `custom_qr`

---

## 2. 参与角色

### 2.1 用户前端

负责展示收款二维码、引导用户支付、在支付完成后采集上报信息。

### 2.2 支付服务器

负责轮询后台新创建的支付单，并将支付单下发给实际收款执行端或内部处理系统。

### 2.3 Soho 支付后台

负责：

- 创建支付单
- 长轮询返回待处理支付单
- 接收支付上报
- 自动匹配支付单
- 匹配失败时进入人工审核

---

## 3. 整体流程

### 3.1 下单与轮询流程

1. 业务系统调用支付下单接口，创建一笔 `custom_qr` 支付单。
2. 后台执行 `CustomQrApis.pay(Order order)` 返回二维码参数。
3. 后台同时通过 Redis 阻塞队列通知轮询通道有新支付单。
4. 支付服务器调用长轮询接口 `GET /pay/guest/api/pay/customQr/pollOrders`。
5. 如果已有新单，接口立即返回。
6. 如果暂时没有新单，接口阻塞等待；一旦新支付单创建，会立即唤醒并返回。

### 3.2 支付完成与上报流程

1. 用户扫码付款。
2. 前端或支付执行端采集支付结果信息。
3. 调用上报接口 `POST /pay/guest/api/pay/customQr/report`。
4. 后台按金额、时间窗口、支付方式进行自动匹配。
5. 若唯一命中支付单，则自动确认支付成功。
6. 若未命中或命中多笔，则进入人工审核。

---

## 4. 长轮询取单接口

### 4.1 接口地址

`GET /pay/guest/api/pay/customQr/pollOrders`

### 4.2 适用场景

- 支付服务器持续拉取新创建的自定义二维码支付单
- 需要尽量减少空轮询请求
- 需要用游标方式避免重复消费

### 4.3 请求参数

| 字段 | 是否必填 | 说明 |
|---|---|---|
| `payInfoId` | 是 | 支付方式 ID，对应 `pay_info.id` |
| `lastOrderId` | 否 | 上次已消费到的支付单 ID，首次可传 `0` |
| `limit` | 否 | 本次最多返回条数，默认 `20`，最大 `100` |
| `waitSeconds` | 否 | 长轮询等待秒数，默认 `25`，最大 `55` |
| `signTimestamp` | 是 | 签名时间戳，毫秒 |
| `signNonce` | 是 | 签名随机串 |
| `sign` | 是 | 请求签名 |

### 4.4 签名规则

参与签名字段：

- `payInfoId`
- `lastOrderId`
- `limit`
- `waitSeconds`
- `signTimestamp`
- `signNonce`

签名步骤：

1. 按 key 升序排序。
2. 过滤空值字段。
3. 按 `k1=v1&k2=v2...` 拼接。
4. 末尾追加 `&key={pay_info.account_private_key}`。
5. 对整串执行 `MD5`，并转为大写。

### 4.5 返回字段

| 字段 | 说明 |
|---|---|
| `success` | 是否处理成功 |
| `message` | 中文说明 |
| `count` | 本次返回支付单数量 |
| `nextOrderId` | 下一次轮询游标 |
| `hasMore` | 是否还有更多未返回支付单 |
| `orders` | 支付单列表 |

`orders` 内字段：

| 字段 | 说明 |
|---|---|
| `id` | 支付单 ID |
| `payId` | 支付方式 ID |
| `orderNo` | 本地支付单号 |
| `trackingNo` | 业务侧跟踪单号 |
| `amount` | 支付金额 |
| `status` | 支付状态 |
| `notifyUrl` | 业务回调地址 |
| `userId` | 用户 ID |
| `createdTime` | 创建时间 |
| `updatedTime` | 更新时间 |

### 4.6 返回说明

#### 有新单时

- 接口立即返回
- `orders` 中带回新支付单
- 调用方应保存 `nextOrderId`，下次继续轮询

#### 无新单时

- 接口会阻塞等待，最长不超过 `waitSeconds`
- 若等待期间仍无新单，则返回空列表

#### 唤醒机制

当前实现不是靠固定频率 sleep 轮询数据库，而是：

1. `CustomQrApis.pay(...)` 在创建支付单时写入 Redis 通知。
2. 长轮询接口阻塞等待 Redis 队列消息。
3. 收到消息后立即重新查库并返回。

---

## 5. 支付上报接口

### 5.1 接口地址

`POST /pay/guest/api/pay/customQr/report`

### 5.2 请求体字段

| 字段 | 是否必填 | 说明 |
|---|---|---|
| `payInfoId` | 是 | 支付方式 ID |
| `payAmount` | 是 | 实际支付金额 |
| `payerName` | 是 | 付款人姓名 |
| `payTime` | 是 | 支付时间，格式 `yyyy-MM-dd HH:mm:ss` |
| `payOrderNo` | 是 | 支付供应商单号或上报单号 |
| `remark` | 否 | 备注 |
| `signTimestamp` | 是 | 签名时间戳，毫秒 |
| `signNonce` | 是 | 签名随机串 |
| `sign` | 是 | 请求签名 |

### 5.3 签名规则

参与签名字段：

- `payInfoId`
- `payAmount`
- `payerName`
- `payTime`
- `payOrderNo`
- `remark`
- `signTimestamp`
- `signNonce`

签名步骤与轮询接口相同，签名密钥仍使用：

- `pay_info.account_private_key`

### 5.4 返回字段

| 字段 | 说明 |
|---|---|
| `success` | 是否处理成功 |
| `message` | 中文说明 |
| `reportId` | 上报记录 ID |
| `autoMatched` | 是否自动匹配成功 |
| `needReview` | 是否进入人工审核 |

### 5.5 自动匹配规则

后台当前按以下规则自动匹配：

1. 仅筛选同 `payInfoId` 的支付单。
2. 支付金额必须与 `payAmount` 一致。
3. 支付单状态必须是 `未支付` 或 `用户支付中`。
4. 支付时间需满足：
   `created_time <= payTime <= created_time + 15分钟`
5. 若命中唯一支付单，则自动确认支付成功。
6. 若未命中或命中多条，则进入人工审核。

### 5.6 常见返回语义

#### 自动匹配成功

返回示例语义：

- `success=true`
- `autoMatched=true`
- `needReview=false`
- `message=自动匹配成功，订单已更新为支付成功`

#### 进入人工审核

返回示例语义：

- `success=true`
- `autoMatched=false`
- `needReview=true`
- `message=已提交，等待人工审核`

#### 参数或签名错误

返回示例语义：

- `success=false`
- `message=签名校验失败`

---

## 6. 支付服务器接入建议

### 6.1 轮询建议

- 维护每个 `payInfoId` 独立的 `lastOrderId`
- 使用长轮询持续拉取，不要高频短轮询
- 收到返回后立即用 `nextOrderId` 发起下一次请求
- 即使本次 `orders` 为空，也继续下一次长轮询

### 6.2 上报建议

- 用户付款完成后尽快上报
- `payOrderNo` 尽量使用实际支付流水号，便于后续人工核对
- `payTime` 必须使用真实支付时间，不要写上报提交时间
- 上报前应使用与服务端一致的签名算法

### 6.3 幂等建议

- 轮询按 `lastOrderId` 保证顺序消费
- 上报接口会校验同支付方式下的 `payOrderNo` 是否重复
- 支付服务器侧也建议记录已处理流水，避免重复上报

---

## 7. Python 对接示例

```python
import hashlib
import time
import uuid
from decimal import Decimal

import requests


def md5_upper(text: str) -> str:
    return hashlib.md5(text.encode("utf-8")).hexdigest().upper()


def build_sign(payload: dict, private_key: str) -> str:
    items = []
    for k in sorted(payload.keys()):
        v = payload[k]
        if v is None:
            continue
        v = str(v).strip()
        if not v:
            continue
        items.append(f"{k}={v}")
    sign_text = "&".join(items) + f"&key={private_key}"
    return md5_upper(sign_text)


def poll_orders(base_url: str, pay_info_id: int, private_key: str, last_order_id: int = 0):
    payload = {
        "payInfoId": pay_info_id,
        "lastOrderId": last_order_id,
        "limit": 20,
        "waitSeconds": 25,
        "signTimestamp": str(int(time.time() * 1000)),
        "signNonce": uuid.uuid4().hex[:16],
    }
    payload["sign"] = build_sign(payload, private_key)
    resp = requests.get(
        base_url + "/pay/guest/api/pay/customQr/pollOrders",
        params=payload,
        timeout=35,
    )
    return resp.json()


def submit_report(base_url: str, pay_info_id: int, private_key: str):
    body = {
        "payInfoId": pay_info_id,
        "payAmount": str(Decimal("99.90")),
        "payerName": "张三",
        "payTime": "2026-04-22 22:30:00",
        "payOrderNo": "ALI202604222230000001",
        "remark": "用户已完成转账",
        "signTimestamp": str(int(time.time() * 1000)),
        "signNonce": uuid.uuid4().hex[:16],
    }
    body["sign"] = build_sign(body, private_key)
    resp = requests.post(
        base_url + "/pay/guest/api/pay/customQr/report",
        json=body,
        headers={"Content-Type": "application/json"},
        timeout=10,
    )
    return resp.json()
```

---

## 8. 相关实现位置

- 长轮询控制器：`src/main/java/work/soho/pay/biz/controller/ClientApiPayManualReportController.java`
- 长轮询服务：`src/main/java/work/soho/pay/biz/service/impl/PayManualReportServiceImpl.java`
- 上报服务：`src/main/java/work/soho/pay/biz/service/impl/PayManualReportServiceImpl.java`
- 新单通知：`src/main/java/work/soho/pay/biz/service/impl/PayManualOrderPollNotifierRedisImpl.java`
- 自定义二维码适配器：`src/main/java/work/soho/pay/biz/platform/customqr/adapter/CustomQrApis.java`

