package work.soho.code.biz.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.service.CodeTableService;
import work.soho.code.biz.service.DbService;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class CodeTableServiceImplTest {

    @Autowired
    private CodeTableService codeTableService;

    @Autowired
    private DbService dbService;

    @Test
    void isExistsTable() {
        assertTrue(dbService.isExistsTable("example"));
        assertFalse(dbService.isExistsTable(")"));
    }

    @Test
    void getTableVoById() {
        CodeTableVo codeTableVo =  codeTableService.getTableVoById(155439173);
        CodeTableVo remoteCodeTableVo = dbService.getTableByName("example_test");

        System.out.println(codeTableVo.toDiffSql(remoteCodeTableVo));
//        System.out.println(codeTableVo.toCreateSql());
    }

    @Test
    void getSqlById() {
        String sql = codeTableService.getSqlById(155439140);
        System.out.println(sql);
    }
}