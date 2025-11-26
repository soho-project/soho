package work.soho.shop.biz.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest( classes = TestApp.class )
@ActiveProfiles("dev")
@Log4j2
class ShopCartItemsServiceTest {
    @Autowired
    private ShopCartItemsService shopCartItemsService;

    @Test
    @SohoWithUser(id = 1L)
    void getUserCart() {
        log.info(shopCartItemsService.getUserCart(1L));
    }
}