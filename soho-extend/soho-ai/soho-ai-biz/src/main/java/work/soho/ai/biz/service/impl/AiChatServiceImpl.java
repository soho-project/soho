package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiResolvedModelRoute;
import work.soho.ai.biz.dto.AiProxySelectionResult;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiModelRouteService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiProxyConfigService;
import work.soho.ai.biz.service.AiProxyRuntimeStateService;
import work.soho.ai.biz.service.AiProxyRelayService;
import work.soho.ai.biz.service.AiProviderRuntimeStateService;
import work.soho.ai.biz.service.AiUpstreamClientFactory;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.ai.biz.utils.AiProviderModelUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.net.Proxy;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
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
    private static final String EXTRA_REQUEST_MODEL = "requestModel";
    private static final String EXTRA_CLIENT_MODEL = "clientModel";
    private static final String EXTRA_ACTUAL_MODEL = "actualModel";
    private static final String EXTRA_FALLBACK_APPLIED = "fallbackApplied";
    private static final String EXTRA_FALLBACK_CHAIN = "fallbackChain";
    private static final long DEFAULT_FIRST_PAYLOAD_TIMEOUT_MS = 8000L;
    private static final String INTERNAL_PROVIDER_KEY = "__resolvedProvider";
    private static final String INTERNAL_PROXY_NODE_ID = "__resolvedProxyNodeId";
    private static final String INTERNAL_PROXY_SUMMARY_KEY = "__resolvedProxySummary";
    private static final int MAX_PROXY_RETRY_ATTEMPTS = 2;
    private static final int RELAY_PROBE_TIMEOUT_MS = 1200;
    private static final long RELAY_PROBE_CACHE_TTL_MS = 15_000L;
    private static final ConcurrentMap<String, RelayProbeSnapshot> RELAY_PROBE_CACHE = new ConcurrentHashMap<>();

    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiFileService aiFileService;
    private final AiProxyConfigService aiProxyConfigService;
    private final AiProxyRelayService aiProxyRelayService;
    private final AiProxyRuntimeStateService aiProxyRuntimeStateService;
    private final AiProviderRuntimeStateService aiProviderRuntimeStateService;
    private final AiUpstreamClientFactory aiUpstreamClientFactory;
    private final AiModelRouteService aiModelRouteService;

    /**
     * 执行非流式聊天，并在请求前完成模型兜底路由。
     *
     * @param request 聊天请求
     * @return 聊天结果
     */
    @Override
    public AiChatResponse chat(AiChatRequest request) {
        ProviderRoutingPlan routingPlan = resolveProviderRoutingPlan(request);
        applyRoutingPlan(request, routingPlan);
        int maxAttempts = Math.min(MAX_FAILOVER_ATTEMPTS, routingPlan.getCandidates().size());
        RuntimeException lastException = null;
        for (int i = 0; i < maxAttempts; i++) {
            AiProviderConfig candidate = routingPlan.getCandidates().get(i);
            try {
                return chat(candidate, request);
            } catch (RuntimeException ex) {
                lastException = ex;
                log.warn("chat upstream attempt failed, attempt={}/{}, providerCode={}, model={}, error={}",
                        i + 1, maxAttempts, candidate.getCode(), resolveClientModel(request, routingPlan.getActualModel()), ex.getMessage());
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        throw new IllegalArgumentException("provider config not found");
    }

    /**
     * 执行流式聊天，并在请求前完成模型兜底路由。
     *
     * @param request 聊天请求
     * @return 流式响应
     */
    @Override
    public Flux<String> streamChat(AiChatRequest request) {
        ProviderRoutingPlan routingPlan = resolveProviderRoutingPlan(request);
        applyRoutingPlan(request, routingPlan);
        int maxAttempts = Math.min(MAX_FAILOVER_ATTEMPTS, routingPlan.getCandidates().size());
        return streamChatWithFailover(routingPlan.getCandidates(), request, 0, maxAttempts);
    }

    @Override
    public AiChatResponse chat(AiProviderConfig providerConfig, AiChatRequest request) {
        if (!aiProviderRuntimeStateService.isRequestAllowed(providerConfig)) {
            throw new IllegalStateException("provider temporarily unavailable: " + providerConfig.getCode());
        }
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String provider = pickProvider(providerConfig, config);
        config.put(INTERNAL_PROVIDER_KEY, provider);
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
        config.put(INTERNAL_PROVIDER_KEY, provider);
        String apiKey = pickApiKey(providerConfig, config);
        String baseUrl = pickBaseUrl(providerConfig, config);
        String model = normalizeModel(provider, pickModel(request, providerConfig, config));
        String clientModel = resolveClientModel(request, model);
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
        String upstreamUrl = resolveStreamUpstreamUrl(provider, baseUrl, model, config);

        if (isCodexResponsesAdapter(config)) {
            Flux<String> stream = streamCodexResponses(baseUrl, apiKey, model, messages, request, config);
            String proxySummary = summarizeProxyForLog(config);
            return withUpstreamStreamTimingLog(providerConfig, config, provider, upstreamUrl, model, proxySummary,
                    applyFirstPayloadTimeout(stream, resolveFirstPayloadTimeoutMs(timeoutMs, config), providerConfig.getCode(), model))
                    .transform(payloadFlux -> rewriteStreamPayloadModel(payloadFlux, clientModel));
        }

        if (Boolean.FALSE.equals(streamSupported)) {
            AiChatResponse resp = chat(providerConfig, request);
            return toOpenAiStream(resp.getContent(), clientModel);
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
        String proxySummary = summarizeProxyForLog(config);
        return withUpstreamStreamTimingLog(providerConfig, config, provider, upstreamUrl, model, proxySummary,
                applyFirstPayloadTimeout(stream, resolveFirstPayloadTimeoutMs(timeoutMs, config), providerConfig.getCode(), model))
                .transform(payloadFlux -> rewriteStreamPayloadModel(payloadFlux, clientModel));
    }

    /**
     * 解析流式调用上游 URL，用于统一日志定位。
     *
     * @param provider 提供方
     * @param baseUrl 基础地址
     * @param model 模型
     * @param config 提供方配置
     * @return 上游 URL
     */
    private String resolveStreamUpstreamUrl(String provider, String baseUrl, String model, Map<String, Object> config) {
        if (isCodexResponsesAdapter(config)) {
            return joinUrl(baseUrl, pickString(config, "codexResponsesPath", "/backend-api/codex/responses"));
        }
        if ("anthropic".equalsIgnoreCase(provider)) {
            return joinUrl(baseUrl, pickString(config, "anthropicPath", "/v1/messages"));
        }
        if ("gemini".equalsIgnoreCase(provider)) {
            String apiVersion = pickString(config, "geminiApiVersion", "v1beta");
            String url = joinUrl(baseUrl, "/" + apiVersion + "/models/" + model + ":streamGenerateContent");
            String apiKey = pickString(config, "apiKey", "");
            if (StringUtils.isNotBlank(apiKey)) {
                return url + "?key=" + apiKey;
            }
            return url;
        }
        if ("ollama".equalsIgnoreCase(provider)) {
            return joinUrl(baseUrl, pickString(config, "ollamaPath", "/api/chat"));
        }
        return joinUrl(baseUrl, pickString(config, "openaiPath", "/v1/chat/completions"));
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
        List<AiProviderConfig> availableCandidates = new ArrayList<>();
        List<AiProviderConfig> enabledConfigs = aiProviderConfigService.listEnabledProviderConfigsByProvider(provider);
        for (AiProviderConfig enabledConfig : enabledConfigs) {
            AiResolvedModelRoute route = aiModelRouteService.resolveRouteForProvider(enabledConfig, model);
            if (StringUtils.isBlank(route.getActualModel())) {
                continue;
            }
            if (aiProviderRuntimeStateService.isRequestAllowed(enabledConfig)) {
                availableCandidates.add(enabledConfig);
            }
        }
        if (availableCandidates.isEmpty()) {
            throw new IllegalArgumentException("provider config not found for provider: " + provider + ", model: " + model);
        }
        AiProviderConfig selected = selectByWeight(availableCandidates);
        return selected == null ? availableCandidates.get(0) : selected;
    }

    /**
     * 解析请求对应的提供方路由计划。
     *
     * @param request 聊天请求
     * @return 路由计划
     */
    private ProviderRoutingPlan resolveProviderRoutingPlan(AiChatRequest request) {
        String providerCode = request == null ? null : request.getProviderCode();
        String requestedModel = request == null ? null : request.getModel();
        List<AiProviderConfig> candidates = resolveProviderConfigCandidates(providerCode, requestedModel);
        AiResolvedModelRoute route = resolveRequestedRoute(providerCode, requestedModel, candidates);
        String actualModel = route == null ? null : route.getActualModel();
        String clientModel = StringUtils.isNotBlank(requestedModel) ? requestedModel : actualModel;
        return new ProviderRoutingPlan(candidates, requestedModel, clientModel, actualModel, route);
    }

    /**
     * 解析请求模型在当前上下文内的实际路由。
     *
     * @param providerCode 提供方编码
     * @param requestedModel 请求模型
     * @param candidates 候选提供方
     * @return 路由结果
     */
    private AiResolvedModelRoute resolveRequestedRoute(String providerCode, String requestedModel, List<AiProviderConfig> candidates) {
        if (StringUtils.isBlank(requestedModel)) {
            return new AiResolvedModelRoute();
        }
        if (StringUtils.isNotBlank(providerCode)) {
            if (candidates == null || candidates.isEmpty()) {
                throw new IllegalArgumentException("provider config not found");
            }
            return aiModelRouteService.resolveRouteForProvider(candidates.get(0), requestedModel);
        }
        return aiModelRouteService.resolveRoute(requestedModel);
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
            validateRouteForProvider(candidate, model);
            return Collections.singletonList(candidate);
        }
        if (StringUtils.isBlank(model)) {
            throw new IllegalArgumentException("providerCode or model is required");
        }
        AiResolvedModelRoute route = aiModelRouteService.resolveRoute(model);
        String actualModel = route == null ? null : route.getActualModel();
        if (StringUtils.isBlank(actualModel)) {
            throw new IllegalArgumentException("provider config not found for model: " + model);
        }
        List<AiProviderConfig> orderedCandidates = loadOrderedEnabledCandidatesByModel(actualModel);
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
     * 校验指定提供方内是否存在可用的实际模型。
     *
     * @param providerConfig 提供方配置
     * @param requestedModel 请求模型
     */
    private void validateRouteForProvider(AiProviderConfig providerConfig, String requestedModel) {
        if (providerConfig == null || StringUtils.isBlank(requestedModel)) {
            return;
        }
        AiResolvedModelRoute route = aiModelRouteService.resolveRouteForProvider(providerConfig, requestedModel);
        if (StringUtils.isBlank(route.getActualModel())) {
            throw new IllegalArgumentException("model not supported: " + requestedModel);
        }
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
        List<AiProviderConfig> enabledConfigs = aiProviderConfigService.listEnabledProviderConfigs();
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
            if (supportsDirectModel(enabledConfig, model)) {
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
                            index + 1, maxAttempts, candidate.getCode(),
                            resolveClientModel(request, resolveActualModelFromRequest(request)), ex.getMessage());
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

    private Map<String, Object> buildRequestBodySummary(Map<String, Object> requestBody) {
        Map<String, Object> summary = new HashMap<>();
        if (requestBody == null) {
            summary.put("size", 0);
            return summary;
        }
        summary.put("size", requestBody.size());
        summary.put("model", requestBody.get("model"));
        summary.put("stream", requestBody.get("stream"));
        summary.put("hasInput", requestBody.get("input") != null);
        summary.put("messagesCount", sizeOfValue(requestBody.get("messages")));
        summary.put("toolsCount", sizeOfValue(requestBody.get("tools")));
        summary.put("includeCount", sizeOfValue(requestBody.get("include")));
        summary.put("contentsCount", sizeOfValue(requestBody.get("contents")));
        return summary;
    }

    private int sizeOfValue(Object value) {
        if (value instanceof List) {
            return ((List<?>) value).size();
        }
        return 0;
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
        String resolvedModel = resolveActualModelFromRequest(request);
        if (StringUtils.isNotBlank(resolvedModel)) {
            return resolvedModel;
        }
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
        if (providerConfig == null || StringUtils.isBlank(model)) {
            return;
        }
        if (!supportsDirectModel(providerConfig, model)) {
            throw new IllegalArgumentException("model not supported: " + model);
        }
    }

    /**
     * 判断提供方是否直接支持当前模型。
     *
     * @param providerConfig 提供方配置
     * @param model 模型名
     * @return 是否支持
     */
    private boolean supportsDirectModel(AiProviderConfig providerConfig, String model) {
        if (providerConfig == null || providerConfig.getId() == null || StringUtils.isBlank(model)) {
            return false;
        }
        List<String> supportedModels = new ArrayList<>();
        for (work.soho.ai.biz.domain.AiModelInfo item : aiProviderModelRelService.listEnabledModelsByProviderConfigId(providerConfig.getId())) {
            if (StringUtils.isNotBlank(item.getModelName())) {
                supportedModels.add(item.getModelName());
            }
        }
        if (supportedModels.isEmpty()) {
            supportedModels = AiProviderModelUtils.extractModels(providerConfig);
        }
        if (supportedModels.isEmpty()) {
            return true;
        }
        return supportedModels.contains(model);
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
//        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));
        putIfNotNull(body, "stream", request.getStream());

        Map<String, String> headers = new HashMap<>();
        if (StringUtils.isNotBlank(apiKey)) {
            headers.put("Authorization", "Bearer " + apiKey);
        }
        return withUpstreamRequestTimingLog(providerConfig, config, provider, model, url, () -> {
            String raw = postJson(url, headers, body, timeoutMs, config);
            String content = extractOpenAiContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, request, model, content, raw, usage);
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
        return withUpstreamRequestTimingLog(providerConfig, config, provider, model, url, () -> {
            String raw = postJson(url, headers, body, timeoutMs, config);
            String content = extractAnthropicContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, request, model, content, raw, usage);
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

        return withUpstreamRequestTimingLog(providerConfig, config, provider, model, requestUrl, () -> {
            String raw = postJson(requestUrl, Collections.emptyMap(), body, timeoutMs, config);
            String content = extractGeminiContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, request, model, content, raw, usage);
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

        return withUpstreamRequestTimingLog(providerConfig, config, provider, model, url, () -> {
            String raw = postJson(url, Collections.emptyMap(), body, timeoutMs, config);
            String content = extractOllamaContent(raw);
            AiUsageSummary usage = extractUsage(provider, raw);
            if (usage.getTotalTokens() == 0) {
                usage = estimateUsage(request, content);
            }
            return buildResponse(providerConfig, provider, request, model, content, raw, usage);
        });
    }

    private AiChatResponse callCodexResponses(AiProviderConfig providerConfig, String provider, String baseUrl, String apiKey, String model,
                                              List<AiChatRequest.Message> messages, AiChatRequest request,
                                              Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "codexResponsesPath", "/backend-api/codex/responses");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = resolveCodexRequestBody(model, messages, request, config, true);

        return withUpstreamRequestTimingLog(providerConfig, config, provider, model, url, () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.TEXT_EVENT_STREAM));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            List<String> payloads = aiUpstreamClientFactory.exchangeStream(
                            url,
                            HttpMethod.POST,
                            headers,
                            body,
                            pickInteger(config, "timeoutMs", DEFAULT_TIMEOUT_MS),
                            buildProxySettings(config)
                    )
                    .transform(this::sseToPayloadFlux)
                    .collectList()
                    .block();

            if (payloads == null) {
                payloads = Collections.emptyList();
            }

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
            return buildResponse(providerConfig, provider, request, model, content, raw, usage);
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
        // 上游不支持该参数 请勿放开
//        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));

        return executeStreamWithProxyRetry(config, "streamOpenAiCompatible", () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.TEXT_EVENT_STREAM));
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.isNotBlank(apiKey)) {
                headers.setBearerAuth(apiKey);
            }
            return aiUpstreamClientFactory.exchangeStream(
                            url,
                            HttpMethod.POST,
                            headers,
                            body,
                            pickInteger(config, "timeoutMs", DEFAULT_TIMEOUT_MS),
                            buildProxySettings(config)
                    )
                    .transform(this::sseToPayloadFlux)
                    .doOnError(ex -> log.error("openai stream upstream request failed, url={}, error={}",
                            url, extractUpstreamErrorMessage(ex), ex));
        });
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

        return executeStreamWithProxyRetry(config, "streamAnthropic", () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.TEXT_EVENT_STREAM));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-api-key", apiKey);
            headers.add("anthropic-version", version);
            return aiUpstreamClientFactory.exchangeStream(
                            url,
                            HttpMethod.POST,
                            headers,
                            body,
                            pickInteger(config, "timeoutMs", DEFAULT_TIMEOUT_MS),
                            buildProxySettings(config)
                    )
                    .transform(this::sseToPayloadFlux)
                    .doOnError(ex -> log.error("anthropic stream upstream request failed, url={}, error={}",
                            url, extractUpstreamErrorMessage(ex), ex));
        });
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

        return executeStreamWithProxyRetry(config, "streamGemini", () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.TEXT_EVENT_STREAM));
            headers.setContentType(MediaType.APPLICATION_JSON);
            return aiUpstreamClientFactory.exchangeStream(
                            requestUrl,
                            HttpMethod.POST,
                            headers,
                            body,
                            pickInteger(config, "timeoutMs", DEFAULT_TIMEOUT_MS),
                            buildProxySettings(config)
                    )
                    .transform(this::sseToPayloadFlux)
                    .doOnError(ex -> log.error("gemini stream upstream request failed, url={}, error={}",
                            requestUrl, extractUpstreamErrorMessage(ex), ex));
        });
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

        return executeStreamWithProxyRetry(config, "streamOllama", () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return aiUpstreamClientFactory.exchangeStream(
                            url,
                            HttpMethod.POST,
                            headers,
                            body,
                            pickInteger(config, "timeoutMs", DEFAULT_TIMEOUT_MS),
                            buildProxySettings(config)
                    )
                    .transform(this::linesToFlux)
                    .doOnError(ex -> log.error("ollama stream upstream request failed, url={}, error={}",
                            url, extractUpstreamErrorMessage(ex), ex));
        });
    }

    private Flux<String> streamCodexResponses(String baseUrl, String apiKey, String model,
                                              List<AiChatRequest.Message> messages, AiChatRequest request,
                                              Map<String, Object> config) {
        String path = pickString(config, "codexResponsesPath", "/backend-api/codex/responses");
        String url = joinUrl(baseUrl, path);
        Map<String, Object> body = resolveCodexRequestBody(model, messages, request, config, true);
        boolean nativeResponses = isNativeResponses(request);

        return executeStreamWithProxyRetry(config, "streamCodexResponses", () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.TEXT_EVENT_STREAM));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            Flux<String> payloadFlux = aiUpstreamClientFactory.exchangeStream(
                            url,
                            HttpMethod.POST,
                            headers,
                            body,
                            pickInteger(config, "timeoutMs", DEFAULT_TIMEOUT_MS),
                            buildProxySettings(config)
                    )
                    .transform(this::sseToPayloadFlux);

            if (nativeResponses) {
                return payloadFlux;
            }
            return payloadFlux.flatMap(payload -> codexPayloadToOpenAiPayload(payload, model));
        });
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
    private Flux<String> withUpstreamStreamTimingLog(AiProviderConfig providerConfig, Map<String, Object> config,
                                                     String provider, String url,
                                                     String model, String proxySummary, Flux<String> source) {
        long startAt = System.currentTimeMillis();
        AtomicLong firstTokenAt = new AtomicLong(-1L);
        return source
                .doOnSubscribe(s -> log.info("ai upstream stream request start, provider={}, model={}, url={}, proxy={}",
                        provider, model, url, proxySummary))
                .doOnNext(payload -> {
                    if (firstTokenAt.get() >= 0) {
                        return;
                    }
                    if (hasTextDelta(payload) && firstTokenAt.compareAndSet(-1L, System.currentTimeMillis())) {
                        long firstTokenMs = firstTokenAt.get() - startAt;
                        log.info("ai upstream stream first token, provider={}, model={}, url={}, proxy={}, first_token_ms={}",
                                provider, model, url, proxySummary, firstTokenMs);
                    }
                })
                .doOnComplete(() -> {
                    long totalMs = System.currentTimeMillis() - startAt;
                    long firstTokenMs = firstTokenAt.get() < 0 ? -1L : firstTokenAt.get() - startAt;
                    aiProviderRuntimeStateService.recordSuccess(providerConfig, totalMs, firstTokenMs > 0 ? firstTokenMs : null);
                    recordProxySuccess(config, totalMs);
                    log.info("ai upstream stream completed, provider={}, model={}, url={}, proxy={}, total_ms={}, first_token_ms={}",
                            provider, model, url, proxySummary, totalMs, firstTokenMs);
                })
                .doOnError(ex -> {
                    long totalMs = System.currentTimeMillis() - startAt;
                    long firstTokenMs = firstTokenAt.get() < 0 ? -1L : firstTokenAt.get() - startAt;
                    aiProviderRuntimeStateService.recordFailure(providerConfig, ex);
                    recordProxyFailure(config, ex);
                    log.warn("ai upstream stream failed, provider={}, model={}, url={}, proxy={}, total_ms={}, first_token_ms={}, error={}",
                            provider, model, url, proxySummary, totalMs, firstTokenMs, ex.getMessage());
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
    private AiChatResponse withUpstreamRequestTimingLog(AiProviderConfig providerConfig, Map<String, Object> config,
                                                        String provider, String model, String url,
                                                        Supplier<AiChatResponse> call) {
        long startAt = System.currentTimeMillis();
        log.info("ai upstream request start, provider={}, model={}, url={}", provider, model, url);
        try {
            AiChatResponse response = call.get();
            long totalMs = System.currentTimeMillis() - startAt;
            aiProviderRuntimeStateService.recordSuccess(providerConfig, totalMs, null);
            recordProxySuccess(config, totalMs);
            log.info("ai upstream request completed, provider={}, model={}, url={}, total_ms={}",
                    provider, model, url, totalMs);
            return response;
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            aiProviderRuntimeStateService.recordFailure(providerConfig, ex);
            recordProxyFailure(config, ex);
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
        return executeWithProxyRetry(config, "postJson", () -> {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            if (headers != null) {
                headers.forEach(httpHeaders::add);
            }
            try {
                ResponseEntity<String> response = aiUpstreamClientFactory.exchangeJson(
                        url,
                        HttpMethod.POST,
                        httpHeaders,
                        body,
                        timeoutMs,
                        buildProxySettings(config)
                );
                return response == null ? null : response.getBody();
            } catch (RuntimeException ex) {
                throw wrapUpstreamException(url, body, ex);
            }
        });
    }

    /**
     * 统一包装上游请求异常，确保日志中包含请求地址、请求体及上游错误信息。
     */
    private RuntimeException wrapUpstreamException(String url, Map<String, Object> requestBody, RuntimeException ex) {
        String message = extractUpstreamErrorMessage(ex);
        log.error("upstream api request failed, url={}, error={}, requestSummary={}",
                url, message, buildRequestBodySummary(requestBody), ex);
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

    /**
     * 按代理节点重试一次请求，避免单个失效节点导致本次请求直接失败。
     *
     * @param config 请求配置
     * @param action 动作名
     * @param supplier 实际请求
     * @param <T> 返回类型
     * @return 请求结果
     */
    private <T> T executeWithProxyRetry(Map<String, Object> config, String action, Supplier<T> supplier) {
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= MAX_PROXY_RETRY_ATTEMPTS; attempt++) {
            try {
                return supplier.get();
            } catch (RuntimeException ex) {
                lastException = ex;
                if (!shouldRetryWithAnotherProxy(config, ex) || attempt >= MAX_PROXY_RETRY_ATTEMPTS) {
                    throw ex;
                }
                Long failedProxyNodeId = resolveProxyNodeId(config);
                recordProxyFailure(config, ex);
                clearResolvedProxyNode(config);
                cacheResolvedProxySummary(config, null);
                log.warn("proxy retry scheduled, action={}, attempt={}/{}, failedProxyNodeId={}, reason={}",
                        action, attempt, MAX_PROXY_RETRY_ATTEMPTS, failedProxyNodeId, extractUpstreamErrorMessage(ex));
            }
        }
        throw lastException == null ? new IllegalStateException("proxy retry failed without exception") : lastException;
    }

    /**
     * 按代理节点重试一次流式请求。
     *
     * @param config 请求配置
     * @param action 动作名
     * @param supplier 实际请求
     * @return 请求流
     */
    private Flux<String> executeStreamWithProxyRetry(Map<String, Object> config, String action, Supplier<Flux<String>> supplier) {
        return Flux.defer(supplier::get)
                .onErrorResume(ex -> {
                    if (!(ex instanceof RuntimeException) || !shouldRetryWithAnotherProxy(config, ex)) {
                        return Flux.error(ex);
                    }
                    Long failedProxyNodeId = resolveProxyNodeId(config);
                    recordProxyFailure(config, ex);
                    clearResolvedProxyNode(config);
                    cacheResolvedProxySummary(config, null);
                    log.warn("proxy retry scheduled, action={}, attempt=1/{}, failedProxyNodeId={}, reason={}",
                            action, MAX_PROXY_RETRY_ATTEMPTS, failedProxyNodeId, extractUpstreamErrorMessage(ex));
                    return Flux.defer(supplier::get);
                });
    }

    /**
     * 判断是否应该切换到其它代理节点重试。
     *
     * @param config 请求配置
     * @param throwable 异常
     * @return 是否重试
     */
    private boolean shouldRetryWithAnotherProxy(Map<String, Object> config, Throwable throwable) {
        return resolveProxyNodeId(config) != null && isProxyRelevantFailure(throwable);
    }

    /**
     * 判断是否为代理相关失败。
     *
     * @param throwable 异常
     * @return 是否为代理相关失败
     */
    private boolean isProxyRelevantFailure(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (StringUtils.isNotBlank(message)) {
                String lower = message.toLowerCase(Locale.ROOT);
                if (lower.contains("proxy")
                        || lower.contains("socks")
                        || lower.contains("relay")
                        || lower.contains("connect")
                        || lower.contains("connection reset")
                        || lower.contains("connection refused")
                        || lower.contains("no route to host")
                        || lower.contains("broken pipe")
                        || lower.contains("unresolved")
                        || lower.contains("unknown host")
                        || lower.contains("dns")
                        || lower.contains("timeout")
                        || lower.contains("timed out")
                        || lower.contains("first token timeout")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private AiProxyLayerUtils.ProxySettings buildProxySettings(Map<String, Object> config) {
        String provider = pickString(config, INTERNAL_PROVIDER_KEY, pickString(config, "provider", ""));
        if (aiProxyRelayService == null) {
            throw new IllegalStateException("aiProxyRelayService is null, provider=" + provider
                    + ", config=" + summarizeProxyConfig(config));
        }
        AiProxySelectionResult selectionResult = aiProxyConfigService.resolveProxySelection(provider);
        cacheResolvedProxyNode(config, selectionResult);
        AiProxyLayerUtils.ProxySettings settings = selectionResult == null ? null : selectionResult.getProxySettings();
        if (settings != null) {
            try {
                settings = aiProxyRelayService.ensureRelay(settings, provider);
            } catch (Exception ex) {
                throw new IllegalStateException("proxy relay resolve failed from aiProxyConfig, provider=" + provider
                        + ", settings=" + summarizeProxySettings(settings)
                        + ", config=" + summarizeProxyConfig(config)
                        + ", error=" + ex.getMessage(), ex);
            }
            log.info("proxy node selected, provider={}, settings={}", provider, summarizeProxySettings(settings));
            cacheResolvedProxySummary(config, settings);
            ensureRelayEndpointAvailable(settings, config);
            return settings;
        }
        AiProxyLayerUtils.ProxySettings fallback = AiProxyLayerUtils.resolve(config);
        clearResolvedProxyNode(config);
        try {
            fallback = aiProxyRelayService.ensureRelay(fallback, provider);
        } catch (Exception ex) {
            throw new IllegalStateException("proxy relay resolve failed from fallback config, provider=" + provider
                    + ", settings=" + summarizeProxySettings(fallback)
                    + ", config=" + summarizeProxyConfig(config)
                    + ", error=" + ex.getMessage(), ex);
        }
        cacheResolvedProxySummary(config, fallback);
        ensureRelayEndpointAvailable(fallback, config);
        if (fallback != null) {
            log.info("proxy node selected from fallback, provider={}, settings={}", provider, summarizeProxySettings(fallback));
        }
        return fallback;
    }

    /**
     * 记录本次请求最终生效的代理摘要，避免日志误用原始配置中的静态代理字段。
     *
     * @param config 请求配置
     * @param settings 实际生效的代理设置，null 表示直连
     */
    private void cacheResolvedProxySummary(Map<String, Object> config, AiProxyLayerUtils.ProxySettings settings) {
        if (config == null) {
            return;
        }
        if (settings == null) {
            config.put(INTERNAL_PROXY_SUMMARY_KEY, "{mode=direct}");
            return;
        }
        config.put(INTERNAL_PROXY_SUMMARY_KEY, summarizeProxySettings(settings));
    }

    /**
     * 缓存本次请求命中的代理节点标识。
     *
     * @param config 请求配置
     * @param result 代理选择结果
     */
    private void cacheResolvedProxyNode(Map<String, Object> config, AiProxySelectionResult result) {
        if (config == null) {
            return;
        }
        clearResolvedProxyNode(config);
        if (result == null || result.getProxyConfig() == null || result.getProxyConfig().getId() == null) {
            return;
        }
        config.put(INTERNAL_PROXY_NODE_ID, String.valueOf(result.getProxyConfig().getId()));
        config.put("proxyNodeId", String.valueOf(result.getProxyConfig().getId()));
        if (StringUtils.isNotBlank(result.getProxyConfig().getName())) {
            config.put("proxyNodeName", result.getProxyConfig().getName());
        }
        if (StringUtils.isNotBlank(result.getProxyConfig().getProvider())) {
            config.put("proxyNodeProvider", result.getProxyConfig().getProvider());
        }
    }

    /**
     * 清理本次请求缓存的代理节点标识。
     *
     * @param config 请求配置
     */
    private void clearResolvedProxyNode(Map<String, Object> config) {
        if (config == null) {
            return;
        }
        config.remove(INTERNAL_PROXY_NODE_ID);
        config.remove("proxyNodeId");
        config.remove("proxyNodeName");
        config.remove("proxyNodeProvider");
    }

    /**
     * 记录代理节点成功。
     *
     * @param config 请求配置
     * @param totalMs 总耗时
     */
    private void recordProxySuccess(Map<String, Object> config, long totalMs) {
        Long proxyNodeId = resolveProxyNodeId(config);
        if (proxyNodeId != null) {
            aiProxyRuntimeStateService.recordSuccess(proxyNodeId, totalMs);
        }
    }

    /**
     * 记录代理节点失败。
     *
     * @param config 请求配置
     * @param throwable 异常
     */
    private void recordProxyFailure(Map<String, Object> config, Throwable throwable) {
        Long proxyNodeId = resolveProxyNodeId(config);
        if (proxyNodeId != null) {
            aiProxyRuntimeStateService.recordFailure(proxyNodeId, throwable);
        }
    }

    /**
     * 读取本次请求命中的代理节点ID。
     *
     * @param config 请求配置
     * @return 代理节点ID
     */
    private Long resolveProxyNodeId(Map<String, Object> config) {
        if (config == null) {
            return null;
        }
        String value = pickString(config, INTERNAL_PROXY_NODE_ID, pickString(config, "proxyNodeId", ""));
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            log.warn("invalid proxy node id, value={}", value);
            return null;
        }
    }

    /**
     * 获取用于日志展示的代理摘要，优先使用本次请求实际生效的代理设置。
     *
     * @param config 请求配置
     * @return 代理摘要
     */
    private String summarizeProxyForLog(Map<String, Object> config) {
        String resolvedSummary = pickString(config, INTERNAL_PROXY_SUMMARY_KEY, "");
        if (StringUtils.isNotBlank(resolvedSummary)) {
            return resolvedSummary;
        }
        return summarizeProxyConfig(config);
    }

    /**
     * 对代理出口进行短路探活，避免连接不可达时进入长时间 TLS/代理超时。
     *
     * @param settings 代理设置
     * @param config 提供方配置
     */
    private void ensureRelayEndpointAvailable(AiProxyLayerUtils.ProxySettings settings, Map<String, Object> config) {
        if (settings == null) {
            return;
        }
        String cacheKey = buildRelayProbeKey(settings);
        long now = System.currentTimeMillis();
        RelayProbeSnapshot cached = RELAY_PROBE_CACHE.get(cacheKey);
        if (cached != null && cached.getExpireAtMs() > now) {
            if (!cached.isAvailable()) {
                throw new IllegalStateException(cached.getErrorMessage());
            }
            return;
        }
        String provider = pickString(config, INTERNAL_PROVIDER_KEY, pickString(config, "provider", ""));
        RelayProbeSnapshot latest = probeRelayEndpoint(settings, provider, now);
        RELAY_PROBE_CACHE.put(cacheKey, latest);
        if (!latest.isAvailable()) {
            throw new IllegalStateException(latest.getErrorMessage());
        }
    }

    /**
     * 执行一次代理出口 TCP 探活。
     *
     * @param settings 代理设置
     * @param provider 供应商编码
     * @param now 当前时间戳
     * @return 探活快照
     */
    private RelayProbeSnapshot probeRelayEndpoint(AiProxyLayerUtils.ProxySettings settings, String provider, long now) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(settings.getHost(), settings.getPort()), RELAY_PROBE_TIMEOUT_MS);
            return RelayProbeSnapshot.available(now + RELAY_PROBE_CACHE_TTL_MS);
        } catch (Exception ex) {
            String protocolHint = settings.isLocalRelayRequired()
                    ? "当前代理协议为 ss/vmess/vless/trojan/hysteria2，需要先启动本地中继并提供 socks5/http 出口。"
                    : "请检查代理主机、端口及认证是否正确。";
            String message = String.format(
                    "proxy endpoint unavailable: provider=%s, relay=%s:%d, reason=%s, hint=%s",
                    provider, settings.getHost(), settings.getPort(), ex.getMessage(), protocolHint);
            log.warn(message);
            return RelayProbeSnapshot.unavailable(now + RELAY_PROBE_CACHE_TTL_MS, message);
        }
    }

    /**
     * 生成代理探活缓存键。
     *
     * @param settings 代理设置
     * @return 缓存键
     */
    private String buildRelayProbeKey(AiProxyLayerUtils.ProxySettings settings) {
        return settings.getJavaProxyType() + "|" + settings.getHost() + ":" + settings.getPort();
    }

    /**
     * 汇总代理配置，用于异常日志快速定位问题。
     *
     * @param config 请求配置
     * @return 摘要字符串
     */
    private String summarizeProxyConfig(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        appendDebugField(sb, "provider", pickString(config, "provider", ""));
        appendDebugField(sb, "proxyType", pickString(config, "proxyType", ""));
        appendDebugField(sb, "proxyNodeId", pickString(config, "proxyNodeId", ""));
        appendDebugField(sb, "proxyNodeName", pickString(config, "proxyNodeName", ""));
        appendDebugField(sb, "proxyNodeProvider", pickString(config, "proxyNodeProvider", ""));
        appendDebugField(sb, "proxyHost", pickString(config, "proxyHost", ""));
        appendDebugField(sb, "proxyPort", String.valueOf(pickInteger(config, "proxyPort", null)));
        appendDebugField(sb, "proxyUrl", pickString(config, "proxyUrl", ""));
        appendDebugField(sb, "proxyUsername", pickString(config, "proxyUsername", ""));
        appendDebugField(sb, "timeoutMs", String.valueOf(pickInteger(config, "timeoutMs", null)));
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * 汇总代理设置，用于异常日志快速定位问题。
     *
     * @param settings 代理设置
     * @return 摘要字符串
     */
    private String summarizeProxySettings(AiProxyLayerUtils.ProxySettings settings) {
        if (settings == null) {
            return "null";
        }
        return "{protocol=" + settings.getProtocol()
                + ",localRelayRequired=" + settings.isLocalRelayRequired()
                + ",httpProxy=" + settings.isHttpProxy()
                + ",host=" + settings.getHost()
                + ",port=" + settings.getPort()
                + ",username=" + settings.getUsername()
                + ",proxyUrl=" + settings.getProxyUrl()
                + "}";
    }

    /**
     * 追加调试字段到摘要字符串。
     *
     * @param sb 字符串构建器
     * @param key 字段名
     * @param value 字段值
     */
    private void appendDebugField(StringBuilder sb, String key, String value) {
        if (StringUtils.isBlank(value) || "null".equalsIgnoreCase(value)) {
            return;
        }
        sb.append(key).append('=').append(value).append(',');
    }

    /**
     * 代理探活缓存快照。
     */
    private static final class RelayProbeSnapshot {
        private final boolean available;
        private final long expireAtMs;
        private final String errorMessage;

        /**
         * 创建探活快照。
         *
         * @param available 是否可用
         * @param expireAtMs 失效时间戳
         * @param errorMessage 错误信息
         */
        private RelayProbeSnapshot(boolean available, long expireAtMs, String errorMessage) {
            this.available = available;
            this.expireAtMs = expireAtMs;
            this.errorMessage = errorMessage;
        }

        /**
         * 构建可用快照。
         *
         * @param expireAtMs 失效时间戳
         * @return 快照
         */
        private static RelayProbeSnapshot available(long expireAtMs) {
            return new RelayProbeSnapshot(true, expireAtMs, null);
        }

        /**
         * 构建不可用快照。
         *
         * @param expireAtMs 失效时间戳
         * @param errorMessage 错误信息
         * @return 快照
         */
        private static RelayProbeSnapshot unavailable(long expireAtMs, String errorMessage) {
            return new RelayProbeSnapshot(false, expireAtMs, errorMessage);
        }

        /**
         * 判断是否可用。
         *
         * @return true 表示可用
         */
        private boolean isAvailable() {
            return available;
        }

        /**
         * 获取过期时间戳。
         *
         * @return 时间戳
         */
        private long getExpireAtMs() {
            return expireAtMs;
        }

        /**
         * 获取错误消息。
         *
         * @return 错误消息
         */
        private String getErrorMessage() {
            return errorMessage;
        }
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
        //该参数上游报错
//        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        // 上游好像不支持 max_output_tokens， 传递这个参数上游会报错
//        putIfNotNull(body, "max_output_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));

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
                    int anthropicInputTokens = root.path("usage").path("input_tokens").asInt(0);
                    int cacheCreationInputTokens = root.path("usage").path("cache_creation_input_tokens").asInt(0);
                    int cacheReadInputTokens = root.path("usage").path("cache_read_input_tokens").asInt(0);
                    summary.setCacheCreationInputTokens(cacheCreationInputTokens);
                    summary.setCacheReadInputTokens(cacheReadInputTokens);
                    summary.setCachedInputTokens(cacheCreationInputTokens + cacheReadInputTokens);
                    summary.setPromptTokens(anthropicInputTokens + summary.getCachedInputTokens());
                    summary.setCompletionTokens(root.path("usage").path("output_tokens").asInt(0));
                    break;
                case "gemini":
                    summary.setPromptTokens(root.path("usageMetadata").path("promptTokenCount").asInt(0));
                    summary.setCompletionTokens(root.path("usageMetadata").path("candidatesTokenCount").asInt(0));
                    summary.setTotalTokens(root.path("usageMetadata").path("totalTokenCount").asInt(0));
                    summary.setCachedInputTokens(root.path("usageMetadata").path("cachedContentTokenCount").asInt(0));
                    break;
                case "ollama":
                    summary.setPromptTokens(root.path("prompt_eval_count").asInt(0));
                    summary.setCompletionTokens(root.path("eval_count").asInt(0));
                    break;
                case "codexresponses":
                    summary.setPromptTokens(root.path("usage").path("input_tokens").asInt(0));
                    summary.setCompletionTokens(root.path("usage").path("output_tokens").asInt(0));
                    summary.setTotalTokens(root.path("usage").path("total_tokens").asInt(0));
                    summary.setCachedInputTokens(root.path("usage").path("input_tokens_details").path("cached_tokens").asInt(0));
                    break;
                default:
                    summary.setPromptTokens(root.path("usage").path("prompt_tokens").asInt(0));
                    summary.setCompletionTokens(root.path("usage").path("completion_tokens").asInt(0));
                    summary.setTotalTokens(root.path("usage").path("total_tokens").asInt(0));
                    summary.setCachedInputTokens(root.path("usage").path("prompt_tokens_details").path("cached_tokens").asInt(0));
                    if (summary.getCachedInputTokens() == null || summary.getCachedInputTokens() == 0) {
                        summary.setCachedInputTokens(root.path("usage").path("input_tokens_details").path("cached_tokens").asInt(0));
                    }
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

    private AiChatResponse buildResponse(AiProviderConfig providerConfig, String provider, AiChatRequest request,
                                         String actualModel, String content, String raw, AiUsageSummary usage) {
        AiChatResponse response = new AiChatResponse();
        response.setProviderConfigId(providerConfig == null ? null : providerConfig.getId());
        response.setProviderCode(providerConfig == null ? null : providerConfig.getCode());
        response.setProvider(provider);
        response.setRequestModel(resolveRequestedModel(request));
        response.setActualModel(actualModel);
        response.setModel(resolveClientModel(request, actualModel));
        response.setContent(content);
        response.setRaw(raw);
        response.setPromptTokens(usage.getPromptTokens());
        response.setCompletionTokens(usage.getCompletionTokens());
        response.setTotalTokens(usage.getTotalTokens());
        response.setCachedInputTokens(usage.getCachedInputTokens());
        response.setCacheCreationInputTokens(usage.getCacheCreationInputTokens());
        response.setCacheReadInputTokens(usage.getCacheReadInputTokens());
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
        Map<String, Object> extra = request.getExtra() == null ? new HashMap<>() : new HashMap<>(request.getExtra());
        String requestedModel = resolveRequestedModel(request);
        extra.put(EXTRA_ACTUAL_PROVIDER_CONFIG_ID, providerConfig.getId());
        extra.put(EXTRA_ACTUAL_PROVIDER_CODE, providerConfig.getCode());
        extra.put(EXTRA_ACTUAL_PROVIDER, provider);
        extra.put(EXTRA_REQUEST_MODEL, requestedModel);
        extra.put(EXTRA_CLIENT_MODEL, StringUtils.isNotBlank(requestedModel) ? requestedModel : model);
        extra.put(EXTRA_ACTUAL_MODEL, model);
        request.setExtra(extra);
    }

    /**
     * 将路由计划回写到请求上下文。
     *
     * @param request 请求对象
     * @param routingPlan 路由计划
     */
    private void applyRoutingPlan(AiChatRequest request, ProviderRoutingPlan routingPlan) {
        if (request == null || routingPlan == null) {
            return;
        }
        Map<String, Object> extra = request.getExtra() == null ? new HashMap<>() : new HashMap<>(request.getExtra());
        if (StringUtils.isNotBlank(routingPlan.getRequestedModel())) {
            extra.put(EXTRA_REQUEST_MODEL, routingPlan.getRequestedModel());
        }
        if (StringUtils.isNotBlank(routingPlan.getClientModel())) {
            extra.put(EXTRA_CLIENT_MODEL, routingPlan.getClientModel());
        }
        if (StringUtils.isNotBlank(routingPlan.getActualModel())) {
            extra.put(EXTRA_ACTUAL_MODEL, routingPlan.getActualModel());
        }
        AiResolvedModelRoute route = routingPlan.getRoute();
        if (route != null) {
            extra.put(EXTRA_FALLBACK_APPLIED, route.isFallbackApplied());
            extra.put(EXTRA_FALLBACK_CHAIN, route.getFallbackChain());
        }
        request.setExtra(extra);
    }

    /**
     * 解析请求上下文中的实际模型。
     *
     * @param request 请求对象
     * @return 实际模型
     */
    private String resolveActualModelFromRequest(AiChatRequest request) {
        if (request == null || request.getExtra() == null) {
            return null;
        }
        Object value = request.getExtra().get(EXTRA_ACTUAL_MODEL);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 解析客户端请求模型。
     *
     * @param request 请求对象
     * @return 请求模型
     */
    private String resolveRequestedModel(AiChatRequest request) {
        if (request == null) {
            return null;
        }
        if (request.getExtra() != null) {
            Object requestModel = request.getExtra().get(EXTRA_REQUEST_MODEL);
            if (requestModel != null && StringUtils.isNotBlank(String.valueOf(requestModel))) {
                return String.valueOf(requestModel);
            }
        }
        return request.getModel();
    }

    /**
     * 解析客户端应看到的模型名。
     *
     * @param request 请求对象
     * @param actualModel 实际模型
     * @return 客户端模型
     */
    private String resolveClientModel(AiChatRequest request, String actualModel) {
        if (request != null && request.getExtra() != null) {
            Object clientModel = request.getExtra().get(EXTRA_CLIENT_MODEL);
            if (clientModel != null && StringUtils.isNotBlank(String.valueOf(clientModel))) {
                return String.valueOf(clientModel);
            }
        }
        String requestedModel = resolveRequestedModel(request);
        return StringUtils.isNotBlank(requestedModel) ? requestedModel : actualModel;
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

    private Flux<String> toOpenAiStream(String content, String model) {
        if (StringUtils.isBlank(content)) {
            return Flux.just("[DONE]");
        }
        return Flux.just(buildOpenAiChunk("assistant", content, model), "[DONE]");
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (!ObjectUtils.isEmpty(value)) {
            map.put(key, value);
        }
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

    /**
     * 将流式 payload 中的模型字段改写为客户端请求模型。
     *
     * @param source 原始流
     * @param clientModel 客户端模型
     * @return 改写后的流
     */
    private Flux<String> rewriteStreamPayloadModel(Flux<String> source, String clientModel) {
        if (StringUtils.isBlank(clientModel)) {
            return source;
        }
        return source.map(payload -> rewritePayloadModel(payload, clientModel));
    }

    /**
     * 改写单条 payload 的模型字段。
     *
     * @param payload 单条 payload
     * @param clientModel 客户端模型
     * @return 改写后的 payload
     */
    private String rewritePayloadModel(String payload, String clientModel) {
        if (StringUtils.isBlank(payload) || "[DONE]".equals(payload)) {
            return payload;
        }
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(payload);
            if (root == null || root.isMissingNode() || !root.isObject()) {
                return payload;
            }
            ((com.fasterxml.jackson.databind.node.ObjectNode) root).put("model", clientModel);
            return JacksonUtils.toJson(root);
        } catch (Exception ex) {
            log.warn("rewrite stream payload model failed, payload={}", payload, ex);
            return payload;
        }
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

    /**
     * 请求路由计划。
     */
    private static final class ProviderRoutingPlan {
        private final List<AiProviderConfig> candidates;
        private final String requestedModel;
        private final String clientModel;
        private final String actualModel;
        private final AiResolvedModelRoute route;

        private ProviderRoutingPlan(List<AiProviderConfig> candidates, String requestedModel, String clientModel,
                                    String actualModel, AiResolvedModelRoute route) {
            this.candidates = candidates == null ? Collections.emptyList() : candidates;
            this.requestedModel = requestedModel;
            this.clientModel = clientModel;
            this.actualModel = actualModel;
            this.route = route;
        }

        private List<AiProviderConfig> getCandidates() {
            return candidates;
        }

        private String getRequestedModel() {
            return requestedModel;
        }

        private String getClientModel() {
            return clientModel;
        }

        private String getActualModel() {
            return actualModel;
        }

        private AiResolvedModelRoute getRoute() {
            return route;
        }
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
