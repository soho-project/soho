package work.soho.ai.biz.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 创建用户 AI API Key 请求
 */
@Data
public class CreateAiUserApiKeyRequest {
    /**
     * API Key 名称
     */
    private String name;

    /**
     * API Key 失效时间，支持 yyyy-MM-dd HH:mm:ss 格式
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireEndTime;
}
