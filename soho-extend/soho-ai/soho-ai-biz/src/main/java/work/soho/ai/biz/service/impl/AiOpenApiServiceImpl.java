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
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiOpenApiService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.service.WalletInfoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class AiOpenApiServiceImpl implements AiOpenApiService {
    private final AiUserApiKeyService aiUserApiKeyService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiChatService aiChatService;
    private final AiApiCallLogService aiApiCallLogService;
    private final WalletInfoService walletInfoService;
    private final WalletInfoApiService walletInfoApiService;

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
                    aiUserApiKeyService.touchLastUsedTime(apiKey.getId());
                    saveSuccessLog(requestId, apiKey, providerConfig, targetModel, usage, amount, walletLogId, "/ai/guest/openai/v1/chat/completions");
                })
                .doOnError(ex -> saveFailedLog(requestId, apiKey, providerConfig, targetModel, ex.getMessage(), "/ai/guest/openai/v1/chat/completions"));
    }

    private String extractBearerToken(String authorization) {
        Assert.hasText(authorization, "Authorization不能为空");
        String prefix = "Bearer ";
        Assert.isTrue(authorization.startsWith(prefix), "Authorization格式错误");
        return authorization.substring(prefix.length()).trim();
    }

    private AiProviderConfig requireProviderConfig(String model) {
        Assert.hasText(model, "model不能为空");
        return aiChatService.resolveProviderConfig(null, model);
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

        AiUsageSummary estimatedUsage = aiChatService.estimateUsage(aiChatRequest, "");
        int expectedCompletionTokens = request.getMaxTokens() == null ? 0 : Math.max(request.getMaxTokens(), 0);
        estimatedUsage.setCompletionTokens(expectedCompletionTokens);
        estimatedUsage.setTotalTokens(estimatedUsage.getPromptTokens() + expectedCompletionTokens);
        billingPlan.estimatedUsage = estimatedUsage;
        return billingPlan;
    }

    private void preCheckBalance(BillingPlan billingPlan) {
        if (!billingPlan.billingEnabled) {
            return;
        }
        Assert.notNull(billingPlan.walletTypeId, "billingWalletTypeId未配置");
        BigDecimal estimatedAmount = calculateAmount(billingPlan, billingPlan.estimatedUsage, billingPlan.estimatedModel);
        if (estimatedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        WalletInfo walletInfo = walletInfoService.getByUserIdAndType(billingPlan.userId, billingPlan.walletTypeId);
        Assert.isTrue(walletInfo.getAmount().compareTo(estimatedAmount) >= 0, "钱包余额不足");
    }

    private Long chargeIfNeeded(BillingPlan billingPlan, String requestId, AiUsageSummary usage, BigDecimal amount, String model) {
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
            return promptCost.add(completionCost).setScale(4, RoundingMode.HALF_UP);
        }
        BigDecimal promptCost = billingPlan.promptPricePer1kTokens
                .multiply(BigDecimal.valueOf(usage.getPromptTokens() == null ? 0 : usage.getPromptTokens()))
                .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
        BigDecimal completionCost = billingPlan.completionPricePer1kTokens
                .multiply(BigDecimal.valueOf(usage.getCompletionTokens() == null ? 0 : usage.getCompletionTokens()))
                .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
        return promptCost.add(completionCost).setScale(4, RoundingMode.HALF_UP);
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
