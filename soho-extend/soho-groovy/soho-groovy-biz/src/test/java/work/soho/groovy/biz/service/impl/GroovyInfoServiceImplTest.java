package work.soho.groovy.biz.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.groovy.biz.domain.GroovyGroup;
import work.soho.groovy.biz.domain.GroovyInfo;
import work.soho.groovy.biz.service.GroovyGroupService;
import work.soho.groovy.biz.service.GroovyInfoService;
import work.soho.groovy.api.service.GroovyInfoApiService;
import work.soho.test.TestApp;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class GroovyInfoServiceImplTest {
    @Autowired
    private GroovyInfoApiService groovyInfoApiService;

    @Autowired
    private GroovyInfoService groovyInfoService;

    @Autowired
    private GroovyGroupService groovyGroupService;

    private final static String TEST_NAME = "tmpGroovyName'";

    @Test
    void executor() {
        GroovyGroup group = new GroovyGroup();
        group.setName("tmpGroovyGroup");
        group.setTitle("tmpGroovyGroup");
        groovyGroupService.save(group);

        assertNotNull(group);
        GroovyInfo groovyInfo = new GroovyInfo();
        groovyInfo.setGroupId(group.getId());
        groovyInfo.setName(TEST_NAME);
        groovyInfo.setCreatedTime(LocalDateTime.now());
        groovyInfo.setUpdatedTime(LocalDateTime.now());

        String code = "x + y";
        groovyInfo.setCode(code);

        groovyInfoService.save(groovyInfo);

        HashMap<String, Object> params = new HashMap<>();
        params.put("x", 1);
        params.put("y", 2);
        Object result = this.groovyInfoApiService.executor(TEST_NAME, params);
        System.out.println(result);
        assertEquals(result, 3);

        groovyInfoService.removeById(groovyInfo.getId());
        groovyGroupService.removeById(group.getId());
    }
}
