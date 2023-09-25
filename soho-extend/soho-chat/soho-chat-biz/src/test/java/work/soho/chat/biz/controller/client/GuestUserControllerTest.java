package work.soho.chat.biz.controller.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.chat.biz.req.LoginReq;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@AutoConfigureMockMvc
class GuestUserControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @SohoWithUser(id = 6, username = "197489090675871745",  role = "chat")
    void login() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", "test-218588670330933248");
        params.put("password", "123456");

        HttpHeaders httpHeaders = new HttpHeaders();
        mockMvc.perform(post("/guest/chat/user/login").headers(httpHeaders).contentType("application/json")
                        .content(JacksonUtils.toJson(params)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SohoWithUser(id = 6, username = "197489090675871745",  role = "chat")
    void create() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", "test-" + IDGeneratorUtils.snowflake().toString());
        params.put("password", "123456");
        params.put("age", "100");

        HttpHeaders httpHeaders = new HttpHeaders();
        mockMvc.perform(post("/guest/chat/user/register").headers(httpHeaders).contentType("application/json")
                        .content(JacksonUtils.toJson(params)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
