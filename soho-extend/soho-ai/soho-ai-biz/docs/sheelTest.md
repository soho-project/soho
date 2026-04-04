# 模型请求接口

curl 'http://127.0.0.1:6677/ai/guest/openai/v1/models' \
-X POST \
-H 'Authorization: Bearer sk-ai-6ba06fa2f342417db6755273110131b2' \
-H 'Content-Type: application/json'

curl 'http://127.0.0.1:6677/ai/guest/openai/v1/chat/completions' \
-X POST \
-H 'Authorization: Bearer sk-ai-6ba06fa2f342417db6755273110131b2' \
-H 'Content-Type: application/json' \
-H 'Accept: text/event-stream' \
-d '{
"model": "gpt-5.4",
"stream": true,
"messages": [
{
"role": "system",
"content": "You are a helpful coding assistant."
},
{
"role": "user",
"content": "write a python hello world"
}
]
}'


curl 'http://127.0.0.1:6677/ai/guest/openai/v1/chat/completions' \
-X POST \
-H 'Authorization: Bearer sk-ai-6ba06fa2f342417db6755273110131b2' \
-H 'Content-Type: application/json' \
-d '{
"model": "gpt-5.4",
"stream": false,
"messages": [
{
"role": "system",
"content": "You are a helpful coding assistant."
},
{
"role": "user",
"content": "write a python hello world"
}
]
}'


curl -N -sS "http://127.0.0.1:6677/ai/guest/openai/v1/responses" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer sk-ai-6ba06fa2f342417db6755273110131b2" \
-d "{
\"model\": \"gpt-5.4\",
\"stream\": true,
\"input\": \"请输出 1 到 3\"
}"


curl -sS "http://127.0.0.1:6677/ai/guest/openai/v1/responses" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer sk-ai-6ba06fa2f342417db6755273110131b2" \
-d "{
\"model\": \"gpt-5.4\",
\"stream\": false,
\"instructions\": \"你是一个简洁助手\",
\"input\": [
  {
    \"role\": \"user\",
    \"content\": [
      {
        \"type\": \"input_text\",
        \"text\": \"写一个 Python hello world\"
      }
    ]
  }
]
}"


curl -sS "https://caowo.xin/v1/responses" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer sk-RQDuwpaJZjU5FdW8oevmNst1q5wNTWS88bjCOab4tAHQV8iz" \
-d "{
\"model\": \"gpt-5.4\",
\"stream\": true,
\"instructions\": \"你是一个简洁助手\",
\"input\": [
{
\"role\": \"user\",
\"content\": [
{
\"type\": \"input_text\",
\"text\": \"写一个 Python hello world\"
}
]
}
]
}"


# 开放平台兑换码领取接口
APP_KEY=551356445229674496   APP_SECRET=0ff5ad94cbe14a51affd9ab956758c91   MEMBER_CARD_NAME='月卡A套餐'   BASE_URL='http://127.0.0.1:6677'   bash -c '
set -euo pipefail
PATH_ONLY="/ai/open/app/memberCardRedeemCode/purchaseByName"
BODY="{\"memberCardName\":\"${MEMBER_CARD_NAME}\"}"
REQ_TIME="$(date +%s)"
SIGN="$(printf "%s" "${PATH_ONLY}_${BODY}_${APP_SECRET}" | md5sum | awk "{print \$1}")"
curl -sS -X POST "${BASE_URL}${PATH_ONLY}" \
-H "Content-Type: application/json" \
-H "app-key: ${APP_KEY}" \
-H "req-time: ${REQ_TIME}" \
-H "sign: ${SIGN}" \
-d "${BODY}"
echo
'
