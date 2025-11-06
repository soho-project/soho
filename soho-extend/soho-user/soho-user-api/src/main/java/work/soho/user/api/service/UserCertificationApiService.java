package work.soho.user.api.service;

import work.soho.user.api.dto.UserCertificationDto;

public interface UserCertificationApiService {
    // 获取推荐用户数量
//    Long getRecommendUserCount(Long userId);

    // 获取指定用户有效身份认证
    UserCertificationDto getActiveUserCertificationByUserId(Long userId);
}
