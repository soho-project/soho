package work.soho.open.biz.service;

import work.soho.open.biz.domain.OpenApi;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OpenApiService extends IService<OpenApi> {
    OpenApi getByMethodAndPathAndVersion(String method, String path, String version);
}