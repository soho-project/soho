package work.soho.chat.biz.service.impl.ai.adapter;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.chat.api.request.AiRequest;
import work.soho.test.TestApp;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class AliyunAiTest {
    @Autowired
    private AliyunAiOptions aliyunAiOptions;

    @Test
    void chatQuery() {
        AliyunAi aliyunAi = new AliyunAi(aliyunAiOptions);
        LinkedList<AiRequest.Message> messages = (new AiRequest.BuildMessages()).systemMessage("假设你是一个程序员，根据提问给出对应的代码,仅仅给出代码，不需要回复其他描述文字")
                .assistantMessage("package work.soho.chat.biz.service.impl.ai.adapter;\n" +
                        "\n" +
                        "import lombok.Data;\n" +
                        "import org.springframework.beans.factory.annotation.Value;\n" +
                        "import org.springframework.stereotype.Component;\n" +
                        "\n" +
                        "@Data\n" +
                        "@Component\n" +
                        "public class AliyunAiOptions {\n" +
                        "    @Value(\"${chat.ai.aliyun.apiKey}\")\n" +
                        "    private String apiKey;\n" +
                        "}\n")
                .userMessage("请写一个类计算pi")
                .build();
        System.out.println(aliyunAi.chatQuery(messages));
    }

    @Test
    void query() throws NoApiKeyException, InputRequiredException {
        AliyunAi aliyunAi = new AliyunAi(aliyunAiOptions);
        String content = aliyunAi.query("请用Java写一个递归");
        System.out.println(content);
    }
}
