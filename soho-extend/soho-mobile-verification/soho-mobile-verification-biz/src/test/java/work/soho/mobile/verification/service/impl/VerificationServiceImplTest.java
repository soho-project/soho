package work.soho.mobile.verification.service.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.mobile.verification.api.service.VerificationServiceApi;
import work.soho.mobile.verification.api.vo.VerificationCodeVo;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class VerificationServiceImplTest {

    @Autowired
    private VerificationServiceApi verificationServiceApi;

    @Test
    void getById() {
        String id = verificationServiceApi.createVerification();
        System.out.printf(id);
        VerificationCodeVo verificationCodeVo = verificationServiceApi.getById(id);
        System.out.printf(verificationCodeVo.toString());
    }

    @Test
    void pushMsg() {
    }

    @Test
    void createVerification() {
    }

    @Test
    void testCreateVerification() {
    }
}