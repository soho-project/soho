package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.mapper.OpenUserMapper;
import work.soho.open.biz.service.OpenUserService;

@RequiredArgsConstructor
@Service
public class OpenUserServiceImpl extends ServiceImpl<OpenUserMapper, OpenUser>
    implements OpenUserService{

    @Override
    public OpenUser getOpenUserByUserId(Long userId) {
        LambdaQueryWrapper<OpenUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(OpenUser::getUserId, userId);
        OpenUser openUser =  getOne(lqw, false);
        if(openUser == null) {
            openUser = new OpenUser();
            openUser.setUserId(userId);
            save(openUser);
        }
        return openUser;
    }
}