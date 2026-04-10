package work.soho.ai.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiPromptTemplate;
import work.soho.ai.biz.dto.AiPromptRenderResult;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.service.AiPromptRenderService;
import work.soho.ai.biz.service.AiPromptTemplateService;
import work.soho.common.core.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示词渲染服务实现。
 */
@Service
@RequiredArgsConstructor
public class AiPromptRenderServiceImpl implements AiPromptRenderService {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_\\.]+)\\s*}}");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AiPromptTemplateService aiPromptTemplateService;

    /**
     * 根据模板配置渲染聊天请求。
     *
     * @param request 聊天请求
     * @return 渲染结果
     */
    @Override
    public AiPromptRenderResult render(AiChatRequest request) {
        AiChatRequest renderedRequest = copyRequest(request);
        AiPromptRenderResult result = new AiPromptRenderResult();
        result.setRenderedRequest(renderedRequest);
        if (request == null) {
            return result;
        }

        AiPromptTemplate template = aiPromptTemplateService.findActiveTemplate(
                request.getTemplateCode(),
                request.getSceneCode(),
                request.getProviderCode(),
                request.getModel());
        if (template == null) {
            result.setRenderedInstructions(request.getInstructions());
            result.setRenderedInput(resolveRenderedInput(renderedRequest));
            return result;
        }

        Map<String, Object> variables = buildVariables(request);
        String renderedSystemPrompt = renderTemplate(template.getSystemPrompt(), variables);
        String mergedInstructions = mergeInstructions(renderedSystemPrompt, request.getInstructions());
        renderedRequest.setInstructions(mergedInstructions);
        renderedRequest.setSceneCode(template.getSceneCode());
        renderedRequest.setTemplateCode(template.getCode());
        applyUserPromptTemplate(renderedRequest, template, variables);

        result.setTemplateId(template.getId());
        result.setTemplateCode(template.getCode());
        result.setTemplateVersion(template.getVersion());
        result.setSceneCode(template.getSceneCode());
        result.setRenderedInstructions(renderedRequest.getInstructions());
        result.setRenderedInput(resolveRenderedInput(renderedRequest));
        return result;
    }

    /**
     * 复制请求，避免直接污染原始请求对象。
     *
     * @param request 原始请求
     * @return 新请求
     */
    private AiChatRequest copyRequest(AiChatRequest request) {
        AiChatRequest copied = new AiChatRequest();
        if (request == null) {
            return copied;
        }
        copied.setProviderCode(request.getProviderCode());
        copied.setModel(request.getModel());
        copied.setInput(request.getInput());
        copied.setMessages(copyMessages(request.getMessages()));
        copied.setTemperature(request.getTemperature());
        copied.setTopP(request.getTopP());
        copied.setMaxTokens(request.getMaxTokens());
        copied.setStream(request.getStream());
        copied.setInstructions(request.getInstructions());
        copied.setExtra(request.getExtra() == null ? null : new HashMap<>(request.getExtra()));
        copied.setSceneCode(request.getSceneCode());
        copied.setTemplateCode(request.getTemplateCode());
        copied.setPromptVars(request.getPromptVars() == null ? null : new HashMap<>(request.getPromptVars()));
        return copied;
    }

    /**
     * 复制消息列表。
     *
     * @param messages 消息列表
     * @return 新消息列表
     */
    private List<AiChatRequest.Message> copyMessages(List<AiChatRequest.Message> messages) {
        if (messages == null) {
            return null;
        }
        List<AiChatRequest.Message> copied = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            if (message == null) {
                copied.add(null);
                continue;
            }
            AiChatRequest.Message item = new AiChatRequest.Message();
            item.setRole(message.getRole());
            item.setContent(message.getContent());
            item.setImageUrls(message.getImageUrls() == null ? null : new ArrayList<>(message.getImageUrls()));
            item.setFileUrls(message.getFileUrls() == null ? null : new ArrayList<>(message.getFileUrls()));
            copied.add(item);
        }
        return copied;
    }

    /**
     * 构造渲染变量。
     *
     * @param request 聊天请求
     * @return 变量集合
     */
    private Map<String, Object> buildVariables(AiChatRequest request) {
        Map<String, Object> variables = new HashMap<>();
        if (request.getPromptVars() != null) {
            variables.putAll(request.getPromptVars());
        }
        LocalDateTime now = LocalDateTime.now();
        variables.put("current_date", now.format(DATE_FORMATTER));
        variables.put("current_time", now.format(DATE_TIME_FORMATTER));
        variables.put("provider_code", request.getProviderCode());
        variables.put("model", request.getModel());
        variables.put("input", firstNonBlank(request.getInput(), resolveLatestUserContent(request.getMessages())));
        return variables;
    }

    /**
     * 渲染模板文本。
     *
     * @param template 模板原文
     * @param variables 变量集合
     * @return 渲染后的文本
     */
    private String renderTemplate(String template, Map<String, Object> variables) {
        if (StringUtils.isBlank(template)) {
            return "";
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();
        List<String> missingVariables = new ArrayList<>();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);
            if (value == null) {
                missingVariables.add(variableName);
                value = "";
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(String.valueOf(value)));
        }
        matcher.appendTail(buffer);
        if (!missingVariables.isEmpty()) {
            throw new IllegalArgumentException("提示词变量缺失: " + String.join(", ", missingVariables));
        }
        return buffer.toString().trim();
    }

    /**
     * 合并模板系统提示词与请求附加说明。
     *
     * @param templateInstructions 模板提示词
     * @param requestInstructions 请求附加说明
     * @return 合并后的结果
     */
    private String mergeInstructions(String templateInstructions, String requestInstructions) {
        if (StringUtils.isBlank(templateInstructions)) {
            return requestInstructions;
        }
        if (StringUtils.isBlank(requestInstructions)) {
            return templateInstructions;
        }
        return templateInstructions + "\n\n附加要求：\n" + requestInstructions.trim();
    }

    /**
     * 应用用户输入模板。
     *
     * @param request 渲染后的请求
     * @param template 提示词模板
     * @param variables 渲染变量
     */
    private void applyUserPromptTemplate(AiChatRequest request, AiPromptTemplate template, Map<String, Object> variables) {
        if (StringUtils.isBlank(template.getUserPromptTemplate())) {
            return;
        }
        String renderedUserPrompt = renderTemplate(template.getUserPromptTemplate(), variables);
        if (StringUtils.isNotBlank(request.getInput())) {
            request.setInput(renderedUserPrompt);
            return;
        }
        replaceLatestUserMessage(request.getMessages(), renderedUserPrompt);
    }

    /**
     * 替换最后一条用户消息内容。
     *
     * @param messages 消息列表
     * @param renderedUserPrompt 渲染后的用户输入
     */
    private void replaceLatestUserMessage(List<AiChatRequest.Message> messages, String renderedUserPrompt) {
        if (messages == null) {
            return;
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            AiChatRequest.Message message = messages.get(i);
            if (message != null && "user".equalsIgnoreCase(message.getRole())) {
                message.setContent(renderedUserPrompt);
                return;
            }
        }
    }

    /**
     * 获取渲染后的输入快照。
     *
     * @param request 渲染后的请求
     * @return 输入快照
     */
    private String resolveRenderedInput(AiChatRequest request) {
        if (StringUtils.isNotBlank(request.getInput())) {
            return request.getInput();
        }
        return resolveLatestUserContent(request.getMessages());
    }

    /**
     * 获取最后一条用户消息文本。
     *
     * @param messages 消息列表
     * @return 用户输入
     */
    private String resolveLatestUserContent(List<AiChatRequest.Message> messages) {
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
     * 选择第一个非空字符串。
     *
     * @param values 候选值
     * @return 结果
     */
    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
