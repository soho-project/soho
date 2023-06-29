package work.soho.common.data.lock.aspect;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;
import work.soho.common.data.lock.annotation.Lock;
import work.soho.common.data.lock.service.HelloService;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@TestPropertySource(locations="classpath:/application.yml")
@SpringBootTest(classes = TestCommonApplication.class)
@Log4j2
class LockAspectTest {
    @Autowired
    private HelloService helloService;

    @Test
    public void lock() {
        helloService.hello("abcd");
    }


}
