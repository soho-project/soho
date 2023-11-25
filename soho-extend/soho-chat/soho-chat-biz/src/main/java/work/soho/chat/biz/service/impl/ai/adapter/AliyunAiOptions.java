package work.soho.chat.biz.service.impl.ai.adapter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AliyunAiOptions {
    @Value("${chat.ai.aliyun.apiKey}")
    private String apiKey;
}
