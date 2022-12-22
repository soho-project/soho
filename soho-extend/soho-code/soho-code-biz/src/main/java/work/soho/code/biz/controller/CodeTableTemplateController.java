package work.soho.code.biz.controller;

import java.time.LocalDateTime;

import work.soho.code.api.request.CodeTableTemplateRunTestRequest;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTable;
import work.soho.code.biz.domain.CodeTableTemplateGroup;
import work.soho.code.biz.service.CodeTableService;
import work.soho.code.biz.service.DbService;
import work.soho.code.biz.service.GroovyService;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.code.biz.domain.CodeTableTemplate;
import work.soho.code.biz.service.CodeTableTemplateService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 代码表模板;;option:id~titleController
 *
 * @author fang
 * @date 2022-12-08 00:31:27
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/codeTableTemplate" )
public class CodeTableTemplateController {

    private final CodeTableTemplateService codeTableTemplateService;

    private final CodeTableService codeTableService;

    private final GroovyService groovyService;

    private final DbService dbService;

    /**
     * 查询代码表模板;;option:id~title列表
     */
    @GetMapping("/list")
    @Node(value = "codeTableTemplate::list", name = "代码表模板;;option:id~title列表")
    public R<PageSerializable<CodeTableTemplate>> list(CodeTableTemplate codeTableTemplate, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<CodeTableTemplate> lqw = new LambdaQueryWrapper<CodeTableTemplate>();
        if(codeTableTemplate.getGroupId() != null) {
            lqw.eq(CodeTableTemplate::getGroupId, codeTableTemplate.getGroupId());
        }
        if (codeTableTemplate.getId() != null){
            lqw.eq(CodeTableTemplate::getId ,codeTableTemplate.getId());
        }
        if (StringUtils.isNotBlank(codeTableTemplate.getTitle())){
            lqw.like(CodeTableTemplate::getTitle ,codeTableTemplate.getTitle());
        }
        if (StringUtils.isNotBlank(codeTableTemplate.getName())){
            lqw.like(CodeTableTemplate::getName ,codeTableTemplate.getName());
        }
        if (codeTableTemplate.getStatus() != null){
            lqw.eq(CodeTableTemplate::getStatus ,codeTableTemplate.getStatus());
        }
        if (StringUtils.isNotBlank(codeTableTemplate.getCode())){
            lqw.like(CodeTableTemplate::getCode ,codeTableTemplate.getCode());
        }
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, CodeTableTemplate::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, CodeTableTemplate::getCreatedTime, betweenCreatedTimeRequest.getEndTime());

        List<CodeTableTemplate> list = codeTableTemplateService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取代码表模板;;option:id~title详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "codeTableTemplate::getInfo", name = "代码表模板;;option:id~title详细信息")
    public R<CodeTableTemplate> getInfo(@PathVariable("id" ) Long id) {
        return R.success(codeTableTemplateService.getById(id));
    }

    /**
     * 新增代码表模板;;option:id~title
     */
    @PostMapping
    @Node(value = "codeTableTemplate::add", name = "代码表模板;;option:id~title新增")
    public R<Boolean> add(@RequestBody CodeTableTemplate codeTableTemplate) {
       codeTableTemplate.setCreatedTime(LocalDateTime.now());
        return R.success(codeTableTemplateService.save(codeTableTemplate));
    }

    /**
     * 修改代码表模板;;option:id~title
     */
    @PutMapping
    @Node(value = "codeTableTemplate::edit", name = "代码表模板;;option:id~title修改")
    public R<Boolean> edit(@RequestBody CodeTableTemplate codeTableTemplate) {
        return R.success(codeTableTemplateService.updateById(codeTableTemplate));
    }

    /**
     * 删除代码表模板;;option:id~title
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "codeTableTemplate::remove", name = "代码表模板;;option:id~title删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(codeTableTemplateService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该表options[name:id, capitalName:Id, capitalKeyName:Id, dbColName:id, type:long, frontType:number, frontName:Id, frontMax:99999999999, frontStep:, isFilter:false, dbType:int, dbUnsigned:false, comment:null, annos:, length:11, scale:0, frontLength:12, defaultValue:null, isNotNull:true, specification:int(11), dbForeignName:, capitalForeignName:, javaForeignName:, javaType:Integer]
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "codeTableTemplate::options", name = "代码表模板;;option:id~titleOptions")
    public R<LinkedHashMap<String, String>> options(CodeTableTemplate codeTableTemplate) {
        LambdaQueryWrapper<CodeTableTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CodeTableTemplate::getStatus, 1);
        if(codeTableTemplate.getGroupId() != null) {
            lambdaQueryWrapper.eq(CodeTableTemplate::getGroupId, codeTableTemplate.getGroupId());
        }
        lambdaQueryWrapper.orderByAsc(CodeTableTemplate::getSort);
        List<CodeTableTemplate> list = codeTableTemplateService.list(lambdaQueryWrapper);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for(CodeTableTemplate item: list) {
            map.put(String.valueOf(item.getId()), item.getTitle());
        }
        return R.success(map);
    }

    /**
     * 运行测试代码
     *
     * @return
     */
    @PostMapping("runTest")
    public R<String> runTest(@RequestBody CodeTableTemplateRunTestRequest runTestRequest) {
        try {
            CodeTableVo tableVo = codeTableService.getTableVoById(runTestRequest.getTableId());
            System.out.println(tableVo);

            HashMap<String , String > binds = new HashMap<>();
            binds.put("baseNamespace", "work.soho.");
            CodeTable codeTable = BeanUtils.copy(tableVo, CodeTable.class);
            String result = groovyService.invoke(runTestRequest.getCode(), binds, "main", tableVo);
            if(result == null) {
                throw new RuntimeException("run error: result is null");
            }
            return R.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.success(e.toString());
        }
    }

}
