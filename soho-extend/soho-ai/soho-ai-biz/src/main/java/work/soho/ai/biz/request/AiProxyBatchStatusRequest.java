package work.soho.ai.biz.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 代理批量状态变更请求。
 */
@Data
@ApiModel("代理批量状态变更请求")
public class AiProxyBatchStatusRequest {
    /**
     * 代理ID列表。
     */
    @ApiModelProperty(value = "代理ID列表")
    private List<Long> ids;

    /**
     * 目标状态：0禁用，1启用。
     */
    @ApiModelProperty(value = "目标状态：0禁用，1启用")
    private Integer status;
}

