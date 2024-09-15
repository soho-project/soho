package work.soho.admin;

import com.littlenb.snowflake.sequence.IdGenerator;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.biz.AdminApplication;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
@Log4j2
public class UtisTest {
    @Autowired
    private IdGenerator idGenerator;

    @Test
    public void idGenerator() {
        log.debug("=================================");
       log.debug(idGenerator.nextId());
        System.out.println("==============================================================");
        System.out.println(idGenerator.nextId());
        System.out.println(idGenerator.nextId());
        System.out.println(idGenerator.nextId());
        System.out.println(idGenerator.nextId());
    }
}
