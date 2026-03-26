package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.domain.AiChatSessionMessage;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.dto.AiUserModelView;
import work.soho.ai.biz.enums.AiApiCallLogEnums;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.UserAiChatRequest;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiChatSessionMessageService;
import work.soho.ai.biz.service.AiChatSessionService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiUserWebChatService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiUserWebChatServiceImpl implements AiUserWebChatService {
    private static final Long USER_WEB_CHAT_API_KEY_ID = 0L;
    private static final String USER_WEB_CHAT_ENDPOINT = "/ai/user/chat";

    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiChatService aiChatService;
    private final AiApiCallLogService aiApiCallLogService;
    private final AiChatSessionService aiChatSessionService;
    private final AiChatSessionMessageService aiChatSessionMessageService;
    private final WalletInfoService walletInfoService;
    private final WalletInfoApiService walletInfoApiService;

    @Override
    public List<AiUserModelView> listModels() {
        List<AiProviderConfig> providerConfigs = aiProviderConfigService.list(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getStatus, 1)
                .orderByDesc(AiProviderConfig::getId));
        List<AiUserModelView> list = new ArrayList<>();
        for (AiProviderConfig providerConfig : providerConfigs) {
            AiUserModelView item = new AiUserModelView();
            item.setProviderConfigId(providerConfig.getId());
            item.setProviderCode(providerConfig.getCode());
            item.setProvider(providerConfig.getProvider());
            item.setDefaultModel(providerConfig.getDefaultModel());
            item.setModels(resolveModels(providerConfig));
            list.add(item);
        }
        return list;
    }

    private List<String> resolveModels(AiProviderConfig providerConfig) {
        List<AiModelInfo> modelInfos = aiProviderModelRelService.listEnabledModelsByProviderConfigId(providerConfig.getId());
        if (!modelInfos.isEmpty()) {
            List<String> models = new ArrayList<>();
            for (AiModelInfo item : modelInfos) {
                if (StringUtils.isNotBlank(item.getModelName())) {
                    models.add(item.getModelName());
                }
            }
            return models;
        }
        return AiProviderModelUtils.extractModels(providerConfig);
    }

    @Override
    public List<AiChatSession> listSessions(Long userId) {
        return aiChatSessionService.list(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getUserId, userId)
                .orderByDesc(AiChatSession::getUpdatedTime)
                .orderByDesc(AiChatSession::getId));
    }

    @Override
    public List<AiChatSessionMessage> listSessionMessages(Long userId, Long sessionId) {
        aiChatSessionService.requireOwnedSession(userId, sessionId);
        return aiChatSessionMessageService.list(new LambdaQueryWrapper<AiChatSessionMessage>()
                .eq(AiChatSessionMessage::getSessionId, sessionId)
                .orderByAsc(AiChatSessionMessage::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSession(Long userId, Long sessionId) {
        aiChatSessionService.requireOwnedSession(userId, sessionId);
        aiChatSessionMessageService.remove(new LambdaQueryWrapper<AiChatSessionMessage>()
                .eq(AiChatSessionMessage::getSessionId, sessionId));
        return aiChatSessionService.removeById(sessionId);
    }

    @Override
    public AiChatSession renameSession(Long userId, Long sessionId, String title) {
        Assert.isTrue(StringUtils.isNotBlank(title), "title不能为空");
        AiChatSession session = aiChatSessionService.requireOwnedSession(userId, sessionId);
        session.setTitle(title);
        session.setUpdatedTime(LocalDateTime.now());
        aiChatSessionService.updateById(session);
        return session;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiChatResponse chat(Long userId, UserAiChatRequest request) {
        AiChatSession session = prepareSession(userId, request);
        AiChatRequest aiChatRequest = toAiChatRequest(request, session);
        AiProviderConfig providerConfig = aiChatService.resolveProviderConfig(aiChatRequest.getProviderCode(), aiChatRequest.getModel());
        BillingPlan billingPlan = buildBillingPlan(userId, providerConfig, aiChatRequest);
        preCheckBalance(billingPlan);
        String requestId = IDGeneratorUtils.uuid32();
        try {
            persistUserMessage(session.getId(), aiChatRequest);
            AiChatResponse response = aiChatService.chat(providerConfig, aiChatRequest);
            AiUsageSummary usage = usageFromResponse(aiChatRequest, response);
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, response.getModel());
            saveSuccessLog(requestId, userId, providerConfig, response.getModel(), usage,
                    calculateAmount(billingPlan, usage, response.getModel()), walletLogId, USER_WEB_CHAT_ENDPOINT);
            persistAssistantMessage(session, response.getContent(), request);
            return response;
        } catch (RuntimeException ex) {
            saveFailedLog(requestId, userId, providerConfig,
                    StringUtils.isNotBlank(aiChatRequest.getModel()) ? aiChatRequest.getModel() : providerConfig.getDefaultModel(),
                    ex.getMessage(), USER_WEB_CHAT_ENDPOINT);
            throw ex;
        }
    }

    @Override
    public Flux<String> streamChat(Long userId, UserAiChatRequest request) {
        AiChatSession session = prepareSession(userId, request);
        AiChatRequest aiChatRequest = toAiChatRequest(request, session);
        AiProviderConfig providerConfig = aiChatService.resolveProviderConfig(aiChatRequest.getProviderCode(), aiChatRequest.getModel());
        BillingPlan billingPlan = buildBillingPlan(userId, providerConfig, aiChatRequest);
        preCheckBalance(billingPlan);
        String requestId = IDGeneratorUtils.uuid32();
        persistUserMessage(session.getId(), aiChatRequest);
        StringBuilder assistantContent = new StringBuilder();
        return aiChatService.streamChat(providerConfig, aiChatRequest)
                .doOnNext(payload -> appendAssistantDelta(payload, assistantContent))
                .doOnComplete(() -> {
                    AiUsageSummary usage = aiChatService.estimateUsage(aiChatRequest, assistantContent.toString());
                    String model = StringUtils.isNotBlank(aiChatRequest.getModel()) ? aiChatRequest.getModel() : providerConfig.getDefaultModel();
                    BigDecimal amount = calculateAmount(billingPlan, usage, model);
                    Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, model);
                    saveSuccessLog(requestId, userId, providerConfig, model, usage, amount, walletLogId, USER_WEB_CHAT_ENDPOINT);
                    persistAssistantMessage(session, assistantContent.toString(), request);
                })
                .doOnError(ex -> saveFailedLog(requestId, userId, providerConfig,
                        StringUtils.isNotBlank(aiChatRequest.getModel()) ? aiChatRequest.getModel() : providerConfig.getDefaultModel(),
                        ex.getMessage(), USER_WEB_CHAT_ENDPOINT));
    }

    private AiChatSession prepareSession(Long userId, UserAiChatRequest request) {
        Assert.isTrue(StringUtils.isNotBlank(request.getProviderCode()) || StringUtils.isNotBlank(request.getModel()) || request.getSessionId() != null,
                "providerCode或model不能为空");
        AiChatSession session;
        if (request.getSessionId() != null) {
            session = aiChatSessionService.requireOwnedSession(userId, request.getSessionId());
        } else {
            String resolvedProviderCode = resolveProviderCode(request);
            session = new AiChatSession();
            session.setUserId(userId);
            session.setProviderCode(resolvedProviderCode);
            session.setModel(request.getModel());
            session.setTitle(resolveTitle(request));
            session.setCreatedTime(LocalDateTime.now());
            session.setUpdatedTime(LocalDateTime.now());
            aiChatSessionService.save(session);
        }
        if (StringUtils.isNotBlank(request.getProviderCode())) {
            session.setProviderCode(request.getProviderCode());
        } else if (StringUtils.isBlank(session.getProviderCode()) && StringUtils.isNotBlank(request.getModel())) {
            session.setProviderCode(resolveProviderCode(request));
        }
        if (StringUtils.isNotBlank(request.getModel())) {
            session.setModel(request.getModel());
        }
        if (request.getSessionId() == null && StringUtils.isNotBlank(request.getTitle())) {
            session.setTitle(request.getTitle());
        }
        session.setUpdatedTime(LocalDateTime.now());
        aiChatSessionService.updateById(session);
        return session;
    }

    private String resolveProviderCode(UserAiChatRequest request) {
        if (StringUtils.isNotBlank(request.getProviderCode())) {
            return request.getProviderCode();
        }
        if (StringUtils.isBlank(request.getModel())) {
            return null;
        }
        return aiChatService.resolveProviderConfig(null, request.getModel()).getCode();
    }

    private String resolveTitle(UserAiChatRequest request) {
        if (StringUtils.isNotBlank(request.getTitle())) {
            return request.getTitle();
        }
        if (StringUtils.isNotBlank(request.getInput())) {
            return truncate(request.getInput(), 30);
        }
        if (request.getMessages() != null) {
            for (AiChatRequest.Message message : request.getMessages()) {
                String preview = userMessagePreview(message);
                if (message != null && "user".equalsIgnoreCase(message.getRole()) && StringUtils.isNotBlank(preview)) {
                    return truncate(preview, 30);
                }
            }
        }
        return "新对话";
    }

    private AiChatRequest toAiChatRequest(UserAiChatRequest request, AiChatSession session) {
        AiChatRequest aiChatRequest = new AiChatRequest();
        aiChatRequest.setProviderCode(session.getProviderCode());
        aiChatRequest.setModel(StringUtils.isBlank(request.getModel()) ? session.getModel() : request.getModel());
        aiChatRequest.setInput(request.getInput());
        aiChatRequest.setMessages(request.getMessages());
        aiChatRequest.setTemperature(request.getTemperature());
        aiChatRequest.setTopP(request.getTopP());
        aiChatRequest.setMaxTokens(request.getMaxTokens());
        aiChatRequest.setStream(request.getStream());
        aiChatRequest.setInstructions(request.getInstructions());
        aiChatRequest.setExtra(request.getExtra());
        return aiChatRequest;
    }

    private void persistUserMessage(Long sessionId, AiChatRequest request) {
        List<AiChatRequest.Message> messages = request.getMessages();
        if (messages != null && !messages.isEmpty()) {
            for (int i = messages.size() - 1; i >= 0; i--) {
                AiChatRequest.Message message = messages.get(i);
                String preview = userMessagePreview(message);
                if (message != null
                        && StringUtils.isNotBlank(preview)
                        && "user".equalsIgnoreCase(message.getRole())) {
                    saveMessage(sessionId, "user", preview);
                    return;
                }
            }
        }
        if (StringUtils.isNotBlank(request.getInput())) {
            saveMessage(sessionId, "user", request.getInput());
        }
    }

    private void persistAssistantMessage(AiChatSession session, String content, UserAiChatRequest request) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        saveMessage(session.getId(), "assistant", content);
        session.setLastMessage(truncate(content, 200));
        if (StringUtils.isBlank(session.getTitle())) {
            session.setTitle(resolveTitle(request));
        }
        session.setUpdatedTime(LocalDateTime.now());
        aiChatSessionService.updateById(session);
    }

    private void saveMessage(Long sessionId, String role, String content) {
        AiChatSessionMessage message = new AiChatSessionMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedTime(LocalDateTime.now());
        message.setUpdatedTime(LocalDateTime.now());
        aiChatSessionMessageService.save(message);
    }

    private String userMessagePreview(AiChatRequest.Message message) {
        if (message == null) {
            return "";
        }
        if (StringUtils.isNotBlank(message.getContent())) {
            return message.getContent();
        }
        if (message.getFileUrls() != null && !message.getFileUrls().isEmpty()) {
            return "文件: " + message.getFileUrls().get(0);
        }
        if (message.getImageUrls() != null && !message.getImageUrls().isEmpty()) {
            return "图片: " + message.getImageUrls().get(0);
        }
        return "";
    }

    private void appendAssistantDelta(String payload, StringBuilder builder) {
        if (StringUtils.isBlank(payload) || "[DONE]".equals(payload)) {
            return;
        }
        try {
            String content = JacksonUtils.getObjectMapper().readTree(payload).at("/choices/0/delta/content").asText("");
            if (StringUtils.isNotBlank(content)) {
                builder.append(content);
            }
        } catch (Exception ignore) {
        }
    }

    private BillingPlan buildBillingPlan(Long userId, AiProviderConfig providerConfig, AiChatRequest request) {
        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        BillingPlan billingPlan = new BillingPlan();
        billingPlan.userId = userId;
        billingPlan.providerConfigId = providerConfig.getId();
        billingPlan.billingEnabled = pickBoolean(config, "billingEnabled", false);
        billingPlan.walletTypeId = pickInteger(config, "billingWalletTypeId", 1);
        billingPlan.promptPricePer1kTokens = pickBigDecimal(config, "promptPricePer1kTokens", BigDecimal.ZERO);
        billingPlan.completionPricePer1kTokens = pickBigDecimal(config, "completionPricePer1kTokens", billingPlan.promptPricePer1kTokens);
        billingPlan.estimatedModel = StringUtils.isNotBlank(request.getModel()) ? request.getModel() : providerConfig.getDefaultModel();

        AiUsageSummary estimatedUsage = aiChatService.estimateUsage(request, "");
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
        Assert.notNull(walletInfo, "钱包不存在");
        Assert.isTrue(walletInfo.getAmount().compareTo(estimatedAmount) >= 0, "钱包余额不足");
    }

    private Long chargeIfNeeded(BillingPlan billingPlan, String requestId, AiUsageSummary usage, String model) {
        BigDecimal amount = calculateAmount(billingPlan, usage, model);
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
        return "AI聊天扣费 model=" + model
                + ", inputTokens=" + promptTokens
                + ", outputTokens=" + completionTokens
                + ", totalTokens=" + totalTokens;
    }

    private BigDecimal calculateAmount(BillingPlan billingPlan, AiUsageSummary usage, String model) {
        if (!billingPlan.billingEnabled || usage == null) {
            return BigDecimal.ZERO;
        }
        ModelPricing modelPricing = resolveModelPricing(billingPlan.providerConfigId, model);
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
                return ModelPricing.split(promptPrice, completionPrice);
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

    private void saveSuccessLog(String requestId, Long userId, AiProviderConfig providerConfig, String model,
                                AiUsageSummary usage, BigDecimal amount, Long walletLogId, String endpoint) {
        work.soho.ai.biz.domain.AiApiCallLog log = new work.soho.ai.biz.domain.AiApiCallLog();
        log.setRequestId(requestId);
        log.setUserId(userId);
        log.setApiKeyId(USER_WEB_CHAT_API_KEY_ID);
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

    private void saveFailedLog(String requestId, Long userId, AiProviderConfig providerConfig, String model,
                               String errorMessage, String endpoint) {
        work.soho.ai.biz.domain.AiApiCallLog log = new work.soho.ai.biz.domain.AiApiCallLog();
        log.setRequestId(requestId);
        log.setUserId(userId);
        log.setApiKeyId(USER_WEB_CHAT_API_KEY_ID);
        log.setProviderConfigId(providerConfig.getId());
        log.setEndpoint(endpoint);
        log.setModel(model);
        log.setAmount(BigDecimal.ZERO);
        log.setStatus(AiApiCallLogEnums.Status.FAILED.getId());
        log.setErrorMessage(StringUtils.isBlank(errorMessage) ? "AI user chat failed" : errorMessage);
        log.setCreatedTime(LocalDateTime.now());
        log.setUpdatedTime(LocalDateTime.now());
        aiApiCallLogService.save(log);
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

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private static final class BillingPlan {
        private Long userId;
        private Long providerConfigId;
        private boolean billingEnabled;
        private Integer walletTypeId;
        private BigDecimal promptPricePer1kTokens;
        private BigDecimal completionPricePer1kTokens;
        private String estimatedModel;
        private AiUsageSummary estimatedUsage;
    }

    private static final class ModelPricing {
        private final BigDecimal promptPricePer1kTokens;
        private final BigDecimal completionPricePer1kTokens;

        private ModelPricing(BigDecimal promptPricePer1kTokens, BigDecimal completionPricePer1kTokens) {
            this.promptPricePer1kTokens = promptPricePer1kTokens;
            this.completionPricePer1kTokens = completionPricePer1kTokens;
        }

        private static ModelPricing empty() {
            return new ModelPricing(BigDecimal.ZERO, BigDecimal.ZERO);
        }

        private static ModelPricing split(BigDecimal promptPricePer1kTokens, BigDecimal completionPricePer1kTokens) {
            return new ModelPricing(promptPricePer1kTokens, completionPricePer1kTokens);
        }

        private boolean hasSplitPrice() {
            return promptPricePer1kTokens.compareTo(BigDecimal.ZERO) > 0
                    || completionPricePer1kTokens.compareTo(BigDecimal.ZERO) > 0;
        }
    }
}
