package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.mapper.OpenUserMapper;
import work.soho.open.biz.service.OpenUserService;

@RequiredArgsConstructor
@Service
public class OpenUserServiceImpl extends ServiceImpl<OpenUserMapper, OpenUser>
    implements OpenUserService{

}