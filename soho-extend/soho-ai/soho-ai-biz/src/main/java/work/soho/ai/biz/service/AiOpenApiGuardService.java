package work.soho.ai.biz.service;

import work.soho.ai.biz.dto.AiOpenApiGuardContext;

public interface AiOpenApiGuardService {
    AiOpenApiGuardContext checkAndAcquire(String authorization, String endpoint);

    void recordFailure(AiOpenApiGuardContext context, Throwable throwable);
}
