package work.soho.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.domain.AdminUser;
import work.soho.api.admin.vo.AdminUserVo;

public interface AdminUserService extends IService<AdminUser> {

	AdminUser getByLoginName(String loginName);

	/**
	 * 更新或者保存用户信息
	 */
	void saveOrUpdate(AdminUserVo adminUserVo);
}
