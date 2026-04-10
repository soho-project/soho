package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.domain.AiChatSessionMessage;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.domain.AiPromptRenderLog;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiPromptRenderResult;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.dto.AiUserModelView;
import work.soho.ai.biz.enums.AiApiCallLogEnums;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.UserAiChatRequest;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiChatSessionMessageService;
import work.soho.ai.biz.service.AiChatSessionService;
import work.soho.ai.biz.service.AiMemberRequestLimitService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiPromptRenderLogService;
import work.soho.ai.biz.service.AiPromptRenderService;
import work.soho.ai.biz.service.AiUserMemberCardService;
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
import java.util.concurrent.atomic.AtomicLong;

@Service
@Log4j2
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
    private final AiMemberRequestLimitService aiMemberRequestLimitService;
    private final AiUserMemberCardService aiUserMemberCardService;
    private final AiPromptRenderService aiPromptRenderService;
    private final AiPromptRenderLogService aiPromptRenderLogService;

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
        List<AiChatSession> sessions = aiChatSessionService.list(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getUserId, userId)
                .orderByDesc(AiChatSession::getUpdatedTime)
                .orderByDesc(AiChatSession::getId));
        for (AiChatSession session : sessions) {
            sanitizeSessionProviderBinding(session);
        }
        return sessions;
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
        sanitizeSessionProviderBinding(session);
        return session;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiChatResponse chat(Long userId, UserAiChatRequest request) {
        AiChatSession session = prepareSession(userId, request);
        String requestId = IDGeneratorUtils.uuid32();
        AiChatRequest aiChatRequest = renderPromptRequest(toAiChatRequest(request, session));
        AiProviderConfig selectedProviderConfig = aiChatService.resolveProviderConfig(aiChatRequest.getProviderCode(), aiChatRequest.getModel());
        BillingPlan billingPlan = buildBillingPlan(userId, selectedProviderConfig, aiChatRequest);
        preCheckBalance(billingPlan);
        savePromptRenderLog(requestId, userId, session.getId(), aiChatRequest);
        long startAt = System.currentTimeMillis();
        try {
            persistUserMessage(session.getId(), aiChatRequest);
            AiChatResponse response = aiChatService.chat(aiChatRequest);
            AiProviderConfig providerConfig = resolveActualProviderConfig(response, aiChatRequest, selectedProviderConfig);
            refreshBillingPlanProviderConfig(billingPlan, providerConfig, response.getModel());
            AiUsageSummary usage = usageFromResponse(aiChatRequest, response);
            Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, response.getModel());
            aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
            long totalMs = System.currentTimeMillis() - startAt;
            saveSuccessLog(requestId, userId, providerConfig, response.getModel(), usage,
                    calculateAmount(billingPlan, usage, response.getModel()), walletLogId, USER_WEB_CHAT_ENDPOINT,
                    totalMs, null);
            persistAssistantMessage(session, response.getContent(), request);
            return response;
        } catch (RuntimeException ex) {
            AiProviderConfig providerConfig = resolveActualProviderConfig(null, aiChatRequest, selectedProviderConfig);
            refreshBillingPlanProviderConfig(billingPlan, providerConfig, resolveActualModel(aiChatRequest, providerConfig));
            long totalMs = System.currentTimeMillis() - startAt;
            saveFailedLog(requestId, userId, providerConfig,
                    resolveActualModel(aiChatRequest, providerConfig),
                    ex.getMessage(), USER_WEB_CHAT_ENDPOINT, totalMs, null);
            throw ex;
        }
    }

    @Override
    public Flux<String> streamChat(Long userId, UserAiChatRequest request) {
        AiChatSession session = prepareSession(userId, request);
        String requestId = IDGeneratorUtils.uuid32();
        AiChatRequest aiChatRequest = renderPromptRequest(toAiChatRequest(request, session));
        AiProviderConfig selectedProviderConfig = aiChatService.resolveProviderConfig(aiChatRequest.getProviderCode(), aiChatRequest.getModel());
        BillingPlan billingPlan = buildBillingPlan(userId, selectedProviderConfig, aiChatRequest);
        preCheckBalance(billingPlan);
        savePromptRenderLog(requestId, userId, session.getId(), aiChatRequest);
        persistUserMessage(session.getId(), aiChatRequest);
        StringBuilder assistantContent = new StringBuilder();
        long startAt = System.currentTimeMillis();
        AtomicLong firstTokenAt = new AtomicLong(-1L);
        return aiChatService.streamChat(aiChatRequest)
                .doOnNext(payload -> {
                    appendAssistantDelta(payload, assistantContent);
                    recordFirstTokenAt(firstTokenAt, startAt, extractAssistantDelta(payload));
                })
                .doOnComplete(() -> {
                    AiProviderConfig providerConfig = resolveActualProviderConfig(null, aiChatRequest, selectedProviderConfig);
                    String model = resolveActualModel(aiChatRequest, providerConfig);
                    refreshBillingPlanProviderConfig(billingPlan, providerConfig, model);
                    AiUsageSummary usage = aiChatService.estimateUsage(aiChatRequest, assistantContent.toString());
                    BigDecimal amount = calculateAmount(billingPlan, usage, model);
                    Long walletLogId = chargeIfNeeded(billingPlan, requestId, usage, model);
                    aiMemberRequestLimitService.consumeIfNeeded(billingPlan.memberLimitDecision, requestId);
                    long totalMs = System.currentTimeMillis() - startAt;
                    saveSuccessLog(requestId, userId, providerConfig, model, usage, amount, walletLogId, USER_WEB_CHAT_ENDPOINT,
                            totalMs, resolveFirstTokenMs(firstTokenAt, startAt));
                    persistAssistantMessage(session, assistantContent.toString(), request);
                })
                .doOnError(ex -> {
                    AiProviderConfig providerConfig = resolveActualProviderConfig(null, aiChatRequest, selectedProviderConfig);
                    String model = resolveActualModel(aiChatRequest, providerConfig);
                    refreshBillingPlanProviderConfig(billingPlan, providerConfig, model);
                    long totalMs = System.currentTimeMillis() - startAt;
                    saveFailedLog(requestId, userId, providerConfig,
                            model,
                            ex.getMessage(), USER_WEB_CHAT_ENDPOINT, totalMs, resolveFirstTokenMs(firstTokenAt, startAt));
                });
    }

    /**
     * 渲染提示词模板并返回最终聊天请求。
     *
     * @param aiChatRequest 原始聊天请求
     * @return 渲染后的聊天请求
     */
    private AiChatRequest renderPromptRequest(AiChatRequest aiChatRequest) {
        AiPromptRenderResult renderResult = aiPromptRenderService.render(aiChatRequest);
        AiChatRequest renderedRequest = renderResult.getRenderedRequest();
        attachPromptMeta(renderedRequest, renderResult);
        return renderedRequest;
    }

    private AiChatSession prepareSession(Long userId, UserAiChatRequest request) {
        Assert.isTrue(StringUtils.isNotBlank(request.getProviderCode()) || StringUtils.isNotBlank(request.getModel()) || request.getSessionId() != null,
                "providerCode或model不能为空");
        AiChatSession session;
        if (request.getSessionId() != null) {
            session = aiChatSessionService.requireOwnedSession(userId, request.getSessionId());
        } else {
            session = new AiChatSession();
            session.setUserId(userId);
            session.setProviderCode(null);
            session.setModel(request.getModel());
            session.setTitle(resolveTitle(request));
            session.setCreatedTime(LocalDateTime.now());
            session.setUpdatedTime(LocalDateTime.now());
            aiChatSessionService.save(session);
        }
        // 供应商不再绑定到会话，每次请求都按当前参数重新路由。
        session.setProviderCode(null);
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
        aiChatRequest.setProviderCode(request.getProviderCode());
        aiChatRequest.setModel(StringUtils.isBlank(request.getModel()) ? session.getModel() : request.getModel());
        aiChatRequest.setInput(request.getInput());
        aiChatRequest.setMessages(request.getMessages());
        aiChatRequest.setTemperature(request.getTemperature());
        aiChatRequest.setTopP(request.getTopP());
        aiChatRequest.setMaxTokens(request.getMaxTokens());
        aiChatRequest.setStream(request.getStream());
        aiChatRequest.setInstructions(request.getInstructions());
        aiChatRequest.setSceneCode(request.getSceneCode());
        aiChatRequest.setTemplateCode(request.getTemplateCode());
        aiChatRequest.setPromptVars(request.getPromptVars());
        aiChatRequest.setExtra(request.getExtra());
        return aiChatRequest;
    }

    /**
     * 清理返回给前端的会话供应商绑定信息。
     *
     * @param session 会话
     */
    private void sanitizeSessionProviderBinding(AiChatSession session) {
        if (session == null) {
            return;
        }
        session.setProviderCode(null);
    }

    /**
     * 把提示词元信息写回请求 extra，便于后续日志和排障。
     *
     * @param request 渲染后的请求
     * @param renderResult 渲染结果
     */
    private void attachPromptMeta(AiChatRequest request, AiPromptRenderResult renderResult) {
        if (request == null || renderResult == null || renderResult.getTemplateId() == null) {
            return;
        }
        Map<String, Object> extra = request.getExtra() == null ? new HashMap<>() : new HashMap<>(request.getExtra());
        extra.put("promptTemplateId", renderResult.getTemplateId());
        extra.put("promptTemplateCode", renderResult.getTemplateCode());
        extra.put("promptTemplateVersion", renderResult.getTemplateVersion());
        extra.put("promptSceneCode", renderResult.getSceneCode());
        request.setExtra(extra);
    }

    /**
     * 保存提示词渲染快照。
     *
     * @param requestId 请求ID
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param request 渲染后的请求
     */
    private void savePromptRenderLog(String requestId, Long userId, Long sessionId, AiChatRequest request) {
        Map<String, Object> extra = request == null ? null : request.getExtra();
        if (extra == null || extra.get("promptTemplateId") == null) {
            return;
        }
        AiPromptRenderLog logRecord = new AiPromptRenderLog();
        logRecord.setRequestId(requestId);
        logRecord.setUserId(userId);
        logRecord.setSessionId(sessionId);
        logRecord.setProviderCode(request.getProviderCode());
        logRecord.setModel(request.getModel());
        logRecord.setSceneCode(request.getSceneCode());
        logRecord.setTemplateId(longValue(extra.get("promptTemplateId")));
        logRecord.setTemplateCode(stringValue(extra.get("promptTemplateCode")));
        logRecord.setTemplateVersion(integerValue(extra.get("promptTemplateVersion")));
        logRecord.setPromptVarsJson(JacksonUtils.toJson(request.getPromptVars()));
        logRecord.setRenderedInstructions(request.getInstructions());
        logRecord.setRenderedInput(resolveRenderedInput(request));
        aiPromptRenderLogService.save(logRecord);
    }

    /**
     * 获取最终用户输入快照。
     *
     * @param request 聊天请求
     * @return 输入文本
     */
    private String resolveRenderedInput(AiChatRequest request) {
        if (request == null) {
            return null;
        }
        if (StringUtils.isNotBlank(request.getInput())) {
            return request.getInput();
        }
        List<AiChatRequest.Message> messages = request.getMessages();
        if (messages == null) {
            return null;
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            AiChatRequest.Message message = messages.get(i);
            if (message != null && "user".equalsIgnoreCase(message.getRole()) && StringUtils.isNotBlank(message.getContent())) {
                return message.getContent();
            }
        }
        return null;
    }

    /**
     * 转换为 Long。
     *
     * @param value 原始值
     * @return Long 值
     */
    private Long longValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    /**
     * 转换为 Integer。
     *
     * @param value 原始值
     * @return Integer 值
     */
    private Integer integerValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    /**
     * 转换为字符串。
     *
     * @param value 原始值
     * @return 字符串
     */
    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
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
            String content = extractAssistantDelta(payload);
            if (StringUtils.isNotBlank(content)) {
                builder.append(content);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 提取流式 payload 中的助手文本增量。
     */
    private String extractAssistantDelta(String payload) {
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
     * 记录首字时间戳。
     */
    private void recordFirstTokenAt(AtomicLong firstTokenAt, long startAt, String delta) {
        if (firstTokenAt.get() >= 0 || StringUtils.isBlank(delta)) {
            return;
        }
        if (firstTokenAt.compareAndSet(-1L, System.currentTimeMillis())) {
            log.info("ai user chat first token captured, first_token_ms={}", firstTokenAt.get() - startAt);
        }
    }

    /**
     * 计算首字耗时。
     */
    private Long resolveFirstTokenMs(AtomicLong firstTokenAt, long startAt) {
        return firstTokenAt.get() < 0 ? null : (firstTokenAt.get() - startAt);
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
        // 会员卡判定：by_request 且未超限则本次免费
        billingPlan.memberLimitDecision = aiMemberRequestLimitService.evaluate(
                billingPlan.userId,
                aiUserMemberCardService.resolveActiveMemberCard(billingPlan.userId)
        );
        // 命中会员免费配额时关闭计费开关
        if (billingPlan.memberLimitDecision.isMemberByRequest() && !billingPlan.memberLimitDecision.isOverLimit()) {
            billingPlan.billingEnabled = false;
        }

        AiUsageSummary estimatedUsage = aiChatService.estimateUsage(request, "");
        int expectedCompletionTokens = request.getMaxTokens() == null ? 0 : Math.max(request.getMaxTokens(), 0);
        estimatedUsage.setCompletionTokens(expectedCompletionTokens);
        estimatedUsage.setTotalTokens(estimatedUsage.getPromptTokens() + expectedCompletionTokens);
        billingPlan.estimatedUsage = estimatedUsage;
        return billingPlan;
    }

    private void preCheckBalance(BillingPlan billingPlan) {
        // 免费请求不需要检查钱包余额
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
        // 免费请求或金额为0时不扣钱包
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
                                AiUsageSummary usage, BigDecimal amount, Long walletLogId, String endpoint,
                                Long totalMs, Long firstTokenMs) {
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
        log.setTotalMs(totalMs);
        log.setFirstTokenMs(firstTokenMs);
        log.setCreatedTime(LocalDateTime.now());
        log.setUpdatedTime(LocalDateTime.now());
        aiApiCallLogService.save(log);
    }

    private void saveFailedLog(String requestId, Long userId, AiProviderConfig providerConfig, String model,
                               String errorMessage, String endpoint, Long totalMs, Long firstTokenMs) {
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
        log.setTotalMs(totalMs);
        log.setFirstTokenMs(firstTokenMs);
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
        billingPlan.providerConfigId = providerConfig.getId();
        billingPlan.walletTypeId = pickInteger(config, "billingWalletTypeId", 1);
        billingPlan.promptPricePer1kTokens = pickBigDecimal(config, "promptPricePer1kTokens", BigDecimal.ZERO);
        billingPlan.completionPricePer1kTokens = pickBigDecimal(config, "completionPricePer1kTokens", billingPlan.promptPricePer1kTokens);
        billingPlan.estimatedModel = StringUtils.isNotBlank(model) ? model : providerConfig.getDefaultModel();
        billingPlan.billingEnabled = pickBoolean(config, "billingEnabled", false);
        if (billingPlan.memberLimitDecision != null
                && billingPlan.memberLimitDecision.isMemberByRequest()
                && !billingPlan.memberLimitDecision.isOverLimit()) {
            billingPlan.billingEnabled = false;
        }
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
        private AiMemberRequestLimitService.Decision memberLimitDecision;
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
