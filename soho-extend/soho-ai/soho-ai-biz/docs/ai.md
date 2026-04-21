# AI 模块文档

## 1. 模块概述

`soho-ai-biz` 是 AI 聚合网关模块，负责：

- 管理上游 AI 提供方配置 `ai_provider_config`
- 管理代理配置 `ai_proxy_config`
- 管理模型信息 `ai_model_info`
- 管理 provider 与模型关联 `ai_provider_model_rel`
- 用户创建自己的调用 Key `ai_user_api_key`
- 提供用户聊天接口
- 提供 OpenAI 兼容调用入口
- 统计 token 用量并写入 `ai_api_call_log`
- 按 provider 配置价格调用钱包扣费

当前模块已经不依赖旧的 `ai_app`。

## 2. 核心能力

### 2.1 支持的调用模式

- 用户登录态聊天：`/ai/user/chat`、`/ai/user/chat/stream`
- OpenAI 兼容调用：`/ai/guest/openai/v1/chat/completions`
- 后台直连调试：`/ai/admin/chat`、`/ai/admin/chat/stream`

### 2.2 支持的消息类型

- 纯文本消息
- 图片 URL 消息
- 文件 URL 消息

### 2.3 文件能力

- 支持用户先上传文件到对象存储，再把 URL 传给聊天接口
- 服务端会下载 `fileUrls` 指向的文件，抽取文本后拼入消息内容，再发送给模型
- 当前支持：
  - `pdf`
  - 常见文本文件：`txt`、`md`、`csv`、`json`、`xml`、`yaml`、`yml`、`log`
  - 常见代码文本：`java`、`js`、`ts`、`py`、`sql`、`html`、`htm`
- 当前不支持：
  - `doc`
  - `docx`
  - `xls`
  - `xlsx`

### 2.4 多模态说明

- `/ai/user/chat` 已兼容 OpenAI 风格 `content` 数组
- `/ai/guest/openai/v1/chat/completions` 兼容 OpenAI 风格 `content` 数组
- 图片 URL 在 OpenAI-compatible 上游可按多模态 block 转发
- 对不支持图片/文件原生输入的 provider，会降级成文本说明或文件抽取文本

## 3. 用户侧接口

所有 `/ai/user/*` 接口都要求用户登录态。

### 3.1 API Key 管理

#### 创建调用 Key

- 路径：`POST /ai/user/apiKey`

请求体：

```json
{
  "name": "codex-key-1",
  "expireEndTime": "2026-04-24 00:00:00"
}
```

说明：

- Key 不再绑定单个 provider config
- `expireEndTime` 选填，格式为 `yyyy-MM-dd HH:mm:ss`
- 返回明文 `apiKey`
- 明文只展示一次

#### 查询 Key 列表

- 路径：`GET /ai/user/apiKey/list`

#### 停用 Key

- 路径：`DELETE /ai/user/apiKey/{id}`

#### 启用 Key

- 路径：`PUT /ai/user/apiKey/{id}/enable`

#### 彻底删除 Key

- 路径：`DELETE /ai/user/apiKey/{id}/destroy`

### 3.2 调用日志

#### 查询调用日志

- 路径：`GET /ai/user/apiCallLog/list`

### 3.3 模型与会话

#### 获取模型列表

- 路径：`GET /ai/user/model/list`

#### 获取会话列表

- 路径：`GET /ai/user/session/list`

#### 获取会话消息列表

- 路径：`GET /ai/user/session/message/list?sessionId=1`

#### 重命名会话

- 路径：`PUT /ai/user/session/rename`

请求体：

```json
{
  "sessionId": 1,
  "title": "Python 示例"
}
```

#### 删除会话

- 路径：`DELETE /ai/user/session/{sessionId}`

### 3.4 文件上传

#### 上传文件到对象存储

- 路径：`POST /ai/user/file/upload`
- 请求：`multipart/form-data`
- 字段：`file`

响应：

- 返回对象存储 URL
- 可直接用于后续聊天请求中的 `fileUrls`

### 3.5 用户聊天

#### 非流式聊天

- 路径：`POST /ai/user/chat`

#### 流式聊天

- 路径：`POST /ai/user/chat/stream`

说明：

- `sessionId` 为空时自动创建新会话
- 新会话时 `providerCode` 必填
- `model` 为空时使用会话模型或 provider 默认模型
- SSE 接口返回 OpenAI 风格 delta payload

#### 基础文本请求

```json
{
  "sessionId": 1,
  "providerCode": "chatgpt_codex",
  "model": "gpt-5-codex",
  "stream": true,
  "messages": [
    { "role": "user", "content": "write a python hello world" }
  ]
}
```

#### 图片 URL 请求

推荐写法：

