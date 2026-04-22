#!/usr/bin/env bash

set -euo pipefail

# 用途：
# 1. 启动自定义二维码支付长轮询 + OCR + 上报一体化脚本
# 2. 可放到任意目录执行
# 3. 命令行环境变量优先，下面的默认值只做兜底

PYTHON_BIN="${PYTHON_BIN:-python3}"
TARGET_SCRIPT="${TARGET_SCRIPT:-/media/fang/ssd1t/home/fang/work/java/admin/soho-extend/soho-pay/soho-pay-biz/docs/script/custom_qr_poll_trigger_report.py}"

# 轮询配置
POLL_BASE_URL="${POLL_BASE_URL:-http://localhost:6677}"
POLL_PAY_INFO_ID="${POLL_PAY_INFO_ID:-7}"
POLL_PRIVATE_KEY="${POLL_PRIVATE_KEY:-951753abc#}"

# 可选配置
POLL_API_PATH="${POLL_API_PATH:-/pay/guest/api/pay/customQr/pollOrders}"
POLL_LIMIT="${POLL_LIMIT:-20}"
POLL_WAIT_SECONDS="${POLL_WAIT_SECONDS:-25}"
POLL_TIMEOUT_SEC="${POLL_TIMEOUT_SEC:-35}"
POLL_INTERVAL_SEC="${POLL_INTERVAL_SEC:-1}"
POLL_TRIGGER_SLEEP_SEC="${POLL_TRIGGER_SLEEP_SEC:-15}"

if [[ ! -f "${TARGET_SCRIPT}" ]]; then
  echo "目标脚本不存在: ${TARGET_SCRIPT}" >&2
  exit 1
fi

exec env \
  POLL_BASE_URL="${POLL_BASE_URL}" \
  POLL_PAY_INFO_ID="${POLL_PAY_INFO_ID}" \
  POLL_PRIVATE_KEY="${POLL_PRIVATE_KEY}" \
  POLL_API_PATH="${POLL_API_PATH}" \
  POLL_LIMIT="${POLL_LIMIT}" \
  POLL_WAIT_SECONDS="${POLL_WAIT_SECONDS}" \
  POLL_TIMEOUT_SEC="${POLL_TIMEOUT_SEC}" \
  POLL_INTERVAL_SEC="${POLL_INTERVAL_SEC}" \
  POLL_TRIGGER_SLEEP_SEC="${POLL_TRIGGER_SLEEP_SEC}" \
  "${PYTHON_BIN}" "${TARGET_SCRIPT}" "$@"
