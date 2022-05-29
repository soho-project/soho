package work.soho.approvalprocess.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import work.soho.approvalprocess.domain.ApprovalProcessNode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class ApprovalProcessVo {
    @ApiModelProperty("id")
    private Integer id;

    /**
     * 审批流编号；业务绑定
     */
    @ApiModelProperty("审批流编号；业务绑定")
    private String no;

    /**
     * 审批流名称
     */
    @ApiModelProperty("审批流名称")
    private String name;

    /**
     * 审批类型； 1 普通列队审批
     */


    @ApiModelProperty("审批类型； 1 普通列队审批")
    private Integer type;

    /**
     * 是否开启 0 不开启  1 开启
     */


    @ApiModelProperty("是否开启 0 不开启  1 开启")
    private Boolean enable;

    /**
     * 审批拒绝动作; 1 默认关闭 10 打回第一个审批人  20 打回上一审批人
     */


    @ApiModelProperty("审批拒绝动作; 1 默认关闭 10 打回第一个审批人  20 打回上一审批人")
    private Integer rejectAction;

    @ApiModelProperty("元数据信息")
    private String metadata;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @ApiModelProperty("审批流节点")
    private List<NodeVo> nodes;

    @Data
    public static class NodeVo {
        private Long userId;
        private Long sourceUserId;
    }
}
