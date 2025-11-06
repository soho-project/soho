package work.soho.user.api.service;

import work.soho.user.api.dto.UserInfoDto;

public interface UserApiService {
    /**
     * 根据用户id获取用户信息
     *
     * @param id
     * @return
     */
    UserInfoDto getUserById(Long id);

    /**
     * 根据用户手机号获取用户信息
     *
     * @param phone
     * @return
     */
    UserInfoDto getUserInfoByPhone(String phone);

    /**
     * 验证支付密码是否正确
     */
    Boolean verificationUserInfoPayPassword(Long userId, String payPassword);

    /**
     * 更新用户信息
     *
     * @param userInfoDto
     * @return
     */
    UserInfoDto updateUserInfoDto(UserInfoDto userInfoDto);
}
