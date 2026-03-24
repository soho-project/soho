package work.soho.ai.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.List;

@Api(tags = "用户 AI API 调用日志")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/user/apiCallLog")
public class UserAiApiCallLogController {
    private final AiApiCallLogService aiApiCallLogService;

    @GetMapping("/list")
    @Node(value = "user::ai::apiCallLog::list", name = "获取 AI API 调用日志")
    @ApiOperation("获取 AI API 调用日志")
    public R<List<AiApiCallLog>> list(@AuthenticationPrincipal SohoUserDetails userDetails) {
        List<AiApiCallLog> list = aiApiCallLogService.list(new LambdaQueryWrapper<AiApiCallLog>()
                .eq(AiApiCallLog::getUserId, userDetails.getId())
                .orderByDesc(AiApiCallLog::getId));
        return R.success(list);
    }
}
