package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import work.soho.admin.domain.AdminRole;
import work.soho.admin.domain.AdminRoleUser;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminRoleService;
import work.soho.admin.service.AdminRoleUserService;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.service.impl.TokenServiceImpl;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.api.admin.result.AdminPage;
import work.soho.api.admin.vo.AdminUserVo;
import work.soho.common.core.result.R;
import work.soho.common.data.upload.utils.UploadUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/user")
public class AdminUserController extends BaseController {
    private final TokenServiceImpl tokenService;
    private final AdminUserService adminUserService;
    private final AdminRoleUserService adminRoleUserService;
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @GetMapping("/user")
    public R user() {
        UserDetailsServiceImpl.UserDetailsImpl userDetails = tokenService.getLoginUser();
        AdminUser adminUser = adminUserService.getById(userDetails.getId());
        return R.ok(adminUser);
    }

    @GetMapping("list")
    public AdminPage<AdminUserVo> list(AdminUserVo adminUserVo, Date startDate
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
        return new AdminPage<AdminUserVo>().setTotal(((Page<AdminUser>)list).getTotal()).setData(voList);
    }

    @PutMapping()
    public Object update(@RequestBody AdminUserVo adminUserVo) {
        AdminUser adminUser = adminUserService.getById(adminUserVo.getId());
        if(adminUser==null) {
            return R.error("没有找到对应的用户");
        }
        BeanUtils.copyProperties(adminUserVo, adminUser);
        adminUserService.updateById(adminUser);

        //授权用户角色信息
        List<AdminRoleUser> adminRoleList = adminRoleUserService.list(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, adminUser.getId()));
        List<Long> oldRoleIds = adminRoleList.stream().map(AdminRoleUser::getRoleId).collect(Collectors.toList());
        if(adminUserVo.getRoleIds() != null) {
            adminUserVo.getRoleIds().forEach(roleId -> {
                if(!oldRoleIds.contains(roleId)) {
                    //不包含该值，新增
                    AdminRoleUser adminRoleUser = new AdminRoleUser();
                    adminRoleUser.setUserId(adminUser.getId());
                    adminRoleUser.setRoleId(roleId);
                    adminRoleUser.setCreatedTime(new Date());
                    adminRoleUser.setStatus(1);
                    adminRoleUserService.save(adminRoleUser);
                }
            });
            //删除更新中不存在的角色
            oldRoleIds.forEach(roleId -> {
                if(!adminUserVo.getRoleIds().contains(roleId)) {
                    adminRoleUserService.remove(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, adminUser.getId())
                            .eq(AdminRoleUser::getRoleId, roleId));
                }
            });
        }

        return R.ok("保存成功");
    }

    @PostMapping()
    public Object create(@RequestBody AdminUserVo adminUserVo) {
        //检查用户是否重复
        if(
        adminUserService.getOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getPhone, adminUserVo.getPhone())
                .or().eq(AdminUser::getEmail, adminUserVo.getEmail())
        )
        != null
        ) {
            return R.error("手机号/Email不能重复");
        }
        AdminUser adminUser = new AdminUser();
        BeanUtils.copyProperties(adminUserVo, adminUser);
        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());
        adminUserService.save(adminUser);

        //授权用户角色信息
        List<AdminRoleUser> adminRoleList = adminRoleUserService.list(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, adminUser.getId()));
        List<Long> oldRoleIds = adminRoleList.stream().map(AdminRoleUser::getRoleId).collect(Collectors.toList());
        if(adminUserVo.getRoleIds() != null) {
            adminUserVo.getRoleIds().forEach(roleId -> {
                if(!oldRoleIds.contains(roleId)) {
                    //不包含该值，新增
                    AdminRoleUser adminRoleUser = new AdminRoleUser();
                    adminRoleUser.setUserId(adminUser.getId());
                    adminRoleUser.setRoleId(roleId);
                    adminRoleUser.setCreatedTime(new Date());
                    adminRoleUser.setStatus(1);
                    adminRoleUserService.save(adminRoleUser);
                }
            });
            //删除更新中不存在的角色
            oldRoleIds.forEach(roleId -> {
                if(!adminUserVo.getRoleIds().contains(roleId)) {
                    adminRoleUserService.remove(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, adminUser.getId())
                            .eq(AdminRoleUser::getRoleId, roleId));
                }
            });
        }
        return R.ok("保存成功");
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
        return R.ok(adminUserVo);
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
        return R.ok("保存成功");
    }

    @ApiOperation("用户头像上传接口")
    @PostMapping("/upload-avatar")
    public Object uploadAvatar(@RequestParam("avatar")CommonsMultipartFile file) {
        try {
            String url = UploadUtils.upload("user/avatar" + file.getName(), file.getInputStream());
            return R.ok(url);
        } catch (IOException ioException) {
            logger.error(ioException.toString());
            return R.error("文件上传失败");
        }
    }
}
