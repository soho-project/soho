package work.soho.ai.biz.service.impl;

import org.junit.Test;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.request.CreateAiUserApiKeyRequest;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AiUserApiKeyServiceImplTest {

    @Test
    public void createKey_whenExpireEndTimeInPast_shouldReject() {
        AiUserApiKeyServiceImpl service = new AiUserApiKeyServiceImpl();
        CreateAiUserApiKeyRequest request = new CreateAiUserApiKeyRequest();
        request.setExpireEndTime(LocalDateTime.now().minusSeconds(1));

        assertThatThrownBy(() -> service.createKey(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("有效期必须晚于当前时间");
    }

    @Test
    public void isExpired_whenExpireEndTimeReached_shouldReturnTrue() throws Exception {
        AiUserApiKeyServiceImpl service = new AiUserApiKeyServiceImpl();
        AiUserApiKey apiKey = new AiUserApiKey();
        apiKey.setExpireEndTime(LocalDateTime.now().minusSeconds(1));

        Method method = AiUserApiKeyServiceImpl.class.getDeclaredMethod("isExpired", AiUserApiKey.class);
        method.setAccessible(true);

        assertThat((Boolean) method.invoke(service, apiKey)).isTrue();
    }

    @Test
    public void isExpired_whenExpireEndTimeMissing_shouldReturnFalse() throws Exception {
        AiUserApiKeyServiceImpl service = new AiUserApiKeyServiceImpl();
        AiUserApiKey apiKey = new AiUserApiKey();

        Method method = AiUserApiKeyServiceImpl.class.getDeclaredMethod("isExpired", AiUserApiKey.class);
        method.setAccessible(true);

        assertThat((Boolean) method.invoke(service, apiKey)).isFalse();
    }
}
