package work.soho.admin.biz.controller.user;

import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.vo.AdminNotificationVo;
import work.soho.admin.biz.service.AdminNotificationService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.Arrays;

/**
 * 用户通知接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user/notification")
@Api(tags = "用户通知")
public class UserNotificationController {
    private static final String RECEIVER_TYPE_USER = "user";

    private final AdminNotificationService adminNotificationService;

    /**
     * 查询当前用户未读通知。
     */
    @GetMapping("/myNotification")
    @Node(value = "user::notification::myNotification", name = "查询当前用户未读通知")
    @ApiOperation("查询当前用户未读通知")
    public R<PageSerializable<AdminNotificationVo>> myNotification(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(adminNotificationService.myNotifications(RECEIVER_TYPE_USER, userDetails.getId()));
    }

    /**
     * 批量标记当前用户通知已读。
     */
    @GetMapping("/read/{ids}")
    @Node(value = "user::notification::read", name = "用户通知已读")
    @ApiOperation("批量标记当前用户通知已读")
    public R<Boolean> read(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(adminNotificationService.readReceivers(RECEIVER_TYPE_USER, userDetails.getId(), Arrays.asList(ids)));
    }

    /**
     * 一键已读当前用户全部未读通知。
     */
    @GetMapping("/readAll")
    @Node(value = "user::notification::readAll", name = "当前用户全部通知已读")
    @ApiOperation("一键已读当前用户全部未读通知")
    public R<Boolean> readAll(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(adminNotificationService.readAll(RECEIVER_TYPE_USER, userDetails.getId()));
    }
}
