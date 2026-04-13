package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiProviderRuntimeStateService;
import work.soho.ai.biz.utils.AiProviderModelUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Log4j2
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    private static final int DEFAULT_TIMEOUT_MS = 60000;
    private static final int MAX_FAILOVER_ATTEMPTS = 3;
    private static final String DEFAULT_CODEX_INSTRUCTIONS = "You are a helpful coding assistant.";
    private static final String EXTRA_NATIVE_RESPONSES = "nativeResponses";
    private static final String EXTRA_RESPONSES_REQUEST_BODY = "responsesRequestBody";
    private static final String EXTRA_ACTUAL_PROVIDER_CONFIG_ID = "actualProviderConfigId";
    private static final String EXTRA_ACTUAL_PROVIDER_CODE = "actualProviderCode";
    private static final String EXTRA_ACTUAL_PROVIDER = "actualProvider";
    private static final String EXTRA_ACTUAL_MODEL = "actualModel";
    private static final long DEFAULT_FIRST_PAYLOAD_TIMEOUT_MS = 8000L;

    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiFileService aiFileService;
    private final AiProviderRuntimeStateService aiProviderRuntimeStateService;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        List<AiProviderConfig> candidates = resolveProviderConfigCandidates(request.getProviderCode(), request.getModel());
        int maxAttempts = Math.min(MAX_FAILOVER_ATTEMPTS, candidates.size());
        RuntimeException lastException = null;
        for (int i = 0; i < maxAttempts; i++) {
            AiProviderConfig candidate = candidates.get(i);
            try {
                return chat(candidate, request);
            } catch (RuntimeException ex) {
                lastException = ex;
                log.warn("chat upstream attempt failed, attempt={}/{}, providerCode={}, model={}, error={}",
                        i + 1, maxAttempts, candidate.getCode(), request.getModel(), ex.getMessage());
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        throw new IllegalArgumentException("provider config not found");
    }

    @Override
    public Flux<String> streamChat(AiChatRequest request) {
        List<AiProviderConfig> candidates = resolveProviderConfigCandidates(request.getProviderCode(), request.getModel());
        int maxAttempts = Math.min(MAX_FAILOVER_ATTEMPTS, candidates.size());
        return streamChatWithFailover(candidates, request, 0, maxAttempts);
    }

    @Override
    public AiChatResponse chat(AiProviderConfig providerConfig, AiChatRequest request) {
        if (!aiProviderRuntimeStateService.isRequestAllowed(providerConfig)) {
            throw new IllegalStateException("provider temporarily unavailable: " + providerConfig.getCode());
        }
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String provider = pickProvider(providerConfig, config);
        String apiKey = pickApiKey(providerConfig, config);
        String baseUrl = pickBaseUrl(providerConfig, config);
        String model = normalizeModel(provider, pickModel(request, providerConfig, config));
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        List<AiChatRequest.Message> messages = enrichMessagesWithFiles(buildMessages(request));
        if (messages.isEmpty()) {
            throw new IllegalArgumentException("messages is empty");
        }
        validateRequired(provider, apiKey, baseUrl, model, config);
        validateSupportedModel(providerConfig, model);
        attachResolvedProviderMetadata(request, providerConfig, provider, model);

        if (isCodexResponsesAdapter(config)) {
            return callCodexResponses(providerConfig, provider, baseUrl, apiKey, model, messages, request, config, timeoutMs);
        }

        switch (provider.toLowerCase(Locale.ROOT)) {
            case "anthropic":
                return callAnthropic(providerConfig, provider, baseUrl, apiKey, model, messages, request, config, timeoutMs);
            case "gemini":
                return callGemini(providerConfig, provider, baseUrl, apiKey, model, messages, request, config, timeoutMs);
            case "ollama":
                return callOllama(providerConfig, provider, baseUrl, model, messages, request, config, timeoutMs);
            case "openai":
            case "deepseek":
            case "qwen":
            default:
                return callOpenAiCompatible(providerConfig, provider, baseUrl, apiKey, model, messages, request, config, timeoutMs);
        }
    }

    @Override
    public Flux<String> streamChat(AiProviderConfig providerConfig, AiChatRequest request) {
        if (!aiProviderRuntimeStateService.isRequestAllowed(providerConfig)) {
            return Flux.error(new IllegalStateException("provider temporarily unavailable: " + providerConfig.getCode()));
        }
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String provider = pickProvider(providerConfig, config);
        String apiKey = pickApiKey(providerConfig, config);
        String baseUrl = pickBaseUrl(providerConfig, config);
        String model = normalizeModel(provider, pickModel(request, providerConfig, config));
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        Boolean streamSupported = pickBoolean(config, "streamSupported", true);
        List<AiChatRequest.Message> messages = enrichMessagesWithFiles(buildMessages(request));
        if (messages.isEmpty()) {
            return Flux.error(new IllegalArgumentException("messages is empty"));
        }
        try {
            validateRequired(provider, apiKey, baseUrl, model, config);
            validateSupportedModel(providerConfig, model);
        } catch (IllegalArgumentException ex) {
            return Flux.error(ex);
        }
        attachResolvedProviderMetadata(request, providerConfig, provider, model);

        if (isCodexResponsesAdapter(config)) {
            Flux<String> stream = streamCodexResponses(baseUrl, apiKey, model, messages, request, config);
            return withUpstreamStreamTimingLog(providerConfig, provider, "", model,
                    applyFirstPayloadTimeout(stream, resolveFirstPayloadTimeoutMs(timeoutMs, config), providerConfig.getCode(), model));
        }

        if (Boolean.FALSE.equals(streamSupported)) {
            AiChatResponse resp = chat(providerConfig, request);
            return toOpenAiStream(resp.getContent());
        }

        Flux<String> stream;
        switch (provider.toLowerCase(Locale.ROOT)) {
            case "anthropic":
                stream = streamAnthropic(baseUrl, apiKey, model, messages, request, config);
                break;
            case "gemini":
                stream = streamGemini(baseUrl, apiKey, model, messages, request, config);
                break;
            case "ollama":
                stream = streamOllama(baseUrl, model, messages, request, config);
                break;
            case "openai":
            case "deepseek":
            case "qwen":
            default:
                stream = streamOpenAiCompatible(baseUrl, apiKey, model, messages, request, config);
                break;
        }
        return withUpstreamStreamTimingLog(providerConfig, provider, "", model,
                applyFirstPayloadTimeout(stream, resolveFirstPayloadTimeoutMs(timeoutMs, config), providerConfig.getCode(), model));
    }

    @Override
    public AiUsageSummary estimateUsage(AiChatRequest request, String content) {
        AiUsageSummary usageSummary = new AiUsageSummary();
        int promptChars = 0;
        if (StringUtils.isNotBlank(request.getInstructions())) {
            promptChars += request.getInstructions().length();
        }
        if (request.getMessages() != null) {
            for (AiChatRequest.Message message : request.getMessages()) {
                if (message != null) {
                    promptChars += buildTextOnlyMessageContent(message).length();
                }
            }
        }
        if (StringUtils.isNotBlank(request.getInput())) {
            promptChars += request.getInput().length();
        }
        int completionChars = StringUtils.isBlank(content) ? 0 : content.length();
        usageSummary.setPromptTokens(estimateTokensByChars(promptChars));
        usageSummary.setCompletionTokens(estimateTokensByChars(completionChars));
        usageSummary.setTotalTokens(usageSummary.getPromptTokens() + usageSummary.getCompletionTokens());
        return usageSummary;
    }

    @Override
    public AiProviderConfig resolveProviderConfig(String providerCode, String model) {
        List<AiProviderConfig> candidates = resolveProviderConfigCandidates(providerCode, model);
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("provider config not found");
        }
        return candidates.get(0);
    }

    /**
     * 按 provider 类型与模型解析可用配置，避免不同 provider 协议串线。
     *
     * @param provider 提供方类型，例如 openai/gemini
     * @param model 模型名
     * @return 提供方配置
     */
    @Override
    public AiProviderConfig resolveProviderConfigByProvider(String provider, String model) {
        if (StringUtils.isBlank(provider) || StringUtils.isBlank(model)) {
            throw new IllegalArgumentException("provider or model is required");
        }
        List<AiProviderConfig> orderedCandidates = loadOrderedEnabledCandidatesByModel(model);
        if (orderedCandidates.isEmpty()) {
            throw new IllegalArgumentException("provider config not found for model: " + model);
        }
        List<AiProviderConfig> availableCandidates = new ArrayList<>();
        for (AiProviderConfig orderedCandidate : orderedCandidates) {
            if (!provider.equalsIgnoreCase(orderedCandidate.getProvider())) {
                continue;
            }
            if (aiProviderRuntimeStateService.isRequestAllowed(orderedCandidate)) {
                availableCandidates.add(orderedCandidate);
            }
        }
        if (availableCandidates.isEmpty()) {
            throw new IllegalArgumentException("provider config not found for provider: " + provider + ", model: " + model);
        }
        AiProviderConfig selected = selectByWeight(availableCandidates);
        return selected == null ? availableCandidates.get(0) : selected;
    }

    private AiProviderConfig selectByWeight(List<AiProviderConfig> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        long totalWeight = 0L;
        for (AiProviderConfig candidate : candidates) {
            int weight = aiProviderRuntimeStateService.getEffectiveWeight(candidate);
            if (weight > 0) {
                totalWeight += weight;
            }
        }
        if (totalWeight <= 0) {
            return candidates.get(0);
        }
        long random = ThreadLocalRandom.current().nextLong(totalWeight) + 1;
        long cursor = 0L;
        for (AiProviderConfig candidate : candidates) {
            int weight = aiProviderRuntimeStateService.getEffectiveWeight(candidate);
            if (weight <= 0) {
                continue;
            }
            cursor += weight;
            if (random <= cursor) {
                return candidate;
            }
        }
        return candidates.get(0);
    }

    private int normalizeWeight(Integer weight) {
        if (weight == null) {
            return 1;
        }
        return Math.max(weight, 0);
    }

    /**
     * 解析候选上游配置列表。
     * 当指定 providerCode 时仅返回该配置；按 model 选择时返回“首个按权重随机 + 剩余按原顺序”的候选列表。
     *
     * @param providerCode 指定的提供方编码
     * @param model 模型名
     * @return 候选配置列表
     */
    private List<AiProviderConfig> resolveProviderConfigCandidates(String providerCode, String model) {
        if (StringUtils.isNotBlank(providerCode)) {
            AiProviderConfig candidate = loadEnabledProviderConfigByCode(providerCode);
            if (!aiProviderRuntimeStateService.isRequestAllowed(candidate)) {
                throw new IllegalStateException("provider temporarily unavailable: " + providerCode);
            }
            return Collections.singletonList(candidate);
        }
        if (StringUtils.isBlank(model)) {
            throw new IllegalArgumentException("providerCode or model is required");
        }
        List<AiProviderConfig> orderedCandidates = loadOrderedEnabledCandidatesByModel(model);
        if (orderedCandidates.isEmpty()) {
            throw new IllegalArgumentException("provider config not found for model: " + model);
        }
        List<AiProviderConfig> availableCandidates = new ArrayList<>();
        for (AiProviderConfig orderedCandidate : orderedCandidates) {
            if (aiProviderRuntimeStateService.isRequestAllowed(orderedCandidate)) {
                availableCandidates.add(orderedCandidate);
            }
        }
        if (availableCandidates.isEmpty()) {
            throw new IllegalStateException("all upstream providers are temporarily unavailable for model: " + model);
        }
        AiProviderConfig first = selectByWeight(availableCandidates);
        if (first == null) {
            return availableCandidates;
        }
        List<AiProviderConfig> candidates = new ArrayList<>();
        candidates.add(first);
        for (AiProviderConfig candidate : availableCandidates) {
            if (!first.getId().equals(candidate.getId())) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    /**
     * 按编码加载启用中的提供方配置。
     *
     * @param providerCode 提供方编码
     * @return 提供方配置
     */
    private AiProviderConfig loadEnabledProviderConfigByCode(String providerCode) {
        log.debug("resolveProviderConfig start providerCode={}", providerCode);
        AiProviderConfig config = aiProviderConfigService.getOne(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getCode, providerCode)
                .eq(AiProviderConfig::getStatus, 1)
                .last("limit 1"));
        if (config == null) {
            log.warn("resolveProviderConfig failed: providerCode not found or disabled, providerCode={}", providerCode);
            throw new IllegalArgumentException("provider config not found");
        }
        return config;
    }

    /**
     * 按模型加载有序候选列表。
     *
     * @param model 模型名
     * @return 候选列表
     */
    private List<AiProviderConfig> loadOrderedEnabledCandidatesByModel(String model) {
        List<Long> providerConfigIds = aiProviderModelRelService.listEnabledProviderConfigIdsByModelName(model);
        log.debug("resolveProviderConfig by model={}, providerConfigIds={}", model, providerConfigIds);
        List<AiProviderConfig> enabledConfigs = aiProviderConfigService.list(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getStatus, 1));
        if (enabledConfigs.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, AiProviderConfig> configMap = new HashMap<>();
        for (AiProviderConfig enabledConfig : enabledConfigs) {
            configMap.put(enabledConfig.getId(), enabledConfig);
        }
        List<AiProviderConfig> orderedCandidates = new ArrayList<>();
        Set<Long> addedConfigIds = new LinkedHashSet<>();
        for (Long providerConfigId : providerConfigIds) {
            AiProviderConfig candidate = configMap.get(providerConfigId);
            if (candidate != null) {
                orderedCandidates.add(candidate);
                addedConfigIds.add(candidate.getId());
            }
        }
        for (AiProviderConfig enabledConfig : enabledConfigs) {
            if (enabledConfig.getId() == null || addedConfigIds.contains(enabledConfig.getId())) {
                continue;
            }
            List<String> declaredModels = AiProviderModelUtils.extractModels(enabledConfig);
            if (declaredModels.contains(model)) {
                orderedCandidates.add(enabledConfig);
            }
        }
        return orderedCandidates;
    }

    /**
     * 流式请求失败自动切换候选上游。
     *
     * @param candidates 候选配置
     * @param request 请求参数
     * @param index 当前尝试索引
     * @param maxAttempts 最大尝试次数
     * @return 流式响应
     */
    private Flux<String> streamChatWithFailover(List<AiProviderConfig> candidates, AiChatRequest request,
                                                int index, int maxAttempts) {
        if (index >= maxAttempts) {
            return Flux.error(new IllegalArgumentException("all upstream providers failed"));
        }
        AiProviderConfig candidate = candidates.get(index);
        return Flux.defer(() -> streamChat(candidate, request))
                .onErrorResume(ex -> {
                    log.warn("stream chat upstream attempt failed, attempt={}/{}, providerCode={}, model={}, error={}",
                            index + 1, maxAttempts, candidate.getCode(), request.getModel(), ex.getMessage());
                    if (index + 1 >= maxAttempts) {
                        return Flux.error(ex);
                    }
                    return streamChatWithFailover(candidates, request, index + 1, maxAttempts);
                });
    }

    private Map<String, Object> parseConfig(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return new HashMap<>();
        }
        Map<String, Object> map = JacksonUtils.toBean(configJson, new TypeReference<Map<String, Object>>() {});
        return map == null ? new HashMap<>() : map;
    }

    private String pickProvider(AiProviderConfig providerConfig, Map<String, Object> config) {
        String provider = pickString(config, "provider", providerConfig.getProvider());
        if (StringUtils.isBlank(provider)) {
            throw new IllegalArgumentException("provider is blank");
        }
        return provider;
    }

    private String pickApiKey(AiProviderConfig providerConfig, Map<String, Object> config) {
        String apiKey = pickString(config, "apiKey", providerConfig.getApiKeyRef());
        return apiKey == null ? "" : apiKey;
    }

    private String pickBaseUrl(AiProviderConfig providerConfig, Map<String, Object> config) {
        String baseUrl = pickString(config, "baseUrl", providerConfig.getBaseUrl());
        return baseUrl == null ? "" : baseUrl;
    }

    private String pickModel(AiChatRequest request, AiProviderConfig providerConfig, Map<String, Object> config) {
        if (StringUtils.isNotBlank(request.getModel())) {
            return request.getModel();
        }
        String model = pickString(config, "model", providerConfig.getDefaultModel());
        return model == null ? "" : model;
    }

    private String normalizeModel(String provider, String model) {
        if (StringUtils.isNotBlank(model)) {
            return model;
        }
        if (provider == null) {
            return "";
        }
        switch (provider.toLowerCase(Locale.ROOT)) {
            case "deepseek":
                return "deepseek-chat";
            case "gemini":
                return "gemini-pro";
            default:
                return "";
        }
    }

    private void validateRequired(String provider, String apiKey, String baseUrl, String model, Map<String, Object> config) {
        if (StringUtils.isBlank(baseUrl)) {
            throw new IllegalArgumentException("baseUrl is blank");
        }
        if (StringUtils.isBlank(model)) {
            throw new IllegalArgumentException("model is blank");
        }
        if (provider == null) {
            return;
        }
        if (isCodexResponsesAdapter(config)) {
            if (StringUtils.isBlank(apiKey)) {
                throw new IllegalArgumentException("apiKey is blank");
            }
            return;
        }
        switch (provider.toLowerCase(Locale.ROOT)) {
            case "openai":
            case "deepseek":
            case "qwen":
            case "anthropic":
            case "gemini":
                if (StringUtils.isBlank(apiKey)) {
                    throw new IllegalArgumentException("apiKey is blank");
                }
                break;
            case "ollama":
            default:
                break;
        }
    }

    private void validateSupportedModel(AiProviderConfig providerConfig, String model) {
        List<String> supportedModels = new ArrayList<>();
        for (work.soho.ai.biz.domain.AiModelInfo item : aiProviderModelRelService.listEnabledModelsByProviderConfigId(providerConfig.getId())) {
            if (StringUtils.isNotBlank(item.getModelName())) {
                supportedModels.add(item.getModelName());
            }
        }
        if (supportedModels.isEmpty()) {
            supportedModels = AiProviderModelUtils.extractModels(providerConfig);
        }
        if (supportedModels.isEmpty() || StringUtils.isBlank(model)) {
            return;
        }
        if (!supportedModels.contains(model)) {
            throw new IllegalArgumentException("model not supported: " + model);
        }
    }

    private List<AiChatRequest.Message> buildMessages(AiChatRequest request) {
        List<AiChatRequest.Message> messages = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getInstructions())) {
            AiChatRequest.Message system = new AiChatRequest.Message();
            system.setRole("system");
            system.setContent(request.getInstructions());
            messages.add(system);
        }
        if (request.getMessages() != null) {
            messages.addAll(request.getMessages());
        }
        if (messages.stream().noneMatch(item -> !"system".equalsIgnoreCase(item.getRole()))
                && StringUtils.isNotBlank(request.getInput())) {
            AiChatRequest.Message user = new AiChatRequest.Message();
            user.setRole("user");
            user.setContent(request.getInput());
            messages.add(user);
        }
        return messages;
    }

    private List<AiChatRequest.Message> enrichMessagesWithFiles(List<AiChatRequest.Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return messages;
        }
        List<AiChatRequest.Message> result = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            if (message == null) {
                continue;
            }
            result.add(enrichMessageWithFiles(message));
        }
        return result;
    }

    private AiChatRequest.Message enrichMessageWithFiles(AiChatRequest.Message message) {
        List<String> fileUrls = normalizeFileUrls(message.getFileUrls());
        if (fileUrls.isEmpty()) {
            return message;
        }

        AiChatRequest.Message enriched = new AiChatRequest.Message();
        enriched.setRole(message.getRole());
        enriched.setImageUrls(message.getImageUrls());
        enriched.setFileUrls(fileUrls);
        enriched.setContent(appendFileContents(message.getContent(), fileUrls));
        return enriched;
    }

    private String appendFileContents(String content, List<String> fileUrls) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(content)) {
            builder.append(content.trim());
        }
        for (String fileUrl : fileUrls) {
            String extractedText = aiFileService.extractTextFromUrl(fileUrl);
            if (StringUtils.isBlank(extractedText)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append("[File Content]\n");
            builder.append("Source: ").append(fileUrl).append("\n");
            builder.append(extractedText);
        }
        return builder.toString();
    }

    private boolean isCodexResponsesAdapter(Map<String, Object> config) {
        String adapter = pickString(config, "adapter", "");
        return "codexResponses".equalsIgnoreCase(adapter)
                || "chatgptCodexResponses".equalsIgnoreCase(adapter);
    }

    private AiChatResponse callOpenAiCompatible(AiProviderConfig providerConfig, String provider, String baseUrl, String apiKey, String model,
                                                List<AiChatRequest.Message> messages, AiChatRequest request,
                                                Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "openaiPath", "/v1/chat/completions");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));
        putIfNotNull(body, "stream", request.getStream());

        Map<String, String> headers = new HashMap<>();
        if (StringUtils.isNotBlank(apiKey)) {
            headers.put("Authorization", "Bearer " + apiKey);
        }
        return withUpstreamRequestTimingLog(providerConfig, provider, model, url, () -> {
            String raw = postJson(url, headers, body, timeoutMs, config);
            String content = extractOpenAiContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, model, content, raw, usage);
        });
    }

    private AiChatResponse callAnthropic(AiProviderConfig providerConfig, String provider, String baseUrl, String apiKey, String model,
                                         List<AiChatRequest.Message> messages, AiChatRequest request,
                                         Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "anthropicPath", "/v1/messages");
        String url = joinUrl(baseUrl, path);
        String version = pickString(config, "anthropicVersion", "2023-06-01");

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toAnthropicMessages(messages));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens(), 1024));
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        String system = pickSystemPrompt(messages);
        if (StringUtils.isNotBlank(system)) {
            body.put("system", system);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", apiKey);
        headers.put("anthropic-version", version);
        return withUpstreamRequestTimingLog(providerConfig, provider, model, url, () -> {
            String raw = postJson(url, headers, body, timeoutMs, config);
            String content = extractAnthropicContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, model, content, raw, usage);
        });
    }

    private AiChatResponse callGemini(AiProviderConfig providerConfig, String provider, String baseUrl, String apiKey, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      Map<String, Object> config, Integer timeoutMs) {
        String apiVersion = pickString(config, "geminiApiVersion", "v1beta");
        String path = "/" + apiVersion + "/models/" + model + ":generateContent";
        String url = joinUrl(baseUrl, path);
        if (StringUtils.isNotBlank(apiKey)) {
            url = url + "?key=" + apiKey;
        }
        final String requestUrl = url;

        Map<String, Object> body = new HashMap<>();
        body.put("contents", toGeminiContents(messages));
        Map<String, Object> generationConfig = new HashMap<>();
        putIfNotNull(generationConfig, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(generationConfig, "topP", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(generationConfig, "maxOutputTokens", pickInteger(config, "maxTokens", request.getMaxTokens()));
        if (!generationConfig.isEmpty()) {
            body.put("generationConfig", generationConfig);
        }
        String system = pickSystemPrompt(messages);
        if (StringUtils.isNotBlank(system)) {
            body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", system))));
        }

        return withUpstreamRequestTimingLog(providerConfig, provider, model, requestUrl, () -> {
            String raw = postJson(requestUrl, Collections.emptyMap(), body, timeoutMs, config);
            String content = extractGeminiContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, model, content, raw, usage);
        });
    }

    private AiChatResponse callOllama(AiProviderConfig providerConfig, String provider, String baseUrl, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "ollamaPath", "/api/chat");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        putIfNotNull(body, "stream", request.getStream() != null ? request.getStream() : Boolean.FALSE);

        return withUpstreamRequestTimingLog(providerConfig, provider, model, url, () -> {
            String raw = postJson(url, Collections.emptyMap(), body, timeoutMs, config);
            String content = extractOllamaContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, model, content, raw, usage);
        });
    }

    private AiChatResponse callCodexResponses(AiProviderConfig providerConfig, String provider, String baseUrl, String apiKey, String model,
                                              List<AiChatRequest.Message> messages, AiChatRequest request,
                                              Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "codexResponsesPath", "/backend-api/codex/responses");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = resolveCodexRequestBody(model, messages, request, config, true);

        return withUpstreamRequestTimingLog(providerConfig, provider, model, url, () -> {
            List<String> payloads = buildWebClient(config)
                    .post()
                    .uri(url)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(errorBody -> {
                                log.error("codex responses request failed status={}, body={}, requestBody={}",
                                        response.statusCode().value(), errorBody, JacksonUtils.toJson(body));
                                return Mono.error(new IllegalArgumentException("codex responses request failed: " + errorBody));
                            }))
                    .bodyToFlux(DataBuffer.class)
                    .map(this::bufferToString)
                    .transform(this::sseToPayloadFlux)
                    .collectList()
                    .block();

            StringBuilder contentBuilder = new StringBuilder();
            String completedPayload = "";
            if (payloads != null) {
                for (String payload : payloads) {
                    if (StringUtils.isBlank(payload) || "[DONE]".equals(payload)) {
                        continue;
                    }
                    completedPayload = payload;
                    appendCodexTextDelta(payload, contentBuilder);
                }
            }
            String raw = StringUtils.isBlank(completedPayload) ? "{}" : completedPayload;
            String content = contentBuilder.toString();
            AiUsageSummary usage = extractUsage("codexResponses", raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, model, content, raw, usage);
        });
    }

    private Flux<String> streamOpenAiCompatible(String baseUrl, String apiKey, String model,
                                                List<AiChatRequest.Message> messages, AiChatRequest request,
                                                Map<String, Object> config) {
        String path = pickString(config, "openaiPath", "/v1/chat/completions");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        body.put("stream", true);
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));

        WebClient.RequestBodySpec req = buildWebClient(config)
                .post()
                .uri(url)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(apiKey)) {
            req.header("Authorization", "Bearer " + apiKey);
        }
        Flux<String> stream = req.bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> buildUpstreamHttpError(url, body, response))
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::sseToPayloadFlux)
                .doOnError(ex -> log.error("openai stream upstream request failed, url={}, error={}",
                        url, extractUpstreamErrorMessage(ex), ex));
        return stream;
    }

    private Flux<String> streamAnthropic(String baseUrl, String apiKey, String model,
                                         List<AiChatRequest.Message> messages, AiChatRequest request,
                                         Map<String, Object> config) {
        String path = pickString(config, "anthropicPath", "/v1/messages");
        String url = joinUrl(baseUrl, path);
        String version = pickString(config, "anthropicVersion", "2023-06-01");

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toAnthropicMessages(messages));
        body.put("stream", true);
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens(), 1024));
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        String system = pickSystemPrompt(messages);
        if (StringUtils.isNotBlank(system)) {
            body.put("system", system);
        }

        Flux<String> stream = buildWebClient(config)
                .post()
                .uri(url)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-api-key", apiKey)
                .header("anthropic-version", version)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> buildUpstreamHttpError(url, body, response))
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::sseToPayloadFlux)
                .doOnError(ex -> log.error("anthropic stream upstream request failed, url={}, error={}",
                        url, extractUpstreamErrorMessage(ex), ex));
        return stream;
    }

    private Flux<String> streamGemini(String baseUrl, String apiKey, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      Map<String, Object> config) {
        String apiVersion = pickString(config, "geminiApiVersion", "v1beta");
        String path = "/" + apiVersion + "/models/" + model + ":streamGenerateContent";
        String url = joinUrl(baseUrl, path);
        if (StringUtils.isNotBlank(apiKey)) {
            url = url + "?key=" + apiKey;
        }
        final String requestUrl = url;

        Map<String, Object> body = new HashMap<>();
        body.put("contents", toGeminiContents(messages));
        Map<String, Object> generationConfig = new HashMap<>();
        putIfNotNull(generationConfig, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(generationConfig, "topP", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(generationConfig, "maxOutputTokens", pickInteger(config, "maxTokens", request.getMaxTokens()));
        if (!generationConfig.isEmpty()) {
            body.put("generationConfig", generationConfig);
        }
        String system = pickSystemPrompt(messages);
        if (StringUtils.isNotBlank(system)) {
            body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", system))));
        }

        Flux<String> stream = buildWebClient(config)
                .post()
                .uri(requestUrl)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> buildUpstreamHttpError(requestUrl, body, response))
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::sseToPayloadFlux)
                .doOnError(ex -> log.error("gemini stream upstream request failed, url={}, error={}",
                        requestUrl, extractUpstreamErrorMessage(ex), ex));
        return stream;
    }

    private Flux<String> streamOllama(String baseUrl, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      Map<String, Object> config) {
        String path = pickString(config, "ollamaPath", "/api/chat");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        body.put("stream", true);

        Flux<String> stream = buildWebClient(config)
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> buildUpstreamHttpError(url, body, response))
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::linesToFlux)
                .doOnError(ex -> log.error("ollama stream upstream request failed, url={}, error={}",
                        url, extractUpstreamErrorMessage(ex), ex));
        return stream;
    }

    private Flux<String> streamCodexResponses(String baseUrl, String apiKey, String model,
                                              List<AiChatRequest.Message> messages, AiChatRequest request,
                                              Map<String, Object> config) {
        String path = pickString(config, "codexResponsesPath", "/backend-api/codex/responses");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = resolveCodexRequestBody(model, messages, request, config, true);
        boolean nativeResponses = isNativeResponses(request);

        Flux<String> payloadFlux = buildWebClient(config)
                .post()
                .uri(url)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(errorBody -> {
                            log.error("codex responses stream request failed status={}, body={}, requestBody={}",
                                    response.statusCode().value(), errorBody, JacksonUtils.toJson(body));
                            return Mono.error(new IllegalArgumentException("codex responses stream request failed: " + errorBody));
                        }))
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::sseToPayloadFlux);

        if (nativeResponses) {
            return payloadFlux;
        }
        Flux<String> openAiStream = payloadFlux.flatMap(payload -> codexPayloadToOpenAiPayload(payload, model));
        return openAiStream;
    }

    /**
     * 记录上游流式请求耗时日志（首字用时、总用时）。
     *
     * @param provider 上游提供方
     * @param url      上游请求地址
     * @param model    上游模型
     * @param source   原始流
     * @return 带日志打点的流
     */
    private Flux<String> withUpstreamStreamTimingLog(AiProviderConfig providerConfig, String provider, String url, String model, Flux<String> source) {
        long startAt = System.currentTimeMillis();
        AtomicLong firstTokenAt = new AtomicLong(-1L);
        return source
                .doOnSubscribe(s -> log.info("ai upstream stream request start, provider={}, model={}, url={}",
                        provider, model, url))
                .doOnNext(payload -> {
                    if (firstTokenAt.get() >= 0) {
                        return;
                    }
                    if (hasTextDelta(payload) && firstTokenAt.compareAndSet(-1L, System.currentTimeMillis())) {
                        long firstTokenMs = firstTokenAt.get() - startAt;
                        log.info("ai upstream stream first token, provider={}, model={}, url={}, first_token_ms={}",
                                provider, model, url, firstTokenMs);
                    }
                })
                .doOnComplete(() -> {
                    long totalMs = System.currentTimeMillis() - startAt;
                    long firstTokenMs = firstTokenAt.get() < 0 ? -1L : firstTokenAt.get() - startAt;
                    aiProviderRuntimeStateService.recordSuccess(providerConfig, totalMs, firstTokenMs > 0 ? firstTokenMs : null);
                    log.info("ai upstream stream completed, provider={}, model={}, url={}, total_ms={}, first_token_ms={}",
                            provider, model, url, totalMs, firstTokenMs);
                })
                .doOnError(ex -> {
                    long totalMs = System.currentTimeMillis() - startAt;
                    long firstTokenMs = firstTokenAt.get() < 0 ? -1L : firstTokenAt.get() - startAt;
                    aiProviderRuntimeStateService.recordFailure(providerConfig, ex);
                    log.warn("ai upstream stream failed, provider={}, model={}, url={}, total_ms={}, first_token_ms={}, error={}",
                            provider, model, url, totalMs, firstTokenMs, ex.getMessage());
                });
    }

    /**
     * 记录上游非流式请求总耗时日志。
     *
     * @param provider 上游提供方
     * @param model    上游模型
     * @param url      上游请求地址
     * @param call     请求执行逻辑
     * @return 上游响应
     */
    private AiChatResponse withUpstreamRequestTimingLog(AiProviderConfig providerConfig, String provider, String model, String url,
                                                        Supplier<AiChatResponse> call) {
        long startAt = System.currentTimeMillis();
        log.info("ai upstream request start, provider={}, model={}, url={}", provider, model, url);
        try {
            AiChatResponse response = call.get();
            long totalMs = System.currentTimeMillis() - startAt;
            aiProviderRuntimeStateService.recordSuccess(providerConfig, totalMs, null);
            log.info("ai upstream request completed, provider={}, model={}, url={}, total_ms={}",
                    provider, model, url, totalMs);
            return response;
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            aiProviderRuntimeStateService.recordFailure(providerConfig, ex);
            log.warn("ai upstream request failed, provider={}, model={}, url={}, total_ms={}, error={}",
                    provider, model, url, totalMs, ex.getMessage());
            throw ex;
        }
    }

    /**
     * 应用首包超时保护，避免慢代理长期占住请求。
     *
     * @param source 原始流
     * @param timeoutMs 超时毫秒
     * @param providerCode 提供方编码
     * @param model 模型
     * @return 处理后的流
     */
    private Flux<String> applyFirstPayloadTimeout(Flux<String> source, long timeoutMs, String providerCode, String model) {
        if (timeoutMs <= 0) {
            return source;
        }
        return Flux.create(sink -> {
            AtomicBoolean firstPayloadReceived = new AtomicBoolean(false);
            Disposable timeoutTask = Schedulers.parallel().schedule(() -> {
                if (firstPayloadReceived.compareAndSet(false, true)) {
                    sink.error(new IllegalStateException("upstream first token timeout, provider=" + providerCode + ", model=" + model));
                }
            }, timeoutMs, TimeUnit.MILLISECONDS);
            Disposable subscription = source.subscribe(
                    payload -> {
                        if (firstPayloadReceived.compareAndSet(false, true)) {
                            timeoutTask.dispose();
                        }
                        sink.next(payload);
                    },
                    error -> {
                        timeoutTask.dispose();
                        sink.error(error);
                    },
                    () -> {
                        timeoutTask.dispose();
                        sink.complete();
                    }
            );
            sink.onDispose(() -> {
                timeoutTask.dispose();
                subscription.dispose();
            });
        });
    }

    /**
     * 解析首包超时时间。
     *
     * @param timeoutMs 请求总超时
     * @param config 配置
     * @return 首包超时时间
     */
    private long resolveFirstPayloadTimeoutMs(Integer timeoutMs, Map<String, Object> config) {
        Integer configured = pickInteger(config, "firstTokenTimeoutMs", null);
        if (configured != null && configured > 0) {
            return configured;
        }
        if (timeoutMs != null && timeoutMs > 0) {
            return Math.min(timeoutMs, (int) Math.max(DEFAULT_FIRST_PAYLOAD_TIMEOUT_MS, timeoutMs / 2L));
        }
        return DEFAULT_FIRST_PAYLOAD_TIMEOUT_MS;
    }

    /**
     * 判断流式 payload 是否包含文本增量。
     *
     * @param payload 单条 payload
     * @return 是否包含文本增量
     */
    private boolean hasTextDelta(String payload) {
        if (StringUtils.isBlank(payload) || "[DONE]".equals(payload)) {
            return false;
        }
        try {
            JsonNode root = JacksonUtils.toBean(payload, JsonNode.class);
            if (root == null || root.isMissingNode()) {
                return false;
            }
            if (hasNonBlankNode(root.at("/choices/0/delta/content"))) {
                return true;
            }
            if (hasNonBlankNode(root.at("/delta/text"))) {
                return true;
            }
            if (hasNonBlankNode(root.at("/candidates/0/content/parts/0/text"))) {
                return true;
            }
            if (hasNonBlankNode(root.at("/message/content"))) {
                return true;
            }
            if (hasNonBlankNode(root.at("/delta"))) {
                return true;
            }
            return hasNonBlankNode(root.at("/output_text"));
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * 判断 JSON 节点是否为非空文本。
     *
     * @param node JSON 节点
     * @return 是否为非空文本
     */
    private boolean hasNonBlankNode(JsonNode node) {
        return node != null && !node.isMissingNode() && !node.isNull() && StringUtils.isNotBlank(node.asText());
    }

    private Map<String, Object> resolveCodexRequestBody(String model, List<AiChatRequest.Message> messages,
                                                        AiChatRequest request, Map<String, Object> config,
                                                        boolean stream) {
        Map<String, Object> body = extractNativeResponsesRequestBody(request);
        if (body != null) {
            body.put("model", model);
            body.put("stream", stream);
            return body;
        }
        return buildCodexRequestBody(model, messages, request, config, stream);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractNativeResponsesRequestBody(AiChatRequest request) {
        if (!isNativeResponses(request) || request.getExtra() == null) {
            return null;
        }
        Object bodyObj = request.getExtra().get(EXTRA_RESPONSES_REQUEST_BODY);
        if (!(bodyObj instanceof Map)) {
            return null;
        }
        return new HashMap<>((Map<String, Object>) bodyObj);
    }

    private boolean isNativeResponses(AiChatRequest request) {
        if (request == null || request.getExtra() == null) {
            return false;
        }
        Object value = request.getExtra().get(EXTRA_NATIVE_RESPONSES);
        return value instanceof Boolean && (Boolean) value;
    }

    private String postJson(String url, Map<String, String> headers, Map<String, Object> body, Integer timeoutMs,
                            Map<String, Object> config) {
        RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        HttpEntity<String> entity = new HttpEntity<>(JacksonUtils.toJson(body), httpHeaders);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (RuntimeException ex) {
            throw wrapUpstreamException(url, body, ex);
        }
    }

    /**
     * 构建 WebClient 上游 HTTP 异常并写入日志，保留上游错误正文用于排查。
     */
    private Mono<? extends Throwable> buildUpstreamHttpError(String url, Map<String, Object> requestBody, ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(errorBody -> {
                    log.error("upstream api http error, url={}, status={}, body={}, requestBody={}",
                            url, response.statusCode().value(), errorBody, JacksonUtils.toJson(requestBody));
                    return Mono.error(new IllegalArgumentException("upstream api request failed: " + errorBody));
                });
    }

    /**
     * 统一包装上游请求异常，确保日志中包含请求地址、请求体及上游错误信息。
     */
    private RuntimeException wrapUpstreamException(String url, Map<String, Object> requestBody, RuntimeException ex) {
        String message = extractUpstreamErrorMessage(ex);
        log.error("upstream api request failed, url={}, error={}, requestBody={}",
                url, message, JacksonUtils.toJson(requestBody), ex);
        return new IllegalArgumentException("upstream api request failed: " + message, ex);
    }

    /**
     * 提取上游错误信息，优先读取 HTTP 响应体，避免只拿到笼统异常描述。
     */
    private String extractUpstreamErrorMessage(Throwable ex) {
        if (ex instanceof WebClientResponseException) {
            String responseBody = ((WebClientResponseException) ex).getResponseBodyAsString();
            if (StringUtils.isNotBlank(responseBody)) {
                return responseBody;
            }
        }
        if (ex instanceof HttpStatusCodeException) {
            String responseBody = ((HttpStatusCodeException) ex).getResponseBodyAsString();
            if (StringUtils.isNotBlank(responseBody)) {
                return responseBody;
            }
        }
        String message = ex == null ? null : ex.getMessage();
        return StringUtils.isBlank(message) ? "unknown upstream error" : message;
    }

    private RestTemplate buildRestTemplate(Integer timeoutMs, Proxy proxy) {
        int timeout = timeoutMs == null || timeoutMs <= 0 ? DEFAULT_TIMEOUT_MS : timeoutMs;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        if (proxy != null) {
            factory.setProxy(proxy);
        }
        return new RestTemplate(factory);
    }

    WebClient buildWebClient() {
        return WebClient.builder().build();
    }

    WebClient buildWebClient(Map<String, Object> config) {
        Proxy proxy = buildProxy(config);
        if (proxy == null) {
            return buildWebClient();
        }
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        HttpClient httpClient = HttpClient.create().proxy(spec -> {
            if (proxy.type() == Proxy.Type.SOCKS) {
                spec.type(ProxyProvider.Proxy.SOCKS5)
                        .host(address.getHostString())
                        .port(address.getPort());
            } else {
                spec.type(ProxyProvider.Proxy.HTTP)
                        .host(address.getHostString())
                        .port(address.getPort());
            }
        });
        return WebClient.builder()
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                .build();
    }

    private Proxy buildProxy(Map<String, Object> config) {
        String proxyType = pickString(config, "proxyType", "");
        String proxyHost = pickString(config, "proxyHost", "");
        Integer proxyPort = pickInteger(config, "proxyPort", null);
        if (StringUtils.isBlank(proxyType) || StringUtils.isBlank(proxyHost) || proxyPort == null || proxyPort <= 0) {
            return null;
        }
        Proxy.Type type;
        switch (proxyType.toLowerCase(Locale.ROOT)) {
            case "http":
            case "https":
                type = Proxy.Type.HTTP;
                break;
            case "socks":
            case "socks5":
                type = Proxy.Type.SOCKS;
                break;
            default:
                throw new IllegalArgumentException("unsupported proxyType: " + proxyType);
        }
        return new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
    }

    private List<Map<String, Object>> toOpenAiMessages(List<AiChatRequest.Message> messages) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            Object content = toOpenAiContent(message);
            if (content == null) {
                continue;
            }
            list.add(Map.of("role", message.getRole(), "content", content));
        }
        return list;
    }

    private List<Map<String, Object>> toAnthropicMessages(List<AiChatRequest.Message> messages) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            String content = buildTextOnlyMessageContent(message);
            if ("system".equalsIgnoreCase(message.getRole()) || StringUtils.isBlank(content)) {
                continue;
            }
            list.add(Map.of("role", message.getRole(), "content", content));
        }
        return list;
    }

    private List<Map<String, Object>> toGeminiContents(List<AiChatRequest.Message> messages) {
        List<Map<String, Object>> contents = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            String content = buildTextOnlyMessageContent(message);
            if ("system".equalsIgnoreCase(message.getRole()) || StringUtils.isBlank(content)) {
                continue;
            }
            String role = "assistant".equalsIgnoreCase(message.getRole()) ? "model" : "user";
            contents.add(Map.of("role", role, "parts", List.of(Map.of("text", content))));
        }
        return contents;
    }

    private Map<String, Object> buildCodexRequestBody(String model, List<AiChatRequest.Message> messages,
                                                      AiChatRequest request, Map<String, Object> config,
                                                      boolean stream) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("store", pickBoolean(config, "store", false));
        body.put("stream", stream);
        putIfNotNull(body, "instructions", buildCodexInstructions(messages));
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(body, "max_output_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));

        List<Map<String, Object>> input = new ArrayList<>();
        AiChatRequest.Message latestUserMessage = findLatestUserMessage(messages);
        String latestUserContent = buildTextOnlyMessageContent(latestUserMessage);
        if (StringUtils.isNotBlank(latestUserContent)) {
            input.add(Map.of("role", "user", "content", latestUserContent));
        }
        if (input.isEmpty()) {
            throw new IllegalArgumentException("codex input is empty");
        }
        body.put("input", input);
        return body;
    }

    private String buildCodexInstructions(List<AiChatRequest.Message> messages) {
        String systemPrompt = pickSystemPrompt(messages);
        StringBuilder history = new StringBuilder();
        AiChatRequest.Message latestUserMessage = findLatestUserMessage(messages);
        for (AiChatRequest.Message message : messages) {
            if (message == null
                    || StringUtils.isBlank(buildTextOnlyMessageContent(message))
                    || "system".equalsIgnoreCase(message.getRole())
                    || message == latestUserMessage) {
                continue;
            }
            if (history.length() > 0) {
                history.append("\n");
            }
            history.append(message.getRole()).append(": ").append(buildTextOnlyMessageContent(message));
        }
        if (StringUtils.isBlank(systemPrompt) && history.length() == 0) {
            return DEFAULT_CODEX_INSTRUCTIONS;
        }
        if (StringUtils.isBlank(systemPrompt)) {
            return DEFAULT_CODEX_INSTRUCTIONS + "\n\nConversation context:\n" + history;
        }
        if (history.length() == 0) {
            return systemPrompt;
        }
        return systemPrompt + "\n\nConversation context:\n" + history;
    }

    private AiChatRequest.Message findLatestUserMessage(List<AiChatRequest.Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            AiChatRequest.Message message = messages.get(i);
            if (message != null
                    && "user".equalsIgnoreCase(message.getRole())
                    && StringUtils.isNotBlank(buildTextOnlyMessageContent(message))) {
                return message;
            }
        }
        return null;
    }

    private String extractOpenAiContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/choices/0/message/content");
            if (node.isMissingNode()) {
                node = root.at("/choices/0/text");
            }
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse openai response failed", e);
            return "";
        }
    }

    private String extractAnthropicContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/content/0/text");
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse anthropic response failed", e);
            return "";
        }
    }

    private String extractGeminiContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/candidates/0/content/parts/0/text");
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse gemini response failed", e);
            return "";
        }
    }

    private String extractOllamaContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/message/content");
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse ollama response failed", e);
            return "";
        }
    }

    private String extractCodexContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode outputText = root.get("output_text");
            if (outputText != null && outputText.isTextual()) {
                return outputText.asText();
            }
            JsonNode output = root.get("output");
            if (output != null && output.isArray()) {
                StringBuilder builder = new StringBuilder();
                for (JsonNode item : output) {
                    JsonNode content = item.get("content");
                    if (content == null || !content.isArray()) {
                        continue;
                    }
                    for (JsonNode contentItem : content) {
                        JsonNode text = contentItem.get("text");
                        if (text != null && text.isTextual()) {
                            builder.append(text.asText());
                        }
                    }
                }
                return builder.toString();
            }
            return "";
        } catch (Exception e) {
            log.error("parse codex response failed", e);
            return "";
        }
    }

    private AiUsageSummary extractUsage(String provider, String raw) {
        AiUsageSummary summary = new AiUsageSummary();
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            switch (provider.toLowerCase(Locale.ROOT)) {
                case "anthropic":
                    summary.setPromptTokens(root.path("usage").path("input_tokens").asInt(0));
                    summary.setCompletionTokens(root.path("usage").path("output_tokens").asInt(0));
                    break;
                case "gemini":
                    summary.setPromptTokens(root.path("usageMetadata").path("promptTokenCount").asInt(0));
                    summary.setCompletionTokens(root.path("usageMetadata").path("candidatesTokenCount").asInt(0));
                    summary.setTotalTokens(root.path("usageMetadata").path("totalTokenCount").asInt(0));
                    break;
                case "ollama":
                    summary.setPromptTokens(root.path("prompt_eval_count").asInt(0));
                    summary.setCompletionTokens(root.path("eval_count").asInt(0));
                    break;
                case "codexresponses":
                    summary.setPromptTokens(root.path("usage").path("input_tokens").asInt(0));
                    summary.setCompletionTokens(root.path("usage").path("output_tokens").asInt(0));
                    summary.setTotalTokens(root.path("usage").path("total_tokens").asInt(0));
                    break;
                default:
                    summary.setPromptTokens(root.path("usage").path("prompt_tokens").asInt(0));
                    summary.setCompletionTokens(root.path("usage").path("completion_tokens").asInt(0));
                    summary.setTotalTokens(root.path("usage").path("total_tokens").asInt(0));
                    // fixed 请求上游的是 codex接口
                    if (summary.getTotalTokens() == null || summary.getTotalTokens() == 0) {
                        summary.setPromptTokens(root.path("usage").path("input_tokens").asInt(0));
                        summary.setCompletionTokens(root.path("usage").path("output_tokens").asInt(0));
                        summary.setTotalTokens(root.path("usage").path("total_tokens").asInt(0));
                    }
                    break;
            }
            if (summary.getTotalTokens() == null || summary.getTotalTokens() == 0) {
                summary.setTotalTokens((summary.getPromptTokens() == null ? 0 : summary.getPromptTokens())
                        + (summary.getCompletionTokens() == null ? 0 : summary.getCompletionTokens()));
            }
        } catch (Exception e) {
            log.warn("extract usage failed", e);
        }
        return summary;
    }

    private AiChatResponse buildResponse(AiProviderConfig providerConfig, String provider, String model, String content, String raw, AiUsageSummary usage) {
        AiChatResponse response = new AiChatResponse();
        response.setProviderConfigId(providerConfig == null ? null : providerConfig.getId());
        response.setProviderCode(providerConfig == null ? null : providerConfig.getCode());
        response.setProvider(provider);
        response.setModel(model);
        response.setContent(content);
        response.setRaw(raw);
        response.setPromptTokens(usage.getPromptTokens());
        response.setCompletionTokens(usage.getCompletionTokens());
        response.setTotalTokens(usage.getTotalTokens());
        return response;
    }

    /**
     * 回写本次请求实际命中的提供方信息。
     *
     * @param request 请求对象
     * @param providerConfig 提供方配置
     * @param provider 提供方类型
     * @param model 模型
     */
    private void attachResolvedProviderMetadata(AiChatRequest request, AiProviderConfig providerConfig, String provider, String model) {
        if (request == null || providerConfig == null) {
            return;
        }
        request.setProviderCode(providerConfig.getCode());
        if (StringUtils.isBlank(request.getModel())) {
            request.setModel(model);
        }
        Map<String, Object> extra = request.getExtra() == null ? new HashMap<>() : new HashMap<>(request.getExtra());
        extra.put(EXTRA_ACTUAL_PROVIDER_CONFIG_ID, providerConfig.getId());
        extra.put(EXTRA_ACTUAL_PROVIDER_CODE, providerConfig.getCode());
        extra.put(EXTRA_ACTUAL_PROVIDER, provider);
        extra.put(EXTRA_ACTUAL_MODEL, model);
        request.setExtra(extra);
    }

    private String joinUrl(String baseUrl, String path) {
        if (StringUtils.isBlank(baseUrl)) {
            return path;
        }
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private String pickSystemPrompt(List<AiChatRequest.Message> messages) {
        for (AiChatRequest.Message message : messages) {
            String content = buildTextOnlyMessageContent(message);
            if ("system".equalsIgnoreCase(message.getRole()) && StringUtils.isNotBlank(content)) {
                return content;
            }
        }
        return null;
    }

    private Object toOpenAiContent(AiChatRequest.Message message) {
        if (message == null) {
            return null;
        }
        List<String> imageUrls = normalizeImageUrls(message.getImageUrls());
        String text = buildOpenAiTextContent(message);
        if (imageUrls.isEmpty()) {
            return StringUtils.isBlank(text) ? null : text;
        }

        List<Map<String, Object>> blocks = new ArrayList<>();
        if (StringUtils.isNotBlank(text)) {
            blocks.add(Map.of("type", "text", "text", text));
        }
        for (String imageUrl : imageUrls) {
            blocks.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl)));
        }
        return blocks.isEmpty() ? null : blocks;
    }

    /**
     * 组装 OpenAI 文本内容，确保文件 URL 在抽取失败时仍可作为上下文兜底传递。
     *
     * @param message 消息对象
     * @return 文本内容
     */
    private String buildOpenAiTextContent(AiChatRequest.Message message) {
        if (message == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(message.getContent())) {
            builder.append(message.getContent().trim());
        }
        List<String> fileUrls = normalizeFileUrls(message.getFileUrls());
        if (!fileUrls.isEmpty()) {
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append("File URLs:");
            for (String fileUrl : fileUrls) {
                builder.append("\n- ").append(fileUrl);
            }
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private String buildTextOnlyMessageContent(AiChatRequest.Message message) {
        if (message == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(message.getContent())) {
            builder.append(message.getContent().trim());
        }
        List<String> imageUrls = normalizeImageUrls(message.getImageUrls());
        if (!imageUrls.isEmpty()) {
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append("Image URLs:");
            for (String imageUrl : imageUrls) {
                builder.append("\n- ").append(imageUrl);
            }
        }
        List<String> fileUrls = normalizeFileUrls(message.getFileUrls());
        if (!fileUrls.isEmpty()) {
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append("File URLs:");
            for (String fileUrl : fileUrls) {
                builder.append("\n- ").append(fileUrl);
            }
        }
        return builder.toString();
    }

    private List<String> normalizeImageUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            if (StringUtils.isNotBlank(imageUrl)) {
                list.add(imageUrl.trim());
            }
        }
        return list;
    }

    private List<String> normalizeFileUrls(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String fileUrl : fileUrls) {
            if (StringUtils.isNotBlank(fileUrl)) {
                list.add(fileUrl.trim());
            }
        }
        return list;
    }

    private String pickString(Map<String, Object> config, String key, String fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val != null) {
                return String.valueOf(val);
            }
        }
        return fallback;
    }

    private Integer pickInteger(Map<String, Object> config, String key, Integer fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
            if (val != null) {
                try {
                    return Integer.parseInt(val.toString());
                } catch (Exception ignore) {
                    return fallback;
                }
            }
        }
        return fallback;
    }

    private Integer pickInteger(Map<String, Object> config, String key, Integer fallback, Integer defaultValue) {
        Integer value = pickInteger(config, key, fallback);
        return value == null ? defaultValue : value;
    }

    private Double pickDouble(Map<String, Object> config, String key, Double fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val instanceof Number) {
                return ((Number) val).doubleValue();
            }
            if (val != null) {
                try {
                    return Double.parseDouble(val.toString());
                } catch (Exception ignore) {
                    return fallback;
                }
            }
        }
        return fallback;
    }

    private Boolean pickBoolean(Map<String, Object> config, String key, Boolean fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val instanceof Boolean) {
                return (Boolean) val;
            }
            if (val != null) {
                return Boolean.parseBoolean(val.toString());
            }
        }
        return fallback;
    }

    private BigDecimal pickBigDecimal(Map<String, Object> config, String key, BigDecimal fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val instanceof Number) {
                return BigDecimal.valueOf(((Number) val).doubleValue());
            }
            if (val != null) {
                try {
                    return new BigDecimal(val.toString());
                } catch (Exception ignore) {
                    return fallback;
                }
            }
        }
        return fallback;
    }

    private Flux<String> toOpenAiStream(String content) {
        if (StringUtils.isBlank(content)) {
            return Flux.just("[DONE]");
        }
        return Flux.just(buildOpenAiChunk("assistant", content, null), "[DONE]");
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (!ObjectUtils.isEmpty(value)) {
            map.put(key, value);
        }
    }

    private String bufferToString(DataBuffer buffer) {
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        DataBufferUtils.release(buffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Flux<String> codexPayloadToOpenAiPayload(String payload, String model) {
        if (StringUtils.isBlank(payload)) {
            return Flux.empty();
        }
        if ("[DONE]".equals(payload)) {
            return Flux.just("[DONE]");
        }
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(payload);
            String type = root.path("type").asText("");
            if ("response.output_text.delta".equals(type)) {
                String delta = root.path("delta").asText("");
                return StringUtils.isBlank(delta) ? Flux.empty() : Flux.just(buildOpenAiChunk("assistant", delta, model));
            }
            if ("response.completed".equals(type) || "response.failed".equals(type)) {
                return Flux.just("[DONE]");
            }
            return Flux.empty();
        } catch (Exception e) {
            log.warn("parse codex stream payload failed: {}", payload, e);
            return Flux.empty();
        }
    }

    private void appendCodexTextDelta(String payload, StringBuilder builder) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(payload);
            if ("response.output_text.delta".equals(root.path("type").asText(""))) {
                String delta = root.path("delta").asText("");
                if (StringUtils.isNotBlank(delta)) {
                    builder.append(delta);
                }
            }
        } catch (Exception e) {
            log.warn("parse codex delta failed: {}", payload, e);
        }
    }

    private String buildOpenAiChunk(String role, String content, String model) {
        Map<String, Object> delta = new HashMap<>();
        if (StringUtils.isNotBlank(role)) {
            delta.put("role", role);
        }
        if (StringUtils.isNotBlank(content)) {
            delta.put("content", content);
        }
        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);
        choice.put("delta", delta);
        choice.put("finish_reason", null);
        Map<String, Object> payload = new HashMap<>();
        payload.put("object", "chat.completion.chunk");
        payload.put("choices", List.of(choice));
        if (StringUtils.isNotBlank(model)) {
            payload.put("model", model);
        }
        return JacksonUtils.toJson(payload);
    }

    private int estimateTokensByChars(int chars) {
        if (chars <= 0) {
            return 0;
        }
        return Math.max(1, (chars + 3) / 4);
    }

    private Flux<String> sseToPayloadFlux(Flux<String> rawTextFlux) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            rawTextFlux.subscribe(
                    part -> {
                        if (part == null) {
                            return;
                        }
                        buffer.append(part);
                        while (true) {
                            DelimiterHit hit = findEventDelimiter(buffer);
                            if (hit.index < 0) {
                                break;
                            }
                            String event = buffer.substring(0, hit.index);
                            buffer.delete(0, hit.index + hit.length);
                            emitDataLines(event, sink);
                        }
                    },
                    sink::error,
                    () -> {
                        if (buffer.length() > 0) {
                            emitDataLines(buffer.toString(), sink);
                        }
                        sink.complete();
                    }
            );
        });
    }

    private Flux<String> linesToFlux(Flux<String> rawTextFlux) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            rawTextFlux.subscribe(
                    part -> {
                        if (part == null) {
                            return;
                        }
                        buffer.append(part);
                        while (true) {
                            int idx = buffer.indexOf("\n");
                            if (idx < 0) {
                                break;
                            }
                            String line = buffer.substring(0, idx).trim();
                            buffer.delete(0, idx + 1);
                            if (!line.isEmpty()) {
                                sink.next(line);
                            }
                        }
                    },
                    sink::error,
                    () -> {
                        String line = buffer.toString().trim();
                        if (!line.isEmpty()) {
                            sink.next(line);
                        }
                        sink.complete();
                    }
            );
        });
    }

    private List<String> extractDataLines(String chunk) {
        if (!org.springframework.util.StringUtils.hasText(chunk)) {
            return List.of();
        }
        String[] lines = chunk.split("\\r?\\n");
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            if (!org.springframework.util.StringUtils.hasText(line)) {
                continue;
            }
            line = line.trim();
            if (line.startsWith(":")) {
                continue;
            }
            if (line.startsWith("data:")) {
                String data = line.substring(5).trim();
                if (org.springframework.util.StringUtils.hasText(data)) {
                    out.add(data);
                }
            }
        }
        return out;
    }

    private void emitDataLines(String event, reactor.core.publisher.FluxSink<String> sink) {
        List<String> dataLines = extractDataLines(event);
        for (String dataLine : dataLines) {
            sink.next(dataLine);
        }
    }

    private DelimiterHit findEventDelimiter(StringBuilder buffer) {
        int lfIdx = buffer.indexOf("\n\n");
        int crlfIdx = buffer.indexOf("\r\n\r\n");
        if (lfIdx < 0 && crlfIdx < 0) {
            return new DelimiterHit(-1, 0);
        }
        if (lfIdx < 0) {
            return new DelimiterHit(crlfIdx, 4);
        }
        if (crlfIdx < 0) {
            return new DelimiterHit(lfIdx, 2);
        }
        return crlfIdx < lfIdx ? new DelimiterHit(crlfIdx, 4) : new DelimiterHit(lfIdx, 2);
    }

    private static final class DelimiterHit {
        private final int index;
        private final int length;

        private DelimiterHit(int index, int length) {
            this.index = index;
            this.length = length;
        }
    }
}
