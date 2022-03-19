package work.soho.admin.service;

import work.soho.admin.domain.AdminUser;

public interface AdminUserService {
	public AdminUser getById(Integer id);

	public int insert(AdminUser adminuser);

	public int update(AdminUser adminuser);

	public String login(String username, String password);

	public String signToken(AdminUser adminUser);

	public Integer getUserIdOuthToken(String token);

	public AdminUser getUserByToken(String token);
}
