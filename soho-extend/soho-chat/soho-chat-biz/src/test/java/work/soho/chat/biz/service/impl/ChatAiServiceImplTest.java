package work.soho.chat.biz.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class ChatAiServiceImplTest {
    @Autowired
    private ChatAiServiceImpl chatAi;

    @Test
    void chat() {
        System.out.println("test");
        chatAi.chat("你好");
        chatAi.chat("你可以模拟电商智能客服吗？");
    }

    @Test
    void createImage() {
        chatAi.createImage("为Java开源项目SOHO创建一个logo;上面的文字只能是英文或者中文");
    }

    @Test
    void audio2Text() {
        String text = chatAi.audio2Text("/home/fang/Music/Monster-YOASOBI.128.mp3");
        System.out.println(text);
    }
}
