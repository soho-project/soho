package work.soho.user.biz.controller.guest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.common.core.util.JacksonUtils;
import work.soho.test.TestApp;
import work.soho.user.api.vo.UserRegisterVo;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
class UserAuthControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void login() throws Exception {
        System.out.println("sendSms");
        HashMap<String, String> request = new HashMap<>();
        request.put("username", "15873164076");
        request.put("password", "123456");

        mockMvc.perform(post("/guest/user/auth/login").contentType("application/json")
                        .content(JacksonUtils.toJson(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void mobileLogin() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", "15873164073");
        map.put("password", "775845");
        map.put("captcha", "418489760848285696");
        mockMvc.perform(post("/guest/user/auth/mobileLogin").contentType("application/json")
                        .content(JacksonUtils.toJson(map)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void sendSms() throws Exception {
        HashMap<String, String> request = new HashMap<>();
        request.put("username", "15873164073");
        request.put("password", "123456");

        mockMvc.perform(post("/guest/user/auth/sendSms").contentType("application/json")
                        .content(JacksonUtils.toJson(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void defaultKaptcha() {
    }

    @Test
    void register() throws Exception {
        UserRegisterVo request = new UserRegisterVo();
        request.setUsername("P15873164076");
        request.setNickname("P15873164076");
        request.setPassword("123456");
        request.setPhone("15873164076");
        request.setEmail("i@liufang.org.cn");
        request.setVerifyCode("111111");
        request.setCodeId("111111");

        mockMvc.perform(post("/guest/user/auth/register").contentType("application/json")
                        .content(JacksonUtils.toJson(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}