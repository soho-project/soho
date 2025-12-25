package work.soho.open.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.open.biz.domain.OpenAppIpWhitelist;

import java.util.List;

public interface OpenAppIpWhitelistService extends IService<OpenAppIpWhitelist> {
    List<OpenAppIpWhitelist> getByAppId(Long appId);
}