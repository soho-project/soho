  #!/usr/bin/env bash
  set -euo pipefail

  # ===== 配置 =====
  BASE_URL="${BASE_URL:-http://127.0.0.1:6677}"
  PATH_ONLY="/ai/open/memberCardRedeemCode/purchaseByName"
  APP_KEY="${APP_KEY:-替换成你的appKey}"
  APP_SECRET="${APP_SECRET:-替换成你的appSecret}"
  MEMBER_CARD_NAME="${MEMBER_CARD_NAME:-月卡}"
  EMAIL="${EMAIL:-}"   # 可空

  # ===== 组装 body（签名和请求必须完全一致）=====
  if [[ -n "$EMAIL" ]]; then
    BODY="{\"memberCardName\":\"${MEMBER_CARD_NAME}\",\"email\":\"${EMAIL}\"}"
  else
    BODY="{\"memberCardName\":\"${MEMBER_CARD_NAME}\"}"
  fi

  REQ_TIME="$(date +%s)"
  SIGN_RAW="${PATH_ONLY}_${BODY}_${APP_SECRET}"

  md5_hex() {
    if command -v md5sum >/dev/null 2>&1; then
      printf '%s' "$1" | md5sum | awk '{print $1}'
    else
      printf '%s' "$1" | md5 | awk '{print $NF}'
    fi
  }
  SIGN="$(md5_hex "$SIGN_RAW")"

  echo "REQ_TIME=$REQ_TIME"
  echo "SIGN=$SIGN"
  echo "BODY=$BODY"

  curl -sS -X POST "${BASE_URL}${PATH_ONLY}" \
    -H "Content-Type: application/json" \
    -H "app-key: ${APP_KEY}" \
    -H "req-time: ${REQ_TIME}" \
    -H "sign: ${SIGN}" \
    -d "${BODY}"
  echo

