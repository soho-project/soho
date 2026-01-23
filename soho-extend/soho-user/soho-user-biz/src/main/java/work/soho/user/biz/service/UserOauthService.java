package work.soho.user.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.user.api.dto.ThridOauthDto;
import work.soho.user.biz.domain.UserOauth;

import java.util.Map;

public interface UserOauthService extends IService<UserOauth> {
    Map<String, String> loginWithCode(String code);
    Map<String, String> login(UserOauth userOauth);
    Map<String, String> loginWithThridOauth(ThridOauthDto thridOauthDto);
}