package work.soho.ai.biz.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 代理批量测试请求。
 */
@Data
@ApiModel("代理批量测试请求")
public class AiProxyBatchTestRequest {
    /**
     * 代理ID列表。
     */
    @ApiModelProperty(value = "代理ID列表")
    private List<Long> ids;

    /**
     * 测试地址，默认 https://chatgpt.com。
     */
    @ApiModelProperty(value = "测试地址，默认 https://chatgpt.com")
    private String testUrl;

    /**
     * 连接超时毫秒，默认 10000。
     */
    @ApiModelProperty(value = "连接超时毫秒，默认 10000")
    private Integer timeoutMs;
}

