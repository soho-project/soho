package work.soho.ai.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.enums.AiApiCallLogEnums;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.List;

@Api(tags = "用户 AI API 调用日志")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/user/apiCallLog")
/**
 * 用户 AI API 调用日志控制器。
 */
public class UserAiApiCallLogController {
    private final AiApiCallLogService aiApiCallLogService;

    /**
     * 查询当前登录用户的 AI API 调用成功记录。
     *
     * @param keyId API Key ID
     * @param betweenCreatedTimeRequest 创建时间范围
     * @param userDetails 当前登录用户
     * @return 调用成功的日志列表
     */
    @GetMapping("/list")
    @Node(value = "user::ai::apiCallLog::list", name = "获取 AI API 调用日志")
    @ApiOperation("获取 AI API 调用日志")
    public R<List<AiApiCallLog>> list(@RequestParam(value = "keyId", required = false) Long keyId,
                                      BetweenCreatedTimeRequest betweenCreatedTimeRequest,
                                      @AuthenticationPrincipal SohoUserDetails userDetails) {
        List<AiApiCallLog> list = aiApiCallLogService.list(new LambdaQueryWrapper<AiApiCallLog>()
                .eq(AiApiCallLog::getUserId, userDetails.getId())
                .eq(keyId != null, AiApiCallLog::getApiKeyId, keyId)
                .eq(AiApiCallLog::getStatus, AiApiCallLogEnums.Status.SUCCESS.getId())
                .ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null,
                        AiApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime())
                .lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null,
                        AiApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime())
                .orderByDesc(AiApiCallLog::getId));
        return R.success(list);
    }
}
