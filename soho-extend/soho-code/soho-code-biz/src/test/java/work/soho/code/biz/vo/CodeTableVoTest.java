package work.soho.code.biz.vo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.service.CodeTableService;
import work.soho.code.biz.service.DbService;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class CodeTableVoTest {
    @Autowired
    private CodeTableService codeTableService;

    @Autowired
    private DbService dbService;


    @Test
    void toDiffSql() {
        System.out.println("test");
        CodeTableVo codeTable = codeTableService.getTableVoById(155439139);
        assertNotNull(codeTable);
        System.out.println(codeTable);
        CodeTableVo remoteCodeTable = dbService.getTableByName(codeTable.getName());
        assertNotNull(remoteCodeTable);

        String sql = codeTable.toDiffSql(remoteCodeTable);
        System.out.println(sql);
    }

    @Test
    void toCreateSql() {
    }
}