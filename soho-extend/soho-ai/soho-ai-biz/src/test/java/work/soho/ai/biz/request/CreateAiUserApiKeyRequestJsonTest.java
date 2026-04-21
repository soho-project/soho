package work.soho.ai.biz.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 创建用户 AI API Key 请求 JSON 绑定测试
 */
public class CreateAiUserApiKeyRequestJsonTest {

    /**
     * 验证 expireEndTime 支持 yyyy-MM-dd HH:mm:ss 格式反序列化。
     *
     * @throws Exception JSON 解析异常
     */
    @Test
    public void shouldDeserializeExpireEndTimeWithDateTimePattern() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String json = "{\"name\":\"aaa\",\"expireEndTime\":\"2026-04-24 00:00:00\"}";
        CreateAiUserApiKeyRequest request = objectMapper.readValue(json, CreateAiUserApiKeyRequest.class);

        assertThat(request.getName()).isEqualTo("aaa");
        assertThat(request.getExpireEndTime()).isEqualTo(LocalDateTime.of(2026, 4, 24, 0, 0, 0));
    }
}
