package work.soho.approvalprocess.biz.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalProcessOrderStatusEnum {
    PENDING(0, "待审核"),
    FINISH(1, "处理完成");

    private final int status;
    private final String name;

    public static ApprovalProcessOrderStatusEnum getByStatus(int status) {
        ApprovalProcessOrderStatusEnum[] valuse = values();
        for (int i = 0; i < valuse.length; i++) {
            if(valuse[i].getStatus() == status) {
                return valuse[i];
            }
        }
        return null;
    }
}
