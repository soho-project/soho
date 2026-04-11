package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUserMemberCardView;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.enums.AiApiCallLogEnums;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiMemberRequestLimitService;
import work.soho.ai.biz.service.AiOpenApiService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.ai.biz.service.AiUserMemberCardService;
import work.soho.ai.biz.utils.AiProviderModelUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.service.WalletInfoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Service
@RequiredArgsConstructor
public class AiOpenApiServiceImpl implements AiOpenApiService {
    private static final String CLIENT_ERROR_MESSAGE = "临时错误，如果长期错误请联系管理员";
    private static final String GEMINI_MOCK_RESPONSE_FILE = "/home/fang/testgemini/test.txt";
    private final AiUserApiKeyService aiUserApiKeyService;
    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiChatService aiChatService;
    private final AiApiCallLogService aiApiCallLogService;
    private final WalletInfoService walletInfoService;
    private final WalletInfoApiService walletInfoApiService;
    private final AiMemberRequestLimitService aiMemberRequestLimitService;
    private final AiUserMemberCardService aiUserMemberCardService;
    private static final long PACKAGE_QUOTA_UNIT = 500000L;

    /**
     * 查询 OpenAI/Codex 兼容余额与用量信息。
     */
    @Override
    public Map<String, Object> balance(String authorization) {
        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        aiUserApiKeyService.touchLastUsedTime(apiKey.getId());

        List<Integer> walletTypeIds = resolveBalanceWalletTypeIds();
        BigDecimal balance = sumWalletBalance(apiKey.getUserId(), walletTypeIds);
        AiMemberRequestLimitService.UsageSnapshot usageSnapshot = resolveRequestUsage(apiKey.getUserId());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("object", "balance");
        response.put("is_active", true);
        response.put("balance", balance);
        response.put("unit", "USD");
        response.put("wallet_type_ids", walletTypeIds);
        response.put("request_usage", buildRequestUsage(usageSnapshot));
        response.put("token_usage", buildTokenUsage(apiKey));
        return response;
    }

    /**
     * 查询当前用户套餐用量信息。
     */
    @Override
    public Map<String, Object> selfPackage(Long userId, String newApiUserHeader) {
        if (userId == null) {
            return buildSelfPackageFailedResponse("未登录");
        }
        if (StringUtils.isNotBlank(newApiUserHeader)
                && !String.valueOf(userId).equals(newApiUserHeader.trim())) {
            return buildSelfPackageFailedResponse("用户信息不匹配");
        }

        AiUserMemberCardView currentCard = aiUserMemberCardService.currentUserCard(userId).orElse(null);
        PackageQuotaSnapshot quotaSnapshot = resolvePackageQuotaSnapshot(currentCard);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("group", currentCard == null || StringUtils.isBlank(currentCard.getName()) ? "默认套餐" : currentCard.getName());
        data.put("quota", quotaSnapshot.remainingQuota);
        data.put("used_quota", quotaSnapshot.usedQuota);
        data.put("total_quota", quotaSnapshot.totalQuota);
        data.put("quota_unit", "USD");
        data.put("limit_mode", currentCard == null ? null : currentCard.getLimitMode());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    /**
     * 查询 OpenAI 兼容模型列表。
     */
    @Override
    public Map<String, Object> models(String authorization) {
        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        aiUserApiKeyService.touchLastUsedTime(apiKey.getId());

        List<AiProviderConfig> providerConfigs = aiProviderConfigService.list(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getStatus, 1)
                .orderByAsc(AiProviderConfig::getId));

        Map<String, Map<String, Object>> modelMap = new LinkedHashMap<>();
        for (AiProviderConfig providerConfig : providerConfigs) {
            List<AiModelInfo> relModels = aiProviderModelRelService.listEnabledModelsByProviderConfigId(providerConfig.getId());
            if (!relModels.isEmpty()) {
                relModels.sort(Comparator.comparing(AiModelInfo::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(AiModelInfo::getId, Comparator.nullsLast(Long::compareTo)));
                for (AiModelInfo relModel : relModels) {
                    addModelRow(modelMap, relModel.getModelName(), providerConfig, relModel.getCreatedTime());
                }
                continue;
            }

            for (String modelName : AiProviderModelUtils.extractModels(providerConfig)) {
                addModelRow(modelMap, modelName, providerConfig, providerConfig.getCreatedTime());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("object", "list");
        response.put("data", new ArrayList<>(modelMap.values()));
        return response;
    }

    /**
     * 查询 Gemini 原生模型列表。
     * 仅允许返回 provider 字段为 gemini 的配置，并复用现有 OpenAI 鉴权与路由选择规则。
     */
    @Override
    public Map<String, Object> geminiModels(String authorization) {
        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        aiUserApiKeyService.touchLastUsedTime(apiKey.getId());

        List<AiProviderConfig> providerConfigs = aiProviderConfigService.list(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getStatus, 1)
                .eq(AiProviderConfig::getProvider, "gemini")
                .orderByAsc(AiProviderConfig::getId));

        Set<String> modelNames = new LinkedHashSet<>();
        for (AiProviderConfig providerConfig : providerConfigs) {
            List<AiModelInfo> relModels = aiProviderModelRelService.listEnabledModelsByProviderConfigId(providerConfig.getId());
            if (!relModels.isEmpty()) {
                relModels.sort(Comparator.comparing(AiModelInfo::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(AiModelInfo::getId, Comparator.nullsLast(Long::compareTo)));
                for (AiModelInfo relModel : relModels) {
                    if (StringUtils.isNotBlank(relModel.getModelName())) {
                        modelNames.add(relModel.getModelName());
                    }
                }
                continue;
            }
            modelNames.addAll(AiProviderModelUtils.extractModels(providerConfig));
        }

        List<Map<String, Object>> models = new ArrayList<>();
        for (String modelName : modelNames) {
            if (StringUtils.isBlank(modelName)) {
                continue;
            }
            try {
                AiProviderConfig selectedConfig = aiChatService.resolveProviderConfig(null, modelName);
                if (selectedConfig == null || !"gemini".equalsIgnoreCase(selectedConfig.getProvider())) {
                    continue;
                }
                models.add(buildGeminiModelItem(modelName));
            } catch (RuntimeException ex) {
                log.debug("skip unavailable gemini model, model={}, msg={}", modelName, ex.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("models", models);
        return result;
    }

    /**
     * 转发 Gemini 原生 generateContent 请求，并复用现有鉴权、路由与 token 计费能力。
     */
    @Override
    public Map<String, Object> geminiGenerateContent(String key, String model, Map<String, Object> request) {
        Assert.hasText(key, "key不能为空");
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(key);
        AiProviderConfig providerConfig = requireProviderConfig(model);
        assertGeminiProvider(providerConfig, model);
        BillingPlan billingPlan = buildGeminiGenerateBillingPlan(apiKey, providerConfig, model, request);
        preCheckBalance(billingPlan);

        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String apiVersion = pickString(config, "geminiApiVersion", "v1beta");
        String path = "/" + apiVersion + "/models/" + model + ":generateContent";
        String url = appendQueryParam(joinUrl(pickBaseUrl(providerConfig, config), path), "key",
                pickApiKey(providerConfig, config));
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        String endpoint = "/ai/guest/openai/v1beta/models/" + model + ":generateContent";
        long startAt = System.currentTimeMillis();

        try {
            // 临时回放模式：禁用真实上游调用，改为读取本地文件中的 Gemini 原始响应体。
             RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_JSON);
             ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                     new HttpEntity<>(request == null ? new HashMap<>() : request, headers), String.class);
            Map<String, Object> result = parseJsonMap(response.getBody());

//            String rawBody = readGeminiMockResponseBody();
//            Map<String, Object> result = parseJsonMap(rawBody);

            AiUsageSummary usage = extractGeminiUsage(result, request);
            BigDecimal amount = calculateAmount(billingPlan, usage, model);
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, model);
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, usage, amount, walletLogId, endpoint, totalMs, null);
            return result;
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    @Override
    public Map<String, Object> chatCompletions(String authorization, OpenAiChatCompletionRequest request) {
        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig selectedProviderConfig = requireProviderConfig(request.getModel());
        AiChatRequest aiChatRequest = convertRequest(null, request);
        BillingPlan billingPlan = buildBillingPlan(apiKey, selectedProviderConfig, aiChatRequest, request);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();
        try {
            AiChatResponse response = aiChatService.chat(aiChatRequest);
            AiProviderConfig providerConfig = resolveActualProviderConfig(response, aiChatRequest, selectedProviderConfig);
            refreshBillingPlanProviderConfig(billingPlan, providerConfig, response.getModel());
            AiUsageSummary usage = usageFromResponse(aiChatRequest, response);
            BigDecimal amount = calculateAmount(billingPlan, usage, response.getModel());
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, response.getModel());
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, response.getModel(), usage, amount, walletLogId,
                    "/ai/guest/openai/v1/chat/completions", totalMs, null);
            return buildOpenAiResponse(requestId, response.getModel(), response.getContent(), usage);
        } catch (RuntimeException ex) {
            AiProviderConfig providerConfig = resolveActualProviderConfig(null, aiChatRequest, selectedProviderConfig);
            String model = resolveActualModel(aiChatRequest, providerConfig);
            refreshBillingPlanProviderConfig(billingPlan, providerConfig, model);
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, ex.getMessage(),
                    "/ai/guest/openai/v1/chat/completions", totalMs, null);
            throw ex;
        }
    }

    @Override
    public Flux<String> streamChatCompletions(String authorization, OpenAiChatCompletionRequest request) {
        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig selectedProviderConfig = requireProviderConfig(request.getModel());
        AiChatRequest aiChatRequest = convertRequest(null, request);
        BillingPlan billingPlan = buildBillingPlan(apiKey, selectedProviderConfig, aiChatRequest, request);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        StringBuilder contentBuilder = new StringBuilder();
        String targetModel = StringUtils.isBlank(request.getModel()) ? selectedProviderConfig.getDefaultModel() : request.getModel();
        long startAt = System.currentTimeMillis();
        AtomicLong firstTokenAt = new AtomicLong(-1L);

        log.info("streamChatCompletions: {}",  aiChatRequest);
        return aiChatService.streamChat(aiChatRequest)
                .doOnNext(payload -> {
                    appendContent(payload, contentBuilder);
                    recordFirstTokenAt(firstTokenAt, startAt, extractDeltaFromChatStreamPayload(payload));
                })
                .doOnComplete(() -> {
                    AiProviderConfig providerConfig = resolveActualProviderConfig(null, aiChatRequest, selectedProviderConfig);
                    String model = resolveActualModel(aiChatRequest, providerConfig);
                    refreshBillingPlanProviderConfig(billingPlan, providerConfig, model);
                    AiUsageSummary usage = aiChatService.estimateUsage(aiChatRequest, contentBuilder.toString());
                    BigDecimal amount = calculateAmount(billingPlan, usage, model);
                    Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, model);
                    aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
                    aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
                    long totalMs = System.currentTimeMillis() - startAt;
                    saveSuccessLog(requestId, apiKey, providerConfig, model, usage, amount, walletLogId,
                            "/ai/guest/openai/v1/chat/completions", totalMs, resolveFirstTokenMs(firstTokenAt, startAt));
                })
                .doOnError(ex -> {
                    AiProviderConfig providerConfig = resolveActualProviderConfig(null, aiChatRequest, selectedProviderConfig);
                    String model = resolveActualModel(aiChatRequest, providerConfig);
                    refreshBillingPlanProviderConfig(billingPlan, providerConfig, model);
                    long totalMs = System.currentTimeMillis() - startAt;
                    saveFailedLog(requestId, apiKey, providerConfig, model, ex.getMessage(),
                            "/ai/guest/openai/v1/chat/completions", totalMs, resolveFirstTokenMs(firstTokenAt, startAt));
                });
    }

