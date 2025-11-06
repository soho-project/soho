package work.soho.wallet.biz.controller.user;

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
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
class UserWalletLogControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }


    @Test
    @SohoWithUser(id = 1, username = "test",  role = "user")
    void list() throws Exception {

        mockMvc.perform(get("/user/user/walletLog/list")
                        .param("type", "1")
                        .param("startTime", "2023-05-01 00:00:00")
                        .param("endTime", "2023-05-01 23:59:59")
                                .contentType("application/json")
//                                .header("")
//                        .content(JacksonUtils.toJson(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}