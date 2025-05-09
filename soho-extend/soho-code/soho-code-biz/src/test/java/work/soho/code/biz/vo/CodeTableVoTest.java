package work.soho.code.biz.vo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

import work.soho.test.TestApp;

import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTable;
import work.soho.code.biz.domain.CodeTableColumn;
import work.soho.code.biz.domain.CodeTableTemplate;
import work.soho.code.biz.domain.CodeTableTemplateGroup;
import work.soho.code.biz.service.*;


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
        CodeTableVo codeTable = codeTableService.getTableVoById(155439140);
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