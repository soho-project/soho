package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiChatResponse;
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
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Service
@RequiredArgsConstructor
public class AiOpenApiServiceImpl implements AiOpenApiService {
    private static final String CLIENT_ERROR_MESSAGE = "临时错误，如果长期错误请联系管理员";
    private final AiUserApiKeyService aiUserApiKeyService;
    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiChatService aiChatService;
    private final AiApiCallLogService aiApiCallLogService;
    private final WalletInfoService walletInfoService;
    private final WalletInfoApiService walletInfoApiService;
    private final AiMemberRequestLimitService aiMemberRequestLimitService;
    private final AiUserMemberCardService aiUserMemberCardService;

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

    @Override
    public Map<String, Object> chatCompletions(String authorization, OpenAiChatCompletionRequest request) {
        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(request.getModel());
        AiChatRequest aiChatRequest = convertRequest(providerConfig.getCode(), request);
        BillingPlan billingPlan = buildBillingPlan(apiKey, providerConfig, aiChatRequest, request);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        try {
            AiChatResponse response = aiChatService.chat(providerConfig, aiChatRequest);
            AiUsageSummary usage = usageFromResponse(aiChatRequest, response);
            BigDecimal amount = calculateAmount(billingPlan, usage, response.getModel());
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, response.getModel());
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            saveSuccessLog(requestId, apiKey, providerConfig, response.getModel(), usage, amount, walletLogId, "/ai/guest/openai/v1/chat/completions");
            return buildOpenAiResponse(requestId, response.getModel(), response.getContent(), usage);
        } catch (RuntimeException ex) {
            saveFailedLog(requestId, apiKey, providerConfig, request.getModel(), ex.getMessage(), "/ai/guest/openai/v1/chat/completions");
            throw ex;
        }
    }

    @Override
    public Flux<String> streamChatCompletions(String authorization, OpenAiChatCompletionRequest request) {
        AiUserApiKey apiKey = aiUserApiKeyService.requireByPlaintextKey(extractBearerToken(authorization));
        AiProviderConfig providerConfig = requireProviderConfig(request.getModel());
        AiChatRequest aiChatRequest = convertRequest(providerConfig.getCode(), request);
        BillingPlan billingPlan = buildBillingPlan(apiKey, providerConfig, aiChatRequest, request);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        StringBuilder contentBuilder = new StringBuilder();
        String targetModel = StringUtils.isBlank(request.getModel()) ? providerConfig.getDefaultModel() : request.getModel();

        log.info("streamChatCompletions: {}",  aiChatRequest);
        return aiChatService.streamChat(providerConfig, aiChatRequest)
                .doOnNext(payload -> appendContent(payload, contentBuilder))
                .doOnComplete(() -> {
                    AiUsageSummary usage = aiChatService.estimateUsage(aiChatRequest, contentBuilder.toString());
                    BigDecimal amount = calculateAmount(billingPlan, usage, targetModel);
                    Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, targetModel);
                    aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
                    aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
                    saveSuccessLog(requestId, apiKey, providerConfig, targetModel, usage, amount, walletLogId, "/ai/guest/openai/v1/chat/completions");
                })
                .doOnError(ex -> saveFailedLog(requestId, apiKey, providerConfig, targetModel, ex.getMessage(), "/ai/guest/openai/v1/chat/completions"));
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
        AiChatRequest aiChatRequest = convertNativeResponsesRequest(providerConfig.getCode(), request, false);
        OpenAiChatCompletionRequest pricingRequest = convertResponsesRequest(request);
        BillingPlan billingPlan = buildBillingPlan(apiKey, providerConfig, aiChatRequest, pricingRequest);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        try {
            AiChatResponse response = aiChatService.chat(providerConfig, aiChatRequest);
            AiUsageSummary usage = usageFromResponse(aiChatRequest, response);
            BigDecimal amount = calculateAmount(billingPlan, usage, response.getModel());
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, response.getModel());
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
            saveSuccessLog(requestId, apiKey, providerConfig, response.getModel(), usage, amount, walletLogId, "/ai/guest/openai/v1/responses");

            Map<String, Object> result = parseNativeResponsesResult(response.getRaw());
            if (result.isEmpty()) {
                Map<String, Object> chatResponse = buildOpenAiResponse(requestId, response.getModel(), response.getContent(), usage);
                result = buildResponsesResponse(chatResponse);
            }
            log.info("responses 最终返回: {}", JacksonUtils.toJson(result));
            return result;
        } catch (RuntimeException ex) {
            saveFailedLog(requestId, apiKey, providerConfig, request.getModel(), ex.getMessage(), "/ai/guest/openai/v1/responses");
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
        AiChatRequest aiChatRequest = convertNativeResponsesRequest(providerConfig.getCode(), request, true);
        OpenAiChatCompletionRequest pricingRequest = convertResponsesRequest(request);
        pricingRequest.setStream(true);
        BillingPlan billingPlan = buildBillingPlan(apiKey, providerConfig, aiChatRequest, pricingRequest);
        preCheckBalance(billingPlan);

        String requestId = IDGeneratorUtils.uuid32();
        String targetModel = StringUtils.isBlank(request.getModel()) ? providerConfig.getDefaultModel() : request.getModel();
        StringBuilder contentBuilder = new StringBuilder();
        AtomicReference<String> completedPayloadRef = new AtomicReference<>("");

        return aiChatService.streamChat(providerConfig, aiChatRequest)
                .filter(payload -> StringUtils.isNotBlank(payload) && !"[DONE]".equals(payload))
                .doOnNext(payload -> {
                    appendResponsesTextDelta(payload, contentBuilder);
                    captureCompletedPayload(payload, completedPayloadRef);
                })
                .doOnComplete(() -> {
                    AiUsageSummary usage = aiChatService.estimateUsage(aiChatRequest, contentBuilder.toString());
                    BigDecimal amount = calculateAmount(billingPlan, usage, targetModel);
                    Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, amount, targetModel);
                    aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
                    aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
                    saveSuccessLog(requestId, apiKey, providerConfig, targetModel, usage, amount, walletLogId, "/ai/guest/openai/v1/responses");
                    if (StringUtils.isNotBlank(completedPayloadRef.get())) {
                        log.info("responses(stream) 最终返回(完成): {}", completedPayloadRef.get());
                    } else {
                        log.info("responses(stream) 最终返回(完成): {}", buildStreamCompletedSummary(targetModel, contentBuilder.toString()));
                    }
                })
                .doOnError(ex -> {
                    saveFailedLog(requestId, apiKey, providerConfig, targetModel, ex.getMessage(), "/ai/guest/openai/v1/responses");
                    String failedPayload = JacksonUtils.toJson(buildResponsesFailedEvent("resp_" + requestId, CLIENT_ERROR_MESSAGE));
                    log.warn("responses(stream) 最终返回(失败): {}", failedPayload);
                });
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

    private BigDecimal calculateAmount(BillingPlan billingPlan, AiUsageSummary usage, String model) {
        if (!billingPlan.billingEnabled || usage == null) {
            return BigDecimal.ZERO;
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

    private void saveSuccessLog(String requestId, AiUserApiKey apiKey, AiProviderConfig providerConfig, String model,
                                AiUsageSummary usage, BigDecimal amount, Long walletLogId, String endpoint) {
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
        log.setCreatedTime(LocalDateTime.now());
        log.setUpdatedTime(LocalDateTime.now());
        aiApiCallLogService.save(log);
    }

    private void saveFailedLog(String requestId, AiUserApiKey apiKey, AiProviderConfig providerConfig, String model,
                               String errorMessage, String endpoint) {
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

    private static final class BillingPlan {
        private Long userId;
        private Long apiKeyId;
        private Long providerConfigId;
        private boolean billingEnabled;
        private Integer walletTypeId;
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

        private boolean matches(String model) {
            if (StringUtils.isBlank(modelName) || StringUtils.isBlank(model)) {
                return false;
            }
            return modelName.equals(model);
        }
    }
}
