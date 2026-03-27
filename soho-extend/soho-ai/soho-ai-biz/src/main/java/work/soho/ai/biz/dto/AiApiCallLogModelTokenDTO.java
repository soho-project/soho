package work.soho.ai.biz.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("AI 调用日志模型 Token 统计")
public class AiApiCallLogModelTokenDTO {
    @ApiModelProperty("模型名")
    private String model;

    @ApiModelProperty("输入 token")
    private Long promptTokens;

    @ApiModelProperty("输出 token")
    private Long completionTokens;

    @ApiModelProperty("总 token")
    private Long totalTokens;
}
