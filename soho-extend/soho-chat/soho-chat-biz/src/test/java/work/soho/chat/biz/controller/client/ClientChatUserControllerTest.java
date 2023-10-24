package work.soho.chat.biz.controller.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.common.core.util.JacksonUtils;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@AutoConfigureMockMvc
class ClientChatUserControllerTest {
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
    @SohoWithUser(id = 1, username = "admin",  role = "chat")
    void authPhone() throws Exception {
        System.out.println("test by fang");
        HttpHeaders httpHeaders = new HttpHeaders();
        ChatUser chatUser = new ChatUser();
        chatUser.setPhone("15873164073");
        mockMvc.perform(post("/chat/chat/chatUser/authPhone").headers(httpHeaders).contentType("application/json")
                        .content(JacksonUtils.toJson(chatUser)))

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SohoWithUser(id = 1, username = "admin",  role = "chat")
    void authEmail() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        ChatUser chatUser = new ChatUser();
        chatUser.setEmail("i@liufang.org.cn");
        mockMvc.perform(post("/chat/chat/chatUser/authEmail").headers(httpHeaders).contentType("application/json")
                        .content(JacksonUtils.toJson(chatUser)))

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
