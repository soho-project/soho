package work.soho.ai.biz.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import work.soho.ai.biz.domain.AiProxyConfig;

/**
 * AI 代理配置监控视图。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("AI代理配置监控视图")
public class AiProxyConfigMonitorVO extends AiProxyConfig {
    /**
     * 当前动态有效权重。
     */
    @ApiModelProperty("当前动态有效权重")
    private Integer effectiveWeight;

    /**
     * 当前是否允许请求。
     */
    @ApiModelProperty("当前是否允许请求")
    private Boolean requestAllowed;

    /**
     * 当前是否熔断。
     */
    @ApiModelProperty("当前是否熔断")
    private Boolean circuitOpen;

    /**
     * 熔断截止时间戳。
     */
    @ApiModelProperty("熔断截止时间戳")
    private Long circuitOpenUntilMs;

    /**
     * 最近成功时间戳。
     */
    @ApiModelProperty("最近成功时间戳")
    private Long lastSuccessAtMs;

    /**
     * 最近失败时间戳。
     */
    @ApiModelProperty("最近失败时间戳")
    private Long lastFailureAtMs;

    /**
     * EWMA 总耗时。
     */
    @ApiModelProperty("EWMA总耗时")
    private Long ewmaTotalMs;

    /**
     * 连续失败次数。
     */
    @ApiModelProperty("连续失败次数")
    private Integer consecutiveFailures;

    /**
     * 连续慢请求次数。
     */
    @ApiModelProperty("连续慢请求次数")
    private Integer consecutiveSlowRequests;

    /**
     * 最近失败原因。
     */
    @ApiModelProperty("最近失败原因")
    private String lastErrorMessage;

    /**
     * 累计成功次数。
     */
    @ApiModelProperty("累计成功次数")
    private Long totalSuccessCount;

    /**
     * 累计失败次数。
     */
    @ApiModelProperty("累计失败次数")
    private Long totalFailureCount;
}
