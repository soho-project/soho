package work.soho.open.biz.service;

import work.soho.open.biz.domain.OpenUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OpenUserService extends IService<OpenUser> {
    OpenUser getOpenUserByUserId(Long userId);
}