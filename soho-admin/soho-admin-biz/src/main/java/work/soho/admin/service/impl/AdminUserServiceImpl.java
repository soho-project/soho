package work.soho.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.mapper.AdminUserMapper;
import work.soho.api.admin.po.AdminUser;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl {
    private final AdminUserMapper adminuserMapper;

    public AdminUser getById(Integer id) {
        return adminuserMapper.getById(id);
    }

    public int insert(AdminUser adminuser) {
        return adminuserMapper.insert(adminuser);
    }

    public int update(AdminUser adminuser) {
        return adminuserMapper.update(adminuser);
    }


}
