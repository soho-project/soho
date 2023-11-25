package work.soho.chat.biz.service.impl.ai.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class BaiduAiTest {

    @Test
    public void testBaidu() {
        BaiduAiOptions baiduAiOptions = new BaiduAiOptions();
        BaiduAi baiduAi = new BaiduAi(baiduAiOptions);
        baiduAi.query("你是谁？");
    }
}
