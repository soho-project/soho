package work.soho.approvalprocess.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class MyProvalProcessOrderVo {
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("审批单ID")
    private Integer approvalProcessId;

    @ApiModelProperty("审批流名称")
    private String approvalProcessName;

    @ApiModelProperty("审批流编号")
    private String approvalProcessNo;

    @ApiModelProperty("申请人用户ID")
    private Long applyUserId;

    @ApiModelProperty("申请人用户名")
    private String appleUsername;

    @ApiModelProperty("外部单号")
    private String outNo;

    @ApiModelProperty("审批状态; 0 待审批  10 审批完成")
    private Integer status;

    @ApiModelProperty("审批状态名")
    private String statusName;

    @ApiModelProperty("审批处理结果： 0 失败  1 成功")
    private Integer applyStatus;

    @ApiModelProperty("审批处理结果名")
    private String applyStatusName;

    @ApiModelProperty("审批内容； json字符串")
    private String content;

    @ApiModelProperty("审批节点")
    private List<Node> nodes;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Data
    public static class Node {
        @ApiModelProperty("id")
        private Integer id;

        @ApiModelProperty("源审批用户ID；0无上一节点")
        private Long sourceUserId;

        @ApiModelProperty("审批用户ID")
        private Long userId;

        @ApiModelProperty("审批用户名")
        private String username;

        @ApiModelProperty("审批状态")
        private Integer status;

        @ApiModelProperty("审批状态名")
        private String statusName;

        @ApiModelProperty("审批批复语")
        private String reply;

        @ApiModelProperty("序列号； 决定审批顺序")
        private Integer serialNumber;

        @ApiModelProperty("创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;

        @ApiModelProperty("审批时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime approvalTime;
    }
}
