package work.soho.admin.biz.controller;

import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.AdminNotificationCreateRequest;
import work.soho.admin.api.vo.AdminNotificationVo;
import work.soho.admin.biz.domain.AdminNotification;
import work.soho.admin.biz.service.AdminNotificationReceiverService;
import work.soho.admin.biz.service.AdminNotificationService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.Arrays;

/**
 * 后台通知管理接口。
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/admin/adminNotification")
@Api(tags = "系统消息")
public class AdminNotificationController extends BaseController {
    private static final String RECEIVER_TYPE_ADMIN = "admin";

    private final AdminNotificationService adminNotificationService;
    private final AdminNotificationReceiverService adminNotificationReceiverService;

    /**
     * 查询通知管理列表。
     */
    @Node("adminNotification:list")
    @GetMapping("/list")
    public R<PageSerializable<AdminNotificationVo>> list(AdminNotification adminNotification) {
        startPage();
        return R.success(adminNotificationService.listNotifications(adminNotification));
    }

    /**
     * 查询当前后台用户未读通知。
     */
    @Node("adminNotification:myNotification")
    @GetMapping("/myNotification")
    public R<PageSerializable<AdminNotificationVo>> myNotification(@AuthenticationPrincipal SohoUserDetails userDetails) {
        startPage();
        return R.success(adminNotificationService.myNotifications(RECEIVER_TYPE_ADMIN, userDetails.getId()));
    }

    /**
     * 获取通知详情。
     */
    @Node("adminNotification:getInfo")
    @GetMapping("/{id}")
    public R<AdminNotificationVo> getInfo(@PathVariable("id") Long id) {
        return R.success(adminNotificationService.getNotificationDetail(id));
    }

    /**
     * 新增通知。
     */
    @Node("adminNotification:add")
    @PostMapping
    public R<Boolean> add(@RequestBody AdminNotificationCreateRequest request,
                          @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(adminNotificationService.createNotification(request, userDetails));
    }

    /**
     * 修改通知主体内容。
     */
    @Node("adminNotification:edit")
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminNotification adminNotification) {
        return R.success(adminNotificationService.updateById(adminNotification));
    }

    /**
     * 删除通知及其接收记录。
     */
    @Node("adminNotification:remove")
    @DeleteMapping("/{ids}")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        adminNotificationReceiverService.remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<work.soho.admin.biz.domain.AdminNotificationReceiver>()
                .in(work.soho.admin.biz.domain.AdminNotificationReceiver::getNotificationId, Arrays.asList(ids)));
        return R.success(adminNotificationService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 标记当前后台用户指定通知已读。
     */
    @Node("adminNotification:read")
    @ApiOperation("已读消息标记")
    @GetMapping("/read/{ids}")
    public R<Boolean> read(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(adminNotificationService.readReceivers(RECEIVER_TYPE_ADMIN, userDetails.getId(), Arrays.asList(ids)));
    }

    /**
     * 标记当前后台用户全部未读通知为已读。
     */
    @Node("adminNotification:readAll")
    @ApiOperation("全部已读消息标记")
    @GetMapping("readAll")
    public R<Boolean> readAll(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(adminNotificationService.readAll(RECEIVER_TYPE_ADMIN, userDetails.getId()));
    }
}
