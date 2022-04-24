package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.domain.AdminRole;
import work.soho.admin.domain.AdminRoleResource;
import work.soho.admin.domain.AdminRoleUser;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminRoleResourceService;
import work.soho.admin.service.AdminRoleService;
import work.soho.admin.service.AdminRoleUserService;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.api.admin.vo.AdminUserOptionVo;
import work.soho.api.admin.vo.AdminUserVo;
import work.soho.api.admin.vo.CurrentAdminUserVo;
import work.soho.common.core.result.R;
import work.soho.common.data.upload.utils.UploadUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminUser")
public class AdminUserController extends BaseController {
    private final UserDetailsServiceImpl userDetailsService;
    private final AdminUserService adminUserService;
    private final AdminRoleService adminRoleService;
    private final AdminRoleUserService adminRoleUserService;
    private final AdminRoleResourceService adminRoleResourceService;
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @GetMapping("/user")
    public R<CurrentAdminUserVo> user() {
        try {
            UserDetailsServiceImpl.UserDetailsImpl userDetails = (UserDetailsServiceImpl.UserDetailsImpl) userDetailsService.getLoginUserDetails();
            AdminUser adminUser = adminUserService.getById(userDetails.getId());
            //获取当前用户所有角色下的资源ID
            List<AdminRoleUser> userRoleList = adminRoleUserService.list(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, adminUser.getId())
                    .eq(AdminRoleUser::getStatus, 1));
            List<Long> adminRoleIds = userRoleList.stream().map(AdminRoleUser::getRoleId).collect(Collectors.toList());

            //获取ID最小的角色
            Long minRoleId = java.util.Collections.min(adminRoleIds);
            AdminRole adminRole = adminRoleService.getById(Math.toIntExact(minRoleId));

            //获取用户资源
            List<Long> userResourceIds = new ArrayList<>();
            if(!adminRoleIds.isEmpty()) {
                userResourceIds = adminRoleResourceService.list(new LambdaQueryWrapper<AdminRoleResource>()
                        .in(AdminRoleResource::getRoleId, adminRoleIds)).stream().map(AdminRoleResource::getResourceId).collect(Collectors.toList());
            }

            CurrentAdminUserVo currentAdminUserVo = new CurrentAdminUserVo();
            currentAdminUserVo.setId(adminUser.getId());
            currentAdminUserVo.setUsername(adminUser.getUsername());
            currentAdminUserVo.setAvatar(adminUser.getAvatar());
            currentAdminUserVo.setPermissions(new CurrentAdminUserVo.Permissions().setRole(adminRole.getName()).setVisit((ArrayList<Long>) userResourceIds));
            return R.success(currentAdminUserVo);
        } catch (Exception e) {
            return R.error("请登录");
        }
    }

    @ApiOperation("用户选项接口")
    @GetMapping("userOptions")
    public R<List<AdminUserOptionVo>> userOption() {
        List<AdminUser> adminUserList = adminUserService.list();
        List<AdminUserOptionVo> list = (List<AdminUserOptionVo>) work.soho.common.core.util.BeanUtils.copyList(adminUserList, AdminUserOptionVo.class);
        return R.success(list);
    }

    @GetMapping("list")
    public R<PageSerializable<AdminUserVo>> list(AdminUserVo adminUserVo, Date startDate
            ,Date endDate) {
        LambdaQueryWrapper<AdminUser> lqw = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(adminUserVo.getUsername())) {
            lqw.like(AdminUser::getUsername, adminUserVo.getUsername());
        }
        if(startDate!=null) {
            lqw.ge(AdminUser::getCreatedTime, startDate);
        }
        if(endDate!=null) {
            lqw.le(AdminUser::getCreatedTime, endDate);
        }

        lqw.eq(AdminUser::getIsDeleted, 0);

        startPage();
        List<AdminUser> list = adminUserService.list(lqw);
        List<AdminUserVo> voList = new ArrayList<>();
        list.forEach(item->{
            AdminUserVo vo = new AdminUserVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        });
        PageSerializable<AdminUserVo> pageSerializable = new PageSerializable<>(voList);
        pageSerializable.setTotal(((Page<AdminUser>)list).getTotal());
        return R.success(pageSerializable);
    }

    @PutMapping()
    public Object update(@RequestBody AdminUserVo adminUserVo) {
        try {
            adminUserService.saveOrUpdate(adminUserVo);
            return R.success("保存成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping()
    public Object create(@RequestBody AdminUserVo adminUserVo) {
        try {
            adminUserService.saveOrUpdate(adminUserVo);
            return R.success("保存成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    @ApiOperation("用户详细信息")
    @GetMapping()
    public R<AdminUserVo> details(Long id) {
        AdminUserVo adminUserVo = new AdminUserVo();
        AdminUser adminUser = adminUserService.getById(id);
        BeanUtils.copyProperties(adminUser, adminUserVo);
        //获取关联角色
        List<Long> roleIds = adminRoleUserService.list(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, id)).stream()
                .map(AdminRoleUser::getRoleId).collect(Collectors.toList());
        adminUserVo.setRoleIds(roleIds);
        return R.success(adminUserVo);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public Object delete(@PathVariable("id") Long id) {
        AdminUser adminUser = adminUserService.getById(id);
        if(adminUser==null) {
            return R.error("没有找到对应的用户");
        }
        adminUser.setIsDeleted(1);
        adminUserService.updateById(adminUser);
        return R.success("保存成功");
    }

    @ApiOperation("用户头像上传接口")
    @PostMapping("/upload-avatar")
    public Object uploadAvatar(@RequestParam(value = "avatar")MultipartFile file) {
        try {
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
            if(!mimeType.getType().equals("image")) {
                return R.error("请传递正确的图片格式");
            }
            System.out.println(file.getContentType());
            String url = UploadUtils.upload("user/avatar", file);
            return R.success(url);
        } catch (Exception ioException) {
            logger.error(ioException.toString());
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }
}
