package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenUserEnterpriseAuth;
import work.soho.open.biz.mapper.OpenUserEnterpriseAuthMapper;
import work.soho.open.biz.service.OpenUserEnterpriseAuthService;

@RequiredArgsConstructor
@Service
public class OpenUserEnterpriseAuthServiceImpl extends ServiceImpl<OpenUserEnterpriseAuthMapper, OpenUserEnterpriseAuth>
    implements OpenUserEnterpriseAuthService{

}