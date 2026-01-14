package work.soho.approvalprocess.biz.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApprovalProcessOrderNodeApplyStatusEnum {
    WAITING(0, "等候审批中"),
    PENDING(10, "审核中"),
    REJECT(20, "驳回"),
    AGREE(30, "同意"),
    PASS_ON(40, "已转交")
    ;
    private final int status;
    private final String name;

    /**
     * 根据value值获取
     *
     * @param status
     * @return
     */
    public static ApprovalProcessOrderNodeApplyStatusEnum getByStatus(int status) {
        ApprovalProcessOrderNodeApplyStatusEnum[] valuse = values();
        for (int i = 0; i < valuse.length; i++) {
            if(valuse[i].getStatus() == status) {
                return valuse[i];
            }
        }
        return null;
    }
}
