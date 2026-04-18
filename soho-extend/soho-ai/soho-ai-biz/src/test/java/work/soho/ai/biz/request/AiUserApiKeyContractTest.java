package work.soho.ai.biz.request;

import org.junit.Test;
import work.soho.ai.biz.dto.AiUserApiKeyCreatedResponse;
import work.soho.ai.biz.dto.AiUserApiKeyView;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AiUserApiKeyContractTest {

    @Test
    public void aiUserApiKeyContracts_doNotExposeProviderConfigId() {
        assertThat(fieldNames(CreateAiUserApiKeyRequest.class)).doesNotContain("providerConfigId");
        assertThat(fieldNames(AiUserApiKeyCreatedResponse.class)).doesNotContain("providerConfigId");
        assertThat(fieldNames(AiUserApiKeyView.class)).doesNotContain("providerConfigId");
    }

    @Test
    public void aiUserApiKeyContracts_shouldExposeExpireEndTime() {
        assertThat(fieldNames(CreateAiUserApiKeyRequest.class)).contains("expireEndTime");
        assertThat(fieldNames(AiUserApiKeyCreatedResponse.class)).contains("expireEndTime");
        assertThat(fieldNames(AiUserApiKeyView.class)).contains("expireEndTime");
    }

    private Set<String> fieldNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toSet());
    }
}
