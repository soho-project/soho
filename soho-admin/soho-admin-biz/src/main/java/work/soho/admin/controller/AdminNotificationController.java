package work.soho.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Arrays;
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
import work.soho.admin.domain.AdminNotification;
import work.soho.admin.service.AdminNotificationService;

/**
 * 管理员 通知 Controller
 *
 * @author i
 * @date 2022-04-10 22:59:32
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminNotification" )
public class AdminNotificationController extends BaseController {

    private final AdminNotificationService adminNotificationService;

    /**
     * 查询管理员通知列表
     */
    @GetMapping("/list")
    public R<PageSerializable<AdminNotification>> list(AdminNotification adminNotification)
    {
        startPage();
        LambdaQueryWrapper<AdminNotification> lqw = new LambdaQueryWrapper<AdminNotification>();

        if (adminNotification.getId() != null){
            lqw.eq(AdminNotification::getId ,adminNotification.getId());
        }
        if (adminNotification.getAdminUserId() != null){
            lqw.eq(AdminNotification::getAdminUserId ,adminNotification.getAdminUserId());
        }
        if (StringUtils.isNotBlank(adminNotification.getTitle())){
            lqw.like(AdminNotification::getTitle ,adminNotification.getTitle());
        }
        if (adminNotification.getCreateAdminUserId() != null){
            lqw.eq(AdminNotification::getCreateAdminUserId ,adminNotification.getCreateAdminUserId());
        }
        if (StringUtils.isNotBlank(adminNotification.getContent())){
            lqw.like(AdminNotification::getContent ,adminNotification.getContent());
        }
        if (adminNotification.getCteatedTime() != null){
            lqw.eq(AdminNotification::getCteatedTime ,adminNotification.getCteatedTime());
        }
        if (adminNotification.getIsRead() != null){
            lqw.eq(AdminNotification::getIsRead ,adminNotification.getIsRead());
        }
        List<AdminNotification> list = adminNotificationService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取管理员通知详细信息
     */
    @GetMapping(value = "/{id}" )
    public R<AdminNotification> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminNotificationService.getById(id));
    }

    /**
     * 新增管理员通知
     */
    @PostMapping
    public R<Boolean> add(@RequestBody AdminNotification adminNotification) {
        return R.success(adminNotificationService.save(adminNotification));
    }

    /**
     * 修改管理员通知
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminNotification adminNotification) {
        return R.success(adminNotificationService.updateById(adminNotification));
    }

    /**
     * 删除管理员通知
     */
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminNotificationService.removeByIds(Arrays.asList(ids)));
    }
}
