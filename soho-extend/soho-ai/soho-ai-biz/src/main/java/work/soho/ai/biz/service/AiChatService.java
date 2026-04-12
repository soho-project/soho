package work.soho.ai.biz.service;

import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.request.AiChatRequest;

public interface AiChatService {
    AiChatResponse chat(AiChatRequest request);

    Flux<String> streamChat(AiChatRequest request);

    AiChatResponse chat(AiProviderConfig providerConfig, AiChatRequest request);

    Flux<String> streamChat(AiProviderConfig providerConfig, AiChatRequest request);

    AiUsageSummary estimateUsage(AiChatRequest request, String content);

    AiProviderConfig resolveProviderConfig(String providerCode, String model);

    /**
     * 按 provider 类型与模型解析可用配置。
     *
     * @param provider 提供方类型，例如 openai/gemini
     * @param model 模型名称
     * @return 已启用且可用的提供方配置
     */
    AiProviderConfig resolveProviderConfigByProvider(String provider, String model);
}
