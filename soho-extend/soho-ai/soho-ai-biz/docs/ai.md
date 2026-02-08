# AI 模块前端对接文档（soho-ai-biz）

本文档说明前端如何调用 AI 接口，以及相关配置字段与注意事项。

## 1. 接口概览

### 1.1 统一对话接口

- 路径：`POST /ai/admin/chat`
- 说明：非流式对话，返回完整结果

### 1.2 流式对话接口（SSE）

- 路径：`POST /ai/admin/chat/stream`
- `Content-Type: application/json`
- 响应：`text/event-stream`
- 说明：流式输出，前端按 SSE 逐条消费

## 2. 请求体（AiChatRequest）

```json
{
  "appCode": "app_code_optional",
  "providerCode": "provider_code_optional",
  "model": "optional_model",
  "input": "hello",
  "messages": [
    { "role": "user", "content": "hello" }
  ],
  "temperature": 0.7,
  "topP": 0.9,
  "maxTokens": 512,
  "stream": true,
  "extra": {}
}
```

字段说明：

- `appCode`：`ai_app.code`，可选。若提供，会按 app 绑定的 provider 配置。
- `providerCode`：`ai_provider_config.code`，可选。若不传且未配置 `appCode`，将无法定位配置。
- `model`：指定模型，优先级高于默认模型。
- `input`：单轮输入（当 `messages` 为空时使用）。
- `messages`：对话消息列表，`role` 为 `system/user/assistant`。
- `temperature/topP/maxTokens/stream/extra`：可选参数。

## 3. 响应体（AiChatResponse）

```json
{
  "code": 2000,
  "msg": "success",
  "payload": {
    "provider": "openai",
    "model": "gpt-4o-mini",
    "content": "Hello",
    "raw": "{...}"
  }
}
```

字段说明：

- `provider`：实际使用的提供方
- `model`：实际使用的模型
- `content`：模型输出文本
- `raw`：上游原始响应（JSON 字符串）

## 4. SSE 流式返回格式

### 4.1 SSE 事件

接口返回的是标准 SSE 流。每个 `event` 的 `data` 为一条 payload：

- OpenAI 兼容：`data: { ... }`
- 结束标记：`data: [DONE]`

### 4.2 非 SSE Provider 的统一输出

当 `ai_provider_config.config_json` 中设置：

```json
{
  "streamSupported": false
}
```

系统会把一次性返回的结果包装成 OpenAI 风格 SSE：

1. `data: {"choices":[{"delta":{"content":"..."}}]}`
2. `data: [DONE]`

前端可以用同一套 SSE 解析逻辑处理所有提供方。

## 5. 配置字段说明（ai_provider_config）

关键字段：

- `provider`：openai / anthropic / qwen / gemini / deepseek / ollama
- `base_url`：上游接口地址
- `api_key_ref`：API Key（当前实现直接当作 key 使用）
- `default_model`：默认模型
- `config_json`：扩展配置（JSON）

常用 `config_json` 示例：

```json
{
  "streamSupported": true,
  "model": "deepseek-chat",
  "baseUrl": "https://api.deepseek.com",
  "apiKey": "sk-xxxx",
  "openaiPath": "/v1/chat/completions",
  "anthropicPath": "/v1/messages",
  "anthropicVersion": "2023-06-01",
  "geminiApiVersion": "v1beta",
  "ollamaPath": "/api/chat"
}
```

注意：

- `model` 为空时：deepseek 会默认 `deepseek-chat`，gemini 会默认 `gemini-pro`。
- 若 `provider` 需要 key 但 `apiKey` 为空，会直接报错。

## 6. 鉴权说明

若系统开启鉴权，前端需携带：

```
Authorization: Bearer <token>
```

是否需要鉴权取决于后端安全配置是否放行 `/ai/**` 路径。

## 7. 前端使用建议

- **普通调用**：`POST /ai/admin/chat`
- **流式调用**：`POST /ai/admin/chat/stream` + SSE 客户端
- **统一解析**：使用 SSE 解析 `data:` 行，遇到 `[DONE]` 结束

