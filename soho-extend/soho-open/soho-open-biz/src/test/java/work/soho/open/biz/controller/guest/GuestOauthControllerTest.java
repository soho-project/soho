package work.soho.open.biz.controller.guest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.enums.OpenAppEnums;
import work.soho.open.biz.service.OpenAppService;
import work.soho.test.TestApp;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class GuestOauthControllerTest {
    private static final String APP_KEY = "app-key-1";
    private static final String APP_SECRET = "app-secret-1";
    private static final String REDIRECT_URI = "https://client.example.com/callback";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAppService openAppService;

    @Autowired
    private TokenServiceImpl tokenService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setup() {
        valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(openAppService.getOpenAppByKey(APP_KEY)).thenReturn(buildOpenApp(REDIRECT_URI, "redirect_uri=" + REDIRECT_URI));
    }

    @Test
    void authorizeRedirectsWithCodeWhenAllowed() throws Exception {
        mockMvc.perform(get("/open/guest/oauth/authorize")
                        .cookie(new Cookie("userToken", buildUserToken()))
                        .param("response_type", "code")
                        .param("client_id", APP_KEY)
                        .param("redirect_uri", REDIRECT_URI)
                        .param("state", "xyz"))
                .andExpect(status().isFound())
                .andDo(print())
                .andExpect(header().string("Location", containsString("code=")))
                .andExpect(header().string("Location", containsString("state=xyz")));

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture(), eq(300L), any());
        assertThat(keyCaptor.getValue()).startsWith("open:oauth:code:");
        assertThat(valueCaptor.getValue()).contains(APP_KEY);
        assertThat(valueCaptor.getValue()).contains(encodedRedirect(REDIRECT_URI));
    }

    @Test
    void authorizeRejectsInvalidRedirectUri() throws Exception {
        when(openAppService.getOpenAppByKey(APP_KEY))
                .thenReturn(buildOpenApp("https://allow.example.com/callback", "redirect_uri=https://allow.example.com/callback"));

        mockMvc.perform(get("/open/guest/oauth/authorize")
                        .param("response_type", "code")
                        .param("client_id", APP_KEY)
                        .param("redirect_uri", REDIRECT_URI)
                        .param("state", "abc"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", containsString("error=invalid_redirect_uri")))
                .andExpect(header().string("Location", containsString("state=abc")));
    }

    @Test
    void exchangeCodeForToken() throws Exception {
        String code = "code-123";
        String stored = APP_KEY + "|1|" + encodedRedirect(REDIRECT_URI) + "|code";
        when(valueOperations.get("open:oauth:code:" + code)).thenReturn(stored);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", "jwt-token");
        tokenMap.put("iat", "1000");
        tokenMap.put("exp", "2000");
        when(tokenService.createTokenInfo(any(), any())).thenReturn(tokenMap);

        mockMvc.perform(get("/open/guest/oauth/2.0")
                        .param("grant_type", "authorization_code")
                        .param("client_id", APP_KEY)
                        .param("client_secret", APP_SECRET)
                        .param("code", code)
                        .param("redirect_uri", REDIRECT_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(2000))
                .andExpect(jsonPath("$.payload.access_token").value("jwt-token"))
                .andExpect(jsonPath("$.payload.token_type").value("Bearer"));
    }

    @Test
    void clientCredentialsGrantReturnsToken() throws Exception {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", "jwt-cc");
        tokenMap.put("iat", "1000");
        tokenMap.put("exp", "2000");
        when(tokenService.createTokenInfo(any(), any())).thenReturn(tokenMap);

        mockMvc.perform(get("/open/guest/oauth/2.0")
                        .param("grant_type", "client_credentials")
                        .param("client_id", APP_KEY)
                        .param("client_secret", APP_SECRET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(2000))
                .andExpect(jsonPath("$.payload.access_token").value("jwt-cc"))
                .andExpect(jsonPath("$.payload.token_type").value("Bearer"));
    }

    private String buildUserToken() {
        SohoUserDetails user = new SohoUserDetails();
        user.setId(1L);
        user.setUsername("username");
        user.setAuthorities(AuthorityUtils.createAuthorityList("user"));
        return tokenService.createToken(user);
    }

    private OpenApp buildOpenApp(String redirectUriList, String remark) {
        OpenApp openApp = new OpenApp();
        openApp.setId(1L);
        openApp.setUserId(1L);
        openApp.setAppName("app");
        openApp.setAppKey(APP_KEY);
        openApp.setAppSecret(APP_SECRET);
        openApp.setStatus(OpenAppEnums.Status.ACTIVE.getId());
        openApp.setRedirectUriList(redirectUriList);
        openApp.setRemark(remark);
        return openApp;
    }

    private String encodedRedirect(String redirectUri) {
        return Base64.getUrlEncoder().encodeToString(redirectUri.getBytes(StandardCharsets.UTF_8));
    }
}
