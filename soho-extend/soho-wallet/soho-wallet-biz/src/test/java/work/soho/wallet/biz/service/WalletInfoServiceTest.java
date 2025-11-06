package work.soho.wallet.biz.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;
import work.soho.wallet.biz.domain.WalletInfo;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
class WalletInfoServiceTest {
    @Autowired
    private WalletInfoService walletInfoService;

    @Test
    void getByUserIdAndType() {
    }

    @Test
    void updateAmount() {
        WalletInfo walletInfo = walletInfoService.getByUserIdAndType(1L, 1);
        walletInfoService.updateAmount(walletInfo, new BigDecimal("100"), "测试");
        walletInfoService.updateAmount(walletInfo, new BigDecimal("-101"), "测试");
    }

    @Test
    void testUpdateAmount() {
    }
}