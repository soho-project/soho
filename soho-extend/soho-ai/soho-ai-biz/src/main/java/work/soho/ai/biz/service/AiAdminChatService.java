package work.soho.ai.biz.service;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.domain.AiChatSessionMessage;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUserModelView;
import work.soho.ai.biz.request.UserAiChatRequest;

import java.util.List;

/**
 * 管理端 AI 聊天服务。
 */
public interface AiAdminChatService {
    /**
     * 获取管理端可用模型列表。
     *
     * @return 模型列表
     */
    List<AiUserModelView> listModels();

    /**
     * 获取当前管理员的会话列表。
     *
     * @param adminId 管理员ID
     * @return 会话列表
     */
    List<AiChatSession> listSessions(Long adminId);

    /**
     * 获取当前管理员的会话消息列表。
     *
     * @param adminId 管理员ID
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatSessionMessage> listSessionMessages(Long adminId, Long sessionId);

    /**
     * 删除当前管理员的会话。
     *
     * @param adminId 管理员ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean deleteSession(Long adminId, Long sessionId);

    /**
     * 重命名当前管理员的会话。
     *
     * @param adminId 管理员ID
     * @param sessionId 会话ID
     * @param title 新标题
     * @return 会话信息
     */
    AiChatSession renameSession(Long adminId, Long sessionId, String title);

    /**
     * 管理端非流式聊天。
     *
     * @param adminId 管理员ID
     * @param request 请求参数
     * @return 聊天结果
     */
    AiChatResponse chat(Long adminId, UserAiChatRequest request);

    /**
     * 管理端流式聊天。
     *
     * @param adminId 管理员ID
     * @param request 请求参数
     * @return SSE 数据流
     */
    Flux<String> streamChat(Long adminId, UserAiChatRequest request);

    /**
     * 上传管理端聊天文件。
     *
     * @param file 文件
     * @return 文件地址
     */
    String uploadFile(MultipartFile file);
}
