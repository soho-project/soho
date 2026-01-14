package work.soho.open.biz.controller.user;

import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.service.OpenApiCallLogService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import work.soho.common.security.userdetails.SohoUserDetails;
import org.springframework.util.Assert;

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

    /**
     * 查询API调用日志列表
     */
    @GetMapping("/list")
    @Node(value = "user::openApiCallLog::list", name = "获取 API调用日志 列表")
    public R<PageSerializable<OpenApiCallLog>> list(OpenApiCallLog openApiCallLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApiCallLog> lqw = new LambdaQueryWrapper<OpenApiCallLog>();
        lqw.eq(openApiCallLog.getId() != null, OpenApiCallLog::getId ,openApiCallLog.getId());
        lqw.eq(openApiCallLog.getAppId() != null, OpenApiCallLog::getAppId ,openApiCallLog.getAppId());
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
     * 获取API调用日志详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openApiCallLog::getInfo", name = "获取 API调用日志 详细信息")
    public R<OpenApiCallLog> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenApiCallLog openApiCallLog = openApiCallLogService.getById(id);
        if (!openApiCallLog.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(openApiCallLog);
    }

}