package work.soho.api.admin.service;

import work.soho.api.admin.vo.AdminUserVo;

public interface AdminInfoApiService {
    AdminUserVo getAdminById(Long id);
}
