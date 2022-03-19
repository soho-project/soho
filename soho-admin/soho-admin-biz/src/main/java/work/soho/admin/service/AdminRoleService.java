package work.soho.admin.service;

import work.soho.admin.domain.AdminRole;

import java.util.List;

public interface AdminRoleService {
    AdminRole getById(Integer id);

    int insert(AdminRole adminrole);

    int update(AdminRole adminrole);

    List<AdminRole> getRoleListByUserId(Integer userId);
}
