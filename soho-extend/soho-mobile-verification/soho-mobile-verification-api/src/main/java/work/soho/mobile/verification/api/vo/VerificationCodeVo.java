package work.soho.mobile.verification.api.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VerificationCodeVo {
    private String id;

    /**
     * 是否已经认证通过
     */
    private Boolean isSuccess = false;

    /**
     * 认证的手机号码
     */
    private String phone;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
