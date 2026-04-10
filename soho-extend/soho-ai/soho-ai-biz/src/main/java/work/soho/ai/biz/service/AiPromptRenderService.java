package work.soho.ai.biz.service;

import work.soho.ai.biz.dto.AiPromptRenderResult;
import work.soho.ai.biz.request.AiChatRequest;

/**
 * 提示词渲染服务。
 */
public interface AiPromptRenderService {
    /**
     * 根据模板配置渲染聊天请求。
     *
     * @param request 聊天请求
     * @return 渲染结果
     */
    AiPromptRenderResult render(AiChatRequest request);
}
