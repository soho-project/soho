# docs/script 说明

## 1. 脚本列表

### `custom_qr_poll_trigger_report.py`

这是一个单文件脚本，已经整合了以下能力：

- 自定义二维码支付单长轮询
- 收款小账本页面自动操作
- 收款记录 OCR 识别
- 详情页识别
- 支付上报

不再依赖外部 `skb_detail_ocr.py`。

### `start_custom_qr_poll_trigger_report.sh`

用于启动上面的 Python 脚本。

特点：

- 可以拷贝到任意目录执行
- 内部使用绝对路径定位 Python 目标脚本
- 支持环境变量覆盖默认配置

---

## 2. 参数优先级

脚本配置优先级如下：

- 命令行参数
- 环境变量

也就是：

`传参优先，环境变量兜底`

敏感信息禁止写死在脚本里。

---

## 3. 必填敏感参数

### 上报相关

- `REPORT_BASE_URL`
- `REPORT_PAY_INFO_ID`
- `REPORT_PRIVATE_KEY`

### 轮询相关

- `POLL_BASE_URL`
- `POLL_PAY_INFO_ID`
- `POLL_PRIVATE_KEY`

如果启用了对应功能但没有提供这些参数，脚本会直接报错退出。

说明：

- 如果未单独提供 `REPORT_BASE_URL/REPORT_PAY_INFO_ID/REPORT_PRIVATE_KEY`
- 脚本会默认复用 `POLL_BASE_URL/POLL_PAY_INFO_ID/POLL_PRIVATE_KEY`

---

## 4. 支持的主要参数

### 上报参数

- `--report-base-url`
- `--report-api-path`
- `--report-pay-info-id`
- `--report-private-key`
- `--report-timeout-sec`

### 轮询参数

- `--poll`
- `--no-poll`
- `--poll-base-url`
- `--poll-api-path`
- `--poll-pay-info-id`
- `--poll-private-key`
- `--poll-limit`
- `--poll-wait-seconds`
- `--poll-timeout-sec`
- `--poll-interval-sec`
- `--poll-trigger-sleep-sec`
- `--poll-state-file`

### 通用参数

- `--log-file`

---

## 5. 使用示例

### 轮询 + OCR + 上报一体化运行

```bash
cd /media/fang/ssd1t/home/fang/work/java/admin/soho-extend/soho-pay/soho-pay-biz/docs/script

python3 custom_qr_poll_trigger_report.py \
  --poll \
  --poll-base-url "https://api.example.com" \
  --poll-pay-info-id "7" \
  --poll-private-key "轮询签名私钥" \
  --report-base-url "https://api.example.com" \
  --report-pay-info-id "7" \
  --report-private-key "上报签名私钥"
```

### 只执行一次本地 OCR + 上报

```bash
python3 custom_qr_poll_trigger_report.py \
  --no-poll \
  --report-base-url "https://api.example.com" \
  --report-pay-info-id "7" \
  --report-private-key "上报签名私钥"
```

### 环境变量兜底

```bash
export POLL_BASE_URL="https://api.example.com"
export POLL_PAY_INFO_ID="7"
export POLL_PRIVATE_KEY="轮询签名私钥"
export REPORT_BASE_URL="https://api.example.com"
export REPORT_PAY_INFO_ID="7"
export REPORT_PRIVATE_KEY="上报签名私钥"

python3 custom_qr_poll_trigger_report.py --poll
```

### 使用启动 shell 脚本

```bash
bash /media/fang/ssd1t/home/fang/work/java/admin/soho-extend/soho-pay/soho-pay-biz/docs/script/start_custom_qr_poll_trigger_report.sh
```

你这次的本地示例可以写成：

```bash
POLL_BASE_URL="http://localhost:6677" \
POLL_PRIVATE_KEY="951753abc#" \
POLL_PAY_INFO_ID="7" \
bash /media/fang/ssd1t/home/fang/work/java/admin/soho-extend/soho-pay/soho-pay-biz/docs/script/start_custom_qr_poll_trigger_report.sh
```

命中新支付单后，脚本默认会先等待 `15` 秒，再执行本地 OCR 检查与上报。

如需修改等待时间：

```bash
POLL_TRIGGER_SLEEP_SEC="20" \
bash /media/fang/ssd1t/home/fang/work/java/admin/soho-extend/soho-pay/soho-pay-biz/docs/script/start_custom_qr_poll_trigger_report.sh
```

---

## 6. 状态文件说明

默认状态文件：

- `.runtime/custom_qr_poll_state.json`

用于记录：

- `lastOrderId`
- `updatedAt`

脚本在成功完成一轮本地 OCR 与上报后，才会推进轮询游标。
