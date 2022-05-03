package work.soho.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.admin.AdminApplication;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.common.core.support.SpringContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class AdminUserControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    public void setAuth() {
        GrantedAuthority[] authorities = new GrantedAuthority[0];

        TestingAuthenticationToken testingAuthenticationToken =
                new TestingAuthenticationToken(userDetailsService.loadUserByUsername("admin"), "123456", Arrays.stream(authorities).collect(Collectors.toList()));
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(testingAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void user() throws Exception {
        setAuth();
        mockMvc.perform(get("/admin/adminUser/user").contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void userOption() {
    }
}