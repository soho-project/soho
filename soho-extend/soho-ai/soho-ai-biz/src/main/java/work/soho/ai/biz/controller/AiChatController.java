package work.soho.ai.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.domain.AiChatSessionMessage;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUserModelView;
import work.soho.ai.biz.request.RenameAiChatSessionRequest;
import work.soho.ai.biz.request.UserAiChatRequest;
import work.soho.ai.biz.service.AiAdminChatService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.List;

/**
 * 管理端 AI 聊天控制器。
 */
@Api(tags = "管理端 AI 聊天")
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin/chat")
public class AiChatController {
    private final AiAdminChatService aiAdminChatService;

    /**
     * 获取管理端可用模型列表。
     *
     * @return 模型列表
     */
    @GetMapping("/model/list")
    @Node(value = "ai::chat::model::list", name = "获取管理端 AI 模型列表")
    @ApiOperation("获取管理端 AI 模型列表")
    public R<List<AiUserModelView>> modelList() {
        return R.success(aiAdminChatService.listModels());
    }

    /**
     * 获取当前管理员会话列表。
     *
     * @param userDetails 登录信息
     * @return 会话列表
     */
    @GetMapping("/session/list")
    @Node(value = "ai::chat::session::list", name = "获取管理端 AI 会话列表")
    @ApiOperation("获取管理端 AI 会话列表")
    public R<List<AiChatSession>> sessionList(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(aiAdminChatService.listSessions(userDetails.getId()));
    }

    /**
     * 获取当前管理员会话消息列表。
     *
     * @param userDetails 登录信息
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/session/message/list")
    @Node(value = "ai::chat::session::message::list", name = "获取管理端 AI 会话消息列表")
    @ApiOperation("获取管理端 AI 会话消息列表")
    public R<List<AiChatSessionMessage>> sessionMessageList(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                            Long sessionId) {
        return R.success(aiAdminChatService.listSessionMessages(userDetails.getId(), sessionId));
    }

    /**
     * 删除当前管理员会话。
     *
     * @param userDetails 登录信息
     * @param sessionId 会话ID
     * @return 是否成功
     */
    @DeleteMapping("/session/{sessionId}")
    @Node(value = "ai::chat::session::remove", name = "删除管理端 AI 会话")
    @ApiOperation("删除管理端 AI 会话")
    public R<Boolean> deleteSession(@AuthenticationPrincipal SohoUserDetails userDetails,
                                    @PathVariable Long sessionId) {
        return R.success(aiAdminChatService.deleteSession(userDetails.getId(), sessionId));
    }

    /**
     * 重命名当前管理员会话。
     *
     * @param userDetails 登录信息
     * @param request 重命名参数
     * @return 会话信息
     */
    @PutMapping("/session/rename")
    @Node(value = "ai::chat::session::rename", name = "重命名管理端 AI 会话")
    @ApiOperation("重命名管理端 AI 会话")
    public R<AiChatSession> renameSession(@AuthenticationPrincipal SohoUserDetails userDetails,
                                          @RequestBody RenameAiChatSessionRequest request) {
        return R.success(aiAdminChatService.renameSession(userDetails.getId(), request.getSessionId(), request.getTitle()));
    }

    /**
     * 管理端非流式聊天。
     *
     * @param userDetails 登录信息
     * @param request 聊天请求
     * @return 聊天结果
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Node(value = "ai::chat", name = "管理端 AI 聊天")
    @ApiOperation("管理端 AI 聊天")
    public R<AiChatResponse> chat(@AuthenticationPrincipal SohoUserDetails userDetails,
                                  @RequestBody UserAiChatRequest request) {
        try {
            return R.success(aiAdminChatService.chat(userDetails.getId(), request));
        } catch (RuntimeException ex) {
            log.warn("admin ai chat failed, adminId={}, msg={}", userDetails.getId(), ex.getMessage());
            return R.error(buildFriendlyMessage(ex));
        }
    }

    /**
     * 管理端 SSE 流式聊天。
     *
     * @param userDetails 登录信息
     * @param request 聊天请求
     * @return SSE 数据流
     */
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Node(value = "ai::chat::sse", name = "管理端 AI SSE流式聊天")
    @ApiOperation("管理端 AI SSE流式聊天")
    public Flux<ServerSentEvent<String>> sseChat(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                 @RequestBody UserAiChatRequest request) {
        try {
            return aiAdminChatService.streamChat(userDetails.getId(), request)
                    .map(payload -> ServerSentEvent.builder(payload).build())
                    .onErrorResume(ex -> Flux.just(buildErrorEvent(userDetails.getId(), ex)));
        } catch (RuntimeException ex) {
            return Flux.just(buildErrorEvent(userDetails.getId(), ex));
        }
    }

    /**
     * 管理端流式聊天。
     *
     * @param userDetails 登录信息
     * @param request 聊天请求
     * @return SSE 数据流
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Node(value = "ai::chat::stream", name = "管理端 AI 流式聊天")
    @ApiOperation("管理端 AI 流式聊天")
    public Flux<ServerSentEvent<String>> streamChat(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                    @RequestBody UserAiChatRequest request) {
        try {
            return aiAdminChatService.streamChat(userDetails.getId(), request)
                    .map(payload -> ServerSentEvent.builder(payload).build())
                    .onErrorResume(ex -> Flux.just(buildErrorEvent(userDetails.getId(), ex)));
        } catch (RuntimeException ex) {
            return Flux.just(buildErrorEvent(userDetails.getId(), ex));
        }
    }

    /**
     * 上传管理端聊天文件。
     *
     * @param file 文件
     * @return 文件地址
     */
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Node(value = "ai::chat::file::upload", name = "管理端 AI 文件上传")
    @ApiOperation("管理端 AI 文件上传")
    public R<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return R.success(aiAdminChatService.uploadFile(file));
    }

    /**
     * 上传管理端聊天图片（与文件上传复用同一存储能力）。
     *
     * @param file 图片文件
     * @return 图片地址
     */
    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Node(value = "ai::chat::image::upload", name = "管理端 AI 图片上传")
    @ApiOperation("管理端 AI 图片上传")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return R.success(aiAdminChatService.uploadFile(file));
    }

    /**
     * 构建友好错误信息。
     *
     * @param ex 异常
     * @return 错误提示
     */
    private String buildFriendlyMessage(Throwable ex) {
        String message = ex == null ? null : ex.getMessage();
        if (StringUtils.isBlank(message)) {
            return "请求失败，请稍后重试";
        }
        return message;
    }

    /**
     * 构造 SSE 错误事件。
     *
     * @param adminId 管理员ID
     * @param ex 异常
     * @return 错误事件
     */
    private ServerSentEvent<String> buildErrorEvent(Long adminId, Throwable ex) {
        String friendlyMessage = buildFriendlyMessage(ex);
        log.warn("admin ai stream chat failed, adminId={}, msg={}", adminId, friendlyMessage);
        String payload = JacksonUtils.toJson(R.error(friendlyMessage));
        return ServerSentEvent.<String>builder(payload).event("error").build();
    }
}
