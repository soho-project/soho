package work.soho.admin.biz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.admin.biz.domain.AdminNotification;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.service.AdminNotificationService;
import work.soho.admin.biz.service.AdminUserService;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.AdminNotificationCreateRequest;
import work.soho.admin.api.vo.AdminNotificationVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员 通知 Controller
 *
 * @author i
 * @date 2022-04-10 22:59:32
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/admin/adminNotification" )
@Api(tags = "系统消息")
public class AdminNotificationController extends BaseController {

    private final AdminNotificationService adminNotificationService;
    private final AdminUserService adminUserService;

    /**
     * 查询管理员通知列表
     */
    @Node("adminNotification:list")
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
        PageSerializable pageSerializable = new PageSerializable<>(list);
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
    @Node("adminNotification:myNotification")
    @GetMapping("/myNotification")
    public R<PageSerializable<AdminNotificationVo>> myNotification(AdminNotification adminNotification)
    {
        startPage();
        LambdaQueryWrapper<AdminNotification> lqw = new LambdaQueryWrapper<AdminNotification>();
        lqw.eq(AdminNotification::getAdminUserId, SecurityUtils.getLoginUserId());
        lqw.eq(AdminNotification::getIsRead, 0);
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
        } else {
            pageSerializable.setList(new ArrayList());
        }

        return R.success(pageSerializable);
    }

    /**
     * 获取管理员通知详细信息
     */
    @Node("adminNotification:getInfo")
    @GetMapping(value = "/{id}" )
    public R<AdminNotification> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminNotificationService.getById(id));
    }

    /**
     * 新增管理员通知
     */
    @Node("adminNotification:add")
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
    @Node("adminNotification:edit")
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminNotification adminNotification) {
        return R.success(adminNotificationService.updateById(adminNotification));
    }

    /**
     * 删除管理员通知
     */
    @Node("adminNotification:remove")
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminNotificationService.removeByIds(Arrays.asList(ids)));
    }

    @Node("adminNotification:read")
    @ApiOperation("已读消息标记")
    @GetMapping("/read/{ids}")
    public R<Boolean> read(@PathVariable Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            AdminNotification adminNotification = adminNotificationService.getById(ids[i]);
            if(adminNotification == null) continue;
            adminNotification.setIsRead(1);
            adminNotificationService.updateById(adminNotification);
        }
        return R.success();
    }

    @Node("adminNotification:readAll")
    @ApiOperation("已读消息标记")
    @GetMapping("readAll")
    public R<Boolean> readAll() {
        LambdaQueryWrapper<AdminNotification> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AdminNotification::getAdminUserId, SecurityUtils.getLoginUserId());
        lqw.eq(AdminNotification::getIsRead, 0);

        AdminNotification adminNotification = new AdminNotification();
        adminNotification.setIsRead(1);

        return R.success(adminNotificationService.update(adminNotification, lqw));
    }
}
