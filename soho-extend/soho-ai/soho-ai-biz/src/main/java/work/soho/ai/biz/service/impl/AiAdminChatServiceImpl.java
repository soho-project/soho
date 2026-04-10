package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.domain.AiChatSessionMessage;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiPromptRenderLog;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiPromptRenderResult;
import work.soho.ai.biz.dto.AiUserModelView;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.UserAiChatRequest;
import work.soho.ai.biz.service.AiAdminChatService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiChatSessionMessageService;
import work.soho.ai.biz.service.AiChatSessionService;
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiPromptRenderLogService;
import work.soho.ai.biz.service.AiPromptRenderService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.utils.AiProviderModelUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 管理端 AI 聊天服务实现。
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AiAdminChatServiceImpl implements AiAdminChatService {
    private static final String ADMIN_CHAT_ENDPOINT = "/ai/admin/chat";

    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiChatService aiChatService;
    private final AiChatSessionService aiChatSessionService;
    private final AiChatSessionMessageService aiChatSessionMessageService;
    private final AiPromptRenderService aiPromptRenderService;
    private final AiPromptRenderLogService aiPromptRenderLogService;
    private final AiFileService aiFileService;

    /**
     * 获取管理端可用模型列表。
     *
     * @return 模型列表
     */
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

    /**
     * 获取当前管理员的会话列表。
     *
     * @param adminId 管理员ID
     * @return 会话列表
     */
    @Override
    public List<AiChatSession> listSessions(Long adminId) {
        Long ownerId = buildAdminOwnerId(adminId);
        return aiChatSessionService.list(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getUserId, ownerId)
                .orderByDesc(AiChatSession::getUpdatedTime)
                .orderByDesc(AiChatSession::getId));
    }

    /**
     * 获取当前管理员的会话消息列表。
     *
     * @param adminId 管理员ID
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Override
    public List<AiChatSessionMessage> listSessionMessages(Long adminId, Long sessionId) {
        requireAdminOwnedSession(adminId, sessionId);
        return aiChatSessionMessageService.list(new LambdaQueryWrapper<AiChatSessionMessage>()
                .eq(AiChatSessionMessage::getSessionId, sessionId)
                .orderByAsc(AiChatSessionMessage::getId));
    }

    /**
     * 删除当前管理员的会话。
     *
     * @param adminId 管理员ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSession(Long adminId, Long sessionId) {
        requireAdminOwnedSession(adminId, sessionId);
        aiChatSessionMessageService.remove(new LambdaQueryWrapper<AiChatSessionMessage>()
                .eq(AiChatSessionMessage::getSessionId, sessionId));
        return aiChatSessionService.removeById(sessionId);
    }

    /**
     * 重命名当前管理员的会话。
     *
     * @param adminId 管理员ID
     * @param sessionId 会话ID
     * @param title 新标题
     * @return 会话信息
     */
    @Override
    public AiChatSession renameSession(Long adminId, Long sessionId, String title) {
        Assert.isTrue(StringUtils.isNotBlank(title), "title不能为空");
        AiChatSession session = requireAdminOwnedSession(adminId, sessionId);
        session.setTitle(title);
        session.setUpdatedTime(LocalDateTime.now());
        aiChatSessionService.updateById(session);
        return session;
    }

    /**
     * 管理端非流式聊天。
     *
     * @param adminId 管理员ID
     * @param request 请求参数
     * @return 聊天结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiChatResponse chat(Long adminId, UserAiChatRequest request) {
        AiChatSession session = prepareSession(adminId, request);
        String requestId = IDGeneratorUtils.uuid32();
        AiChatRequest aiChatRequest = renderPromptRequest(toAiChatRequest(request, session));
        AiProviderConfig providerConfig = aiChatService.resolveProviderConfig(aiChatRequest.getProviderCode(), aiChatRequest.getModel());
        savePromptRenderLog(requestId, buildAdminOwnerId(adminId), session.getId(), aiChatRequest);
        persistUserMessage(session.getId(), aiChatRequest);
        AiChatResponse response = aiChatService.chat(providerConfig, aiChatRequest);
        persistAssistantMessage(session, response.getContent(), request);
        return response;
    }

    /**
     * 管理端流式聊天。
     *
     * @param adminId 管理员ID
     * @param request 请求参数
     * @return SSE 数据流
     */
    @Override
    public Flux<String> streamChat(Long adminId, UserAiChatRequest request) {
        AiChatSession session = prepareSession(adminId, request);
        String requestId = IDGeneratorUtils.uuid32();
        AiChatRequest aiChatRequest = renderPromptRequest(toAiChatRequest(request, session));
        AiProviderConfig providerConfig = aiChatService.resolveProviderConfig(aiChatRequest.getProviderCode(), aiChatRequest.getModel());
        savePromptRenderLog(requestId, buildAdminOwnerId(adminId), session.getId(), aiChatRequest);
        persistUserMessage(session.getId(), aiChatRequest);
        StringBuilder assistantContent = new StringBuilder();
        long startAt = System.currentTimeMillis();
        AtomicLong firstTokenAt = new AtomicLong(-1L);
        return aiChatService.streamChat(providerConfig, aiChatRequest)
                .doOnNext(payload -> {
                    appendAssistantDelta(payload, assistantContent);
                    recordFirstTokenAt(firstTokenAt, startAt, extractAssistantDelta(payload));
                })
                .doOnComplete(() -> persistAssistantMessage(session, assistantContent.toString(), request))
                .doOnError(ex -> log.warn("admin ai stream failed, adminId={}, sessionId={}, msg={}",
                        adminId, session.getId(), ex.getMessage()));
    }

    /**
     * 上传管理端聊天文件。
     *
     * @param file 文件
     * @return 文件地址
     */
    @Override
    public String uploadFile(MultipartFile file) {
        return aiFileService.uploadUserFile(file);
    }

    /**
     * 获取提供方下可用模型。
     *
     * @param providerConfig 提供方配置
     * @return 模型列表
     */
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

    /**
     * 准备当前管理员会话。
     *
     * @param adminId 管理员ID
     * @param request 请求参数
     * @return 会话
     */
    private AiChatSession prepareSession(Long adminId, UserAiChatRequest request) {
        Assert.isTrue(StringUtils.isNotBlank(request.getProviderCode()) || StringUtils.isNotBlank(request.getModel()) || request.getSessionId() != null,
                "providerCode或model不能为空");
        Long ownerId = buildAdminOwnerId(adminId);
        AiChatSession session;
        if (request.getSessionId() != null) {
            session = aiChatSessionService.requireSessionByOwnerId(ownerId, request.getSessionId());
        } else {
            String resolvedProviderCode = resolveProviderCode(request);
            session = new AiChatSession();
            session.setUserId(ownerId);
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

    /**
     * 校验管理员会话归属。
     *
     * @param adminId 管理员ID
     * @param sessionId 会话ID
     * @return 会话
     */
    private AiChatSession requireAdminOwnedSession(Long adminId, Long sessionId) {
        return aiChatSessionService.requireSessionByOwnerId(buildAdminOwnerId(adminId), sessionId);
    }

    /**
     * 生成管理端会话归属ID。
     *
     * @param adminId 管理员ID
     * @return 归属ID
     */
    private Long buildAdminOwnerId(Long adminId) {
        Assert.notNull(adminId, "adminId不能为空");
        if (adminId.equals(Long.MIN_VALUE)) {
            return Long.MIN_VALUE;
        }
        return -Math.abs(adminId);
    }

    /**
     * 解析提供方编码。
     *
     * @param request 请求参数
     * @return 提供方编码
     */
    private String resolveProviderCode(UserAiChatRequest request) {
        if (StringUtils.isNotBlank(request.getProviderCode())) {
            return request.getProviderCode();
        }
        if (StringUtils.isBlank(request.getModel())) {
            return null;
        }
        return aiChatService.resolveProviderConfig(null, request.getModel()).getCode();
    }

    /**
     * 解析会话标题。
     *
     * @param request 请求参数
     * @return 标题
     */
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
        return "管理端新对话";
    }

    /**
     * 转换聊天请求对象。
     *
     * @param request 原始请求
     * @param session 会话
     * @return 标准聊天请求
     */
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
        aiChatRequest.setSceneCode(request.getSceneCode());
        aiChatRequest.setTemplateCode(request.getTemplateCode());
        aiChatRequest.setPromptVars(request.getPromptVars());
        aiChatRequest.setExtra(request.getExtra());
        return aiChatRequest;
    }

    /**
     * 渲染提示词模板并回写元信息。
     *
     * @param aiChatRequest 原始请求
     * @return 渲染后的请求
     */
    private AiChatRequest renderPromptRequest(AiChatRequest aiChatRequest) {
        AiPromptRenderResult renderResult = aiPromptRenderService.render(aiChatRequest);
        AiChatRequest renderedRequest = renderResult.getRenderedRequest();
        attachPromptMeta(renderedRequest, renderResult);
        return renderedRequest;
    }

    /**
     * 写入提示词元数据。
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
     * 记录提示词渲染日志。
     *
     * @param requestId 请求ID
     * @param ownerId 归属ID
     * @param sessionId 会话ID
     * @param request 渲染后的请求
     */
    private void savePromptRenderLog(String requestId, Long ownerId, Long sessionId, AiChatRequest request) {
        Map<String, Object> extra = request == null ? null : request.getExtra();
        if (extra == null || extra.get("promptTemplateId") == null) {
            return;
        }
        AiPromptRenderLog logRecord = new AiPromptRenderLog();
        logRecord.setRequestId(requestId);
        logRecord.setUserId(ownerId);
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
     * 获取最终输入快照。
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
     * 转为 Long。
     *
     * @param value 原始值
     * @return Long值
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
     * 转为 Integer。
     *
     * @param value 原始值
     * @return Integer值
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
     * 转为字符串。
     *
     * @param value 原始值
     * @return 字符串
     */
    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 持久化用户消息。
     *
     * @param sessionId 会话ID
     * @param request 请求
     */
    private void persistUserMessage(Long sessionId, AiChatRequest request) {
        List<AiChatRequest.Message> messages = request.getMessages();
        if (messages != null && !messages.isEmpty()) {
            for (int i = messages.size() - 1; i >= 0; i--) {
                AiChatRequest.Message message = messages.get(i);
                String preview = userMessagePreview(message);
                if (message != null && StringUtils.isNotBlank(preview) && "user".equalsIgnoreCase(message.getRole())) {
                    saveMessage(sessionId, "user", preview);
                    return;
                }
            }
        }
        if (StringUtils.isNotBlank(request.getInput())) {
            saveMessage(sessionId, "user", request.getInput());
        }
    }

    /**
     * 持久化助手消息。
     *
     * @param session 会话
     * @param content 回复内容
     * @param request 原始请求
     */
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

    /**
     * 保存会话消息。
     *
     * @param sessionId 会话ID
     * @param role 角色
     * @param content 内容
     */
    private void saveMessage(Long sessionId, String role, String content) {
        AiChatSessionMessage message = new AiChatSessionMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedTime(LocalDateTime.now());
        message.setUpdatedTime(LocalDateTime.now());
        aiChatSessionMessageService.save(message);
    }

    /**
     * 获取用户消息预览。
     *
     * @param message 消息
     * @return 预览文本
     */
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

    /**
     * 追加流式输出文本。
     *
     * @param payload 流式片段
     * @param builder 文本构造器
     */
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
     * 提取流式输出增量。
     *
     * @param payload 流式片段
     * @return 文本增量
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
     * 记录首字时间。
     *
     * @param firstTokenAt 首字时间戳
     * @param startAt 开始时间
     * @param delta 增量文本
     */
    private void recordFirstTokenAt(AtomicLong firstTokenAt, long startAt, String delta) {
        if (firstTokenAt.get() >= 0 || StringUtils.isBlank(delta)) {
            return;
        }
        if (firstTokenAt.compareAndSet(-1L, System.currentTimeMillis())) {
            log.info("ai admin chat first token captured, first_token_ms={}", firstTokenAt.get() - startAt);
        }
    }

    /**
     * 截断文本。
     *
     * @param value 原始值
     * @param maxLength 最大长度
     * @return 截断后的文本
     */
    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
