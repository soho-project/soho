package work.soho.approvalprocess.domain;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 审批流节点
 *
 * @TableName approval_process_node
 */
@Data
public class ApprovalProcessNode implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 审批流ID
     */
    @ApiModelProperty("审批流ID")
    private Integer approvalProcessId;

    @ApiModelProperty("源审批用户ID；0无上一节点")
    private Long sourceUserId;

    /**
     * 审批用户ID
     */
    @ApiModelProperty("审批用户ID")
    private Long userId;

    /**
     * 序列号； 决定审批顺序
     */
    @ApiModelProperty("序列号； 决定审批顺序")
    private Integer serialNumber;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
