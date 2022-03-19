package work.soho.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import work.soho.admin.service.AdminRoleService;
import work.soho.admin.domain.AdminRole;
import work.soho.admin.mapper.AdminRoleMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {

	@Autowired
	private AdminRoleMapper adminroleMapper;

	public AdminRole getById(Integer id) {
		return adminroleMapper.selectById(id);
	}

	public int insert(AdminRole adminrole) {
		return adminroleMapper.insert(adminrole);
	}

	public int update(AdminRole adminrole) {
		return adminroleMapper.updateById(adminrole);
	}

	/**
	 * 获取指定用户所有的角色信息
	 *
	 * @param userId
	 * @return
	 */
	public List<AdminRole> getRoleListByUserId(Integer userId) {
		return adminroleMapper.getAdminRoleListByUserId(userId);
	}
}
