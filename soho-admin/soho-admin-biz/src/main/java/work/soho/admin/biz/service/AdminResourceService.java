package work.soho.admin.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.biz.domain.AdminResource;

public interface AdminResourceService extends IService<AdminResource> {
    public void syncResource2Db();
}
