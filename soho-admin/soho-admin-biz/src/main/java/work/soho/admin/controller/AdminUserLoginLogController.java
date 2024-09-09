package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.domain.AdminUserLoginLog;
import work.soho.admin.service.AdminUserLoginLogService;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 用户登录日志Controller
 *
 * @author fang
 */
@Api(tags = "用户登录日志管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/admin/adminUserLoginLog" )
public class AdminUserLoginLogController {

    private final AdminUserLoginLogService adminUserLoginLogService;

    /**
     * 查询用户登录日志列表
     */
    @GetMapping("/list")
    @Node(value = "adminUserLoginLog::list", name = "用户登录日志列表")
    public R<PageSerializable<AdminUserLoginLog>> list(AdminUserLoginLog adminUserLoginLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AdminUserLoginLog> lqw = new LambdaQueryWrapper<AdminUserLoginLog>();
        if (adminUserLoginLog.getId() != null){
            lqw.eq(AdminUserLoginLog::getId ,adminUserLoginLog.getId());
        }
        if (adminUserLoginLog.getAdminUserId() != null){
            lqw.eq(AdminUserLoginLog::getAdminUserId ,adminUserLoginLog.getAdminUserId());
        }
        if (adminUserLoginLog.getClientIp() != null){
            lqw.eq(AdminUserLoginLog::getClientIp ,adminUserLoginLog.getClientIp());
        }
        if (adminUserLoginLog.getClientUserAgent() != null){
            lqw.eq(AdminUserLoginLog::getClientUserAgent ,adminUserLoginLog.getClientUserAgent());
        }
        if (adminUserLoginLog.getToken() != null){
            lqw.eq(AdminUserLoginLog::getToken ,adminUserLoginLog.getToken());
        }
        if (adminUserLoginLog.getCreatedTime() != null){
            lqw.eq(AdminUserLoginLog::getCreatedTime ,adminUserLoginLog.getCreatedTime());
        }
        lqw.orderByDesc(AdminUserLoginLog::getId);
        List<AdminUserLoginLog> list = adminUserLoginLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户登录日志详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminUserLoginLog::getInfo", name = "用户登录日志详细信息")
    public R<AdminUserLoginLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminUserLoginLogService.getById(id));
    }

    /**
     * 新增用户登录日志
     */
    @PostMapping
    @Node(value = "adminUserLoginLog::add", name = "用户登录日志新增")
    public R<Boolean> add(@RequestBody AdminUserLoginLog adminUserLoginLog) {
        return R.success(adminUserLoginLogService.save(adminUserLoginLog));
    }

    /**
     * 修改用户登录日志
     */
    @PutMapping
    @Node(value = "adminUserLoginLog::edit", name = "用户登录日志修改")
    public R<Boolean> edit(@RequestBody AdminUserLoginLog adminUserLoginLog) {
        return R.success(adminUserLoginLogService.updateById(adminUserLoginLog));
    }

    /**
     * 删除用户登录日志
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminUserLoginLog::remove", name = "用户登录日志删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminUserLoginLogService.removeByIds(Arrays.asList(ids)));
    }
}
