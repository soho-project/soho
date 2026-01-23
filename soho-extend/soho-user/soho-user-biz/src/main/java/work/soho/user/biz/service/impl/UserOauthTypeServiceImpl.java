package work.soho.user.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.user.biz.domain.UserOauthType;
import work.soho.user.biz.mapper.UserOauthTypeMapper;
import work.soho.user.biz.service.UserOauthTypeService;

@RequiredArgsConstructor
@Service
public class UserOauthTypeServiceImpl extends ServiceImpl<UserOauthTypeMapper, UserOauthType>
    implements UserOauthTypeService{

}