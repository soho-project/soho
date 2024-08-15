package work.soho.code.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTable;
import work.soho.code.biz.domain.CodeTableColumn;
import work.soho.code.biz.service.CodeTableColumnService;
import work.soho.code.biz.service.CodeTableService;
import work.soho.code.biz.service.DbService;
import work.soho.code.biz.service.GroovyService;
import work.soho.code.biz.utils.ZipUtils;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;

import java.util.List;

@Api(tags = "代码表测试")
@RequestMapping("/client/api/table")
@RestController
@RequiredArgsConstructor
public class TestTableController {
    private final DbService dbService;
    private final CodeTableService codeTableService;
    private final CodeTableColumnService codeTableColumnService;

    private final GroovyService groovyService;

    @GetMapping("/get-table")
    public R<List<Object>> getTables() {
        dbService.getTableByName("pay_info");
        return R.success(dbService.getTableNames());
    }

    @GetMapping("/load_table")
    public R<Boolean> loadTable() {
        CodeTableVo tableVo = dbService.getTableByName("pay_info");
        //TODO 保存数据
        CodeTable codeTable = BeanUtils.copy(tableVo, CodeTable.class);
        //检查是否存在
        CodeTable oldTable = codeTableService.getOne(new LambdaQueryWrapper<CodeTable>().eq(CodeTable::getName, codeTable.getName()));
        if(oldTable != null) {
            codeTable.setId(oldTable.getId());
            //update
            codeTableService.updateById(codeTable);
        } else {
            codeTableService.save(codeTable);
        }

        //删除原有数据
        codeTableColumnService.remove(new LambdaQueryWrapper<CodeTableColumn>().eq(CodeTableColumn::getTableId, codeTable.getId()));
        //新增更新数据
        for(CodeTableVo.Column column: tableVo.getColumnList()) {
            CodeTableColumn codeTableColumn = BeanUtils.copy(column, CodeTableColumn.class);
            codeTableColumn.setTableId(codeTable.getId());
            codeTableColumnService.save(codeTableColumn);
        }


        return R.success();
    }

    @GetMapping("/run")
    public R<String> run() {
        String txt = groovyService.invoke03("\n" +
                "def sayHello(name) {\n" +
                "\tprintln name\n" +
                "\t\n" +
                "\t// 如果不写return, groovy方法的默认最后一行为 方法的返回值\n" +
                "\t//return \"我是测试要返回的值\"\n" +
                "\t\"GroovyShell_1中的sayHello()方法的返回值\" + name\n" +
                "}\n", "sayHello", "fang.liu");
//        System.out.println(txt);
        return R.success(txt);
    }

    @GetMapping("/zip")
    public R<String> zip() throws Exception {
        ZipUtils zipUtils = new ZipUtils("/tmp/fangtestzip2.zip");
        zipUtils.appendFile("a/a.txt", "test by fang");
        zipUtils.appendFile("a/b.txt", "test by fang");
        zipUtils.appendFile("a/c.txt", "这是中文测试");
        zipUtils.close();
        return R.success();
    }
}
