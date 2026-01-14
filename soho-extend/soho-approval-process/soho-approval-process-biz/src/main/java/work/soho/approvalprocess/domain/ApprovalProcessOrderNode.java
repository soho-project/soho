package work.soho.approvalprocess.domain;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 审批单节点
 *
 * @TableName approval_process_order_node
 */
@Data
public class ApprovalProcessOrderNode implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 审批单ID
     */
    @ApiModelProperty("")
    private Integer orderId;

    @ApiModelProperty("上一审批人； 源审批人")
    private Long sourceUserId;

    @ApiModelProperty("审批序号；排序")
    private Integer serialNumber;

    /**
     * 审批人ID
     */
    @ApiModelProperty("审批人ID")
    private Long userId;

    /**
     * 审批状态
     */
    @ApiModelProperty("审批状态")
    private Integer status;

    /**
     * 审批回复
     */
    @ApiModelProperty("审批回复")
    private String reply;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @ApiModelProperty("审批时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalTime;
}
