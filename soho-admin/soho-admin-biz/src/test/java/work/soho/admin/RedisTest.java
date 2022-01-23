package work.soho.admin;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = AdminApplication.class)
@Log4j2
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testKV() {
        System.out.println("=========================================");
        log.debug("redis set data");
        redisTemplate.opsForValue().set("test", "test");
        log.debug("redis set data end");
    }
}
