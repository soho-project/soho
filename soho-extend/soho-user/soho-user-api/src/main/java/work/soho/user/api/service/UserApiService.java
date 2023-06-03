package work.soho.user.api.service;

import work.soho.user.api.dto.UserInfoDto;

public interface UserApiService {
    UserInfoDto getUserById(Long id);
}
