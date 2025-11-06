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
import work.soho.common.core.util.JacksonUtils;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;
import work.soho.wallet.api.request.CreateWithdrawalOrderRequest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
class UserWalletWithdrawalOrderControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SohoWithUser(id = 1, username = "test",  role = "user")
    void create() throws Exception {
        CreateWithdrawalOrderRequest request = new CreateWithdrawalOrderRequest();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(1.00));
        request.setWithdrawBankId(1L);
        request.setPayPassword("123456");
        request.setNotes("test");

        mockMvc.perform(post("/wallet/user/walletWithdrawalOrder/create")
                                .contentType("application/json")
//                                .header("")
                        .content(JacksonUtils.toJson(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}