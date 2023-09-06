package work.soho.chat.biz.listen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.test.TestApp;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class LongLinkListenTest {

    @Test
    public void parseMessage() {
        String message = "{\"toSessionId\":2,\"message\":{\"_id\":\"unique-id-1533728969\",\"type\":\"image\",\"content\":{\"picUrl\":\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/2023_9_7_212008093117059072.png\"},\"position\":\"right\"}}";
        System.out.println("test by fang");

        Map<Object, Object> map = JacksonUtils.toBean(message, Map.class);
        System.out.println(map);
        System.out.println(map.get("message"));
        System.out.println(((Map)map.get("message")).get("type"));
    }
}
