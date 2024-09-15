package work.soho.groovy.biz.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.groovy.biz.service.GroovyExecutorService;
import work.soho.groovy.service.GroovyExecutorApiService;
import work.soho.test.TestApp;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class GroovyExecutorServiceImplTest {
    @Autowired
    private GroovyExecutorService groovyExecutorService;

    @Autowired
    private GroovyExecutorApiService groovyExecutorApiService;

    @Test
    void execute() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("x", 1);
        params.put("y", 2);
        Object result = this.groovyExecutorApiService.execute("x + y", params);
        assertEquals(3, result);
    }

    @Test
    void loadObjectFromCode() {
        this.groovyExecutorApiService.execute("println 'hello'.toUpperCase()");
    }
}
