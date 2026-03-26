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
