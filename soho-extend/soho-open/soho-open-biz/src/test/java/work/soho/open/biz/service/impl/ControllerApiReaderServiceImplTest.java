package work.soho.open.biz.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
@AutoConfigureMockMvc
class ControllerApiReaderServiceImplTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ControllerApiReaderServiceImpl controllerApiReaderService;


    @Test
    void getControllerApiInfo() {
    }

    @Test
    void getAllControllerApis() throws JsonProcessingException {
//        controllerApiReaderService.getAllControllerApis().forEach((k, v) -> {
//            log.info("{}", k);
//            log.info("{}", v);
//        });

        String md = controllerApiReaderService.printAsMarkdown(controllerApiReaderService.getAllControllerApis());
        System.out.println(md);
    }

}