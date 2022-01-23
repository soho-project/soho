package work.soho.admin;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import work.soho.admin.mapper.AdminUserMapper;
import work.soho.api.admin.po.AdminUser;

import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class)
@Log4j2
public class CrudTest {
    @Autowired
    public AdminUserMapper adminUserMapper;

    @Test
    public void insert() {
        for (int i=0; i<100; i++) {
            AdminUser adminUser = new AdminUser();
            adminUser.setNickname("a");
            adminUser.setRealname("fang.liu");
            adminUser.setEmail("i@liufang.org.cn");
            adminUser.setPhone(158731L);
            adminUser.setPassword("123456");
            adminUser.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            adminUser.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            adminUserMapper.insert(adminUser);
        }
    }

    @Test
    public void select() {
        AdminUser adminUser = new AdminUser();
        adminUser.setNickname("a");
        List<AdminUser> list = adminUserMapper.select(adminUser);
        System.out.println(list);
    }
}
