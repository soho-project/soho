package work.soho.open.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.open.biz.domain.OpenApp;

import java.util.List;

public interface OpenAppService extends IService<OpenApp> {
    OpenApp getOpenAppByKey(String appKey);
    List<Long> getOpenAppIdsByUserId(Long id);
}