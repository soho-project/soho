package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.mapper.OpenAppMapper;
import work.soho.open.biz.service.OpenAppService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Long> getOpenAppIdsByUserId(Long id) {
        List<OpenApp> list = list(new LambdaQueryWrapper<OpenApp>().eq(OpenApp::getUserId, id));
        if(list != null && list.size() > 0) {
            return list.stream().map(OpenApp::getUserId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}