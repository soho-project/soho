package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.open.biz.domain.OpenAppApi;
import work.soho.open.biz.mapper.OpenAppApiMapper;
import work.soho.open.biz.service.OpenAppApiService;

@RequiredArgsConstructor
@Service
public class OpenAppApiServiceImpl extends ServiceImpl<OpenAppApiMapper, OpenAppApi>
    implements OpenAppApiService{

    @Override
    public boolean canAccess(Long appId, Long apiId) {
        return count(new LambdaQueryWrapper<OpenAppApi>().eq(OpenAppApi::getAppId, appId)
                .eq(OpenAppApi::getApiId, apiId)) > 0;
    }
}