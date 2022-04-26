package work.soho.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.utils.SecurityUtils;
import work.soho.api.admin.request.AdminNotificationCreateRequest;
import work.soho.api.admin.vo.AdminNotificationVo;
import work.soho.common.core.util.BeanUtils;
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
    private final AdminUserService adminUserService;

    /**
     * 查询管理员通知列表
     */
    @GetMapping("/list")
    public R<PageSerializable<AdminNotificationVo>> list(AdminNotification adminNotification)
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
        if (adminNotification.getCreatedTime() != null){
            lqw.eq(AdminNotification::getCreatedTime ,adminNotification.getCreatedTime());
        }
        if (adminNotification.getIsRead() != null){
            lqw.eq(AdminNotification::getIsRead ,adminNotification.getIsRead());
        }
        List<AdminNotification> list = adminNotificationService.list(lqw);
        PageSerializable pageSerializable = new PageSerializable<>();
        if(!list.isEmpty()) {
            //获取所有的管理员ID
            List<Long> adminUserIds = list.stream().map(AdminNotification::getAdminUserId).collect(Collectors.toList());
            adminUserIds.addAll(list.stream().map(AdminNotification::getCreateAdminUserId).collect(Collectors.toList()));
            HashMap<Long, String> adminUserHashMap = new HashMap<>();
            adminUserService.list(new LambdaQueryWrapper<AdminUser>().in(AdminUser::getId, adminUserIds)).forEach(user->{
                adminUserHashMap.put(user.getId(), user.getUsername());
            });
            List<Object> data = list.stream().map(item->{
                AdminNotificationVo adminNotificationVo = BeanUtils.copy(item, AdminNotificationVo.class);
                adminNotificationVo.setAdminUser(adminUserHashMap.get(item.getAdminUserId()));
                adminNotificationVo.setCreateAdminUser(adminUserHashMap.get(item.getCreateAdminUserId()));
                return adminNotificationVo;
            }).collect(Collectors.toList());
            pageSerializable.setList(data);
        }

        return R.success(pageSerializable);
    }

    /**
     * 查询当前用户消息
     */
    @GetMapping("/myNotification")
    public R<PageSerializable<AdminNotificationVo>> myNotification(AdminNotification adminNotification)
    {
        startPage();
        LambdaQueryWrapper<AdminNotification> lqw = new LambdaQueryWrapper<AdminNotification>();
        lqw.eq(AdminNotification::getAdminUserId, SecurityUtils.getLoginUserId());
        List<AdminNotification> list = adminNotificationService.list(lqw);
        PageSerializable pageSerializable = new PageSerializable<>();
        if(!list.isEmpty()) {
            //获取所有的管理员ID
            List<Long> adminUserIds = list.stream().map(AdminNotification::getAdminUserId).collect(Collectors.toList());
            adminUserIds.addAll(list.stream().map(AdminNotification::getCreateAdminUserId).collect(Collectors.toList()));
            HashMap<Long, String> adminUserHashMap = new HashMap<>();
            adminUserService.list(new LambdaQueryWrapper<AdminUser>().in(AdminUser::getId, adminUserIds)).forEach(user->{
                adminUserHashMap.put(user.getId(), user.getUsername());
            });
            List<Object> data = list.stream().map(item->{
                AdminNotificationVo adminNotificationVo = BeanUtils.copy(item, AdminNotificationVo.class);
                adminNotificationVo.setAdminUser(adminUserHashMap.get(item.getAdminUserId()));
                adminNotificationVo.setCreateAdminUser(adminUserHashMap.get(item.getCreateAdminUserId()));
                return adminNotificationVo;
            }).collect(Collectors.toList());
            pageSerializable.setList(data);
        }

        return R.success(pageSerializable);
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
    public R<Boolean> add(@RequestBody AdminNotificationCreateRequest adminNotificationCreateRequest) {
        Long[] adminUserIds = adminNotificationCreateRequest.getAdminUserIds();
        if(adminUserIds != null && adminUserIds.length>0) {
            for (int i = 0; i < adminUserIds.length; i++) {
                AdminNotification adminNotification = new AdminNotification();
                adminNotification.setTitle(adminNotificationCreateRequest.getTitle());
                adminNotification.setContent(adminNotificationCreateRequest.getContent());
                adminNotification.setCreateAdminUserId(SecurityUtils.getLoginUserId());
                adminNotification.setAdminUserId(adminUserIds[i]);
                adminNotification.setCreatedTime(LocalDateTime.now());
                adminNotificationService.save(adminNotification);
            }

        }
        return R.success(true);
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
