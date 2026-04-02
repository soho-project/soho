# 自定义二维码收款支付(收款码 + 用户上报 + 自动匹配)

## 业务流程

### 前端流程
   用户下单，系统生成一笔待支付订单
   页面展示个人收款码
   用户支付后，点击“我已支付”
   弹出表单，填写：
   支付方式ID（payInfoId）
   付款人姓名
   支付金额
   支付时间
   支付单号（用户上报单号）
   可选：备注
   前端调用你们的上报接口

### 后端流程
   接收支付上报
   验签鉴权（使用 pay_info.account_private_key）
   基础校验
   进入匹配引擎
   自动匹配成功：
   更新业务订单状态
   自动匹配不确定：
   进入人工审核队列
   审核通过后改单

## 自动匹配规则（当前版本）

接口：`POST /pay/guest/api/pay/customQr/report`

上报字段：
- `payInfoId`
- `payAmount`
- `payerName`
- `payTime`
- `payOrderNo`
- `remark`（可选）
- `signTimestamp`（必填）
- `signNonce`（必填）
- `sign`（必填）

签名规则：
1. 参与签名字段：`payInfoId,payAmount,payerName,payTime,payOrderNo,remark,signTimestamp,signNonce`
2. 字段按 key 升序拼接：`k1=v1&k2=v2...`
3. 末尾拼接：`&key={pay_info.account_private_key}`
4. 对整串做 `MD5` 并转大写，得到 `sign`
5. 服务端校验时间窗：`signTimestamp` 与服务端时间差不超过 10 分钟

匹配逻辑：
1. 仅筛选同 `payInfoId` 且 `payAmount` 相同的支付单。
2. 支付时间需满足：`created_time <= payTime <= created_time + 5分钟`。
3. 若命中唯一支付单：自动改为支付成功。
4. 若未命中或命中多条：进入人工审核队列。

## Python 请求 Demo

```python
import hashlib
import time
import uuid
from decimal import Decimal

import requests


def md5_upper(text: str) -> str:
    return hashlib.md5(text.encode("utf-8")).hexdigest().upper()


def build_sign(payload: dict, private_key: str) -> str:
    # 服务端同款规则：按 key 升序拼接，空值不参与
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


def submit_report():
    base_url = "http://127.0.0.1:8080"
    api_path = "/pay/guest/api/pay/customQr/report"

    # 与 pay_info.id 对应
    pay_info_id = 1001
    # 使用 pay_info.account_private_key
    private_key = "YOUR_ACCOUNT_PRIVATE_KEY"

    sign_timestamp = str(int(time.time() * 1000))
    sign_nonce = uuid.uuid4().hex[:16]
    pay_time = "2026-04-03 12:20:30"

    body = {
        "payInfoId": pay_info_id,
        "payAmount": str(Decimal("99.90")),
        "payerName": "张三",
        "payTime": pay_time,
        "payOrderNo": "ALI202604031220300001",
        "remark": "用户已完成转账",
        "signTimestamp": sign_timestamp,
        "signNonce": sign_nonce,
    }
    body["sign"] = build_sign(body, private_key)

    resp = requests.post(
        base_url + api_path,
        json=body,
        headers={"Content-Type": "application/json"},
        timeout=10,
    )
    print(resp.status_code)
    print(resp.text)


if __name__ == "__main__":
    submit_report()
```
