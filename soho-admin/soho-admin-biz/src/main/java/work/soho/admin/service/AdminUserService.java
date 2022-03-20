package work.soho.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.domain.AdminUser;

public interface AdminUserService extends IService<AdminUser> {

	public AdminUser getByLoginName(String loginName);
}
