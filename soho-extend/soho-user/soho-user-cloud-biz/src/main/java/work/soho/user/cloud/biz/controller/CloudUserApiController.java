package work.soho.user.cloud.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud/user/user")
public class CloudUserApiController {
    private final UserApiService userApiService;

    @GetMapping(value = "{id}")
    public UserInfoDto getUserInfo(@PathVariable("id") Long id) {
        return userApiService.getUserById(id);
    }
}
