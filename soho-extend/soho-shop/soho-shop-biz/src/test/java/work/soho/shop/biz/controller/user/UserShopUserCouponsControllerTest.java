package work.soho.shop.biz.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
class UserShopUserCouponsControllerTest {

    @Test
    void getUserCoupons() {
        mockMvc.perform(get("/shop/user/shopUserCoupons/getUserCoupons"))
                //.andExpect(status().is(404))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}