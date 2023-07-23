package work.soho.groovy.biz.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.groovy.biz.service.GroovyExecutorService;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class GroovyExecutorServiceImplTest {
    @Autowired
    private GroovyExecutorService groovyExecutorService;

    @Test
    void execute() {
    }

    @Test
    void loadObjectFromCode() {
        this.groovyExecutorService.execute("println 'hello'.toUpperCase()");
    }
}
