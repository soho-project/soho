package work.soho.admin.service;

import org.springframework.stereotype.Service;
import work.soho.admin.domain.AdminResource;

import java.util.List;

@Service
public interface AdminResourceService {
    public void syncResource2Db();

    AdminResource getByPath(String path);

    List<AdminResource> getListByRoleIds(Integer[] roleIds);
}
