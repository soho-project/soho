• 1. P0 请求内容明文落日志，存在敏感信息泄露风险
soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/filter/OpenAiResponsesRawRequestLogFilter.java:53
soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:35
soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:51
建议：默认不打印完整 prompt/input/tools；仅保留 requestId、model、token 统计，必要时做脱敏采样日志。
2. P0 文件解析链路可被利用做 SSRF（内网探测/元数据读取）
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiChatServiceImpl.java:422
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiFileServiceImpl.java:56
   建议：只允许你们上传域名白名单；禁止私网/回环/链路本地地址；限制重定向目标；增加 URL scheme 校验。
3. P1 Guest SSE 用 SseEmitter 直接 subscribe，未管理取消，客户端断开后上游可能继续跑
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:38
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/guest/GuestAiOpenAiController.java:54
   建议：保存 Disposable，在 emitter.onCompletion/onTimeout/onError 里 dispose()，避免无效消耗与记账偏差。
4. P1 OpenAPI 预扣余额分支对钱包空值未保护，可能 NPE
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiOpenApiServiceImpl.java:719
   建议：补 Assert.notNull(walletInfo, "钱包不存在")，与用户 Web 聊天实现保持一致。
5. P1 配置里有 rateLimit 字段，但主流程未实际执行限流
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/domain/AiProviderConfig.java:118
   建议：在 AiOpenApiServiceImpl/AiUserWebChatServiceImpl 入口接入统一限流（用户+APIKey+provider+model 维度）。
6. P1 请求参数缺少 Bean Validation，异常主要靠运行时兜底
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/request/OpenAiChatCompletionRequest.java:9
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/request/OpenAiResponsesRequest.java:10
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/controller/user/UserAiChatWebController.java:80
   建议：为 model/messages/maxTokens/temperature/topP 增加 @Valid + @NotBlank/@Size/@Min/@Max，统一返回可读错误。
7. P2 会员卡只实现 by_request，by_token 直接降级非会员
   soho-extend/soho-ai/soho-ai-biz/src/main/java/work/soho/ai/biz/service/impl/AiMemberRequestLimitServiceImpl.java:31
   建议：尽快实现 token 维度配额扣减，不然会员策略与计费规则会出现认知偏差。
8. P2 测试覆盖偏薄，高风险点未覆盖
   soho-extend/soho-ai/soho-ai-biz/src/test/java/work/soho/ai/biz/service/impl/AiChatServiceImplTest.java:36
   soho-extend/soho-ai/soho-ai-biz/src/test/java/work/soho/ai/biz/service/impl/AiOpenApiServiceImplTest.java:29
   建议：补 4 类测试：日志脱敏、SSRF 拦截、SSE 断开取消、余额/扣费边界（钱包不存在、并发扣费）。

