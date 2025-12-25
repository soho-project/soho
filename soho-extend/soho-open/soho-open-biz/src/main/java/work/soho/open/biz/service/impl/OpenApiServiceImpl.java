package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.open.biz.domain.OpenApi;
import work.soho.open.biz.mapper.OpenApiMapper;
import work.soho.open.biz.service.OpenApiService;

@RequiredArgsConstructor
@Service
public class OpenApiServiceImpl extends ServiceImpl<OpenApiMapper, OpenApi>
    implements OpenApiService{

    @Override
    public OpenApi getByMethodAndPathAndVersion(String method, String path, String version) {
        return getOne(new LambdaQueryWrapper<OpenApi>().eq(OpenApi::getMethod, method).eq(OpenApi::getPath, path).eq(OpenApi::getVersion, version));
    }
}