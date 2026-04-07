package work.soho.user.cloud.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.BeanUtils;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户云端 API 控制器。
 */
@RestController
@RequestMapping("/cloud/user/user")
@RequiredArgsConstructor
public class CloudUserApiController {
    private final UserInfoService userInfoService;

    /**
     * 根据用户 ID 获取用户信息。
     */
    @GetMapping("/{id}")
    public UserInfoDto getUserById(@PathVariable("id") Long id) {
        UserInfo userInfo = userInfoService.getById(id);
        if (userInfo == null) {
            return null;
        }
        return BeanUtils.copy(userInfo, UserInfoDto.class);
    }

    /**
     * 获取全部用户 ID。
     */
    @GetMapping("/ids")
    public List<Long> getAllUserIds() {
        return userInfoService.list().stream()
                .map(UserInfo::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
