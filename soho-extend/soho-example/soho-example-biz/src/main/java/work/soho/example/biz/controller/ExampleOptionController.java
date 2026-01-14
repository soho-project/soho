package work.soho.example.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.example.biz.domain.ExampleOption;
import work.soho.example.biz.service.ExampleOptionService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 样例选项Controller
 *
 * @author fang
 */
@Api(value="样例选项",tags = "样例选项")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/example/admin/exampleOption" )
public class ExampleOptionController {

    private final ExampleOptionService exampleOptionService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询样例选项列表
     */
    @GetMapping("/list")
    @Node(value = "exampleOption::list", name = "获取 样例选项 列表")
    @ApiOperation(value = "获取 样例选项 列表", notes = "获取 样例选项 列表")
    public R<PageSerializable<ExampleOption>> list(ExampleOption exampleOption, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExampleOption> lqw = new LambdaQueryWrapper<>();
        lqw.eq(exampleOption.getId() != null, ExampleOption::getId ,exampleOption.getId());
        lqw.like(StringUtils.isNotBlank(exampleOption.getKey()),ExampleOption::getKey ,exampleOption.getKey());
        lqw.like(StringUtils.isNotBlank(exampleOption.getValue()),ExampleOption::getValue ,exampleOption.getValue());
        lqw.eq(exampleOption.getUpdatedTime() != null, ExampleOption::getUpdatedTime ,exampleOption.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExampleOption::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExampleOption::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ExampleOption::getId);
        List<ExampleOption> list = exampleOptionService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取样例选项详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "exampleOption::getInfo", name = "获取 样例选项 详细信息")
    @ApiOperation(value = "获取 样例选项 详细信息", notes = "获取 样例选项 详细信息")
    public R<ExampleOption> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleOptionService.getById(id));
    }

    /**
     * 新增样例选项
     */
    @PostMapping
    @Node(value = "exampleOption::add", name = "新增 样例选项")
    @ApiOperation(value = "新增 样例选项", notes = "新增 样例选项")
    public R<Boolean> add(@RequestBody ExampleOption exampleOption) {
        return R.success(exampleOptionService.save(exampleOption));
    }

    /**
     * 修改样例选项
     */
    @PutMapping
    @Node(value = "exampleOption::edit", name = "修改 样例选项")
    @ApiOperation(value = "修改 样例选项", notes = "修改 样例选项")
    public R<Boolean> edit(@RequestBody ExampleOption exampleOption) {
        return R.success(exampleOptionService.updateById(exampleOption));
    }

    /**
     * 删除样例选项
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "exampleOption::remove", name = "删除 样例选项")
    @ApiOperation(value = "删除 样例选项", notes = "删除 样例选项")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(exampleOptionService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该样例选项 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "exampleOption::options", name = "获取 样例选项 选项")
    @ApiOperation(value = "获取 样例选项 选项", notes = "获取 样例选项 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<ExampleOption> list = exampleOptionService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(ExampleOption item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getValue());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 样例选项 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ExampleOption.class)
    @Node(value = "exampleOption::exportExcel", name = "导出 样例选项 Excel")
    @ApiOperation(value = "导出 样例选项 Excel", notes = "导出 样例选项 Excel")
    public Object exportExcel(ExampleOption exampleOption, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ExampleOption> lqw = new LambdaQueryWrapper<ExampleOption>();
        lqw.eq(exampleOption.getId() != null, ExampleOption::getId ,exampleOption.getId());
        lqw.like(StringUtils.isNotBlank(exampleOption.getKey()),ExampleOption::getKey ,exampleOption.getKey());
        lqw.like(StringUtils.isNotBlank(exampleOption.getValue()),ExampleOption::getValue ,exampleOption.getValue());
        lqw.eq(exampleOption.getUpdatedTime() != null, ExampleOption::getUpdatedTime ,exampleOption.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExampleOption::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExampleOption::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ExampleOption::getId);
        return exampleOptionService.list(lqw);
    }

    /**
     * 导入 样例选项 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "exampleOption::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 样例选项 Excel", notes = "导入 样例选项 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ExampleOption.class, new ReadListener<ExampleOption>() {
                @Override
                public void invoke(ExampleOption exampleOption, AnalysisContext analysisContext) {
                    exampleOptionService.save(exampleOption);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    //nothing todo
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(e.getMessage());
        }
    }
}