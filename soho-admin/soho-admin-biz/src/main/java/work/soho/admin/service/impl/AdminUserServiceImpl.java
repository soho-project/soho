package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import work.soho.admin.mapper.AdminUserMapper;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.domain.AdminUser;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {
    @Override
    public AdminUser getByLoginName(String loginName) {
        LambdaQueryWrapper<AdminUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AdminUser::getPhone, loginName);
        lambdaQueryWrapper.or().eq(AdminUser::getEmail, loginName);
        return getOne(lambdaQueryWrapper);
    }
}
