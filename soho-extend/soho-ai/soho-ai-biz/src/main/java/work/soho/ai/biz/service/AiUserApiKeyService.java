package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiUserApiKeyCreatedResponse;
import work.soho.ai.biz.request.CreateAiUserApiKeyRequest;

public interface AiUserApiKeyService extends IService<AiUserApiKey> {
    AiUserApiKeyCreatedResponse createKey(Long userId, CreateAiUserApiKeyRequest request);

    AiUserApiKey requireByPlaintextKey(String plaintextKey);

    AiUserApiKey findByPlaintextKey(String plaintextKey);

    /**
     * 按用户ID获取一个可用的 API Key。
     *
     * @param userId 用户ID
     * @return 启用状态的 API Key
     */
    AiUserApiKey requireEnabledByUserId(Long userId);

    boolean disableKey(Long userId, Long id);

    boolean enableKey(Long userId, Long id);

    boolean deleteKey(Long userId, Long id);

    void touchLastUsedTime(Long id);
}
