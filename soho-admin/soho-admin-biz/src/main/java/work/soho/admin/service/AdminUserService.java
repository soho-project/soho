package work.soho.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import work.soho.api.admin.po.AdminUser;
import work.soho.admin.mapper.AdminUserMapper;

@Service
public interface AdminUserService {
	public AdminUser getById(Integer id);

	public int insert(AdminUser adminuser);

	public int update(AdminUser adminuser);
}
