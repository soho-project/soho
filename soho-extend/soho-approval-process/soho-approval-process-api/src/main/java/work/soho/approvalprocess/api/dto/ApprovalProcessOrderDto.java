package work.soho.approvalprocess.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@ApiModel(value = "审批单", description = "审批单")
public class ApprovalProcessOrderDto {
    /**
     * id
     */
    @ApiModelProperty("id")
    private Integer id;

    /**
     * 审批单ID
     */


    @ApiModelProperty("审批单ID")
    private Integer approvalProcessId;

    @ApiModelProperty("申请人用户ID")
    private Long applyUserId;

    /**
     * 外部单号
     */
    @ApiModelProperty("外部单号")
    private String outNo;

    @ApiModelProperty("审批内容； json字符串")
    private String content;

    /**
     * 审批状态; 0 待审批  10 审批完成
     */


    @ApiModelProperty("审批状态; 0 待审批  10 审批完成")
    private Integer status;

    @ApiModelProperty("审批处理结果： 0 失败  1 成功")
    private Integer applyStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
