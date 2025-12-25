package work.soho.open.biz.service;

import work.soho.open.biz.domain.OpenAppApi;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OpenAppApiService extends IService<OpenAppApi> {
    boolean canAccess(Long appId, Long apiId);
}