    @Override
    public Map<String, Object> responses(String authorization, OpenAiResponsesRequest request) {
        log.info("responses 请求体: {}", JacksonUtils.toJson(request));
        AiProviderConfig providerConfig = requireProviderConfig(request.getModel());
        if (!isCodexResponsesProvider(providerConfig)) {
            Map<String, Object> response = responsesByChatCompatibility(authorization, request);
            log.info("responses 最终返回: {}", JacksonUtils.toJson(response));
            return response;
        }

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiChatRequest aiChatRequest = convertNativeResponsesRequest(null, request, false);
        OpenAiChatCompletionRequest pricingRequest = convertResponsesRequest(request);
        BillingPlan billingPlan = buildBillingPlan(apiKey, providerConfig, aiChatRequest, pricingRequest);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();
        try {
            AiChatResponse response = aiChatService.chat(aiChatRequest);
            providerConfig = resolveActualProviderConfig(response, aiChatRequest, providerConfig);
            refreshBillingPlanProviderConfig(billingPlan, providerConfig, response.getModel());
            AiUsageSummary usage = usageFromResponse(aiChatRequest, response);
            BigDecimal amount = calculateAmount(billingPlan, usage, response.getModel());
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, response.getModel());
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, response.getModel(), usage, amount, walletLogId,
                    "/ai/guest/openai/v1/responses", totalMs, null);

