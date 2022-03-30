package work.soho.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import work.soho.admin.domain.AdminResource;

import java.util.List;

public interface AdminResourceService extends IService<AdminResource> {
    public void syncResource2Db();

    AdminResource getByPath(String path);

    List<AdminResource> getListByRoleIds(Integer[] roleIds);
}