```json
{
  "sessionId": 1,
  "providerCode": "gpt-4o",
  "messages": [
    {
      "role": "user",
      "content": "请描述这张图片",
      "imageUrls": ["https://example.com/cat.png"]
    }
  ]
}
```

兼容 OpenAI 风格写法：

```json
{
  "sessionId": 1,
  "providerCode": "gpt-4o",
  "messages": [
    {
      "role": "user",
      "content": [
        { "type": "text", "text": "请描述这张图片" },
        { "type": "image_url", "image_url": { "url": "https://example.com/cat.png" } }
      ]
    }
  ]
}
```

#### 文件 URL 请求

推荐写法：

```json
{
  "sessionId": 1,
  "providerCode": "gpt-4o",
  "messages": [
    {
      "role": "user",
      "content": "请总结这个文件",
      "fileUrls": ["https://example.com/demo.pdf"]
    }
  ]
}
```

兼容 OpenAI 风格写法：

```json
{
  "sessionId": 1,
  "providerCode": "gpt-4o",
  "messages": [
    {
      "role": "user",
      "content": [
        { "type": "text", "text": "请总结这个文件" },
        { "type": "file_url", "file_url": { "url": "https://example.com/demo.pdf" } }
      ]
    }
  ]
}
```

## 4. OpenAI 兼容接口

### 4.1 chat completions

- 路径：`POST /ai/guest/openai/v1/chat/completions`
- 鉴权：`Authorization: Bearer <用户创建的AI调用Key>`

说明：

- 只支持 `chat.completions`
- 不提供 OpenAI 官方的 `files` API
- `file_url` 是本项目自定义扩展，不是 OpenAI 官方标准字段

#### 基础请求

```json
{
  "model": "gpt-5-codex",
  "messages": [
    { "role": "system", "content": "You are a helpful coding assistant." },
    { "role": "user", "content": "write a python hello world" }
  ],
  "stream": true,
  "max_tokens": 1024,
  "temperature": 0.2
}
```

#### 图片 URL 请求

```json
{
  "model": "gpt-4o-mini",
  "messages": [
    {
      "role": "user",
      "content": [
        { "type": "text", "text": "请描述这张图片" },
        { "type": "image_url", "image_url": { "url": "https://example.com/cat.png" } }
      ]
    }
  ]
}
```

#### 文件 URL 请求

```json
{
  "model": "gpt-4o-mini",
  "messages": [
    {
      "role": "user",
      "content": [
        { "type": "text", "text": "请总结这个文件" },
        { "type": "file_url", "file_url": { "url": "https://example.com/demo.pdf" } }
      ]
    }
  ]
}
```

### 4.2 响应格式

非流式响应会转换成 OpenAI 风格：

```json
{
  "id": "chatcmpl-xxx",
  "object": "chat.completion",
  "created": 1710000000,
  "model": "gpt-4o-mini",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "..."
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 100,
    "completion_tokens": 20,
    "total_tokens": 120
  }
}
```

流式响应会转换成 `chat.completion.chunk` 风格 SSE。

## 5. 后台管理接口

### 5.1 Provider Config 管理

- 路径前缀：`/ai/admin/aiProviderConfig`
- 作用：管理 provider 基础配置

支持：

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /options`

#### 新增/编辑时直接关联模型

`AiProviderConfig` 当前支持非持久化字段 `modelInfoIds`。

新增或编辑请求可直接传：

```json
{
  "code": "openai_prod",
  "provider": "openai",
  "baseUrl": "https://api.openai.com",
  "defaultModel": "gpt-4o-mini",
  "status": 1,
  "modelInfoIds": [1, 2, 5]
}
```

说明：

- 保存 provider config 后，会自动同步 `ai_provider_model_rel`
- `GET /ai/admin/aiProviderConfig/{id}` 会回显 `modelInfoIds`
- 编辑时传空数组可清空关联
- 编辑时不传 `modelInfoIds` 则保留现有关联

### 5.2 Model Info 管理

- 路径前缀：`/ai/admin/aiModelInfo`
- 作用：维护模型基础信息，如 `modelName`、`promptPrice`、`completionPrice`、`sort`

支持：

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /options`

### 5.3 Provider Model Rel 管理

- 路径前缀：`/ai/admin/aiProviderModelRel`
- 作用：底层 provider 与模型关联表

说明：

- 现在一般不建议前端直接单独维护这张表
- 推荐通过 `aiProviderConfig.modelInfoIds` 一体化维护

### 5.4 API Key 管理

- 路径前缀：`/ai/admin/aiUserApiKey`

### 5.5 调用日志管理

- 路径前缀：`/ai/admin/aiApiCallLog`

### 5.6 后台直连聊天

### 5.7 会话路由策略

