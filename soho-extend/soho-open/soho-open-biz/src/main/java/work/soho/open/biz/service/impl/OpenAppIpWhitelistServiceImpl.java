package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.open.biz.domain.OpenAppIpWhitelist;
import work.soho.open.biz.mapper.OpenAppIpWhitelistMapper;
import work.soho.open.biz.service.OpenAppIpWhitelistService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OpenAppIpWhitelistServiceImpl extends ServiceImpl<OpenAppIpWhitelistMapper, OpenAppIpWhitelist>
    implements OpenAppIpWhitelistService{

    @Override
    public List<OpenAppIpWhitelist> getByAppId(Long appId) {
        return list(new LambdaQueryWrapper<OpenAppIpWhitelist>().eq(OpenAppIpWhitelist::getAppId, appId));
    }
}