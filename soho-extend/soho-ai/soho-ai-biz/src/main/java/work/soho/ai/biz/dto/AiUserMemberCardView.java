package work.soho.ai.biz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiUserMemberCardView {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userCardId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long memberCardId;

    private String name;
    private String cardType;
    private String limitMode;
    private Integer status;
    private Integer priority;
    private Boolean isSelected;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer rateLimit5h;
    private Integer rateLimit7d;
    private Boolean rateLimit5hEnabled;
    private Boolean rateLimit7dEnabled;
    private Integer rateLimitWindow5h;
    private Integer rateLimitWindow7d;
}
