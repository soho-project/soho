package work.soho.code.biz.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
import work.soho.code.biz.domain.CodeTableTemplateGroup;
import work.soho.code.biz.service.CodeTableTemplateGroupService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 模本分组;;option:id~nameController
 *
 * @author fang
 * @date 2022-12-12 17:29:51
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/codeTableTemplateGroup" )
public class CodeTableTemplateGroupController {

    private final CodeTableTemplateGroupService codeTableTemplateGroupService;

    /**
     * 查询模本分组;;option:id~name列表
     */
    @GetMapping("/list")
    @Node(value = "codeTableTemplateGroup::list", name = "模本分组;;option:id~name列表")
    public R<PageSerializable<CodeTableTemplateGroup>> list(CodeTableTemplateGroup codeTableTemplateGroup, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<CodeTableTemplateGroup> lqw = new LambdaQueryWrapper<CodeTableTemplateGroup>();
        if (codeTableTemplateGroup.getId() != null){
            lqw.eq(CodeTableTemplateGroup::getId ,codeTableTemplateGroup.getId());
        }
        if (StringUtils.isNotBlank(codeTableTemplateGroup.getTitle())){
            lqw.like(CodeTableTemplateGroup::getTitle ,codeTableTemplateGroup.getTitle());
        }
        if (StringUtils.isNotBlank(codeTableTemplateGroup.getName())){
            lqw.like(CodeTableTemplateGroup::getName ,codeTableTemplateGroup.getName());
        }
        if (StringUtils.isNotBlank(codeTableTemplateGroup.getMainFunction())){
            lqw.like(CodeTableTemplateGroup::getMainFunction ,codeTableTemplateGroup.getMainFunction());
        }
        if (StringUtils.isNotBlank(codeTableTemplateGroup.getExplain())){
            lqw.like(CodeTableTemplateGroup::getExplain ,codeTableTemplateGroup.getExplain());
        }
        if (codeTableTemplateGroup.getUpdatedTime() != null){
            lqw.eq(CodeTableTemplateGroup::getUpdatedTime ,codeTableTemplateGroup.getUpdatedTime());
        }
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, CodeTableTemplateGroup::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, CodeTableTemplateGroup::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<CodeTableTemplateGroup> list = codeTableTemplateGroupService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取模本分组;;option:id~name详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "codeTableTemplateGroup::getInfo", name = "模本分组;;option:id~name详细信息")
    public R<CodeTableTemplateGroup> getInfo(@PathVariable("id" ) Long id) {
        return R.success(codeTableTemplateGroupService.getById(id));
    }

    /**
     * 新增模本分组;;option:id~name
     */
    @PostMapping
    @Node(value = "codeTableTemplateGroup::add", name = "模本分组;;option:id~name新增")
    public R<Boolean> add(@RequestBody CodeTableTemplateGroup codeTableTemplateGroup) {
       codeTableTemplateGroup.setUpdatedTime(LocalDateTime.now());
       codeTableTemplateGroup.setCreatedTime(LocalDateTime.now());
        return R.success(codeTableTemplateGroupService.save(codeTableTemplateGroup));
    }

    /**
     * 修改模本分组;;option:id~name
     */
    @PutMapping
    @Node(value = "codeTableTemplateGroup::edit", name = "模本分组;;option:id~name修改")
    public R<Boolean> edit(@RequestBody CodeTableTemplateGroup codeTableTemplateGroup) {
       codeTableTemplateGroup.setUpdatedTime(LocalDateTime.now());
        return R.success(codeTableTemplateGroupService.updateById(codeTableTemplateGroup));
    }

    /**
     * 删除模本分组;;option:id~name
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "codeTableTemplateGroup::remove", name = "模本分组;;option:id~name删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(codeTableTemplateGroupService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该表options[name:id, capitalName:Id, capitalKeyName:Id, dbColName:id, type:long, frontType:number, frontName:Id, frontMax:99999999999, frontStep:, isFilter:false, dbType:int, dbUnsigned:false, comment:ID, annos:, length:11, scale:0, frontLength:12, defaultValue:null, isNotNull:true, specification:int(11), dbForeignName:, capitalForeignName:, javaForeignName:, javaType:Integer]
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "codeTableTemplateGroup::options", name = "模本分组;;option:id~nameOptions")
    public R<LinkedList<OptionVo<Integer, String>>> options() {
        LambdaQueryWrapper<CodeTableTemplateGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(CodeTableTemplateGroup::getSort);
        List<CodeTableTemplateGroup> list = codeTableTemplateGroupService.list(lambdaQueryWrapper);
        System.out.println(list);
        LinkedList<OptionVo<Integer, String>> optionVos = new LinkedList<>();
        for(CodeTableTemplateGroup item: list) {
            OptionVo<Integer, String> option = new OptionVo<>();
            option.setValue(item.getId());
            option.setLabel(item.getName());
            optionVos.add(option);
        }
        return R.success(optionVos);
    }

    /**
     * 获取项目所有模块目录
     *
     * @param groupId
     * @return
     */
    @GetMapping("getBaseDirTree")
    public R<TreeNodeVo<String, String, String, String>> getModuleTree(Integer groupId) throws IOException {
        //递归查询 pom 文件目录
        CodeTableTemplateGroup group = codeTableTemplateGroupService.getById(groupId);
        return R.success(this.loopDir(group.getBasePath()));
    }

    /**
     * 获取目录树
     *
     * @param parentDir
     * @return
     * @throws IOException
     */
    public TreeNodeVo<String, String, String, String> loopDir(String parentDir) throws IOException {
        File file = new File(parentDir);
        //忽略依赖包目录; .开头  或者 node_modules 开头的忽略
        if(file.getName().equals("node_modules") || file.getName().startsWith(".")) {
            return null;
        }
        System.out.println(parentDir);
        TreeNodeVo<String, String, String, String> nodeVo = new TreeNodeVo<>(file.getCanonicalPath(), file.getCanonicalPath(), null, file.getName());
        if(file.isDirectory()) {
            //TODO 检查当前目录下是否存在模块标志性文件
            if(file.listFiles() != null) {
                for(File f:file.listFiles()) {
                    if(f.isFile()) {
                        continue;
                    }
                    TreeNodeVo sonNode = loopDir(f.getCanonicalPath());
                    if(sonNode != null) {
                        nodeVo.getChildren().add(sonNode);
                    }
                }
            }
        } else {
            return null;
        }
        return nodeVo;
    }
}
