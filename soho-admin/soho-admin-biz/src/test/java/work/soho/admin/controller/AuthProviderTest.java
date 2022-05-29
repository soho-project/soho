package work.soho.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.admin.AdminApplication;
import work.soho.common.core.util.JacksonUtils;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class AuthProviderTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void testPassword() {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }

    /**
     * 登录测试
     *
     * @throws Exception
     */
    @Test
    void login() throws Exception {
        HashMap<String, String> request = new HashMap<>();
        request.put("username", "admin");
        request.put("password", "123456");

        mockMvc.perform(post("/login").contentType("application/json")
                                .content(JacksonUtils.toJson(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}