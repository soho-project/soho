package work.soho.admin.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.api.service.AdminResourceApiService;
import work.soho.test.TestApp;

import java.util.List;

@Slf4j
//@ContextConfiguration
//@WebAppConfiguration("src/main/resources")
//@SpringBootTest(classes = AdminApplication.class)
@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class AdminResourceServiceImplTest {
    @Autowired
    private AdminResourceApiService adminResourceApiService;

    @Test
    void getResourceKeyListByRole() {
        List<String> resourceKeyListByRole = adminResourceApiService.getResourceKeyListByRole("admin");
        System.out.println(resourceKeyListByRole);
    }
}