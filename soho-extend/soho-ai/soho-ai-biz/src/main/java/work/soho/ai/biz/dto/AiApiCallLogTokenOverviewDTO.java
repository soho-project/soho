package work.soho.ai.biz.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("AI 调用日志 Token 汇总")
public class AiApiCallLogTokenOverviewDTO {
    @ApiModelProperty("总输入 token")
    private Long promptTokens;

    @ApiModelProperty("总输出 token")
    private Long completionTokens;

    @ApiModelProperty("总 token")
    private Long totalTokens;
}
