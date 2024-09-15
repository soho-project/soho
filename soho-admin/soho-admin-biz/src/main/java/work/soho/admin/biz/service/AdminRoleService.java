package work.soho.admin.biz.service;

import work.soho.admin.biz.domain.AdminRole;

import java.util.List;

public interface AdminRoleService {
    AdminRole getById(Integer id);

    int insert(AdminRole adminrole);

    int update(AdminRole adminrole);

    List<AdminRole> getRoleListByUserId(Long userId);
}
