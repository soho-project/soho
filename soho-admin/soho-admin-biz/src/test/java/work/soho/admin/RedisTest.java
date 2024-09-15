package work.soho.admin;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import work.soho.admin.biz.AdminApplication;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AdminApplication.class)
@Log4j2
class RedisTest {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;


	@Test
	void testKV() {
		System.out.println("=========================================");
		log.debug("redis set data");
		redisTemplate.opsForValue().set("test", "test");
		log.debug("redis set data end");
	}

	@Test
	void testObject() {
		ArrayList<String> list = new ArrayList<>();
		list.add("test 1");
		list.add("test 2");
		list.add("test 3");
		redisTemplate.opsForValue().set("object", list);

		list = (ArrayList<String>) redisTemplate.opsForValue().get("object");
		System.out.println(list);
		assertEquals(list.size(), 3);
	}
}
