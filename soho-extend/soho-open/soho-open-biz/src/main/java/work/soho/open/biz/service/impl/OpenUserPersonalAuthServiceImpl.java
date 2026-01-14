package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenUserPersonalAuth;
import work.soho.open.biz.mapper.OpenUserPersonalAuthMapper;
import work.soho.open.biz.service.OpenUserPersonalAuthService;

@RequiredArgsConstructor
@Service
public class OpenUserPersonalAuthServiceImpl extends ServiceImpl<OpenUserPersonalAuthMapper, OpenUserPersonalAuth>
    implements OpenUserPersonalAuthService{

}