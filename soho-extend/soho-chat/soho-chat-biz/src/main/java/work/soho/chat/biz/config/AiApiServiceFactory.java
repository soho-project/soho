package work.soho.chat.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import work.soho.chat.api.service.ai.AiApiService;
import work.soho.chat.biz.service.impl.ai.adapter.AliyunAi;
import work.soho.chat.biz.service.impl.ai.adapter.AliyunAiOptions;

/**
 * TODO 实现其他适配构造
 */
@Component
@RequiredArgsConstructor
public class AiApiServiceFactory {
    private final AliyunAiOptions aliyunAiOptions;

    @Bean
    public AiApiService factory()  {
        AliyunAi aliyun = new AliyunAi(aliyunAiOptions);
        return aliyun;
    }
}
