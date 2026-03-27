package work.soho.ai.biz.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("AI 调用日志分时 Token 统计")
public class AiApiCallLogHourTokenDTO {
    @ApiModelProperty("小时，格式 yyyy-MM-dd HH")
    private String hour;

    @ApiModelProperty("输入 token")
    private Long promptTokens;

    @ApiModelProperty("输出 token")
    private Long completionTokens;

    @ApiModelProperty("总 token")
    private Long totalTokens;
}
