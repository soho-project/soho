package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import work.soho.admin.domain.AdminRoleUser;
import work.soho.admin.mapper.AdminUserMapper;
import work.soho.admin.service.AdminRoleUserService;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.domain.AdminUser;
import work.soho.api.admin.vo.AdminUserVo;
import work.soho.common.core.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {
    private final AdminRoleUserService adminRoleUserService;

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
            throw new IllegalArgumentException("没有找到对应的用户");
        }
        BeanUtils.copyProperties(adminUserVo, adminUser);
        if(StringUtils.isNotEmpty(adminUser.getPassword())) {
            adminUser.setPassword(new BCryptPasswordEncoder().encode(adminUser.getPassword()));
        }
        saveOrUpdate(adminUser);

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
    }
}