- `ai_chat_session.provider_code` 不再作为实际路由依据
- 聊天请求默认按本次请求的 `providerCode + model` 决定路由
- 当本次请求未指定 `providerCode` 时，系统会按 `model` 重新执行动态权重选择
- 历史会话中残留的 `provider_code` 建议执行 `docs/sql/20260411_ai_chat_session_clear_provider_code.sql` 清理

- 路径：`POST /ai/admin/chat`
- 路径：`POST /ai/admin/chat/stream`

说明：

- 主要用于后台调试或内部调用
- 请求结构与 `AiChatRequest` 一致

## 6. Provider 适配说明

### 6.1 已支持 provider 类型

代码中当前支持：

- `openai`
- `deepseek`
- `qwen`
- `anthropic`
- `gemini`
- `ollama`
- `codexResponses` adapter

### 6.2 provider 选择逻辑

- OpenAI 兼容入口按 `model` 反查 provider config
- 业务聊天接口优先使用请求里的 `providerCode`
- 如果没有 `providerCode`，会尝试根据 `model` 反查已关联的 provider config

### 6.3 模型校验

- provider config 若已绑定模型，则调用时会校验模型是否属于该 provider
- 若未绑定模型，则退回使用配置内的 `supportedModels` 或 provider 提取结果

## 7. Provider 配置说明

`ai_provider_config.config_json` 常用字段：

### 7.1 通用字段

```json
{
  "provider": "openai",
  "baseUrl": "https://api.openai.com",
  "apiKey": "sk-xxx",
  "model": "gpt-4o-mini",
  "timeoutMs": 60000
}
```

### 7.2 OpenAI-compatible

```json
{
  "openaiPath": "/v1/chat/completions"
}
```

### 7.3 Anthropic

```json
{
  "provider": "anthropic",
  "anthropicPath": "/v1/messages",
  "anthropicVersion": "2023-06-01"
}
```

### 7.4 Gemini

```json
{
  "provider": "gemini",
  "geminiApiVersion": "v1beta"
}
```

### 7.5 Ollama

```json
{
  "provider": "ollama",
  "ollamaPath": "/api/chat"
}
```

### 7.6 Codex Responses adapter

```json
{
  "adapter": "codexResponses",
  "codexResponsesPath": "/backend-api/codex/responses",
  "store": false
}
```

说明：

- OpenAI `chat.completions` 请求会被转换成 Codex Responses 请求
- 流式时会把上游 `response.output_text.delta` 转成 OpenAI chunk

## 8. 代理配置

从独立表 `ai_proxy_config` 读取代理信息（不再依赖 `ai_provider_config.config_json`）：

- `provider`: 绑定供应商（如 `openai` / `gemini`），为空表示全局代理
- `weight`: 代理权重（值越大越容易命中）
- `status`: 代理状态（`1` 可用）
- 代理连接字段可二选一：
  - `proxy_type + proxy_host + proxy_port (+ proxy_username/proxy_password)`
  - `proxy_url`（`protocol://[user:pass@]host:port`）

供应商优先绑定规则：

1. 先查 `provider=当前供应商` 且 `status=1` 的代理，按权重随机。
2. 若未命中，再查 `provider` 为空的全局代理，按权重随机。
3. 若仍未命中，回退历史兼容逻辑（读取 `config_json` 内代理字段）。

支持协议：

- `http`
- `https`
- `socks5`
- `ss`
- `vmess`
- `vless`
- `trojan`

说明：

- `ss/vmess/vless/trojan` 在代理层统一按 SOCKS5 出口处理。
- 这类协议通常需要先由本机中继工具（如 clash/sing-box/xray）暴露本地端口。

## 9. 计费

从 `ai_provider_config.config_json` 读取：

```json
{
  "billingEnabled": true,
  "billingWalletTypeId": 1,
  "promptPricePer1kTokens": 0.02,
  "completionPricePer1kTokens": 0.08
}
```

逻辑：

1. 调用前按 `prompt_tokens + max_tokens` 做余额预检查
2. 调用后优先读取上游 usage
3. 上游没有 usage 时按字符数近似估算 token
4. 调用钱包扣费
5. 写入 `ai_api_call_log`

## 10. 数据表

主要数据表：

- `ai_provider_config`
- `ai_proxy_config`
- `ai_model_info`
- `ai_provider_model_rel`
- `ai_user_api_key`
- `ai_api_call_log`
- `ai_chat_session`
- `ai_chat_session_message`

建表 SQL 见 [sql.sql](./sql.sql)。

## 11. 当前限制

- OpenAI 兼容入口当前只实现 `chat.completions`
- `file_url` 是本项目扩展字段，不是 OpenAI 官方标准字段
- 文件解析当前不支持 Office 文档
- 非 OpenAI-compatible provider 对图片 URL 的支持能力取决于对应适配实现，不是所有 provider 都能原生识别图片
