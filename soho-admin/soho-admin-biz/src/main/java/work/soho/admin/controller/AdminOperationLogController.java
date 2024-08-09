package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.domain.AdminOperationLog;
import work.soho.admin.service.AdminOperationLogService;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 管理操作日志Controller
 *
 * @author fang
 */
@Api(tags = "系统管理操作日志")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminOperationLog" )
public class AdminOperationLogController {

    private final AdminOperationLogService adminOperationLogService;

    /**
     * 查询管理操作日志列表
     */
    @GetMapping("/list")
    @Node(value = "adminOperationLog::list", name = "管理操作日志列表")
    public R<PageSerializable<AdminOperationLog>> list(AdminOperationLog adminOperationLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AdminOperationLog> lqw = new LambdaQueryWrapper<AdminOperationLog>();
        lqw.eq(adminOperationLog.getId() != null, AdminOperationLog::getId ,adminOperationLog.getId());
        lqw.eq(adminOperationLog.getAdminUserId() != null,AdminOperationLog::getAdminUserId ,adminOperationLog.getAdminUserId());
        lqw.like(StringUtils.isNotBlank(adminOperationLog.getMethod()),AdminOperationLog::getMethod ,adminOperationLog.getMethod());
        lqw.like(StringUtils.isNotBlank(adminOperationLog.getPath()),AdminOperationLog::getPath ,adminOperationLog.getPath());
        lqw.like(StringUtils.isNotBlank(adminOperationLog.getParams()),AdminOperationLog::getParams ,adminOperationLog.getParams());
        lqw.like(StringUtils.isNotBlank(adminOperationLog.getContent()),AdminOperationLog::getContent ,adminOperationLog.getContent());
        lqw.like(StringUtils.isNotBlank(adminOperationLog.getResponse()),AdminOperationLog::getResponse ,adminOperationLog.getResponse());
        lqw.eq(adminOperationLog.getUpdatedTime() != null, AdminOperationLog::getUpdatedTime ,adminOperationLog.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AdminOperationLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AdminOperationLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AdminOperationLog::getId);
        List<AdminOperationLog> list = adminOperationLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取管理操作日志详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminOperationLog::getInfo", name = "管理操作日志详细信息")
    public R<AdminOperationLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminOperationLogService.getById(id));
    }

    /**
     * 新增管理操作日志
     */
    @PostMapping
    @Node(value = "adminOperationLog::add", name = "管理操作日志新增")
    public R<Boolean> add(@RequestBody AdminOperationLog adminOperationLog) {
        return R.success(adminOperationLogService.save(adminOperationLog));
    }

    /**
     * 修改管理操作日志
     */
    @PutMapping
    @Node(value = "adminOperationLog::edit", name = "管理操作日志修改")
    public R<Boolean> edit(@RequestBody AdminOperationLog adminOperationLog) {
        return R.success(adminOperationLogService.updateById(adminOperationLog));
    }

    /**
     * 删除管理操作日志
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminOperationLog::remove", name = "管理操作日志删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminOperationLogService.removeByIds(Arrays.asList(ids)));
    }
}