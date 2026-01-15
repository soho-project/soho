package work.soho.open.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.dto.HourCountDTO;
import work.soho.open.biz.service.OpenApiCallLogService;
import work.soho.open.biz.service.OpenAppService;

import java.util.ArrayList;
import java.util.List;

;

/**
 * API调用日志Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("open/user/openApiCallLog" )
public class UserOpenApiCallLogController {

    private final OpenApiCallLogService openApiCallLogService;

    private final OpenAppService openAppService;

    /**
     * 查询API调用日志列表
     */
    @GetMapping("/list")
    @Node(value = "user::openApiCallLog::list", name = "获取 API调用日志 列表")
    public R<PageSerializable<OpenApiCallLog>> list(OpenApiCallLog openApiCallLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        List<Long> appIds = openAppService.getOpenAppIdsByUserId(userDetails.getId());

        PageUtils.startPage();
        LambdaQueryWrapper<OpenApiCallLog> lqw = new LambdaQueryWrapper<OpenApiCallLog>();
        lqw.eq(openApiCallLog.getId() != null, OpenApiCallLog::getId ,openApiCallLog.getId());
        lqw.in(OpenApiCallLog::getAppId , appIds.size()>0?appIds:new ArrayList<>());
        lqw.eq(openApiCallLog.getApiId() != null, OpenApiCallLog::getApiId ,openApiCallLog.getApiId());
        lqw.like(StringUtils.isNotBlank(openApiCallLog.getRequestId()),OpenApiCallLog::getRequestId ,openApiCallLog.getRequestId());
        lqw.like(StringUtils.isNotBlank(openApiCallLog.getClientIp()),OpenApiCallLog::getClientIp ,openApiCallLog.getClientIp());
        lqw.eq(openApiCallLog.getResponseCode() != null, OpenApiCallLog::getResponseCode ,openApiCallLog.getResponseCode());
        lqw.eq(openApiCallLog.getCostMs() != null, OpenApiCallLog::getCostMs ,openApiCallLog.getCostMs());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<OpenApiCallLog> list = openApiCallLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 统计当前用户今天的API调用次数
     */
    @GetMapping("/statisticsToday")
    public R<Long> statisticsToday(@AuthenticationPrincipal SohoUserDetails userDetails) {
        List<Long> appIds = openAppService.getOpenAppIdsByUserId(userDetails.getId());
        return R.success(openApiCallLogService.statisticsToday(appIds));
    }

    /**
     * 统计当前用户api总调用数
     */
    @GetMapping("/statisticsTotal")
    public R<Long> statisticsTotal(@AuthenticationPrincipal SohoUserDetails userDetails) {
        List<Long> appIds = openAppService.getOpenAppIdsByUserId(userDetails.getId());
        return R.success(openApiCallLogService.statisticsTotal(appIds));
    }

    /**
     * statisticsLast24Hour
     */
    @GetMapping("/statisticsLast24Hour")
    public R<List<HourCountDTO>> statisticsLast24Hour(@AuthenticationPrincipal SohoUserDetails userDetails) {
        List<Long> appIds = openAppService.getOpenAppIdsByUserId(userDetails.getId());
        return R.success(openApiCallLogService.statisticsLast24HourList(appIds));
    }
}