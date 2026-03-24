package work.soho.ai.biz.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.domain.AiChatSessionMessage;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUserModelView;
import work.soho.ai.biz.request.RenameAiChatSessionRequest;
import work.soho.ai.biz.request.UserAiChatRequest;
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiUserWebChatService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.List;

@Api(tags = "用户 AI Web 聊天")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/user")
public class UserAiChatWebController {
    private final AiUserWebChatService aiUserWebChatService;
    private final AiFileService aiFileService;

    @GetMapping("/model/list")
    @Node(value = "user::ai::model::list", name = "获取 AI 模型列表")
    @ApiOperation("获取 AI 模型列表")
    public R<List<AiUserModelView>> modelList() {
        return R.success(aiUserWebChatService.listModels());
    }

    @GetMapping("/session/list")
    @Node(value = "user::ai::session::list", name = "获取 AI 会话列表")
    @ApiOperation("获取 AI 会话列表")
    public R<List<AiChatSession>> sessionList(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(aiUserWebChatService.listSessions(userDetails.getId()));
    }

    @GetMapping("/session/message/list")
    @Node(value = "user::ai::session::message::list", name = "获取 AI 会话消息列表")
    @ApiOperation("获取 AI 会话消息列表")
    public R<List<AiChatSessionMessage>> sessionMessageList(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                            Long sessionId) {
        return R.success(aiUserWebChatService.listSessionMessages(userDetails.getId(), sessionId));
    }

    @DeleteMapping("/session/{sessionId}")
    @Node(value = "user::ai::session::remove", name = "删除 AI 会话")
    @ApiOperation("删除 AI 会话")
    public R<Boolean> deleteSession(@AuthenticationPrincipal SohoUserDetails userDetails,
                                    @PathVariable Long sessionId) {
        return R.success(aiUserWebChatService.deleteSession(userDetails.getId(), sessionId));
    }

    @PutMapping("/session/rename")
    @Node(value = "user::ai::session::rename", name = "重命名 AI 会话")
    @ApiOperation("重命名 AI 会话")
    public R<AiChatSession> renameSession(@AuthenticationPrincipal SohoUserDetails userDetails,
                                          @RequestBody RenameAiChatSessionRequest request) {
        return R.success(aiUserWebChatService.renameSession(userDetails.getId(), request.getSessionId(), request.getTitle()));
    }

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    @Node(value = "user::ai::chat", name = "AI 聊天")
    @ApiOperation("AI 聊天")
    public R<AiChatResponse> chat(@AuthenticationPrincipal SohoUserDetails userDetails,
                                  @RequestBody UserAiChatRequest request) {
        return R.success(aiUserWebChatService.chat(userDetails.getId(), request));
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Node(value = "user::ai::chat::sse", name = "AI SSE流式聊天")
    @ApiOperation("AI SSE流式聊天")
    public Flux<ServerSentEvent<String>> sseChat(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                 @RequestBody UserAiChatRequest request) {
        return aiUserWebChatService.streamChat(userDetails.getId(), request)
                .map(payload -> ServerSentEvent.builder(payload).build());
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Node(value = "user::ai::chat::stream", name = "AI 流式聊天")
    @ApiOperation("AI 流式聊天")
    public Flux<ServerSentEvent<String>> streamChat(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                    @RequestBody UserAiChatRequest request) {
        return aiUserWebChatService.streamChat(userDetails.getId(), request)
                .map(payload -> ServerSentEvent.builder(payload).build());
    }

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Node(value = "user::ai::file::upload", name = "AI 文件上传")
    @ApiOperation("AI 文件上传")
    public R<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return R.success(aiFileService.uploadUserFile(file));
    }
}
