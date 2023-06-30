package work.soho.code.biz.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.code.biz.CodeApplication;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class CodeTableControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void sql() {
    }

    @Test
    void exec() {
    }

    @Test
    void getCode() throws Exception {
        System.out.println("load table");
        mockMvc.perform(get("/admin/codeTable/codeFile")
                        .param("id", "155439143")
                        .param("templateId", "1170362382")
                        .param("codeNamespace", "work.soho.admin.biz")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("2000"))
                .andExpect(jsonPath("$.payload.fileName").value("src/pages/admin_user_login_log/index.js"))
                .andReturn();
    }

    @Test
    void saveCodes() {
    }

    @Test
    void createCodes() {
    }

    @Test
    void zipCodes() {
    }

    @Test
    void getTables() {
    }

    @Test
    void loadTable() throws Exception {
        System.out.println("load table");
        mockMvc.perform(get("/admin/codeTable/saveTables").param("tableNames", "pay_info").contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
