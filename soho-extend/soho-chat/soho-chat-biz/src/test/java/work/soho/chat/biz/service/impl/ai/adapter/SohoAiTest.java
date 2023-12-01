package work.soho.chat.biz.service.impl.ai.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class SohoAiTest {

    @Test
    void query() {
        SohoAi sohoAi = new SohoAi(new SohoAiOptions());
        sohoAi.query("请用php写一个函数计算Pi");
    }
}
