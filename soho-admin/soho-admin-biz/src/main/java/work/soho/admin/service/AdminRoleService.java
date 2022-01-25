package work.soho.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import work.soho.api.admin.po.AdminRole;
import work.soho.admin.mapper.AdminRoleMapper;

@Service
public class AdminRoleService {

	@Autowired
	private AdminRoleMapper adminroleMapper;

	public AdminRole getById(Integer id) {
		return adminroleMapper.getById(id);
	}

	public int insert(AdminRole adminrole) {
		return adminroleMapper.insert(adminrole);
	}

	public int update(AdminRole adminrole) {
		return adminroleMapper.update(adminrole);
	}

}
