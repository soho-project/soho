package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiUserApiKeyCreatedResponse;
import work.soho.ai.biz.enums.AiUserApiKeyEnums;
import work.soho.ai.biz.mapper.AiUserApiKeyMapper;
import work.soho.ai.biz.request.CreateAiUserApiKeyRequest;
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AiUserApiKeyServiceImpl extends ServiceImpl<AiUserApiKeyMapper, AiUserApiKey>
        implements AiUserApiKeyService {
    /**
     * 创建用户 AI API Key，并按请求记录有效期；未传有效期时默认永久有效。
     */
    @Override
    public AiUserApiKeyCreatedResponse createKey(Long userId, CreateAiUserApiKeyRequest request) {
        Assert.notNull(userId, "userId不能为空");
        Assert.notNull(request, "request不能为空");
        validateExpireEndTime(request.getExpireEndTime());

        String plaintextKey = "sk-ai-" + IDGeneratorUtils.uuid32();
        LocalDateTime now = LocalDateTime.now();
        AiUserApiKey entity = new AiUserApiKey();
        entity.setUserId(userId);
        entity.setName(StringUtils.isBlank(request.getName()) ? "ai-key" : request.getName());
        entity.setApiKeyPrefix(plaintextKey.substring(0, Math.min(12, plaintextKey.length())));
        entity.setApiKeyHash(sha256(plaintextKey));
        entity.setStatus(AiUserApiKeyEnums.Status.ENABLED.getId());
        entity.setExpireEndTime(request.getExpireEndTime());
        entity.setCreatedTime(now);
        entity.setUpdatedTime(now);
        save(entity);

        AiUserApiKeyCreatedResponse response = new AiUserApiKeyCreatedResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setApiKey(plaintextKey);
        response.setApiKeyPrefix(entity.getApiKeyPrefix());
        response.setExpireEndTime(entity.getExpireEndTime());
        return response;
    }

    /**
     * 按明文 key 获取可用 key，并统一校验启用状态与有效期。
     */
    @Override
    public AiUserApiKey requireByPlaintextKey(String plaintextKey) {
        AiUserApiKey apiKey = findByPlaintextKey(plaintextKey);
        Assert.notNull(apiKey, "无效的api key");
        Assert.isTrue(apiKey.getStatus() != null
                && apiKey.getStatus().intValue() == AiUserApiKeyEnums.Status.ENABLED.getId(), "api key已禁用");
        Assert.isTrue(!isExpired(apiKey), "api key已过期");
        return apiKey;
    }

    @Override
    public AiUserApiKey findByPlaintextKey(String plaintextKey) {
        Assert.hasText(plaintextKey, "api key不能为空");
        return getOne(new LambdaQueryWrapper<AiUserApiKey>()
                .eq(AiUserApiKey::getApiKeyHash, sha256(plaintextKey))
                .last("limit 1"));
    }

    /**
     * 按用户ID获取一个启用状态的 API Key。
     *
     * @param userId 用户ID
     * @return 启用状态的 API Key
     */
    @Override
    public AiUserApiKey requireEnabledByUserId(Long userId) {
        Assert.notNull(userId, "userId不能为空");
        AiUserApiKey apiKey = getOne(new LambdaQueryWrapper<AiUserApiKey>()
                .eq(AiUserApiKey::getUserId, userId)
                .eq(AiUserApiKey::getStatus, AiUserApiKeyEnums.Status.ENABLED.getId())
                .and(wrapper -> wrapper.isNull(AiUserApiKey::getExpireEndTime)
                        .or()
                        .gt(AiUserApiKey::getExpireEndTime, LocalDateTime.now()))
                .orderByDesc(AiUserApiKey::getLastUsedTime)
                .orderByDesc(AiUserApiKey::getId)
                .last("limit 1"));
        Assert.notNull(apiKey, "当前用户没有可用的api key");
        return apiKey;
    }

    @Override
    public boolean disableKey(Long userId, Long id) {
        AiUserApiKey entity = getOne(new LambdaQueryWrapper<AiUserApiKey>()
                .eq(AiUserApiKey::getId, id)
                .eq(AiUserApiKey::getUserId, userId)
                .last("limit 1"));
        Assert.notNull(entity, "api key不存在");
        entity.setStatus(AiUserApiKeyEnums.Status.DISABLED.getId());
        entity.setUpdatedTime(LocalDateTime.now());
        return updateById(entity);
    }

    @Override
    public boolean enableKey(Long userId, Long id) {
        AiUserApiKey entity = getOne(new LambdaQueryWrapper<AiUserApiKey>()
                .eq(AiUserApiKey::getId, id)
                .eq(AiUserApiKey::getUserId, userId)
                .last("limit 1"));
        Assert.notNull(entity, "api key不存在");
        entity.setStatus(AiUserApiKeyEnums.Status.ENABLED.getId());
        entity.setUpdatedTime(LocalDateTime.now());
        return updateById(entity);
    }

    @Override
    public boolean deleteKey(Long userId, Long id) {
        AiUserApiKey entity = getOne(new LambdaQueryWrapper<AiUserApiKey>()
                .eq(AiUserApiKey::getId, id)
                .eq(AiUserApiKey::getUserId, userId)
                .last("limit 1"));
        Assert.notNull(entity, "api key不存在");
        return removeById(id);
    }

    @Override
    public void touchLastUsedTime(Long id) {
        AiUserApiKey entity = new AiUserApiKey();
        entity.setId(id);
        entity.setLastUsedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());
        updateById(entity);
    }

    /**
     * 校验有效期必须晚于当前时间。
     */
    private void validateExpireEndTime(LocalDateTime expireEndTime) {
        if (expireEndTime == null) {
            return;
        }
        Assert.isTrue(expireEndTime.isAfter(LocalDateTime.now()), "有效期必须晚于当前时间");
    }

    /**
     * 判断 key 是否已过期。
     */
    private boolean isExpired(AiUserApiKey apiKey) {
        return apiKey.getExpireEndTime() != null && !apiKey.getExpireEndTime().isAfter(LocalDateTime.now());
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException("sha256 error", e);
        }
    }
}
