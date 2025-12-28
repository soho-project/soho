package work.soho.express.biz.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
@AutoConfigureMockMvc
class ExpressOrderServiceTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ExpressOrderService expressOrderService;

    @Test
    void push() {
        expressOrderService.push(1L, 1L);
    }
}