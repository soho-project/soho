package work.soho.approvalprocess.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * 接口用； 请求创建审批单
 */
@Data
public class ApprovalProcessOrderVo {
    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 审批单ID
     */
    @ApiModelProperty("审批单ID")
    private Integer approvalProcessId;

    @ApiModelProperty("审批流编号")
    private String approvalProcessNo;

    @ApiModelProperty("申请人用户ID")
    private Long applyUserId;

    /**
     * 外部单号
     */
    @ApiModelProperty("外部单号")
    private String outNo;

    @ApiModelProperty("审批内容； json字符串")
    private LinkedList<ContentItem> contentItemList;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Data
    @Accessors(chain = true)
    public static class ContentItem {
        private String key;
        private String title;
        private String content;
    }
}
