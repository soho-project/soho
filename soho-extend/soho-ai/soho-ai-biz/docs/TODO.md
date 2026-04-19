# soho-ai-biz TODO

## 待处理

### P0 文件解析链路存在 SSRF 风险
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:655`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiFileServiceImpl.java:42`
- 现状：文件内容提取仍会根据外部 URL 发起请求，当前仍可见 `new URL(...)` 与 `HttpURLConnection` 直连。
- 风险：可被利用做内网探测、云元数据读取或跳板下载。
- 建议：限制 scheme；只允许可信上传域名；拦截私网/回环/链路本地地址；限制重定向目标；补针对 SSRF 的测试。

### P1 provider 级 `rateLimit` 字段仍未真正接入请求限流
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/domain/AiProviderConfig.java:118`
- 现状：字段仍存在，但当前代码里主要只在管理端查询/复制时使用，主请求链路未见按该字段执行统一限流。
- 建议：明确限流语义后，在 `AiOpenApiServiceImpl` / `AiUserWebChatServiceImpl` 入口接入统一限流，至少覆盖 user、API key、provider、model 维度。

### P1 OpenAI 兼容请求对象仍缺少 Bean Validation
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/request/OpenAiChatCompletionRequest.java:8`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/request/OpenAiResponsesRequest.java:9`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:229`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:241`
- 现状：请求 DTO 仍未见 `@NotBlank`、`@Size`、`@Min`、`@Max` 等校验注解，Controller 入口也未见 `@Valid`。
- 建议：为 model、messages、maxTokens、temperature、topP 等关键字段增加参数校验，并统一错误返回格式。

### P2 高风险点测试覆盖仍不足
- 现状：已新增 HTTP 栈统一相关测试，但以下风险点仍未见专门覆盖：
  - SSRF 拦截
  - 请求日志脱敏/摘要边界
  - 钱包不存在与扣费边界
  - provider 级限流生效
- 建议：优先补集成/单测，避免后续回归。

### P2 文件内容抽取仍在同步主链路
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:655`
- 现状：`appendFileContents(...)` 仍会在聊天主链路同步抽取文件内容。
- 建议：补缓存；同一 URL 去重；必要时改为异步预处理或上传后预抽取。

### P2 failover / proxy retry / 首包超时策略仍可继续优化
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:1300`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:1329`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:1148`
- 现状：重试和首包超时已有实现，但仍是较通用策略。
- 建议：按 provider / model / error type 分层调整，降低长尾请求放大。

### P2 代理节点调度仍可从“权重随机”继续优化为“健康优先”
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiProxyConfigServiceImpl.java`
- 现状：运行态能力已经接入，但 TODO 中提到的“最近成功节点优先 / 低延时优先”还没有明确落地记录。
- 建议：利用已有 runtime state，做健康优先 + 权重随机。

## 已处理

### 已处理：请求日志已改为摘要日志，原始正文泄露风险已显著收敛
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/filter/OpenAiResponsesRawRequestLogFilter.java:50`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:230`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:328`
- 现状：当前仅记录 `model`、`stream`、消息数、tools 数、include 数、bodyBytes 等摘要信息，未再直接打印完整 prompt/input/tools。

### 已处理：Guest SSE 订阅已补取消管理
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:354`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:366`
- 现状：已保存 `Disposable`，并在 `onCompletion` / `onTimeout` / `onError` 中调用 `dispose()`。

### 已处理：OpenAPI 预扣余额已补钱包空值保护
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiOpenApiServiceImpl.java:2026`
- 现状：已显式 `Assert.notNull(walletInfo, "钱包不存在")`。

### 已处理：会员卡 `by_token` 配额已接入
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiMemberRequestLimitServiceImpl.java:34`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiMemberRequestLimitServiceImpl.java:72`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiOpenApiServiceImpl.java:1909`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiUserWebChatServiceImpl.java:541`
- 现状：已支持按周 prompt/completion/total token 配额评估与消费，不再是简单降级非会员。

### 已处理：上游 HTTP 客户端已统一为共享工厂并复用连接
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiUpstreamClientFactoryImpl.java:47`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiUpstreamClientFactoryImpl.java:107`
- 现状：非流式和流式都已经通过 `AiUpstreamClientFactory` 统一出站，复用缓存 `WebClient`。

### 已处理：减少一次请求里的重复 DB 查询
- 现状：本地缓存已接入 provider 列表、model-provider 关系等高频查询路径，相关测试已补齐。

### 已处理：OpenAPI 与内部 Chat 的代理 HTTP 栈已统一
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:810`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:868`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:1004`
- 位置：`soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiUpstreamClientFactory.java:52`
- 现状：Chat 流式请求已收口到 `exchangeStream(...)`，与 OpenAPI 共用连接池、超时、代理与错误处理入口。

## 备注
- 这份文档已按当前代码状态做过一次清理，原来一些“已处理但仍混在待办里”的条目已移动到“已处理”。
- 如果你希望，我下一步可以继续把这份 TODO 再拆成：`安全` / `稳定性` / `性能` / `测试` 四类。 
