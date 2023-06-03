package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.domain.AdminRoleResource;
import work.soho.admin.domain.AdminRoleUser;
import work.soho.admin.mapper.AdminUserMapper;
import work.soho.admin.service.AdminResourceService;
import work.soho.admin.service.AdminRoleUserService;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.domain.AdminUser;
import work.soho.api.admin.service.AdminInfoApiService;
import work.soho.api.admin.vo.AdminUserVo;
import work.soho.common.core.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService, AdminInfoApiService {
    private final AdminRoleUserService adminRoleUserService;
    private final AdminRoleResourceServiceImpl adminRoleResourceService;
    private final AdminResourceService adminResourceService;

    @Override
    public AdminUser getByLoginName(String loginName) {
        LambdaQueryWrapper<AdminUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AdminUser::getPhone, loginName);
        lambdaQueryWrapper.or().eq(AdminUser::getUsername, loginName);
        return getOne(lambdaQueryWrapper);
    }

    @Override
    public void saveOrUpdate(AdminUserVo adminUserVo) {
        AdminUser adminUser = getById(adminUserVo.getId());
        if(adminUser==null) {
            adminUser = new AdminUser();
        }
        BeanUtils.copyProperties(adminUserVo, adminUser);
        if(StringUtils.isNotEmpty(adminUser.getPassword())) {
            adminUser.setPassword(new BCryptPasswordEncoder().encode(adminUser.getPassword()));
        } else {
            adminUser.setPassword(null);
        }
        saveOrUpdate(adminUser);

        //授权用户角色信息
        List<AdminRoleUser> adminRoleList = adminRoleUserService.list(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, adminUser.getId()));
        List<Long> oldRoleIds = adminRoleList.stream().map(AdminRoleUser::getRoleId).collect(Collectors.toList());
        if(adminUserVo.getRoleIds() != null) {
            AdminUser finalAdminUser = adminUser;
            adminUserVo.getRoleIds().forEach(roleId -> {
                if(!oldRoleIds.contains(roleId)) {
                    //不包含该值，新增
                    AdminRoleUser adminRoleUser = new AdminRoleUser();
                    adminRoleUser.setUserId(finalAdminUser.getId());
                    adminRoleUser.setRoleId(roleId);
                    adminRoleUser.setCreatedTime(new Date());
                    adminRoleUser.setStatus(1);
                    adminRoleUserService.save(adminRoleUser);
                }
            });
            //删除更新中不存在的角色
            AdminUser finalAdminUser1 = adminUser;
            oldRoleIds.forEach(roleId -> {
                if(!adminUserVo.getRoleIds().contains(roleId)) {
                    adminRoleUserService.remove(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, finalAdminUser1.getId())
                            .eq(AdminRoleUser::getRoleId, roleId));
                }
            });
        }
    }

    /**
     * 获取用户资源
     *
     * @param uid
     * @return
     */
    public Map<String, AdminResource> getResourceByUid(Long uid) {
        LambdaQueryWrapper<AdminRoleUser> ruLqw = new LambdaQueryWrapper();
        ruLqw.eq(AdminRoleUser::getUserId, uid);
        List<AdminRoleUser> roleUsers = adminRoleUserService.list(ruLqw);
        if(roleUsers.size() == 0) {
            return null;
        }
        List<Long> roleIds = roleUsers.stream().map(item->item.getRoleId()).collect(Collectors.toList());
        //获取角色对应的资源ID
        LambdaQueryWrapper<AdminRoleResource> arLqw = new LambdaQueryWrapper<>();
        arLqw.in(AdminRoleResource::getRoleId, roleIds);
        List<AdminRoleResource> adminRoleResourcesList = adminRoleResourceService.list(arLqw);
        if(adminRoleResourcesList.size() == 0) {
            return null;
        }
        List<Long> resourceIds = adminRoleResourcesList.stream().map(item->item.getResourceId()).collect(Collectors.toList());
        //获取菜单信息
        LambdaQueryWrapper<AdminResource> adminResourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminResourceLambdaQueryWrapper.in(AdminResource::getId, resourceIds);
        List<AdminResource> list = adminResourceService.list(adminResourceLambdaQueryWrapper);
        return list.stream().collect(Collectors.toMap(AdminResource::getRoute, v->v));
    }

    @Override
    public AdminUserVo getAdminById(Long id) {
        AdminUserVo adminUserVo = new AdminUserVo();
        BeanUtils.copyProperties(getById(id), adminUserVo);
        return adminUserVo;
    }
}
