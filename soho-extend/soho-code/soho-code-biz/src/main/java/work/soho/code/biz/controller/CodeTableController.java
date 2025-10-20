package work.soho.code.biz.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.vo.OptionVo;
import work.soho.code.api.request.CodeTableTemplateSaveCodeRequest;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTable;
import work.soho.code.biz.domain.CodeTableColumn;
import work.soho.code.biz.domain.CodeTableTemplate;
import work.soho.code.biz.domain.CodeTableTemplateGroup;
import work.soho.code.biz.service.*;
import work.soho.code.biz.utils.ZipUtils;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码表;;option:id~nameController
 *
 * @author fang
 * @date 2022-11-30 15:47:28
 */
@Api(tags = "代码表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/codeTable" )
public class CodeTableController {

    private final CodeTableService codeTableService;
    private final CodeTableColumnService codeTableColumnService;

    private final CodeTableTemplateService codeTableTemplateService;

    private final CodeTableTemplateGroupService codeTableTemplateGroupService;

    private final GroovyService groovyService;

    private final DbService dbService;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 查询代码表;;option:id~name列表
     */
    @GetMapping("/list")
    @Node(value = "codeTable::list", name = "代码表;;option:id~name列表")
    public R<PageSerializable<CodeTable>> list(CodeTable codeTable)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<CodeTable> lqw = new LambdaQueryWrapper<CodeTable>();
        if (codeTable.getId() != null){
            lqw.eq(CodeTable::getId ,codeTable.getId());
        }
        if (StringUtils.isNotBlank(codeTable.getName())){
            lqw.like(CodeTable::getName ,codeTable.getName());
        }
        if (StringUtils.isNotBlank(codeTable.getTitle())){
            lqw.like(CodeTable::getTitle ,codeTable.getTitle());
        }
        if (StringUtils.isNotBlank(codeTable.getComment())){
            lqw.like(CodeTable::getComment ,codeTable.getComment());
        }
        lqw.orderByDesc(CodeTable::getId);
        List<CodeTable> list = codeTableService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取代码表;;option:id~name详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "codeTable::getInfo", name = "代码表;;option:id~name详细信息")
    public R<CodeTable> getInfo(@PathVariable("id" ) Long id) {
        return R.success(codeTableService.getById(id));
    }

    /**
     * 新增代码表;;option:id~name
     */
    @PostMapping
    @Node(value = "codeTable::add", name = "代码表;;option:id~name新增")
    public R<Boolean> add(@RequestBody CodeTable codeTable) {
        return R.success(codeTableService.save(codeTable));
    }

    /**
     * 修改代码表;;option:id~name
     */
    @PutMapping
    @Node(value = "codeTable::edit", name = "代码表;;option:id~name修改")
    public R<Boolean> edit(@RequestBody CodeTable codeTable) {
        return R.success(codeTableService.updateById(codeTable));
    }

    /**
     * 删除代码表;;option:id~name
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "codeTable::remove", name = "代码表;;option:id~name删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        codeTableColumnService.remove(new LambdaQueryWrapper<CodeTableColumn>().in(CodeTableColumn::getTableId, ids));
        return R.success(codeTableService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该表options[name:id, capitalName:Id, capitalKeyName:Id, dbColName:id, type:long, frontType:number, frontName:Id, frontMax:99999999999, frontStep:, dbType:int, dbUnsigned:false, comment:null, annos:, length:11, scale:0, frontLength:12, defaultValue:null, isNotNull:true, specification:int(11), dbForeignName:, capitalForeignName:, javaForeignName:, javaType:Integer]
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "codeTable::options", name = "代码表;;option:id~nameOptions")
    public R<HashMap<Integer, String>> options() {
        List<CodeTable> list = codeTableService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(CodeTable item: list) {
            map.put(item.getId(), item.getName());
        }
        return R.success(map);
    }

    /**
     * 获取创建表Sql
     *
     * @param id
     * @return
     */
    @GetMapping("sql")
    public R<String> sql(Integer id) {
        CodeTable codeTable = codeTableService.getById(id);
        if(codeTable == null) {
            return R.error("表不存在， 请检查");
        }
        return R.success(codeTableService.getSqlById(id));
    }

    /**
     * 获取同步到远程的sql
     *
     * @param id
     * @return
     */
    @GetMapping("diffSql")
    public R<String> diffSql(Integer id) {
        //获取本地表结构
        CodeTableVo codeTable = codeTableService.getTableVoById(id);
        if(codeTable == null) {
            return R.error("表不存在， 请检查");
        }

        //检查远程表是否存在
        if(dbService.isExistsTable(codeTable.getName(), codeTable.getDbSource())) {
            //获取远程表结构
            CodeTableVo remoteCodeTable = dbService.getTableByName(codeTable.getName(), codeTable.getDbSource());
            return R.success(codeTable.toDiffSql(remoteCodeTable));
        } else {
            return R.success(codeTable.toCreateSql());
        }
    }

    /**
     * 同步数据库表到低代码业务表
     *
     * @param id
     * @return
     */
    @GetMapping("syncTable2CodeTable/{id}")
    public R<Boolean> syncTable2CodeTable(@PathVariable Integer id) {
        CodeTable codeTable = codeTableService.getById(id);
        Assert.notNull(codeTable, "表不存在， 请检查");
        CodeTableVo codeTableVo = dbService.getTableByName(codeTable.getName(), codeTable.getDbSource());
        Assert.notNull(codeTableVo, "表不存在， 请检查");
        codeTableService.table2CodeTable(codeTableVo);
        return R.success();
    }

    /**
     * 执行生成sql
     *
     * @param id 同步的表
     * @param drop 是否删除远程库
     * @param isDiff 执行差异sql
     * @return
     */
    @GetMapping("exec")
    public R<Boolean> exec(Integer id, Boolean drop, Boolean isDiff) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try {
            connection.setAutoCommit(false);
            CodeTableVo codeTable = codeTableService.getTableVoById(id);
            if(codeTable == null) {
                return R.error("表不存在， 请检查");
            }
            if(drop) {
                dbService.dropTable(codeTable.getName(), codeTable.getDbSource());
            }
            if(isDiff !=null && isDiff) {
                //查询远程数据结构
                CodeTableVo remoteCodeTable = dbService.getTableByName(codeTable.getName());
                log.info(codeTable.toDiffSql(remoteCodeTable));
                dbService.execute(codeTable.toDiffSql(remoteCodeTable), codeTable.getDbSource());
            } else {
                log.info(codeTable.toCreateSql());
                dbService.execute(codeTable.toCreateSql(), codeTable.getDbSource());
            }
            connection.commit();
            return R.success();
        } catch (Exception e) {
            connection.rollback();
            return R.error(e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * 获取表指定模板生成代码
     *
     * @param id
     * @param templateId
     * @return
     */
    @GetMapping("codeFile")
    public R<HashMap<String, Object>> getCode(Integer id, Integer templateId, String codeNamespace, String moduleName) {
        try {
            HashMap<String, Object> res = new HashMap<>();
            CodeTableVo codeTableVo = codeTableService.getTableVoById(id);
            if(codeTableVo == null) {
                return R.error("表不存在");
            }
            //获取模板信息
            CodeTableTemplate codeTableTemplate = codeTableTemplateService.getById(templateId);
            if(codeTableTemplate == null) {
                return R.error("执行模板不存在");
            }

            HashMap<String, String> binds = new HashMap<>();
            binds.put("baseNamespace", codeNamespace);
            binds.put("moduleName", moduleName);
            String fileName = groovyService.runById(templateId, binds,"getFileName", codeTableVo);
            if(fileName == null) {
                return R.error("该文件无法生成，文件路径不能为空");
            }
            String code = groovyService.runById(templateId, binds, "getCode", codeTableVo);

            res.put("body", code);
            res.put("fileName", fileName);
            res.put("id", id);
            res.put("templateId", templateId);
            return R.success(res);
        } catch (Exception e) {
            String error = e.getMessage();
            //输出最近5行
            for(int i=0; i<e.getStackTrace().length && i<4; i++) {
                error += "\n";
                error += e.getStackTrace()[i].toString();
            }
            e.printStackTrace();
            return R.error(error);
        }
    }

    /**
     * 写入磁盘
     *
     * @param request
     * @return
     */
    @PostMapping("saveCode")
    public R<Boolean> saveCodes(@RequestBody CodeTableTemplateSaveCodeRequest request) {
        try {
            // 如果有传递groupId 则查询这个分组下所有的模板ID
            if(request.getGroupId() != null) {
                List<CodeTableTemplate> list = codeTableTemplateService.list(
                        new QueryWrapper<CodeTableTemplate>()
                                .eq("group_id", request.getGroupId()));
                ArrayList<Integer> templateIds = list.stream().map(CodeTableTemplate::getId).collect(Collectors.toCollection(ArrayList::new));
                request.setTemplateId(templateIds);
            }

            HashMap<String, String> files = getFiles(request, false);
            Iterator<String> it = files.keySet().iterator();
            while(it.hasNext()) {
                String filePath = it.next();
                String realPath =   request.getPath() + "/" + filePath ;
                log.info("保存文件到磁盘: {}", realPath);
                FileUtil.writeBytes(files.get(filePath).getBytes(), new File(realPath));
            }
            return R.success();
        } catch (Exception e) {
            return R.error(e.toString());
        }
    }

    /**
     * 获取代码
     *
     * @param request
     * @return
     */
    @PostMapping("codes")
    public R<HashMap<String, String>> createCodes(@RequestBody CodeTableTemplateSaveCodeRequest request) {
        try {
            // 如果有传递groupId 则查询这个分组下所有的模板ID
            if(request.getGroupId() != null) {
                List<CodeTableTemplate> list = codeTableTemplateService.list(
                        new QueryWrapper<CodeTableTemplate>()
                                .eq("group_id", request.getGroupId()));
                ArrayList<Integer> templateIds = list.stream().map(CodeTableTemplate::getId).collect(Collectors.toCollection(ArrayList::new));
                request.setTemplateId(templateIds);
            }
            HashMap<String, String> files = getFiles(request, false);
            return R.success(files);
        } catch (Exception e) {
            return R.error(e.toString());
        }
    }

    /**
     * 打包下载
     *
     * @param request
     * @return
     */
    @PostMapping("zipCodes")
    public HttpServletResponse zipCodes(@RequestBody CodeTableTemplateSaveCodeRequest request, HttpServletResponse response) throws IOException {
        String zipFile = null;
        InputStream inStream = null;
        try {
            // 如果有传递groupId 则查询这个分组下所有的模板ID
            if(request.getGroupId() != null) {
                List<CodeTableTemplate> list = codeTableTemplateService.list(
                        new QueryWrapper<CodeTableTemplate>()
                                .eq("group_id", request.getGroupId()));
                ArrayList<Integer> templateIds = list.stream().map(CodeTableTemplate::getId).collect(Collectors.toCollection(ArrayList::new));
                request.setTemplateId(templateIds);
            }

            HashMap<String, String> files = getFiles(request, false);
            Iterator<String> it = files.keySet().iterator();
            zipFile = "/tmp/code-" + System.currentTimeMillis() + ".zip";
            ZipUtils zipUtils = new ZipUtils(zipFile);
            while(it.hasNext()) {
                String filePath = it.next();
                zipUtils.appendFile(filePath, files.get(filePath));
            }
            zipUtils.close();

            inStream = new FileInputStream(zipFile);// 文件的存放路径
            // 设置输出的格式
            response.reset();
            response.setContentType("bin");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + System.currentTimeMillis() + ".zip\"");
            // 循环取出流中的数据
            byte[] b = new byte[100];
            int len;
            try {
                while ((len = inStream.read(b)) > 0)
                    response.getOutputStream().write(b, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(zipFile != null) {
                FileUtil.del(zipFile);
            }
            if(inStream != null) {
                inStream.close();
            }
        }
        return null;
    }

    /**
     * 获取对应的代码
     *
     * @param request
     * @return
     * @throws InvalidPropertiesFormatException
     */
    private HashMap<String, String> getFiles(CodeTableTemplateSaveCodeRequest request, Boolean realPath) throws InvalidPropertiesFormatException {
        HashMap<String, String> files = new HashMap<>();
        HashMap<String, String> binds = new HashMap<>();
        binds.put("baseNamespace", request.getCodeNamespace()); //基本命名空间
        binds.put("moduleName", request.getModuleName()); // 绑定模块名称
        binds.put("basePath", request.getPath()); //基本写入路径

        // 如果有传递groupId 则查询这个分组下所有的模板ID
        if(request.getGroupId() != null) {
            List<CodeTableTemplate> list = codeTableTemplateService.list(
                    new QueryWrapper<CodeTableTemplate>()
                            .eq("group_id", request.getGroupId()));
            ArrayList<Integer> templateIds = list.stream().map(CodeTableTemplate::getId).collect(Collectors.toCollection(ArrayList::new));
            request.setTemplateId(templateIds);
        }

        for(Integer templateId: request.getTemplateId()) {
            try {
                CodeTableVo codeTableVo = codeTableService.getTableVoById(request.getId());
                if(codeTableVo == null) {
                    throw new InvalidPropertiesFormatException("请传递正确的表ID");
                }
                //获取模板信息
                CodeTableTemplate codeTableTemplate = codeTableTemplateService.getById(templateId);
                if(codeTableTemplate == null) {
                    throw  new InvalidPropertiesFormatException("模板ID（"+templateId+"）不存在");
                }

                String code = groovyService.runById(templateId, binds, "getCode", codeTableVo);
                String fileName = groovyService.runById(templateId,  binds,"getFileName", codeTableVo);
                //跳过路径为空文件
                if(fileName == null) {
                    continue;
                }

                //从group获取基本路径
                if(codeTableTemplate.getGroupId() == null) {
                    continue;
                }

                String basePath = fileName;
                if(realPath) {
                    //代码写入文件系统
                    CodeTableTemplateGroup codeTableTemplateGroup = codeTableTemplateGroupService.getById(codeTableTemplate.getGroupId());
                    basePath = codeTableTemplateGroup.getBasePath();
                    if(request.getPath() != null) {
                        basePath += request.getPath();
                    }
                    basePath += fileName;
                }

                files.put(basePath, code);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return files;
    }

    /**
     * 获取数据库列表
     *
     * @return
     */
    @GetMapping("/getDbMap")
    public R<HashMap<String, String>> getDbSet() {
        Set<String> dbList = dbService.getDbList();
        HashMap<String, String> map = new HashMap<>();
        for(String item: dbList) {
            map.put(item, item);
        }
        return R.success(map);
    }

    /**
     * 获取数据库中的表
     *
     * @return
     */
    @GetMapping("/getDbTables")
    public R<HashMap<Object, Object>> getTables(String db) {
//        dbService.getTableByName("pay_info");
        List<Object> list = dbService.getTableNames(db);
        HashMap<Object, Object> map = new HashMap<>();
        for(Object item: list) {
            map.put(item, item);
        }
        return R.success(map);
    }

    /**
     * 保存数据库表结构到数据库表
     *
     * @return
     */
    @GetMapping("/saveTables")
    public R<Boolean> loadTable(@RequestParam("tableNames") String tables, @RequestParam String db) {
        String[] tableNames = tables.split( ",");

        if(tableNames == null || tableNames.length == 0) {
            return R.error("请选择需要导入的表");
        }

        // soho_shop.shop_freight_rule,shop_freight_template,shop_freight_calc_log
        for(int i=0; i<tableNames.length; i++) {
            CodeTableVo tableVo = dbService.getTableByName(tableNames[i], db);

            //保存数据
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
        }
        return R.success();
    }
}
