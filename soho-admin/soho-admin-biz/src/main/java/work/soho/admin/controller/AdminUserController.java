package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import work.soho.admin.domain.AdminRole;
import work.soho.admin.domain.AdminRoleUser;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminRoleService;
import work.soho.admin.service.AdminRoleUserService;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.api.admin.result.AdminPage;
import work.soho.api.admin.vo.AdminUserVo;
import work.soho.common.core.result.R;
import work.soho.common.data.upload.utils.UploadUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/user")
public class AdminUserController extends BaseController {
    private final UserDetailsServiceImpl userDetailsService;
    private final AdminUserService adminUserService;
    private final AdminRoleUserService adminRoleUserService;
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @GetMapping("/user")
    public R<AdminUser> user() {
        UserDetailsServiceImpl.UserDetailsImpl userDetails = (UserDetailsServiceImpl.UserDetailsImpl) userDetailsService.getLoginUserDetails();
        AdminUser adminUser = adminUserService.getById(userDetails.getId());
        return R.success(adminUser);
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
        PageSerializable pageSerializable = new PageSerializable(voList);
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
    public Object uploadAvatar(@RequestParam("avatar")CommonsMultipartFile file) {
        try {
            String url = UploadUtils.upload("user/avatar" + file.getName(), file.getInputStream());
            return R.success(url);
        } catch (IOException ioException) {
            logger.error(ioException.toString());
            return R.error("文件上传失败");
        }
    }
}
