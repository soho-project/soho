package work.soho.approvalprocess.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 审批流元数据单元
 */
@Data
@Accessors(chain = true)
public class ApprovalProcessMetadataVo {
    /**
     * 关键字
     */
    private String key;

    /**
     * 显示标题名
     */
    private String title;

    /**
     * 数据类型； 显示组件类型，数据类型
     */
    private String type;
}
