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



1. 先统一改成连接复用，别每次 new 客户端                                                                                                                                                                                                                                                                          
   - 现在非流式大量走 RestTemplate + SimpleClientHttpRequestFactory，几乎每次请求都新建，连接池/TLS 复用很弱：AiChatServiceImpl.java:1244、AiOpenApiServiceImpl.java:369、AiOpenApiServiceImpl.java:736                                                                                                            
   - 建议统一收口到可复用的 WebClient/Reactor Netty 或带连接池的 Apache HttpClient。                                                                                                                                                                                                                               
   - 这通常是最快见效的优化，尤其是 HTTPS + 代理场景。
2. 把代理解析/relay 预热前移，避免请求时启动                                     
   - 当前请求时才做代理选择、中继确保、探活：AiChatServiceImpl.java:1431-1485、AiProxyRelayServiceImpl.java:63-82                                                                                                                                                                                                  
   - ss/vmess/vless/trojan/hy2 首次请求会吃掉 xray 启动成本。
   - 建议把“高频 provider 对应节点”做后台预热，至少让常用节点常驻 relay。
3. 减少一次请求里的重复 DB 查询
   - resolveProviderConfigCandidates(...)、loadOrderedEnabledCandidatesByModel(...)、模型校验、计费模型查询都在反复查：AiChatServiceImpl.java:370-406、AiChatServiceImpl.java:599-615、AiOpenApiServiceImpl.java:2116-2141                                                                                         
   - 建议对“启用 provider 列表 / model-provider 关系 / model pricing”加 30~60 秒本地缓存。
   - 这是低风险高收益，能明显降 P50。
4. 把文件内容抽取从同步主链路移出去
   - appendFileContents(...) 会同步抽文件文本：AiChatServiceImpl.java:666-683                                                                                                                                                                                                                                      
   - 带文件请求时，这一段很可能比模型调用前的准备还慢。    
   - 建议做文件抽取缓存；同一 URL 不重复抽；大文件异步预处理。
5. 收紧 failover / proxy retry 触发条件
   - 现在 provider failover 最多 3 次：AiChatServiceImpl.java:76-77,103-121                                                                                                                                                                                                                                        
   - proxy retry 最多 2 次：AiChatServiceImpl.java:89,1315-1358、AiOpenApiServiceImpl.java:90,1055-1073
   - 好处是稳定，坏处是慢请求会被放大成超慢请求。                                                                                                                                                                                                                                                                  
   - 建议：
   - 默认 1 次主请求 + 1 次快速重试
   - 仅对明确网络错误重试
   - 对 first token timeout 再更激进，直接切节点
6. 把“首字超时”调成更贴近真实业务
   - 现在默认首包 8 秒：AiChatServiceImpl.java:85、AiChatServiceImpl.java:1155-1164                                                                                                                                                                                                                                
   - 对 chat/completions 场景建议按模型类型细分：
   - 普通文本模型：3~5s
   - reasoning/大模型：6~8s
     - 这样能更早淘汰坏代理，减少长尾。
7. 按 provider/model 做“最近成功节点优先”                                                                                                                                                                                                                                                                         
   - 现在代理是纯权重随机：AiProxyConfigServiceImpl.java:123-143
   - 建议改成：健康优先 + 权重随机，最近 N 分钟成功、低延时节点优先。                                                                                                                                                                                                                                              
   - 你已经有 runtime state 了，这一步非常适合接上。
8. 把 OpenAPI 和内部 Chat 的代理 HTTP 栈统一
   - 现在一部分用 WebClient，一部分用 RestTemplate，行为不一致：AiChatServiceImpl.java:1431 vs AiOpenApiServiceImpl.java:736
   - 统一后才能真正复用连接池、超时、代理、重试和监控策略。

如果你要我只选一个“先改哪里”，我建议第一步先改：把 AiOpenApiServiceImpl 里所有上游 HTTP 调用收口成可复用连接池客户端。这一步改动集中，收益最大，通常能先把整体延时降一截。下一步我可以直接给你出一个分阶段实施方案，或者直接开始改代码。                                                                          
                                              