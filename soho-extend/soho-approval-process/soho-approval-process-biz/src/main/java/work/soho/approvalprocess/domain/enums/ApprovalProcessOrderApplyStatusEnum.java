package work.soho.approvalprocess.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApprovalProcessOrderApplyStatusEnum {
    PENDING(0, "审核中"),
    REJECT(1, "驳回"),
    AGREE(2, "同意");
    private final int status;
    private final String name;

    public static ApprovalProcessOrderApplyStatusEnum getByStatus(int status) {
        ApprovalProcessOrderApplyStatusEnum[] valuse = values();
        for (int i = 0; i < valuse.length; i++) {
            if(valuse[i].getStatus() == status) {
                return valuse[i];
            }
        }
        return null;
    }
}
