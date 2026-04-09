package work.soho.admin.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.biz.domain.AdminResource;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.api.vo.AdminUserVo;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

/**
 * 管理员用户服务。
 */
public interface AdminUserService extends IService<AdminUser> {

	/**
	 * 根据登录名获取管理员。
	 *
	 * @param loginName 登录名
	 * @return 管理员
	 */
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

	/**
	 * 批量填充用户部门和岗位信息。
	 *
	 * @param adminUserVoList 用户列表
	 */
	void fillDeptAndPostInfo(List<AdminUserVo> adminUserVoList);

	/**
	 * 填充单个用户部门和岗位信息。
	 *
	 * @param adminUserVo 用户信息
	 */
	void fillDeptAndPostInfo(AdminUserVo adminUserVo);
}
