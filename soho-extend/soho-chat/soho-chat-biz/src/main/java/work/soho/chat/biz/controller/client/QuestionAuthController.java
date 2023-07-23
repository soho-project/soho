package work.soho.chat.biz.controller.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.admin.common.security.utils.SecurityUtils;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;

import java.util.Map;

@RestController
@RequestMapping("/client/api/auth")
public class QuestionAuthController {
    /**
     * token密钥
     */
    @Value("${token.secret:defaultValue}")
    private String secret;

    /**
     * 获取访客token
     */
    @PostMapping
    public R<Map> token() {
        SohoUserDetails sohoUserDetails = new SohoUserDetails();
        sohoUserDetails.setId(IDGeneratorUtils.snowflake().longValue());
        sohoUserDetails.setUsername(String.valueOf(sohoUserDetails.getId()));
        sohoUserDetails.setAuthorities(AuthorityUtils.createAuthorityList("chat"));

        Map<String, String> tokenInfo = SecurityUtils.createTokenInfo(sohoUserDetails, 3600 * 24 * 30, secret);
        return R.success(tokenInfo);
    }
}
