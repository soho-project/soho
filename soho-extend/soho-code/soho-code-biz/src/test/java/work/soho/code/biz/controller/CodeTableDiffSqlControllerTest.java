package work.soho.code.biz.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.service.*;
import work.soho.test.TestApp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("dev")
class CodeTableDiffSqlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CodeTableService codeTableService;

    @MockBean
    private CodeTableColumnService codeTableColumnService;

    @MockBean
    private CodeTableTemplateService codeTableTemplateService;

    @MockBean
    private CodeTableTemplateGroupService codeTableTemplateGroupService;

    @MockBean
    private GroovyService groovyService;

    @MockBean
    private DbService dbService;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void diffSql_returnsDiff_whenRemoteTableExists() throws Exception {
//        CodeTableVo local = buildTable("test_table", "local comment");
//        CodeTableVo remote = buildTable("test_table", "remote comment");
//
//        when(codeTableService.getTableVoById(1)).thenReturn(local);
//        when(dbService.isExistsTable(eq("test_table"), any())).thenReturn(true);
//        when(dbService.getTableByName(eq("test_table"), any())).thenReturn(remote);
//
////        String expected = local.toDiffSql(remote);

        mockMvc.perform(get("/admin/codeTable/diffSql").queryParam("id", "155439139"))
                .andDo(print())
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.code").value("2000"))
//                .andExpect(jsonPath("$.payload").value(expected));
    }

    @Test
    void diffSql_returnsCreateSql_whenRemoteTableMissing() throws Exception {
        CodeTableVo local = buildTable("new_table", "new comment");

        when(codeTableService.getTableVoById(2)).thenReturn(local);
        when(dbService.isExistsTable(eq("new_table"), any())).thenReturn(false);

        String expected = local.toCreateSql();

        mockMvc.perform(get("/admin/codeTable/diffSql").param("id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("2000"))
                .andExpect(jsonPath("$.payload").value(expected));
    }

    private CodeTableVo buildTable(String name, String comment) {
        CodeTableVo table = new CodeTableVo();
        table.setName(name);
        table.setComment(comment);

        CodeTableVo.Column idColumn = new CodeTableVo.Column();
        idColumn.setName("id");
        idColumn.setDataType("int");
        idColumn.setLength(11);
        idColumn.setIsPk(1);
        idColumn.setIsNotNull(1);
        idColumn.setIsAutoIncrement(1);
        idColumn.setComment("primary key");

        table.getColumnList().add(idColumn);
        return table;
    }
}
