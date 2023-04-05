package work.soho.code.biz.controller;

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
import work.soho.code.biz.domain.CodeTableColumn;
import work.soho.code.biz.service.CodeTableColumnService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 代码表字段Controller
 *
 * @author fang
 * @date 2022-11-30 02:58:03
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/codeTableColumn" )
public class CodeTableColumnController {

    private final CodeTableColumnService codeTableColumnService;

    /**
     * 查询代码表字段列表
     */
    @GetMapping("/list")
    @Node(value = "codeTableColumn::list", name = "代码表字段列表")
    public R<PageSerializable<CodeTableColumn>> list(CodeTableColumn codeTableColumn)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<CodeTableColumn> lqw = new LambdaQueryWrapper<CodeTableColumn>();
        if (codeTableColumn.getId() != null){
            lqw.eq(CodeTableColumn::getId ,codeTableColumn.getId());
        }
        if (codeTableColumn.getTableId() != null){
            lqw.eq(CodeTableColumn::getTableId ,codeTableColumn.getTableId());
        }
        if (StringUtils.isNotBlank(codeTableColumn.getName())){
            lqw.like(CodeTableColumn::getName ,codeTableColumn.getName());
        }
        if (StringUtils.isNotBlank(codeTableColumn.getTitle())){
            lqw.like(CodeTableColumn::getTitle ,codeTableColumn.getTitle());
        }
        if (StringUtils.isNotBlank(codeTableColumn.getDataType())){
            lqw.like(CodeTableColumn::getDataType ,codeTableColumn.getDataType());
        }
        if (codeTableColumn.getIsPk() != null){
            lqw.eq(CodeTableColumn::getIsPk ,codeTableColumn.getIsPk());
        }
        if (codeTableColumn.getIsNotNull() != null){
            lqw.eq(CodeTableColumn::getIsNotNull ,codeTableColumn.getIsNotNull());
        }
        if (codeTableColumn.getIsUnique() != null){
            lqw.eq(CodeTableColumn::getIsUnique ,codeTableColumn.getIsUnique());
        }
        if (codeTableColumn.getIsAutoIncrement() != null){
            lqw.eq(CodeTableColumn::getIsAutoIncrement ,codeTableColumn.getIsAutoIncrement());
        }
        if (codeTableColumn.getIsZeroFill() != null){
            lqw.eq(CodeTableColumn::getIsZeroFill ,codeTableColumn.getIsZeroFill());
        }
        if (StringUtils.isNotBlank(codeTableColumn.getDefaultValue())){
            lqw.like(CodeTableColumn::getDefaultValue ,codeTableColumn.getDefaultValue());
        }
        if (codeTableColumn.getLength() != null){
            lqw.eq(CodeTableColumn::getLength ,codeTableColumn.getLength());
        }
        if (codeTableColumn.getScale() != null){
            lqw.eq(CodeTableColumn::getScale ,codeTableColumn.getScale());
        }
        lqw.orderByAsc(CodeTableColumn::getId);
        List<CodeTableColumn> list = codeTableColumnService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取代码表字段详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "codeTableColumn::getInfo", name = "代码表字段详细信息")
    public R<CodeTableColumn> getInfo(@PathVariable("id" ) Long id) {
        return R.success(codeTableColumnService.getById(id));
    }

    /**
     * 新增代码表字段
     */
    @PostMapping
    @Node(value = "codeTableColumn::add", name = "代码表字段新增")
    public R<Boolean> add(@RequestBody CodeTableColumn codeTableColumn) {
        //小数点位不能为空
        if(codeTableColumn.getScale() == null) {
            codeTableColumn.setScale(0);
        }
        return R.success(codeTableColumnService.save(codeTableColumn));
    }

    /**
     * 修改代码表字段
     */
    @PutMapping
    @Node(value = "codeTableColumn::edit", name = "代码表字段修改")
    public R<Boolean> edit(@RequestBody CodeTableColumn codeTableColumn) {
        return R.success(codeTableColumnService.updateById(codeTableColumn));
    }

    /**
     * 删除代码表字段
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "codeTableColumn::remove", name = "代码表字段删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(codeTableColumnService.removeByIds(Arrays.asList(ids)));
    }
}
