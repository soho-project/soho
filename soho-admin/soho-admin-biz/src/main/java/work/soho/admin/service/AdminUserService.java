package work.soho.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.domain.AdminUser;
import work.soho.api.admin.vo.AdminUserVo;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public interface AdminUserService extends IService<AdminUser> {

	AdminUser getByLoginName(String loginName);

	/**
	 * 更新或者保存用户信息
	 */
	void saveOrUpdate(AdminUserVo adminUserVo);

	/**
     * 获取指定用户资源
     *
     * @param uid
     * @return
     */
	HashMap<String, AdminResource> getResourceByUid(Long uid) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
