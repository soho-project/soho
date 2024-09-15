package work.soho.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import work.soho.admin.biz.AdminApplication;
import work.soho.admin.biz.mapper.AdminUserMapper;
import work.soho.admin.biz.domain.AdminUser;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest(classes = AdminApplication.class)
@Slf4j
class CrudTest {

	@Autowired
	public AdminUserMapper adminUserMapper;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Test
	void insert() {
		SqlSession sqlSession = sqlSessionFactory.openSession(false);
		AdminUserMapper userMapper = sqlSession.getMapper(AdminUserMapper.class);
		for (int i = 0; i < 100; i++) {
			AdminUser adminUser = new AdminUser();
			adminUser.setNickName("a");
			adminUser.setRealName("fang.liu");
			adminUser.setEmail("i@liufang.org.cn");
			adminUser.setPhone("");
			adminUser.setPassword("123456");
			adminUser.setCreatedTime(new Timestamp(System.currentTimeMillis()));
			adminUser.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
			userMapper.insert(adminUser);
		}
		sqlSession.commit();
		sqlSession.close();
	}

	@Test
	void select() {
		AdminUser adminUser = new AdminUser();
		adminUser.setNickName("a");
		List<AdminUser> list = adminUserMapper.selectList(null);
	}

}
