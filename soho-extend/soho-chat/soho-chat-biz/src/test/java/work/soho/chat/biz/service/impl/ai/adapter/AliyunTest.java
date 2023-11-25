package work.soho.chat.biz.service.impl.ai.adapter;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class AliyunTest {

    @Autowired
    private AliyunAiOptions aliyunAiOptions;

    @Test
    void query() throws NoApiKeyException, InputRequiredException {
        AliyunAi aliyunAi = new AliyunAi(aliyunAiOptions);
        String content = aliyunAi.query("请用Java写一个递归");
        System.out.println(content);
    }
}
