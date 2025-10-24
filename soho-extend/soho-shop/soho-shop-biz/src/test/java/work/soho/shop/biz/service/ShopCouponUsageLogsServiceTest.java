package work.soho.shop.biz.service;

import lombok.extern.log4j.Log4j2;
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
import work.soho.shop.biz.domain.ShopCouponUsageLogs;
import work.soho.test.TestApp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest( classes = TestApp.class )
@ActiveProfiles("dev")
@Log4j2
class ShopCouponUsageLogsServiceTest {
    @Autowired
    private ShopCouponUsageLogsService shopCouponUsageLogsService;
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
    public void testDb() {
        ShopCouponUsageLogs shopCouponUsageLogs = new ShopCouponUsageLogs();
        shopCouponUsageLogs.setUserId(1L);
        shopCouponUsageLogs.setCouponId(1L);
        shopCouponUsageLogs.setOrderId(1L);
        shopCouponUsageLogs.setOrderAmount(new BigDecimal("100"));
        shopCouponUsageLogs.setDiscountAmount(new BigDecimal("10"));
        shopCouponUsageLogs.setUsedAt(LocalDateTime.now());
        shopCouponUsageLogsService.save(shopCouponUsageLogs);

        shopCouponUsageLogsService.removeById(shopCouponUsageLogs.getId());
        System.out.println("删除成功");
    }
}