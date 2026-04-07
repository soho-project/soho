package work.soho.user.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;
import work.soho.user.cloud.bridge.feign.UserApiFeign;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserApiServiceImpl implements UserApiService {
    private final UserApiFeign userApiFeign;

    @Override
    public UserInfoDto getUserById(Long id) {
        return userApiFeign.getUserById(id);
    }

    @Override
    public UserInfoDto getUserInfoByPhone(String phone) {
        return null;
    }

    @Override
    public Boolean verificationUserInfoPayPassword(Long userId, String payPassword) {
        return null;
    }

    @Override
    public UserInfoDto updateUserInfoDto(UserInfoDto userInfoDto) {
        return null;
    }

    @Override
    public List<Long> getAllUserIds() {
        return userApiFeign.getAllUserIds();
    }
}
