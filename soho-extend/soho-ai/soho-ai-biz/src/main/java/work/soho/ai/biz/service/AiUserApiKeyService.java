package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiUserApiKeyCreatedResponse;
import work.soho.ai.biz.request.CreateAiUserApiKeyRequest;

public interface AiUserApiKeyService extends IService<AiUserApiKey> {
    AiUserApiKeyCreatedResponse createKey(Long userId, CreateAiUserApiKeyRequest request);

    AiUserApiKey requireByPlaintextKey(String plaintextKey);

    boolean disableKey(Long userId, Long id);

    boolean enableKey(Long userId, Long id);

    boolean deleteKey(Long userId, Long id);

    void touchLastUsedTime(Long id);
}
