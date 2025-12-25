package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.mapper.OpenAppMapper;
import work.soho.open.biz.service.OpenAppService;

@RequiredArgsConstructor
@Service
public class OpenAppServiceImpl extends ServiceImpl<OpenAppMapper, OpenApp>
    implements OpenAppService{

    @Override
    public OpenApp getOpenAppByKey(String appKey) {
        LambdaQueryWrapper<OpenApp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OpenApp::getAppKey, appKey);
        return getOne(queryWrapper);
    }
}