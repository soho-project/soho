package work.soho.ai.biz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("AI 调用日志当天按用户统计")
public class AiApiCallLogUserTodayStatsDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("请求次数")
    private Long requestCount;

    @ApiModelProperty("输入 token 总数")
    private Long promptTokens;

    @ApiModelProperty("输出 token 总数")
    private Long completionTokens;

    @ApiModelProperty("总 token 数")
    private Long totalTokens;
}
