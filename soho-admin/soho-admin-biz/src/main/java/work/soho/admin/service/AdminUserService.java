package work.soho.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import work.soho.api.admin.po.AdminUser;
import work.soho.admin.mapper.AdminUserMapper;

@Service
public class AdminUserService {
   @Autowired
   private AdminUserMapper adminuserMapper;

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