            Map<String, Object> result = parseNativeResponsesResult(response.getRaw());
            if (result.isEmpty()) {
                Map<String, Object> chatResponse = buildOpenAiResponse(requestId, response.getModel(), response.getContent(), usage);
                result = buildResponsesResponse(chatResponse);
            }
            log.info("responses 最终返回: {}", JacksonUtils.toJson(result));
            return result;
        } catch (RuntimeException ex) {
            providerConfig = resolveActualProviderConfig(null, aiChatRequest, providerConfig);
            String model = resolveActualModel(aiChatRequest, providerConfig);
            refreshBillingPlanProviderConfig(billingPlan, providerConfig, model);
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, ex.getMessage(),
                    "/ai/guest/openai/v1/responses", totalMs, null);
            throw ex;
        }
    }

    @Override
    public Flux<String> streamResponses(String authorization, OpenAiResponsesRequest request) {
        log.info("responses(stream) 请求体: {}", JacksonUtils.toJson(request));
        AiProviderConfig providerConfig = requireProviderConfig(request.getModel());
        if (!isCodexResponsesProvider(providerConfig)) {
            return streamResponsesByChatCompatibility(authorization, request);
        }

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiChatRequest aiChatRequest = convertNativeResponsesRequest(null, request, true);
        OpenAiChatCompletionRequest pricingRequest = convertResponsesRequest(request);
        pricingRequest.setStream(true);
        BillingPlan billingPlan = buildBillingPlan(apiKey, providerConfig, aiChatRequest, pricingRequest);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        String targetModel = StringUtils.isBlank(request.getModel()) ? providerConfig.getDefaultModel() : request.getModel();
        StringBuilder contentBuilder = new StringBuilder();
        AtomicReference<String> completedPayloadRef = new AtomicReference<>("");
        long startAt = System.currentTimeMillis();
        AtomicLong firstTokenAt = new AtomicLong(-1L);

        return aiChatService.streamChat(aiChatRequest)
                .filter(payload -> StringUtils.isNotBlank(payload) && !"[DONE]".equals(payload))
                .doOnNext(payload -> {
                    appendResponsesTextDelta(payload, contentBuilder);
                    captureCompletedPayload(payload, completedPayloadRef);
                    recordFirstTokenAt(firstTokenAt, startAt, extractDeltaFromResponsesPayload(payload));
                })
                .doOnComplete(() -> {
                    AiProviderConfig actualProviderConfig = resolveActualProviderConfig(null, aiChatRequest, providerConfig);
                    String model = resolveActualModel(aiChatRequest, actualProviderConfig);
                    refreshBillingPlanProviderConfig(billingPlan, actualProviderConfig, model);
                    AiUsageSummary usage = aiChatService.estimateUsage(aiChatRequest, contentBuilder.toString());
                    BigDecimal amount = calculateAmount(billingPlan, usage, model);
                    Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, model);
                    aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
                    aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
                    long totalMs = System.currentTimeMillis() - startAt;
                    saveSuccessLog(requestId, apiKey, actualProviderConfig, model, usage, amount, walletLogId,
                            "/ai/guest/openai/v1/responses", totalMs, resolveFirstTokenMs(firstTokenAt, startAt));
                    if (StringUtils.isNotBlank(completedPayloadRef.get())) {
                        log.info("responses(stream) 最终返回(完成): {}", completedPayloadRef.get());
                    } else {
                        log.info("responses(stream) 最终返回(完成): {}", buildStreamCompletedSummary(model, contentBuilder.toString()));
                    }
                })
                .doOnError(ex -> {
                    AiProviderConfig actualProviderConfig = resolveActualProviderConfig(null, aiChatRequest, providerConfig);
                    String model = resolveActualModel(aiChatRequest, actualProviderConfig);
                    refreshBillingPlanProviderConfig(billingPlan, actualProviderConfig, model);
                    long totalMs = System.currentTimeMillis() - startAt;
                    saveFailedLog(requestId, apiKey, actualProviderConfig, model, ex.getMessage(),
                            "/ai/guest/openai/v1/responses", totalMs, resolveFirstTokenMs(firstTokenAt, startAt));
                    String failedPayload = JacksonUtils.toJson(buildResponsesFailedEvent("resp_" + requestId, CLIENT_ERROR_MESSAGE));
                    log.warn("responses(stream) 最终返回(失败): {}", failedPayload);
                });
    }

    /**
     * 转发 OpenAI 兼容图片生成请求。
     */
    @Override
    public Map<String, Object> imageGenerations(String authorization, Map<String, Object> request) {
        return proxyFixedPriceJsonRequest(authorization, request, "model", "imagesPath",
                "/v1/images/generations", "/ai/guest/openai/v1/images/generations");
    }

    /**
     * 转发 OpenAI 兼容向量请求。
     */
    @Override
    public Map<String, Object> embeddings(String authorization, Map<String, Object> request) {
        return proxyEmbeddingsRequest(authorization, request, "model", "embeddingsPath",
                "/v1/embeddings", "/ai/guest/openai/v1/embeddings");
    }

    /**
     * 转发 OpenAI 兼容音频转写请求。
     */
    @Override
    public Object audioTranscriptions(String authorization, Map<String, String> request, MultipartFile file) {
        return proxyFixedPriceMultipartRequest(authorization, request, file, "model", "audioTranscriptionsPath",
                "/v1/audio/transcriptions", "/ai/guest/openai/v1/audio/transcriptions");
    }

    /**
     * 转发 OpenAI 兼容音频翻译请求。
     */
    @Override
    public Object audioTranslations(String authorization, Map<String, String> request, MultipartFile file) {
        return proxyFixedPriceMultipartRequest(authorization, request, file, "model", "audioTranslationsPath",
                "/v1/audio/translations", "/ai/guest/openai/v1/audio/translations");
    }

    /**
     * 转发 OpenAI 兼容语音合成请求。
     */
    @Override
    public ResponseEntity<byte[]> audioSpeech(String authorization, Map<String, Object> request) {
        return proxyFixedPriceBinaryRequest(authorization, request, "model", "audioSpeechPath",
                "/v1/audio/speech", "/ai/guest/openai/v1/audio/speech");
    }

    private Map<String, Object> responsesByChatCompatibility(String authorization, OpenAiResponsesRequest request) {
        OpenAiChatCompletionRequest chatRequest = convertResponsesRequest(request);
        Map<String, Object> chatResponse = chatCompletions(authorization, chatRequest);
        return buildResponsesResponse(chatResponse);
    }

    private Flux<String> streamResponsesByChatCompatibility(String authorization, OpenAiResponsesRequest request) {
        OpenAiChatCompletionRequest chatRequest = convertResponsesRequest(request);
        chatRequest.setStream(true);
        String responseId = "resp_" + IDGeneratorUtils.uuid32();
        String outputItemId = "msg_" + IDGeneratorUtils.uuid32();
        String model = request.getModel();
        long createdAt = System.currentTimeMillis() / 1000;
        StringBuilder outputTextBuilder = new StringBuilder();
        AtomicBoolean failed = new AtomicBoolean(false);

        Flux<String> head = Flux.just(
                JacksonUtils.toJson(buildResponsesCreatedEvent(responseId, model, createdAt)),
                JacksonUtils.toJson(buildResponsesInProgressEvent(responseId, model, createdAt)),
                JacksonUtils.toJson(buildResponsesOutputItemAddedEvent(responseId, outputItemId)),
                JacksonUtils.toJson(buildResponsesContentPartAddedEvent(responseId, outputItemId))
        );

        Flux<String> body = streamChatCompletions(authorization, chatRequest)
                .flatMap(payload -> {
                    if ("[DONE]".equals(payload)) {
                        return Flux.empty();
                    }
                    String delta = extractDeltaFromChatStreamPayload(payload);
                    if (StringUtils.isBlank(delta)) {
                        return Flux.empty();
                    }
                    outputTextBuilder.append(delta);
                    return Flux.just(JacksonUtils.toJson(buildResponsesDeltaEvent(delta)));
                })
                .onErrorResume(ex -> {
                    failed.set(true);
                    String failedPayload = JacksonUtils.toJson(buildResponsesFailedEvent(responseId, CLIENT_ERROR_MESSAGE));
                    log.warn("responses(stream) 最终返回(失败): {}", failedPayload);
                    return Flux.just(failedPayload);
                });

        Flux<String> tail = Flux.defer(() -> {
            if (failed.get()) {
                return Flux.empty();
            }
            String completedPayload = JacksonUtils.toJson(buildResponsesCompletedEvent(responseId, model, createdAt, outputItemId, outputTextBuilder.toString()));
            log.info("responses(stream) 最终返回(完成): {}", completedPayload);
            return Flux.just(
                    JacksonUtils.toJson(buildResponsesOutputTextDoneEvent(outputTextBuilder.toString())),
                    JacksonUtils.toJson(buildResponsesContentPartDoneEvent(responseId, outputItemId, outputTextBuilder.toString())),
                    JacksonUtils.toJson(buildResponsesOutputItemDoneEvent(responseId, outputItemId, outputTextBuilder.toString())),
                    completedPayload
            );
        });

        return Flux.concat(head, body, tail);
    }

    /**
     * 统一代理 JSON 类型的 OpenAI 兼容请求。
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> proxyJsonRequest(String authorization, Map<String, Object> request, String modelField,
                                                 String pathConfigKey, String defaultPath, String endpoint) {
        String model = request == null ? null : stringify(request.get(modelField));
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(model);
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String url = joinUrl(pickBaseUrl(providerConfig, config), pickString(config, pathConfigKey, defaultPath));
        String upstreamApiKey = pickApiKey(providerConfig, config);
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
            HttpHeaders headers = buildAuthorizationHeaders(upstreamApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(request == null ? new HashMap<>() : request, headers), String.class);

            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, emptyUsage(), BigDecimal.ZERO, null,
                    endpoint, totalMs, null);
            return parseJsonMap(response.getBody());
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    /**
     * 处理 embeddings 请求并按 usage 扣费。
     */
    private Map<String, Object> proxyEmbeddingsRequest(String authorization, Map<String, Object> request, String modelField,
                                                       String pathConfigKey, String defaultPath, String endpoint) {
        String model = request == null ? null : stringify(request.get(modelField));
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(model);
        BillingPlan billingPlan = buildEmbeddingBillingPlan(apiKey, providerConfig, model, request);
        preCheckBalance(billingPlan);

        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String url = joinUrl(pickBaseUrl(providerConfig, config), pickString(config, pathConfigKey, defaultPath));
        String upstreamApiKey = pickApiKey(providerConfig, config);
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
            HttpHeaders headers = buildAuthorizationHeaders(upstreamApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(request == null ? new HashMap<>() : request, headers), String.class);

            Map<String, Object> result = parseJsonMap(response.getBody());
            AiUsageSummary usage = extractEmbeddingUsage(result, request);
            BigDecimal amount = calculateAmount(billingPlan, usage, model);
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, model);
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, usage, amount, walletLogId, endpoint, totalMs, null);
            return result;
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    /**
     * 处理固定单价的 JSON 请求。
     */
    private Map<String, Object> proxyFixedPriceJsonRequest(String authorization, Map<String, Object> request, String modelField,
                                                           String pathConfigKey, String defaultPath, String endpoint) {
        String model = request == null ? null : stringify(request.get(modelField));
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(model);
        BillingPlan billingPlan = buildFixedPriceBillingPlan(apiKey, providerConfig, model);
        preCheckBalance(billingPlan);

        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String url = joinUrl(pickBaseUrl(providerConfig, config), pickString(config, pathConfigKey, defaultPath));
        String upstreamApiKey = pickApiKey(providerConfig, config);
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
            HttpHeaders headers = buildAuthorizationHeaders(upstreamApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(request == null ? new HashMap<>() : request, headers), String.class);

            AiUsageSummary usage = emptyUsage();
            BigDecimal amount = calculateAmount(billingPlan, usage, model);
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, model);
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, usage, amount, walletLogId, endpoint, totalMs, null);
            return parseJsonMap(response.getBody());
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    /**
     * 统一代理 multipart/form-data 类型的 OpenAI 兼容请求。
     */
    private Object proxyMultipartRequest(String authorization, Map<String, String> request, MultipartFile file,
                                         String modelField, String pathConfigKey, String defaultPath, String endpoint) {
        Assert.notNull(file, "file不能为空");
        String model = request == null ? null : request.get(modelField);
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(model);
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String url = joinUrl(pickBaseUrl(providerConfig, config), pickString(config, pathConfigKey, defaultPath));
        String upstreamApiKey = pickApiKey(providerConfig, config);
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
            HttpHeaders headers = buildAuthorizationHeaders(upstreamApiKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(buildMultipartBody(request, file), headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);

            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, emptyUsage(), BigDecimal.ZERO, null,
                    endpoint, totalMs, null);
            return parseResponseBody(response);
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    /**
     * 处理固定单价的 multipart 请求。
     */
    private Object proxyFixedPriceMultipartRequest(String authorization, Map<String, String> request, MultipartFile file,
                                                   String modelField, String pathConfigKey, String defaultPath, String endpoint) {
        Assert.notNull(file, "file不能为空");
        String model = request == null ? null : request.get(modelField);
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(model);
        BillingPlan billingPlan = buildFixedPriceBillingPlan(apiKey, providerConfig, model);
        preCheckBalance(billingPlan);

        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String url = joinUrl(pickBaseUrl(providerConfig, config), pickString(config, pathConfigKey, defaultPath));
        String upstreamApiKey = pickApiKey(providerConfig, config);
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
            HttpHeaders headers = buildAuthorizationHeaders(upstreamApiKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(buildMultipartBody(request, file), headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);

            AiUsageSummary usage = emptyUsage();
            BigDecimal amount = calculateAmount(billingPlan, usage, model);
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, model);
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, usage, amount, walletLogId, endpoint, totalMs, null);
            return parseResponseBody(response);
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    /**
     * 统一代理二进制响应的 OpenAI 兼容请求。
     */
    private ResponseEntity<byte[]> proxyBinaryRequest(String authorization, Map<String, Object> request, String modelField,
                                                      String pathConfigKey, String defaultPath, String endpoint) {
        String model = request == null ? null : stringify(request.get(modelField));
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(model);
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String url = joinUrl(pickBaseUrl(providerConfig, config), pickString(config, pathConfigKey, defaultPath));
        String upstreamApiKey = pickApiKey(providerConfig, config);
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
            HttpHeaders headers = buildAuthorizationHeaders(upstreamApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(request == null ? new HashMap<>() : request, headers), byte[].class);

            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, emptyUsage(), BigDecimal.ZERO, null,
                    endpoint, totalMs, null);
            return ResponseEntity.status(response.getStatusCode())
                    .headers(filterBinaryResponseHeaders(response.getHeaders()))
                    .body(response.getBody());
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    /**
     * 处理固定单价的二进制请求。
     */
    private ResponseEntity<byte[]> proxyFixedPriceBinaryRequest(String authorization, Map<String, Object> request, String modelField,
                                                                String pathConfigKey, String defaultPath, String endpoint) {
        String model = request == null ? null : stringify(request.get(modelField));
        Assert.hasText(model, "model不能为空");

        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(model);
        BillingPlan billingPlan = buildFixedPriceBillingPlan(apiKey, providerConfig, model);
        preCheckBalance(billingPlan);

        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String url = joinUrl(pickBaseUrl(providerConfig, config), pickString(config, pathConfigKey, defaultPath));
        String upstreamApiKey = pickApiKey(providerConfig, config);
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());
        String requestId = IDGeneratorUtils.uuid32();
        long startAt = System.currentTimeMillis();

        try {
            RestTemplate restTemplate = buildRestTemplate(timeoutMs, buildProxy(config));
            HttpHeaders headers = buildAuthorizationHeaders(upstreamApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(request == null ? new HashMap<>() : request, headers), byte[].class);

            AiUsageSummary usage = emptyUsage();
            BigDecimal amount = calculateAmount(billingPlan, usage, model);
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, model);
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, apiKey, providerConfig, model, usage, amount, walletLogId, endpoint, totalMs, null);
            return ResponseEntity.status(response.getStatusCode())
                    .headers(filterBinaryResponseHeaders(response.getHeaders()))
                    .body(response.getBody());
        } catch (RuntimeException ex) {
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, apiKey, providerConfig, model, extractUpstreamErrorMessage(ex), endpoint, totalMs, null);
            throw ex;
        }
    }

    /**
     * 构建透传请求的认证头。
     */
    private HttpHeaders buildAuthorizationHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isNotBlank(apiKey)) {
            headers.setBearerAuth(apiKey);
        }
        return headers;
    }

    /**
     * 构建 multipart 请求体，保留原始文件名。
     */
    private MultiValueMap<String, Object> buildMultipartBody(Map<String, String> request, MultipartFile file) {
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (request != null) {
            request.forEach((key, value) -> {
                if (StringUtils.isNotBlank(value)) {
                    body.add(key, value);
                }
            });
        }
        try {
            body.add("file", new NamedByteArrayResource(file.getBytes(), file.getOriginalFilename()));
            return body;
        } catch (Exception ex) {
            throw new IllegalArgumentException("读取上传文件失败", ex);
        }
    }

    /**
     * 解析上游响应体，兼容 JSON 与纯文本格式。
     */
    private Object parseResponseBody(ResponseEntity<byte[]> response) {
        byte[] body = response.getBody();
        if (body == null || body.length == 0) {
            return new HashMap<>();
        }
        MediaType contentType = response.getHeaders().getContentType();
        String content = new String(body);
        if (contentType != null && MediaType.APPLICATION_JSON.includes(contentType)) {
            return parseJsonMap(content);
        }
        if (looksLikeJson(content)) {
            return parseJsonMap(content);
        }
        return content;
    }

    /**
     * 过滤二进制透传时需要保留的响应头。
     */
    private HttpHeaders filterBinaryResponseHeaders(HttpHeaders source) {
        HttpHeaders headers = new HttpHeaders();
        MediaType contentType = source.getContentType();
        if (contentType != null) {
            headers.setContentType(contentType);
        }
        long contentLength = source.getContentLength();
        if (contentLength >= 0) {
            headers.setContentLength(contentLength);
        }
        String disposition = source.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        if (StringUtils.isNotBlank(disposition)) {
            headers.set(HttpHeaders.CONTENT_DISPOSITION, disposition);
        }
        return headers;
    }

    /**
     * 解析 JSON 对象字符串。
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonMap(String body) {
        if (StringUtils.isBlank(body)) {
            return new HashMap<>();
        }
        Map<String, Object> result = JacksonUtils.toBean(body, Map.class);
        return result == null ? new HashMap<>() : result;
    }

    /**
     * 构建空 token 用量，用于暂未计费的透传接口。
     */
    private AiUsageSummary emptyUsage() {
        AiUsageSummary usage = new AiUsageSummary();
        usage.setPromptTokens(0);
        usage.setCompletionTokens(0);
        usage.setTotalTokens(0);
        return usage;
    }

    /**
     * 判断响应体是否可能为 JSON。
     */
    private boolean looksLikeJson(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        String value = content.trim();
        return value.startsWith("{") || value.startsWith("[");
    }

    /**
     * 统一转字符串，便于读取动态请求字段。
     */
    private String stringify(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String extractBearerToken(String authorization) {
        Assert.hasText(authorization, "Authorization不能为空");
        String prefix = "Bearer ";
        Assert.isTrue(authorization.startsWith(prefix), "Authorization格式错误");
        return authorization.substring(prefix.length()).trim();
    }

    private void addModelRow(Map<String, Map<String, Object>> modelMap, String modelName, AiProviderConfig providerConfig,
                             LocalDateTime createdTime) {
        if (StringUtils.isBlank(modelName) || modelMap.containsKey(modelName)) {
            return;
        }
        Map<String, Object> row = new HashMap<>();
        row.put("id", modelName);
        row.put("object", "model");
        row.put("created", toEpochSeconds(createdTime));
        row.put("owned_by", StringUtils.isNotBlank(providerConfig.getProvider()) ? providerConfig.getProvider() : "soho");
        modelMap.put(modelName, row);
    }

    private long toEpochSeconds(LocalDateTime createdTime) {
        LocalDateTime value = createdTime == null ? LocalDateTime.now() : createdTime;
        return value.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 构造 Gemini models 接口返回模型项。
     *
     * @param modelName 模型名
     * @return 模型数据
     */
    private Map<String, Object> buildGeminiModelItem(String modelName) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", "models/" + modelName);
        item.put("displayName", modelName);
        item.put("description", "Gemini model routed by Soho AI");
        item.put("inputTokenLimit", 0);
        item.put("outputTokenLimit", 0);
        item.put("supportedGenerationMethods", List.of("generateContent", "streamGenerateContent"));
        return item;
    }

    /**
     * 解析套餐额度快照。
     */
    private PackageQuotaSnapshot resolvePackageQuotaSnapshot(AiUserMemberCardView currentCard) {
        if (currentCard == null || !Boolean.TRUE.equals(currentCard.getUsageAvailable())) {
            return PackageQuotaSnapshot.empty();
        }
        if (Boolean.TRUE.equals(currentCard.getRateLimit7dEnabled())) {
            return PackageQuotaSnapshot.of(currentCard.getRateLimit7dUsed(), currentCard.getRateLimit7dRemaining());
        }
        if (Boolean.TRUE.equals(currentCard.getRateLimit5hEnabled())) {
            return PackageQuotaSnapshot.of(currentCard.getRateLimit5hUsed(), currentCard.getRateLimit5hRemaining());
        }
        return PackageQuotaSnapshot.empty();
    }

    /**
     * 构建套餐查询失败响应。
     */
    private Map<String, Object> buildSelfPackageFailedResponse(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }

    /**
     * 解析余额接口使用的钱包类型列表。
     */
    private List<Integer> resolveBalanceWalletTypeIds() {
        List<AiProviderConfig> providerConfigs = aiProviderConfigService.list(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getStatus, 1)
                .orderByAsc(AiProviderConfig::getId));
        List<Integer> walletTypeIds = new ArrayList<>();
        for (AiProviderConfig providerConfig : providerConfigs) {
            Integer walletTypeId = pickInteger(parseConfig(providerConfig.getConfigJson()), "billingWalletTypeId", 1);
            if (!walletTypeIds.contains(walletTypeId)) {
                walletTypeIds.add(walletTypeId);
            }
        }
        if (walletTypeIds.isEmpty()) {
            walletTypeIds.add(1);
        }
        return walletTypeIds;
    }

    /**
     * 汇总用户在 AI 钱包中的可用余额。
     */
    private BigDecimal sumWalletBalance(Long userId, List<Integer> walletTypeIds) {
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Integer walletTypeId : walletTypeIds) {
            WalletInfo walletInfo = walletInfoService.getByUserIdAndType(userId, walletTypeId);
            if (walletInfo == null || walletInfo.getAmount() == null) {
                continue;
            }
            totalBalance = totalBalance.add(walletInfo.getAmount());
        }
        return totalBalance.setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 查询当前用户的会员请求量使用情况。
     */
    private AiMemberRequestLimitService.UsageSnapshot resolveRequestUsage(Long userId) {
        return aiUserMemberCardService.resolveActiveMemberCard(userId)
                .map(card -> aiMemberRequestLimitService.queryUsage(userId, card))
                .orElse(AiMemberRequestLimitService.UsageSnapshot.empty());
    }

    /**
     * 构建请求量统计结构。
     */
    private Map<String, Object> buildRequestUsage(AiMemberRequestLimitService.UsageSnapshot usageSnapshot) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("usage_available", usageSnapshot.isUsageAvailable());
        result.put("five_hour_enabled", usageSnapshot.isFiveHourEnabled());
        result.put("five_hour_limit", usageSnapshot.getFiveHourLimit());
        result.put("five_hour_used", usageSnapshot.getFiveHourUsed());
        result.put("five_hour_remaining", usageSnapshot.getFiveHourRemaining());
        result.put("five_hour_next_reset_millis", usageSnapshot.getFiveHourNextResetMillis());
        result.put("seven_day_enabled", usageSnapshot.isSevenDayEnabled());
        result.put("seven_day_limit", usageSnapshot.getSevenDayLimit());
        result.put("seven_day_used", usageSnapshot.getSevenDayUsed());
        result.put("seven_day_remaining", usageSnapshot.getSevenDayRemaining());
        result.put("seven_day_next_reset_millis", usageSnapshot.getSevenDayNextResetMillis());
        return result;
    }

    /**
     * 构建 token 用量统计结构。
     */
    private Map<String, Object> buildTokenUsage(AiUserApiKey apiKey) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("today", queryTokenUsage(apiKey, LocalDate.now().atStartOfDay(), LocalDateTime.now()));
        result.put("total", queryTokenUsage(apiKey, null, null));
        return result;
    }

    /**
     * 聚合 token 用量。
     */
    private Map<String, Object> queryTokenUsage(AiUserApiKey apiKey, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<AiApiCallLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "COUNT(*) AS request_count",
                "SUM(prompt_tokens) AS prompt_tokens",
                "SUM(completion_tokens) AS completion_tokens",
                "SUM(total_tokens) AS total_tokens",
                "SUM(amount) AS amount"
        );
        queryWrapper.eq("user_id", apiKey.getUserId());
        queryWrapper.eq("api_key_id", apiKey.getId());
        queryWrapper.eq("status", AiApiCallLogEnums.Status.SUCCESS.getId());
        if (startTime != null && endTime != null) {
            queryWrapper.between("created_time", startTime, endTime);
        }

        Map<String, Object> row = aiApiCallLogService.getMap(queryWrapper);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("request_count", longValue(row, "request_count"));
        result.put("prompt_tokens", longValue(row, "prompt_tokens"));
        result.put("completion_tokens", longValue(row, "completion_tokens"));
        result.put("total_tokens", longValue(row, "total_tokens"));
        result.put("amount", decimalValue(row, "amount"));
        return result;
    }

    /**
     * 读取长整型聚合值。
     */
    private long longValue(Map<String, Object> row, String key) {
        if (row == null) {
            return 0L;
        }
        Object value = row.get(key);
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    /**
     * 读取金额聚合值。
     */
    private BigDecimal decimalValue(Map<String, Object> row, String key) {
        if (row == null || row.get(key) == null) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        Object value = row.get(key);
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).setScale(4, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 套餐额度快照。
     */
    private static class PackageQuotaSnapshot {
        /**
         * 已用额度。
         */
        private final long usedQuota;

        /**
         * 剩余额度。
         */
        private final long remainingQuota;

        /**
         * 总额度。
         */
        private final long totalQuota;

        /**
         * 构造套餐额度快照。
         */
        private PackageQuotaSnapshot(long usedQuota, long remainingQuota, long totalQuota) {
            this.usedQuota = usedQuota;
            this.remainingQuota = remainingQuota;
            this.totalQuota = totalQuota;
        }

        /**
         * 创建空快照。
         */
        private static PackageQuotaSnapshot empty() {
            return new PackageQuotaSnapshot(0L, 0L, 0L);
        }

        /**
         * 基于请求量创建快照。
         */
        private static PackageQuotaSnapshot of(Integer used, Integer remaining) {
            long usedValue = Math.max(used == null ? 0 : used, 0);
            long remainingValue = Math.max(remaining == null ? 0 : remaining, 0);
            return new PackageQuotaSnapshot(
                    usedValue * PACKAGE_QUOTA_UNIT,
                    remainingValue * PACKAGE_QUOTA_UNIT,
                    (usedValue + remainingValue) * PACKAGE_QUOTA_UNIT
            );
        }
    }

    private AiProviderConfig requireProviderConfig(String model) {
        Assert.hasText(model, "model不能为空");
        try {
            AiProviderConfig providerConfig = aiChatService.resolveProviderConfig(null, model);
            log.debug("requireProviderConfig success model={}, configId={}, code={}, provider={}",
                    model, providerConfig.getId(), providerConfig.getCode(), providerConfig.getProvider());
            return providerConfig;
        } catch (IllegalArgumentException ex) {
            log.warn("requireProviderConfig failed model={}", model, ex);
            throw ex;
        }
    }

    private AiChatRequest convertRequest(String providerCode, OpenAiChatCompletionRequest request) {
        AiChatRequest aiChatRequest = new AiChatRequest();
        aiChatRequest.setProviderCode(providerCode);
        aiChatRequest.setModel(request.getModel());
        aiChatRequest.setStream(Boolean.TRUE.equals(request.getStream()));
        aiChatRequest.setTemperature(request.getTemperature());
        aiChatRequest.setTopP(request.getTopP());
        aiChatRequest.setMaxTokens(request.getMaxTokens());
        List<AiChatRequest.Message> messages = new ArrayList<>();
        if (request.getMessages() != null) {
            for (OpenAiChatCompletionRequest.Message message : request.getMessages()) {
                AiChatRequest.Message item = new AiChatRequest.Message();
                item.setRole(message.getRole());
                populateMessageContent(item, message.getContent());
                messages.add(item);
            }
        }
        aiChatRequest.setMessages(messages);
        return aiChatRequest;
    }

    private OpenAiChatCompletionRequest convertResponsesRequest(OpenAiResponsesRequest request) {
        OpenAiChatCompletionRequest chatRequest = new OpenAiChatCompletionRequest();
        chatRequest.setModel(request.getModel());
        chatRequest.setStream(Boolean.TRUE.equals(request.getStream()));
        chatRequest.setTemperature(request.getTemperature());
        chatRequest.setTopP(request.getTopP());
        chatRequest.setMaxTokens(request.getMaxOutputTokens());
        chatRequest.setMessages(convertResponsesInputToMessages(request.getInput(), request.getInstructions()));
        return chatRequest;
    }

    private AiChatRequest convertNativeResponsesRequest(String providerCode, OpenAiResponsesRequest request, boolean stream) {
        AiChatRequest aiChatRequest = new AiChatRequest();
        aiChatRequest.setProviderCode(providerCode);
        aiChatRequest.setModel(request.getModel());
        aiChatRequest.setStream(stream);
        aiChatRequest.setTemperature(request.getTemperature());
        aiChatRequest.setTopP(request.getTopP());
        aiChatRequest.setMaxTokens(request.getMaxOutputTokens());
        aiChatRequest.setInstructions(request.getInstructions());

        List<OpenAiChatCompletionRequest.Message> sourceMessages = convertResponsesInputToMessages(request.getInput(), null);
        List<AiChatRequest.Message> messages = new ArrayList<>();
        for (OpenAiChatCompletionRequest.Message source : sourceMessages) {
            AiChatRequest.Message item = new AiChatRequest.Message();
            item.setRole(source.getRole());
            populateMessageContent(item, source.getContent());
            messages.add(item);
        }
        aiChatRequest.setMessages(messages);

        Map<String, Object> extra = new HashMap<>();
        extra.put("nativeResponses", true);
        extra.put("responsesRequestBody", buildNativeResponsesBody(request, stream));
        aiChatRequest.setExtra(extra);
        return aiChatRequest;
    }

    private Map<String, Object> buildNativeResponsesBody(OpenAiResponsesRequest request, boolean stream) {
        Map<String, Object> body = new HashMap<>();
        putIfNotNull(body, "model", request.getModel());
        putIfNotNull(body, "instructions", request.getInstructions());
        putIfNotNull(body, "input", request.getInput());
        putIfNotNull(body, "tools", request.getTools());
        putIfNotNull(body, "tool_choice", request.getToolChoice());
        putIfNotNull(body, "parallel_tool_calls", request.getParallelToolCalls());
        putIfNotNull(body, "reasoning", request.getReasoning());
        putIfNotNull(body, "store", request.getStore());
        body.put("stream", stream);
        putIfNotNull(body, "include", request.getInclude());
        putIfNotNull(body, "service_tier", request.getServiceTier());
        putIfNotNull(body, "prompt_cache_key", request.getPromptCacheKey());
        putIfNotNull(body, "text", request.getText());
        putIfNotNull(body, "temperature", request.getTemperature());
        putIfNotNull(body, "top_p", request.getTopP());
        putIfNotNull(body, "max_output_tokens", request.getMaxOutputTokens());
        return body;
    }

    private boolean isCodexResponsesProvider(AiProviderConfig providerConfig) {
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String adapter = String.valueOf(config.getOrDefault("adapter", ""));
        return "codexResponses".equalsIgnoreCase(adapter) || "chatgptCodexResponses".equalsIgnoreCase(adapter);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseNativeResponsesResult(String raw) {
        if (StringUtils.isBlank(raw)) {
            return new HashMap<>();
        }
        try {
            Map<String, Object> root = JacksonUtils.toBean(raw, Map.class);
            if (root == null) {
                return new HashMap<>();
            }
            Object object = root.get("object");
            if ("response".equals(object)) {
                return root;
            }
            Object response = root.get("response");
            if (response instanceof Map) {
                return (Map<String, Object>) response;
            }
            return root;
        } catch (Exception ex) {
            log.warn("parse native responses result failed, raw={}", raw, ex);
            return new HashMap<>();
        }
    }

    private void appendResponsesTextDelta(String payload, StringBuilder builder) {
        try {
            String type = JacksonUtils.getObjectMapper().readTree(payload).path("type").asText("");
            if (!"response.output_text.delta".equals(type)) {
                return;
            }
            String delta = JacksonUtils.getObjectMapper().readTree(payload).path("delta").asText("");
            if (StringUtils.isNotBlank(delta)) {
                builder.append(delta);
            }
        } catch (Exception ignore) {
        }
    }

    private void captureCompletedPayload(String payload, AtomicReference<String> completedPayloadRef) {
        try {
            String type = JacksonUtils.getObjectMapper().readTree(payload).path("type").asText("");
            if ("response.completed".equals(type)) {
                completedPayloadRef.set(payload);
            }
        } catch (Exception ignore) {
        }
    }

    private String buildStreamCompletedSummary(String model, String outputText) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "response.completed");
        response.put("model", model);
        response.put("output_text", outputText);
        return JacksonUtils.toJson(response);
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private List<OpenAiChatCompletionRequest.Message> convertResponsesInputToMessages(Object input, String instructions) {
        List<OpenAiChatCompletionRequest.Message> messages = new ArrayList<>();
        if (StringUtils.isNotBlank(instructions)) {
            OpenAiChatCompletionRequest.Message systemMessage = new OpenAiChatCompletionRequest.Message();
            systemMessage.setRole("system");
            systemMessage.setContent(instructions);
            messages.add(systemMessage);
        }

        if (input == null) {
            return messages;
        }

        if (input instanceof String) {
            messages.add(buildMessage("user", input));
            return messages;
        }

        if (input instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) input;
            Object roleObj = map.get("role");
            String role = roleObj == null ? "user" : String.valueOf(roleObj);
            messages.add(buildMessage(role, normalizeResponsesContent(map.get("content"))));
            return messages;
        }

        if (input instanceof List) {
            for (Object item : (List<?>) input) {
                if (item == null) {
                    continue;
                }
                if (item instanceof String) {
                    messages.add(buildMessage("user", item));
                    continue;
                }
                if (!(item instanceof Map)) {
                    messages.add(buildMessage("user", String.valueOf(item)));
                    continue;
                }
                Map<?, ?> map = (Map<?, ?>) item;
                Object roleObj = map.get("role");
                String role = roleObj == null ? "user" : String.valueOf(roleObj);
                Object content = normalizeResponsesContent(map.get("content"));
                messages.add(buildMessage(role, content));
            }
            return messages;
        }

        messages.add(buildMessage("user", String.valueOf(input)));
        return messages;
    }

    private Object normalizeResponsesContent(Object content) {
        if (!(content instanceof List)) {
            return content;
        }
        List<?> blocks = (List<?>) content;
        List<Map<String, Object>> transformed = new ArrayList<>();
        for (Object blockObj : blocks) {
            if (!(blockObj instanceof Map)) {
                continue;
            }
            Map<?, ?> block = (Map<?, ?>) blockObj;
            String type = String.valueOf(block.get("type"));
            if ("input_text".equals(type) || "text".equals(type)) {
                if (block.get("text") == null) {
                    continue;
                }
                Map<String, Object> textBlock = new HashMap<>();
                textBlock.put("type", "text");
                textBlock.put("text", String.valueOf(block.get("text")));
                transformed.add(textBlock);
                continue;
            }
            if ("input_image".equals(type) || "image_url".equals(type)) {
                Object imageUrl = block.get("image_url");
                if (imageUrl == null) {
                    imageUrl = block.get("url");
                }
                if (imageUrl == null) {
                    continue;
                }
                Map<String, Object> imageBlock = new HashMap<>();
                imageBlock.put("type", "image_url");
                if (imageUrl instanceof Map) {
                    imageBlock.put("image_url", imageUrl);
                } else {
                    Map<String, Object> imageUrlMap = new HashMap<>();
                    imageUrlMap.put("url", String.valueOf(imageUrl));
                    imageBlock.put("image_url", imageUrlMap);
                }
                transformed.add(imageBlock);
                continue;
            }
            if ("input_file".equals(type) || "file_url".equals(type)) {
                Object fileUrl = block.get("file_url");
                if (fileUrl == null) {
                    fileUrl = block.get("url");
                }
                if (fileUrl == null) {
                    continue;
                }
                Map<String, Object> fileBlock = new HashMap<>();
                fileBlock.put("type", "file_url");
                if (fileUrl instanceof Map) {
                    fileBlock.put("file_url", fileUrl);
                } else {
                    Map<String, Object> fileUrlMap = new HashMap<>();
                    fileUrlMap.put("url", String.valueOf(fileUrl));
                    fileBlock.put("file_url", fileUrlMap);
                }
                transformed.add(fileBlock);
            }
        }
        return transformed.isEmpty() ? content : transformed;
    }

    private OpenAiChatCompletionRequest.Message buildMessage(String role, Object content) {
        OpenAiChatCompletionRequest.Message message = new OpenAiChatCompletionRequest.Message();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    private void populateMessageContent(AiChatRequest.Message message, Object content) {
        if (content == null) {
            return;
        }
        if (content instanceof String) {
            message.setContent((String) content);
            return;
        }
        try {
            if (content instanceof List) {
                StringBuilder builder = new StringBuilder();
                List<String> imageUrls = new ArrayList<>();
                List<?> list = (List<?>) content;
                for (Object item : list) {
                    Map<?, ?> map = item instanceof Map ? (Map<?, ?>) item : null;
                    if (map == null) {
                        continue;
                    }
                    Object type = map.get("type");
                    if ("text".equals(type) && map.get("text") != null) {
                        if (builder.length() > 0) {
                            builder.append("\n");
                        }
                        builder.append(map.get("text"));
                    } else if ("image_url".equals(type)) {
                        String imageUrl = extractImageUrl(map.get("image_url"));
                        if (StringUtils.isNotBlank(imageUrl)) {
                            imageUrls.add(imageUrl);
                        }
                    } else if ("file_url".equals(type)) {
                        String fileUrl = extractFileUrl(map.get("file_url"));
                        if (StringUtils.isNotBlank(fileUrl)) {
                            if (message.getFileUrls() == null) {
                                message.setFileUrls(new ArrayList<>());
                            }
                            message.getFileUrls().add(fileUrl);
                        }
                    }
                }
                if (builder.length() > 0) {
                    message.setContent(builder.toString());
                }
                if (!imageUrls.isEmpty()) {
                    message.setImageUrls(imageUrls);
                }
                return;
            }
            message.setContent(JacksonUtils.toJson(content));
        } catch (Exception e) {
            message.setContent(String.valueOf(content));
        }
    }

    private String extractImageUrl(Object imageUrlValue) {
        if (imageUrlValue instanceof String) {
            return (String) imageUrlValue;
        }
        if (imageUrlValue instanceof Map) {
            Object url = ((Map<?, ?>) imageUrlValue).get("url");
            return url == null ? null : String.valueOf(url);
        }
        return null;
    }

    private String extractFileUrl(Object fileUrlValue) {
        if (fileUrlValue instanceof String) {
            return (String) fileUrlValue;
        }
        if (fileUrlValue instanceof Map) {
            Object url = ((Map<?, ?>) fileUrlValue).get("url");
            return url == null ? null : String.valueOf(url);
        }
        return null;
    }

    private BillingPlan buildBillingPlan(AiUserApiKey apiKey, AiProviderConfig providerConfig, AiChatRequest aiChatRequest,
                                         OpenAiChatCompletionRequest request) {
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String requestedModel = StringUtils.isNotBlank(request.getModel()) ? request.getModel() : providerConfig.getDefaultModel();
        ModelPricing modelPricing = resolveModelPricing(providerConfig.getId(), requestedModel);
        BillingPlan billingPlan = new BillingPlan();
        billingPlan.userId = apiKey.getUserId();
        billingPlan.apiKeyId = apiKey.getId();
        billingPlan.providerConfigId = providerConfig.getId();
        billingPlan.billingEnabled = modelPricing.hasSplitPrice() || pickBoolean(config, "billingEnabled", false);
        billingPlan.walletTypeId = pickInteger(config, "billingWalletTypeId", 1);
        billingPlan.promptPricePer1kTokens = pickBigDecimal(config, "promptPricePer1kTokens", BigDecimal.ZERO);
        billingPlan.completionPricePer1kTokens = pickBigDecimal(config, "completionPricePer1kTokens", billingPlan.promptPricePer1kTokens);
        billingPlan.estimatedModel = requestedModel;
        billingPlan.modelPricing = modelPricing;
        // 会员卡判定：只对 by_request 模式生效，返回是否超限等结果
        billingPlan.memberLimitDecision = aiMemberRequestLimitService.evaluate(
                billingPlan.userId,
                aiUserMemberCardService.resolveActiveMemberCard(billingPlan.userId)
        );
        // 会员未超限时，本次请求免费，不走钱包扣费
        if (billingPlan.memberLimitDecision.isMemberByRequest() && !billingPlan.memberLimitDecision.isOverLimit()) {
            billingPlan.billingEnabled = false;
        }

        AiUsageSummary estimatedUsage = aiChatService.estimateUsage(aiChatRequest, "");
        int expectedCompletionTokens = request.getMaxTokens() == null ? 0 : Math.max(request.getMaxTokens(), 0);
        estimatedUsage.setCompletionTokens(expectedCompletionTokens);
        estimatedUsage.setTotalTokens(estimatedUsage.getPromptTokens() + expectedCompletionTokens);
        billingPlan.estimatedUsage = estimatedUsage;
        return billingPlan;
    }

    /**
     * 构建 embeddings 计费方案，按输入 token 计费。
     */
    private BillingPlan buildEmbeddingBillingPlan(AiUserApiKey apiKey, AiProviderConfig providerConfig, String model,
                                                  Map<String, Object> request) {
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        ModelPricing modelPricing = resolveModelPricing(providerConfig.getId(), model);
        BillingPlan billingPlan = new BillingPlan();
        billingPlan.userId = apiKey.getUserId();
        billingPlan.apiKeyId = apiKey.getId();
        billingPlan.providerConfigId = providerConfig.getId();
        billingPlan.billingEnabled = modelPricing.hasSplitPrice() || pickBoolean(config, "billingEnabled", false);
        billingPlan.walletTypeId = pickInteger(config, "billingWalletTypeId", 1);
        billingPlan.promptPricePer1kTokens = pickBigDecimal(config, "promptPricePer1kTokens", BigDecimal.ZERO);
        billingPlan.completionPricePer1kTokens = BigDecimal.ZERO;
        billingPlan.estimatedModel = model;
        billingPlan.modelPricing = modelPricing;
        billingPlan.memberLimitDecision = aiMemberRequestLimitService.evaluate(
                billingPlan.userId,
                aiUserMemberCardService.resolveActiveMemberCard(billingPlan.userId)
        );
        if (billingPlan.memberLimitDecision.isMemberByRequest() && !billingPlan.memberLimitDecision.isOverLimit()) {
            billingPlan.billingEnabled = false;
        }
        billingPlan.estimatedUsage = estimateEmbeddingUsage(request);
        return billingPlan;
    }

    /**
     * 构建 Gemini generateContent 的计费方案，按 token 规则计费。
     */
    private BillingPlan buildGeminiGenerateBillingPlan(AiUserApiKey apiKey, AiProviderConfig providerConfig, String model,
                                                       Map<String, Object> request) {
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        ModelPricing modelPricing = resolveModelPricing(providerConfig.getId(), model);
        BillingPlan billingPlan = new BillingPlan();
        billingPlan.userId = apiKey.getUserId();
        billingPlan.apiKeyId = apiKey.getId();
        billingPlan.providerConfigId = providerConfig.getId();
        billingPlan.billingEnabled = modelPricing.hasSplitPrice() || pickBoolean(config, "billingEnabled", false);
        billingPlan.walletTypeId = pickInteger(config, "billingWalletTypeId", 1);
        billingPlan.promptPricePer1kTokens = pickBigDecimal(config, "promptPricePer1kTokens", BigDecimal.ZERO);
        billingPlan.completionPricePer1kTokens = pickBigDecimal(config, "completionPricePer1kTokens", billingPlan.promptPricePer1kTokens);
        billingPlan.estimatedModel = model;
        billingPlan.modelPricing = modelPricing;
        billingPlan.memberLimitDecision = aiMemberRequestLimitService.evaluate(
                billingPlan.userId,
                aiUserMemberCardService.resolveActiveMemberCard(billingPlan.userId)
        );
        // TODO 暂时gemini类型请求不支持会员卡， 只能按量扣费
        if (billingPlan.memberLimitDecision.isMemberByRequest() && !billingPlan.memberLimitDecision.isOverLimit()) {
//            billingPlan.billingEnabled = false;
        }
        billingPlan.estimatedUsage = estimateGeminiUsage(request);
        return billingPlan;
    }

    /**
     * 构建固定单价计费方案，按次扣费。
     */
    private BillingPlan buildFixedPriceBillingPlan(AiUserApiKey apiKey, AiProviderConfig providerConfig, String model) {
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        ModelPricing modelPricing = resolveModelPricing(providerConfig.getId(), model);
        BillingPlan billingPlan = new BillingPlan();
        billingPlan.userId = apiKey.getUserId();
        billingPlan.apiKeyId = apiKey.getId();
        billingPlan.providerConfigId = providerConfig.getId();
        billingPlan.billingEnabled = modelPricing.hasFixedRequestPrice() || pickBoolean(config, "billingEnabled", false);
        billingPlan.walletTypeId = pickInteger(config, "billingWalletTypeId", 1);
        billingPlan.promptPricePer1kTokens = BigDecimal.ZERO;
        billingPlan.completionPricePer1kTokens = BigDecimal.ZERO;
        billingPlan.fixedRequestPrice = modelPricing.fixedRequestPrice();
        billingPlan.fixedRequestBilling = billingPlan.fixedRequestPrice.compareTo(BigDecimal.ZERO) > 0;
        billingPlan.estimatedModel = model;
        billingPlan.modelPricing = modelPricing;
        billingPlan.memberLimitDecision = aiMemberRequestLimitService.evaluate(
                billingPlan.userId,
                aiUserMemberCardService.resolveActiveMemberCard(billingPlan.userId)
        );
        if (billingPlan.memberLimitDecision.isMemberByRequest() && !billingPlan.memberLimitDecision.isOverLimit()) {
            billingPlan.billingEnabled = false;
        }
        billingPlan.estimatedUsage = emptyUsage();
        return billingPlan;
    }

    private void preCheckBalance(BillingPlan billingPlan) {
        // 免费请求（billingEnabled=false）直接跳过余额检查
        if (!billingPlan.billingEnabled) {
            return;
        }
        Assert.notNull(billingPlan.walletTypeId, "billingWalletTypeId未配置");
        BigDecimal estimatedAmount = calculateAmount(billingPlan, billingPlan.estimatedUsage, billingPlan.estimatedModel);
        if (estimatedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        WalletInfo walletInfo = walletInfoService.getByUserIdAndType(billingPlan.userId, billingPlan.walletTypeId);
        Assert.notNull(walletInfo, "钱包不存在");
        Assert.isTrue(walletInfo.getAmount().compareTo(estimatedAmount) >= 0, "钱包余额不足");
    }

    private Long chargeIfNeeded(BillingPlan billingPlan, String requestId, AiUsageSummary usage, BigDecimal amount, String model) {
        // 免费请求或金额为0时不扣费
        if (!billingPlan.billingEnabled || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return walletInfoApiService.changeWalletAmount(
                billingPlan.userId,
                billingPlan.walletTypeId,
                WalletLogEnums.BizId.PAY_ORDER.getId(),
                requestId,
                amount.negate(),
                buildChargeNotes(model, usage)
        );
    }

    private String buildChargeNotes(String model, AiUsageSummary usage) {
        int promptTokens = usage != null && usage.getPromptTokens() != null ? usage.getPromptTokens() : 0;
        int completionTokens = usage != null && usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0;
        int totalTokens = usage != null && usage.getTotalTokens() != null ? usage.getTotalTokens() : 0;
        return "AI调用扣费 model=" + model
                + ", inputTokens=" + promptTokens
                + ", outputTokens=" + completionTokens
                + ", totalTokens=" + totalTokens;
    }

    /**
     * 读取 Gemini 回放响应体文件。
     */
    private String readGeminiMockResponseBody() {
        try {
            return Files.readString(Path.of(GEMINI_MOCK_RESPONSE_FILE));
        } catch (Exception ex) {
            throw new IllegalArgumentException("读取 Gemini 回放文件失败: " + GEMINI_MOCK_RESPONSE_FILE, ex);
        }
    }

    private BigDecimal calculateAmount(BillingPlan billingPlan, AiUsageSummary usage, String model) {
        if (!billingPlan.billingEnabled || usage == null) {
            return BigDecimal.ZERO;
        }
        if (billingPlan.fixedRequestBilling) {
            return billingPlan.fixedRequestPrice == null
                    ? BigDecimal.ZERO
                    : billingPlan.fixedRequestPrice.setScale(6, RoundingMode.HALF_UP);
        }
        ModelPricing modelPricing = billingPlan.modelPricing;
        if (modelPricing == null || !modelPricing.matches(model)) {
            modelPricing = resolveModelPricing(billingPlan.providerConfigId, model);
        }
        if (modelPricing.hasSplitPrice()) {
            BigDecimal promptCost = modelPricing.promptPricePer1kTokens
                    .multiply(BigDecimal.valueOf(usage.getPromptTokens() == null ? 0 : usage.getPromptTokens()))
                    .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
            BigDecimal completionCost = modelPricing.completionPricePer1kTokens
                    .multiply(BigDecimal.valueOf(usage.getCompletionTokens() == null ? 0 : usage.getCompletionTokens()))
                    .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
            return promptCost.add(completionCost).setScale(6, RoundingMode.HALF_UP);
        }
        BigDecimal promptCost = billingPlan.promptPricePer1kTokens
                .multiply(BigDecimal.valueOf(usage.getPromptTokens() == null ? 0 : usage.getPromptTokens()))
                .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
        BigDecimal completionCost = billingPlan.completionPricePer1kTokens
                .multiply(BigDecimal.valueOf(usage.getCompletionTokens() == null ? 0 : usage.getCompletionTokens()))
                .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
        return promptCost.add(completionCost).setScale(6, RoundingMode.HALF_UP);
    }

    private ModelPricing resolveModelPricing(Long providerConfigId, String model) {
        if (providerConfigId == null || StringUtils.isBlank(model)) {
            return ModelPricing.empty();
        }
        for (AiModelInfo item : aiProviderModelRelService.listEnabledModelsByProviderConfigId(providerConfigId)) {
            if (!model.equals(item.getModelName())) {
                continue;
            }
            BigDecimal promptPrice = item.getPromptPrice() == null ? BigDecimal.ZERO : item.getPromptPrice();
            BigDecimal completionPrice = item.getCompletionPrice() == null ? BigDecimal.ZERO : item.getCompletionPrice();
            if (promptPrice.compareTo(BigDecimal.ZERO) > 0 || completionPrice.compareTo(BigDecimal.ZERO) > 0) {
                if (promptPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    promptPrice = completionPrice;
                }
                if (completionPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    completionPrice = promptPrice;
                }
                return ModelPricing.split(item.getModelName(), promptPrice, completionPrice);
            }
        }
        return ModelPricing.empty();
    }

    /**
     * 估算 embeddings 请求的 token。
     */
    private AiUsageSummary estimateEmbeddingUsage(Map<String, Object> request) {
        AiUsageSummary usage = new AiUsageSummary();
        if (request == null) {
            return usage;
        }
        int promptTokens = estimateTokensByObject(request.get("input"));
        usage.setPromptTokens(promptTokens);
        usage.setCompletionTokens(0);
        usage.setTotalTokens(promptTokens);
        return usage;
    }

    /**
     * 从 embeddings 响应中提取 usage。
     */
    @SuppressWarnings("unchecked")
    private AiUsageSummary extractEmbeddingUsage(Map<String, Object> response, Map<String, Object> request) {
        AiUsageSummary usage = new AiUsageSummary();
        if (response != null && response.get("usage") instanceof Map) {
            Map<String, Object> usageMap = (Map<String, Object>) response.get("usage");
            usage.setPromptTokens(toNumber(usageMap.get("prompt_tokens")).intValue());
            usage.setCompletionTokens(0);
            int totalTokens = toNumber(usageMap.get("total_tokens")).intValue();
            usage.setTotalTokens(totalTokens > 0 ? totalTokens : usage.getPromptTokens());
        }
        if (usage.getTotalTokens() == 0) {
            return estimateEmbeddingUsage(request);
        }
        return usage;
    }

    /**
     * 从 Gemini generateContent 响应中提取 usage。
     */
    @SuppressWarnings("unchecked")
    private AiUsageSummary extractGeminiUsage(Map<String, Object> response, Map<String, Object> request) {
        AiUsageSummary usage = new AiUsageSummary();
        if (response != null && response.get("usageMetadata") instanceof Map) {
            Map<String, Object> usageMetadata = (Map<String, Object>) response.get("usageMetadata");
            int promptTokens = toNumber(usageMetadata.get("promptTokenCount")).intValue();
            int completionTokens = toNumber(usageMetadata.get("candidatesTokenCount")).intValue()
                    + toNumber(usageMetadata.get("thoughtsTokenCount")).intValue();
            int totalTokens = toNumber(usageMetadata.get("totalTokenCount")).intValue();
            usage.setPromptTokens(promptTokens);
            usage.setCompletionTokens(completionTokens);
            usage.setTotalTokens(totalTokens > 0 ? totalTokens : (promptTokens + completionTokens));
        }
        if (usage.getTotalTokens() == 0) {
            return estimateGeminiUsage(request);
        }
        return usage;
    }

    /**
     * 估算 Gemini generateContent 请求的 token 用量。
     */
    @SuppressWarnings("unchecked")
    private AiUsageSummary estimateGeminiUsage(Map<String, Object> request) {
        AiUsageSummary usage = new AiUsageSummary();
        if (request == null) {
            usage.setPromptTokens(0);
            usage.setCompletionTokens(0);
            usage.setTotalTokens(0);
            return usage;
        }
        int promptTokens = estimateTokensByObject(request.get("contents"));
        int completionTokens = 0;
        Object generationConfigObj = request.get("generationConfig");
        if (generationConfigObj instanceof Map) {
            Map<String, Object> generationConfig = (Map<String, Object>) generationConfigObj;
            completionTokens = Math.max(0, toNumber(generationConfig.get("maxOutputTokens")).intValue());
        }
        usage.setPromptTokens(promptTokens);
        usage.setCompletionTokens(completionTokens);
        usage.setTotalTokens(promptTokens + completionTokens);
        return usage;
    }

    /**
     * 估算任意输入对象的 token 数。
     */
    private int estimateTokensByObject(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof String) {
            return estimateTokensByChars(((String) value).length());
        }
        if (value instanceof List) {
            int total = 0;
            for (Object item : (List<?>) value) {
                total += estimateTokensByObject(item);
            }
            return total;
        }
        if (value instanceof Map) {
            return estimateTokensByChars(JacksonUtils.toJson(value).length());
        }
        return estimateTokensByChars(String.valueOf(value).length());
    }

    /**
     * 按字符数近似估算 token。
     */
    private int estimateTokensByChars(int chars) {
        if (chars <= 0) {
            return 0;
        }
        return Math.max(1, (chars + 3) / 4);
    }

    private AiUsageSummary usageFromResponse(AiChatRequest request, AiChatResponse response) {
        AiUsageSummary usage = new AiUsageSummary();
        usage.setPromptTokens(response.getPromptTokens() == null ? 0 : response.getPromptTokens());
        usage.setCompletionTokens(response.getCompletionTokens() == null ? 0 : response.getCompletionTokens());
        usage.setTotalTokens(response.getTotalTokens() == null ? 0 : response.getTotalTokens());
        if (usage.getTotalTokens() == 0) {
            return aiChatService.estimateUsage(request, response.getContent());
        }
        return usage;
    }

    private void appendContent(String payload, StringBuilder contentBuilder) {
        if (StringUtils.isBlank(payload) || "[DONE]".equals(payload)) {
            return;
        }
        try {
            String content = JacksonUtils.getObjectMapper().readTree(payload).at("/choices/0/delta/content").asText("");
            if (StringUtils.isNotBlank(content)) {
                contentBuilder.append(content);
            }
        } catch (Exception ignore) {
        }
    }

    private String extractDeltaFromChatStreamPayload(String payload) {
        if (StringUtils.isBlank(payload) || "[DONE]".equals(payload)) {
            return "";
        }
        try {
            return JacksonUtils.getObjectMapper().readTree(payload).at("/choices/0/delta/content").asText("");
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 提取 Responses 流式协议中的文本增量。
     */
    private String extractDeltaFromResponsesPayload(String payload) {
        if (StringUtils.isBlank(payload) || "[DONE]".equals(payload)) {
            return "";
        }
        try {
            if (!"response.output_text.delta".equals(JacksonUtils.getObjectMapper().readTree(payload).path("type").asText(""))) {
                return "";
            }
            return JacksonUtils.getObjectMapper().readTree(payload).path("delta").asText("");
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 记录首字时间戳。
     */
    private void recordFirstTokenAt(AtomicLong firstTokenAt, long startAt, String delta) {
        if (firstTokenAt.get() >= 0 || StringUtils.isBlank(delta)) {
            return;
        }
        if (firstTokenAt.compareAndSet(-1L, System.currentTimeMillis())) {
            log.info("ai openapi first token captured, first_token_ms={}", firstTokenAt.get() - startAt);
        }
    }

    /**
     * 计算首字耗时。
     */
    private Long resolveFirstTokenMs(AtomicLong firstTokenAt, long startAt) {
        return firstTokenAt.get() < 0 ? null : (firstTokenAt.get() - startAt);
    }

    private void saveSuccessLog(String requestId, AiUserApiKey apiKey, AiProviderConfig providerConfig, String model,
                                AiUsageSummary usage, BigDecimal amount, Long walletLogId, String endpoint,
                                Long totalMs, Long firstTokenMs) {
        AiApiCallLog log = new AiApiCallLog();
        log.setRequestId(requestId);
        log.setUserId(apiKey.getUserId());
        log.setApiKeyId(apiKey.getId());
        log.setProviderConfigId(providerConfig.getId());
        log.setEndpoint(endpoint);
        log.setModel(model);
        log.setPromptTokens(usage.getPromptTokens());
        log.setCompletionTokens(usage.getCompletionTokens());
        log.setTotalTokens(usage.getTotalTokens());
        log.setAmount(amount);
        log.setStatus(AiApiCallLogEnums.Status.SUCCESS.getId());
        log.setWalletLogId(walletLogId);
        log.setTotalMs(totalMs);
        log.setFirstTokenMs(firstTokenMs);
        log.setCreatedTime(LocalDateTime.now());
        log.setUpdatedTime(LocalDateTime.now());
        aiApiCallLogService.save(log);
    }

    private void saveFailedLog(String requestId, AiUserApiKey apiKey, AiProviderConfig providerConfig, String model,
                               String errorMessage, String endpoint, Long totalMs, Long firstTokenMs) {
        AiApiCallLog log = new AiApiCallLog();
        log.setRequestId(requestId);
        log.setUserId(apiKey.getUserId());
        log.setApiKeyId(apiKey.getId());
        log.setProviderConfigId(providerConfig.getId());
        log.setEndpoint(endpoint);
        log.setModel(model);
        log.setAmount(BigDecimal.ZERO);
        log.setStatus(AiApiCallLogEnums.Status.FAILED.getId());
        log.setErrorMessage(StringUtils.isBlank(errorMessage) ? "AI request failed" : errorMessage);
        log.setTotalMs(totalMs);
        log.setFirstTokenMs(firstTokenMs);
        log.setCreatedTime(LocalDateTime.now());
        log.setUpdatedTime(LocalDateTime.now());
        aiApiCallLogService.save(log);
    }

    private Map<String, Object> buildOpenAiResponse(String requestId, String model, String content, AiUsageSummary usage) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", content);

        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);
        choice.put("message", message);
        choice.put("finish_reason", "stop");

        Map<String, Object> response = new HashMap<>();
        response.put("id", "chatcmpl-" + requestId);
        response.put("object", "chat.completion");
        response.put("created", System.currentTimeMillis() / 1000);
        response.put("model", model);
        response.put("choices", List.of(choice));

        Map<String, Object> usageMap = new HashMap<>();
        usageMap.put("prompt_tokens", usage.getPromptTokens());
        usageMap.put("completion_tokens", usage.getCompletionTokens());
        usageMap.put("total_tokens", usage.getTotalTokens());
        response.put("usage", usageMap);
        return response;
    }

    private Map<String, Object> buildResponsesResponse(Map<String, Object> chatResponse) {
        String id = String.valueOf(chatResponse.getOrDefault("id", "resp_" + IDGeneratorUtils.uuid32()));
        String model = String.valueOf(chatResponse.getOrDefault("model", ""));
        String text = extractTextFromChatResponse(chatResponse);
        Map<String, Object> usageMap = chatResponse.get("usage") instanceof Map
                ? new HashMap<>((Map<String, Object>) chatResponse.get("usage"))
                : new HashMap<>();

        String outputItemId = "msg_" + IDGeneratorUtils.uuid32();
        Map<String, Object> outputItem = buildAssistantOutputItem(outputItemId, text);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id.replace("chatcmpl-", "resp_"));
        response.put("object", "response");
        response.put("created_at", System.currentTimeMillis() / 1000);
        response.put("status", "completed");
        response.put("model", model);
        response.put("error", null);
        response.put("incomplete_details", null);
        response.put("output", Collections.singletonList(outputItem));
        response.put("output_text", text);
        response.put("usage", buildResponsesUsage(usageMap));
        return response;
    }

    private String extractTextFromChatResponse(Map<String, Object> chatResponse) {
        Object choicesObj = chatResponse.get("choices");
        if (!(choicesObj instanceof List) || ((List<?>) choicesObj).isEmpty()) {
            return "";
        }
        Object firstChoice = ((List<?>) choicesObj).get(0);
        if (!(firstChoice instanceof Map)) {
            return "";
        }
        Object messageObj = ((Map<?, ?>) firstChoice).get("message");
        if (!(messageObj instanceof Map)) {
            return "";
        }
        Object content = ((Map<?, ?>) messageObj).get("content");
        return content == null ? "" : String.valueOf(content);
    }

    private Map<String, Object> buildResponsesUsage(Map<String, Object> chatUsageMap) {
        Number promptTokens = toNumber(chatUsageMap.get("prompt_tokens"));
        Number completionTokens = toNumber(chatUsageMap.get("completion_tokens"));
        Number totalTokens = toNumber(chatUsageMap.get("total_tokens"));
        Map<String, Object> usage = new HashMap<>();
        usage.put("input_tokens", promptTokens.intValue());
        usage.put("output_tokens", completionTokens.intValue());
        usage.put("total_tokens", totalTokens.intValue());
        return usage;
    }

    private Number toNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        }
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ex) {
            return 0;
        }
    }

    private Map<String, Object> buildResponsesDeltaEvent(String delta) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.output_text.delta");
        event.put("delta", delta);
        event.put("output_index", 0);
        event.put("content_index", 0);
        return event;
    }

    private Map<String, Object> buildResponsesOutputTextDoneEvent(String text) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.output_text.done");
        event.put("text", text == null ? "" : text);
        event.put("output_index", 0);
        event.put("content_index", 0);
        return event;
    }

    private Map<String, Object> buildResponsesCreatedEvent(String responseId, String model, long createdAt) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.created");
        event.put("response", buildResponsesSkeleton(responseId, model, createdAt, "in_progress"));
        return event;
    }

    private Map<String, Object> buildResponsesInProgressEvent(String responseId, String model, long createdAt) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.in_progress");
        event.put("response", buildResponsesSkeleton(responseId, model, createdAt, "in_progress"));
        return event;
    }

    private Map<String, Object> buildResponsesOutputItemAddedEvent(String responseId, String outputItemId) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.output_item.added");
        event.put("response_id", responseId);
        event.put("output_index", 0);
        event.put("item", buildAssistantOutputItem(outputItemId, ""));
        return event;
    }

    private Map<String, Object> buildResponsesContentPartAddedEvent(String responseId, String outputItemId) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.content_part.added");
        event.put("response_id", responseId);
        event.put("output_index", 0);
        event.put("item_id", outputItemId);
        event.put("content_index", 0);
        Map<String, Object> part = new HashMap<>();
        part.put("type", "output_text");
        part.put("text", "");
        event.put("part", part);
        return event;
    }

    private Map<String, Object> buildResponsesContentPartDoneEvent(String responseId, String outputItemId, String text) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.content_part.done");
        event.put("response_id", responseId);
        event.put("output_index", 0);
        event.put("item_id", outputItemId);
        event.put("content_index", 0);
        Map<String, Object> part = new HashMap<>();
        part.put("type", "output_text");
        part.put("text", text == null ? "" : text);
        event.put("part", part);
        return event;
    }

    private Map<String, Object> buildResponsesOutputItemDoneEvent(String responseId, String outputItemId, String text) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.output_item.done");
        event.put("response_id", responseId);
        event.put("output_index", 0);
        event.put("item", buildAssistantOutputItem(outputItemId, text));
        return event;
    }

    private Map<String, Object> buildResponsesFailedEvent(String responseId, String message) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.failed");
        event.put("response_id", responseId);
        Map<String, Object> error = new HashMap<>();
        error.put("code", "server_error");
        error.put("message", StringUtils.isBlank(message) ? "stream failed" : message);
        event.put("error", error);
        return event;
    }

    private Map<String, Object> buildResponsesCompletedEvent(String responseId, String model, long createdAt,
                                                             String outputItemId, String text) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "response.completed");
        Map<String, Object> response = buildResponsesSkeleton(responseId, model, createdAt, "completed");
        response.put("output", Collections.singletonList(buildAssistantOutputItem(outputItemId, text)));
        response.put("output_text", text == null ? "" : text);
        event.put("response", response);
        return event;
    }

    private Map<String, Object> buildResponsesSkeleton(String responseId, String model, long createdAt, String status) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", responseId);
        response.put("object", "response");
        response.put("created_at", createdAt);
        response.put("status", status);
        response.put("model", model);
        response.put("error", null);
        response.put("incomplete_details", null);
        response.put("output", new ArrayList<>());
        response.put("output_text", "");
        return response;
    }

    private Map<String, Object> buildAssistantOutputItem(String outputItemId, String text) {
        Map<String, Object> outputTextBlock = new HashMap<>();
        outputTextBlock.put("type", "output_text");
        outputTextBlock.put("text", text == null ? "" : text);

        Map<String, Object> outputItem = new HashMap<>();
        outputItem.put("id", outputItemId);
        outputItem.put("type", "message");
        outputItem.put("status", "completed");
        outputItem.put("role", "assistant");
        outputItem.put("content", Collections.singletonList(outputTextBlock));
        return outputItem;
    }

    private Map<String, Object> parseConfig(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return new HashMap<>();
        }
        Map<String, Object> map = JacksonUtils.toBean(configJson, Map.class);
        return map == null ? new HashMap<>() : map;
    }

    /**
     * 读取上游 API Key。
     */
    private String pickApiKey(AiProviderConfig providerConfig, Map<String, Object> config) {
        String apiKey = pickString(config, "apiKey", providerConfig.getApiKeyRef());
        return apiKey == null ? "" : apiKey;
    }

    /**
     * 读取上游基础地址。
     */
    private String pickBaseUrl(AiProviderConfig providerConfig, Map<String, Object> config) {
        String baseUrl = pickString(config, "baseUrl", providerConfig.getBaseUrl());
        return baseUrl == null ? "" : baseUrl;
    }

    /**
     * 拼接基础地址与路径。
     */
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

    /**
     * 追加 URL 查询参数，避免手工拼接导致格式错误。
     */
    private String appendQueryParam(String url, String key, String value) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return url;
        }
        return org.springframework.web.util.UriComponentsBuilder.fromUriString(url)
                .queryParam(key, value)
                .build(true)
                .toUriString();
    }

    /**
     * 校验当前路由命中的配置必须是 Gemini。
     */
    private void assertGeminiProvider(AiProviderConfig providerConfig, String model) {
        Assert.notNull(providerConfig, "providerConfig不能为空");
        Assert.isTrue("gemini".equalsIgnoreCase(providerConfig.getProvider()),
                "模型[" + model + "]当前路由提供方不是gemini");
    }

    private Integer pickInteger(Map<String, Object> config, String key, Integer fallback) {
        Object value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (Exception ignore) {
            }
        }
        return fallback;
    }

    private Boolean pickBoolean(Map<String, Object> config, String key, Boolean fallback) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return value == null ? fallback : Boolean.parseBoolean(value.toString());
    }

    private BigDecimal pickBigDecimal(Map<String, Object> config, String key, BigDecimal fallback) {
        Object value = config.get(key);
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        if (value != null) {
            try {
                return new BigDecimal(value.toString());
            } catch (Exception ignore) {
            }
        }
        return fallback;
    }

    /**
     * 解析本次请求实际命中的提供方配置。
     *
     * @param response 响应对象
     * @param request 请求对象
     * @param fallback 兜底配置
     * @return 实际命中的提供方配置
     */
    private AiProviderConfig resolveActualProviderConfig(AiChatResponse response, AiChatRequest request, AiProviderConfig fallback) {
        String providerCode = response != null && StringUtils.isNotBlank(response.getProviderCode())
                ? response.getProviderCode()
                : request == null ? null : request.getProviderCode();
        if (StringUtils.isBlank(providerCode)) {
            return fallback;
        }
        AiProviderConfig providerConfig = aiProviderConfigService.getOne(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getCode, providerCode)
                .last("limit 1"));
        return providerConfig == null ? fallback : providerConfig;
    }

    /**
     * 解析本次请求实际命中的模型。
     *
     * @param request 请求对象
     * @param providerConfig 提供方配置
     * @return 模型名
     */
    private String resolveActualModel(AiChatRequest request, AiProviderConfig providerConfig) {
        if (request != null && StringUtils.isNotBlank(request.getModel())) {
            return request.getModel();
        }
        return providerConfig == null ? null : providerConfig.getDefaultModel();
    }

    /**
     * 用实际命中的提供方刷新计费配置。
     *
     * @param billingPlan 计费方案
     * @param providerConfig 提供方配置
     * @param model 模型
     */
    private void refreshBillingPlanProviderConfig(BillingPlan billingPlan, AiProviderConfig providerConfig, String model) {
        if (billingPlan == null || providerConfig == null) {
            return;
        }
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        ModelPricing modelPricing = resolveModelPricing(providerConfig.getId(), model);
        billingPlan.providerConfigId = providerConfig.getId();
        billingPlan.walletTypeId = pickInteger(config, "billingWalletTypeId", 1);
        billingPlan.promptPricePer1kTokens = pickBigDecimal(config, "promptPricePer1kTokens", BigDecimal.ZERO);
        billingPlan.completionPricePer1kTokens = pickBigDecimal(config, "completionPricePer1kTokens", billingPlan.promptPricePer1kTokens);
        billingPlan.fixedRequestPrice = modelPricing.fixedRequestPrice();
        billingPlan.fixedRequestBilling = billingPlan.fixedRequestPrice.compareTo(BigDecimal.ZERO) > 0;
        billingPlan.estimatedModel = StringUtils.isNotBlank(model) ? model : providerConfig.getDefaultModel();
        billingPlan.modelPricing = modelPricing;
        billingPlan.billingEnabled = modelPricing.hasSplitPrice() || billingPlan.fixedRequestBilling || pickBoolean(config, "billingEnabled", false);
        if (billingPlan.memberLimitDecision != null
                && billingPlan.memberLimitDecision.isMemberByRequest()
                && !billingPlan.memberLimitDecision.isOverLimit()) {
            billingPlan.billingEnabled = false;
        }
    }

    /**
     * 读取字符串配置。
     */
    private String pickString(Map<String, Object> config, String key, String fallback) {
        Object value = config.get(key);
        return value == null ? fallback : String.valueOf(value);
    }

    /**
     * 构建支持代理的 RestTemplate。
     */
    private RestTemplate buildRestTemplate(Integer timeoutMs, Proxy proxy) {
        int timeout = timeoutMs == null || timeoutMs <= 0 ? 60000 : timeoutMs;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        if (proxy != null) {
            factory.setProxy(proxy);
        }
        return new RestTemplate(factory);
    }

    /**
     * 构建上游代理配置。
     */
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

    /**
     * 尽量提取上游错误正文，便于排查问题。
     */
    private String extractUpstreamErrorMessage(Throwable ex) {
        if (ex instanceof HttpStatusCodeException) {
            String responseBody = ((HttpStatusCodeException) ex).getResponseBodyAsString();
            if (StringUtils.isNotBlank(responseBody)) {
                return responseBody;
            }
        }
        String message = ex == null ? null : ex.getMessage();
        return StringUtils.isBlank(message) ? "unknown upstream error" : message;
    }

    private static final class BillingPlan {
        private Long userId;
        private Long apiKeyId;
        private Long providerConfigId;
        private boolean billingEnabled;
        private boolean fixedRequestBilling;
        private Integer walletTypeId;
        private BigDecimal fixedRequestPrice;
        private BigDecimal promptPricePer1kTokens;
        private BigDecimal completionPricePer1kTokens;
        private String estimatedModel;
        private AiUsageSummary estimatedUsage;
        private ModelPricing modelPricing;
        private AiMemberRequestLimitService.Decision memberLimitDecision;
    }

    private static final class ModelPricing {
        private final String modelName;
        private final BigDecimal promptPricePer1kTokens;
        private final BigDecimal completionPricePer1kTokens;

        private ModelPricing(String modelName, BigDecimal promptPricePer1kTokens, BigDecimal completionPricePer1kTokens) {
            this.modelName = modelName;
            this.promptPricePer1kTokens = promptPricePer1kTokens;
            this.completionPricePer1kTokens = completionPricePer1kTokens;
        }

        private static ModelPricing empty() {
            return new ModelPricing(null, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        private static ModelPricing split(String modelName, BigDecimal promptPricePer1kTokens, BigDecimal completionPricePer1kTokens) {
            return new ModelPricing(modelName, promptPricePer1kTokens, completionPricePer1kTokens);
        }

        private boolean hasSplitPrice() {
            return promptPricePer1kTokens.compareTo(BigDecimal.ZERO) > 0
                    || completionPricePer1kTokens.compareTo(BigDecimal.ZERO) > 0;
        }

        private boolean hasFixedRequestPrice() {
            return fixedRequestPrice().compareTo(BigDecimal.ZERO) > 0;
        }

        private BigDecimal fixedRequestPrice() {
            if (promptPricePer1kTokens.compareTo(BigDecimal.ZERO) > 0) {
                return promptPricePer1kTokens;
            }
            if (completionPricePer1kTokens.compareTo(BigDecimal.ZERO) > 0) {
                return completionPricePer1kTokens;
            }
            return BigDecimal.ZERO;
        }

        private boolean matches(String model) {
            if (StringUtils.isBlank(modelName) || StringUtils.isBlank(model)) {
                return false;
            }
            return modelName.equals(model);
        }
    }

    /**
     * 携带原始文件名的二进制资源。
     */
    private static final class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        private NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return StringUtils.isBlank(filename) ? "upload.bin" : filename;
        }
    }
}
