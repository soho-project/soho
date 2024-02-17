package work.soho.mobile.verification.api.service;

import work.soho.mobile.verification.api.vo.VerificationCodeVo;

public interface VerificationServiceApi {
    /**
     * 根据ID获取认证信息
     *
     * @param id
     * @return
     */
    VerificationCodeVo getById(String id);

    /**
     * 创建认证
     *
     * @return
     */
    String createVerification();

    /**
     * 根据手机号创建认证
     *
     * @param phone
     * @return
     */
    String createVerification(String phone);
}
