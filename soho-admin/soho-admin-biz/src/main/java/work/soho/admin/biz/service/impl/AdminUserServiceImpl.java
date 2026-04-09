package work.soho.admin.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import work.soho.admin.api.service.AdminInfoApiService;
import work.soho.admin.api.vo.AdminUserVo;
import work.soho.admin.biz.domain.AdminDept;
import work.soho.admin.biz.domain.AdminPost;
import work.soho.admin.biz.domain.AdminResource;
import work.soho.admin.biz.domain.AdminRoleResource;
import work.soho.admin.biz.domain.AdminRoleUser;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.mapper.AdminDeptMapper;
import work.soho.admin.biz.mapper.AdminPostMapper;
import work.soho.admin.biz.mapper.AdminUserMapper;
import work.soho.admin.biz.service.AdminResourceService;
import work.soho.admin.biz.service.AdminRoleUserService;
import work.soho.admin.biz.service.AdminUserService;
import work.soho.common.core.util.HashMapUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.core.util.TreeUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理员用户服务实现。
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService, AdminInfoApiService {
    private final AdminRoleUserService adminRoleUserService;
    private final AdminRoleResourceServiceImpl adminRoleResourceService;
    private final AdminResourceService adminResourceService;
    private final AdminDeptMapper adminDeptMapper;
    private final AdminPostMapper adminPostMapper;

    /**
     * 根据登录名获取管理员。
     *
     * @param loginName 登录名
     * @return 管理员
     */
    @Override
    public AdminUser getByLoginName(String loginName) {
        LambdaQueryWrapper<AdminUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AdminUser::getPhone, loginName);
        lambdaQueryWrapper.or().eq(AdminUser::getUsername, loginName);
        return getOne(lambdaQueryWrapper);
    }

    /**
     * 更新或者保存用户信息。
     *
     * @param adminUserVo 用户信息
     */
    @Override
    public void saveOrUpdate(AdminUserVo adminUserVo) {
        validateDeptAndPost(adminUserVo);

        AdminUser adminUser = getById(adminUserVo.getId());
        if (adminUser == null) {
            adminUser = new AdminUser();
        }
        BeanUtils.copyProperties(adminUserVo, adminUser);
        adminUser.setUpdatedTime(new Date());
        if (StringUtils.isNotEmpty(adminUserVo.getPassword())) {
            adminUser.setPassword(new BCryptPasswordEncoder().encode(adminUserVo.getPassword()));
        } else {
            adminUser.setPassword(null);
        }

        if (adminUser.getId() == null) {
            adminUser.setCreatedTime(new Date());
            save(adminUser);
        } else {
            updateAdminUser(adminUserVo, adminUser);
        }

        updateUserRoles(adminUserVo, adminUser.getId());
    }

    /**
     * 获取指定用户资源。
     *
     * @param uid 用户ID
     * @return 资源映射
     * @throws NoSuchMethodException 反射异常
     * @throws InvocationTargetException 反射异常
     * @throws IllegalAccessException 反射异常
     */
    @Override
    public HashMap<String, AdminResource> getResourceByUid(Long uid)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LambdaQueryWrapper<AdminRoleUser> ruLqw = new LambdaQueryWrapper<>();
        ruLqw.eq(AdminRoleUser::getUserId, uid);
        List<AdminRoleUser> roleUsers = adminRoleUserService.list(ruLqw);
        if (roleUsers.isEmpty()) {
            return null;
        }
        List<Long> roleIds = roleUsers.stream().map(AdminRoleUser::getRoleId).collect(Collectors.toList());
        LambdaQueryWrapper<AdminRoleResource> arLqw = new LambdaQueryWrapper<>();
        arLqw.in(AdminRoleResource::getRoleId, roleIds);
        List<AdminRoleResource> adminRoleResourcesList = adminRoleResourceService.list(arLqw);
        if (adminRoleResourcesList.isEmpty()) {
            return null;
        }
        List<Long> resourceIds = adminRoleResourcesList.stream().map(AdminRoleResource::getResourceId).collect(Collectors.toList());

        TreeUtils<Long, AdminResource> treeUtils = new TreeUtils<>();
        Class<?> c = AdminResource.class;
        treeUtils.loadData(adminResourceService.list(), c.getMethod("getId"), c.getMethod("getBreadcrumbParentId"));
        List<AdminResource> myList = treeUtils.getAllTreeNodeWidthIds(resourceIds);
        return (HashMap<String, AdminResource>) HashMapUtils.fromList(myList, "route");
    }

    /**
     * 获取资源权限Key集合。
     *
     * @param id 用户ID
     * @return 权限Key集合
     */
    @SneakyThrows
    public HashSet<String> getResourceKeys(Long id) {
        HashMap<String, AdminResource> res = getResourceByUid(id);
        return (HashSet<String>) res.keySet();
    }

    /**
     * 获取管理员信息。
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    @Override
    public AdminUserVo getAdminById(Long id) {
        AdminUserVo adminUserVo = new AdminUserVo();
        BeanUtils.copyProperties(getById(id), adminUserVo);
        fillDeptAndPostInfo(adminUserVo);
        return adminUserVo;
    }

    /**
     * 批量填充用户部门和岗位信息。
     *
     * @param adminUserVoList 用户列表
     */
    @Override
    public void fillDeptAndPostInfo(List<AdminUserVo> adminUserVoList) {
        if (adminUserVoList == null || adminUserVoList.isEmpty()) {
            return;
        }
        List<Long> deptIds = adminUserVoList.stream().map(AdminUserVo::getDeptId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> postIds = adminUserVoList.stream().map(AdminUserVo::getPostId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, String> deptMap = deptIds.isEmpty() ? new HashMap<>() : adminDeptMapper.selectBatchIds(deptIds).stream()
                .collect(Collectors.toMap(AdminDept::getId, AdminDept::getName));
        Map<Long, String> postMap = postIds.isEmpty() ? new HashMap<>() : adminPostMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(AdminPost::getId, AdminPost::getName));

        adminUserVoList.forEach(item -> {
            item.setDeptName(deptMap.get(item.getDeptId()));
            item.setPostName(postMap.get(item.getPostId()));
        });
    }

    /**
     * 填充单个用户部门和岗位信息。
     *
     * @param adminUserVo 用户信息
     */
    @Override
    public void fillDeptAndPostInfo(AdminUserVo adminUserVo) {
        if (adminUserVo == null) {
            return;
        }
        if (adminUserVo.getDeptId() != null) {
            AdminDept adminDept = adminDeptMapper.selectById(adminUserVo.getDeptId());
            adminUserVo.setDeptName(adminDept == null ? null : adminDept.getName());
        }
        if (adminUserVo.getPostId() != null) {
            AdminPost adminPost = adminPostMapper.selectById(adminUserVo.getPostId());
            adminUserVo.setPostName(adminPost == null ? null : adminPost.getName());
        }
    }

    /**
     * 校验部门和岗位是否存在。
     *
     * @param adminUserVo 用户信息
     */
    private void validateDeptAndPost(AdminUserVo adminUserVo) {
        if (adminUserVo.getDeptId() != null && adminDeptMapper.selectById(adminUserVo.getDeptId()) == null) {
            throw new IllegalArgumentException("部门不存在");
        }
        if (adminUserVo.getPostId() != null && adminPostMapper.selectById(adminUserVo.getPostId()) == null) {
            throw new IllegalArgumentException("岗位不存在");
        }
    }

    /**
     * 更新管理员信息，显式支持清空空值字段。
     *
     * @param adminUserVo 请求参数
     * @param adminUser 管理员实体
     */
    private void updateAdminUser(AdminUserVo adminUserVo, AdminUser adminUser) {
        LambdaUpdateWrapper<AdminUser> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(AdminUser::getId, adminUser.getId());
        updateWrapper.set(AdminUser::getUsername, adminUser.getUsername());
        updateWrapper.set(AdminUser::getPhone, adminUser.getPhone());
        updateWrapper.set(AdminUser::getNickName, adminUserVo.getNickName());
        updateWrapper.set(AdminUser::getRealName, adminUserVo.getRealName());
        updateWrapper.set(AdminUser::getAvatar, adminUserVo.getAvatar());
        updateWrapper.set(AdminUser::getEmail, adminUserVo.getEmail());
        updateWrapper.set(AdminUser::getSex, adminUserVo.getSex());
        updateWrapper.set(AdminUser::getAge, adminUserVo.getAge());
        updateWrapper.set(AdminUser::getDeptId, adminUserVo.getDeptId());
        updateWrapper.set(AdminUser::getPostId, adminUserVo.getPostId());
        updateWrapper.set(AdminUser::getUpdatedTime, adminUser.getUpdatedTime());
        if (StringUtils.isNotEmpty(adminUser.getPassword())) {
            updateWrapper.set(AdminUser::getPassword, adminUser.getPassword());
        }
        update(updateWrapper);
    }

    /**
     * 更新用户角色关系。
     *
     * @param adminUserVo 用户参数
     * @param userId 用户ID
     */
    private void updateUserRoles(AdminUserVo adminUserVo, Long userId) {
        List<AdminRoleUser> adminRoleList = adminRoleUserService.list(new LambdaQueryWrapper<AdminRoleUser>().eq(AdminRoleUser::getUserId, userId));
        List<Long> oldRoleIds = adminRoleList.stream().map(AdminRoleUser::getRoleId).collect(Collectors.toList());
        if (adminUserVo.getRoleIds() == null) {
            return;
        }
        adminUserVo.getRoleIds().forEach(roleId -> {
            if (!oldRoleIds.contains(roleId)) {
                AdminRoleUser adminRoleUser = new AdminRoleUser();
                adminRoleUser.setUserId(userId);
                adminRoleUser.setRoleId(roleId);
                adminRoleUser.setCreatedTime(new Date());
                adminRoleUser.setStatus(1);
                adminRoleUserService.save(adminRoleUser);
            }
        });
        oldRoleIds.forEach(roleId -> {
            if (!adminUserVo.getRoleIds().contains(roleId)) {
                adminRoleUserService.remove(new LambdaQueryWrapper<AdminRoleUser>()
                        .eq(AdminRoleUser::getUserId, userId)
                        .eq(AdminRoleUser::getRoleId, roleId));
            }
        });
    }
}
