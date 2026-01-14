package work.soho.example.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.example.biz.domain.ExampleCategory;
import work.soho.example.biz.service.ExampleCategoryService;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 分类样例Controller
 *
 * @author fang
 */
@Api(value="分类样例",tags = "分类样例")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/example/admin/exampleCategory" )
public class ExampleCategoryController {

    private final ExampleCategoryService exampleCategoryService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询分类样例列表
     */
    @GetMapping("/list")
    @Node(value = "exampleCategory::list", name = "获取 分类样例 列表")
    @ApiOperation(value = "获取 分类样例 列表", notes = "获取 分类样例 列表")
    public R<PageSerializable<ExampleCategory>> list(ExampleCategory exampleCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExampleCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(exampleCategory.getId() != null, ExampleCategory::getId ,exampleCategory.getId());
        lqw.like(StringUtils.isNotBlank(exampleCategory.getTitle()),ExampleCategory::getTitle ,exampleCategory.getTitle());
        lqw.eq(exampleCategory.getParentId() != null, ExampleCategory::getParentId ,exampleCategory.getParentId());
        lqw.eq(exampleCategory.getUpdatedTime() != null, ExampleCategory::getUpdatedTime ,exampleCategory.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExampleCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExampleCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(exampleCategory.getOnlyDate() != null, ExampleCategory::getOnlyDate ,exampleCategory.getOnlyDate());
        lqw.eq(exampleCategory.getPayDatetime() != null, ExampleCategory::getPayDatetime ,exampleCategory.getPayDatetime());
        lqw.like(StringUtils.isNotBlank(exampleCategory.getImg()),ExampleCategory::getImg ,exampleCategory.getImg());
        lqw.orderByDesc(ExampleCategory::getId);
        List<ExampleCategory> list = exampleCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取分类样例详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "exampleCategory::getInfo", name = "获取 分类样例 详细信息")
    @ApiOperation(value = "获取 分类样例 详细信息", notes = "获取 分类样例 详细信息")
    public R<ExampleCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleCategoryService.getById(id));
    }

    /**
     * 新增分类样例
     */
    @PostMapping
    @Node(value = "exampleCategory::add", name = "新增 分类样例")
    @ApiOperation(value = "新增 分类样例", notes = "新增 分类样例")
    public R<Boolean> add(@RequestBody ExampleCategory exampleCategory) {
        return R.success(exampleCategoryService.save(exampleCategory));
    }

    /**
     * 修改分类样例
     */
    @PutMapping
    @Node(value = "exampleCategory::edit", name = "修改 分类样例")
    @ApiOperation(value = "修改 分类样例", notes = "修改 分类样例")
    public R<Boolean> edit(@RequestBody ExampleCategory exampleCategory) {
        return R.success(exampleCategoryService.updateById(exampleCategory));
    }

    /**
     * 删除分类样例
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "exampleCategory::remove", name = "删除 分类样例")
    @ApiOperation(value = "删除 分类样例", notes = "删除 分类样例")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        List<ExampleCategory> oldData = exampleCategoryService.listByIds(Arrays.asList(ids));
        if(oldData.size() != ids.length) {
            return R.error("数据不存在");
        }
        //检查是否有子节点
        LambdaQueryWrapper<ExampleCategory> lqw = new LambdaQueryWrapper<>();
        lqw.in(ExampleCategory::getParentId, Arrays.asList(ids));
        lqw.notIn(ExampleCategory::getId, Arrays.asList(ids));
        List<ExampleCategory> list = exampleCategoryService.list(lqw);
        if (!list.isEmpty()) {
            return R.error("还有子节点，无法删除");
        }
        return R.success(exampleCategoryService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该分类样例 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "exampleCategory::options", name = "获取 分类样例 选项")
    @ApiOperation(value = "获取 分类样例 选项", notes = "获取 分类样例 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<ExampleCategory> list = exampleCategoryService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(ExampleCategory item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getTitle());
            options.add(optionVo);
        }
        return R.success(options);
    }

    @GetMapping("tree")
    @Node(value = "exampleCategory::tree", name = "获取 分类样例 树")
    @ApiOperation(value = "获取 分类样例 树", notes = "获取 分类样例 树")
    public R<List<TreeNodeVo>> tree() {
        List<ExampleCategory> list = exampleCategoryService.list();
        List<TreeNodeVo<Integer, Integer, Integer, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getTitle());
        }).collect(Collectors.toList());

        Map<Integer, List<TreeNodeVo>> mapVo = new HashMap<>();
        listVo.stream().forEach(item -> {
            if(mapVo.get(item.getParentId()) == null) {
                mapVo.put(item.getParentId(), new ArrayList<>());
            }
            mapVo.get(item.getParentId()).add(item);
        });

        listVo.forEach(item -> {
            if(mapVo.containsKey(item.getKey())) {
                item.setChildren(mapVo.get(item.getKey()));
            }
        });
        return R.success(mapVo.get(0));
    }

    /**
     * 导出 分类样例 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ExampleCategory.class)
    @Node(value = "exampleCategory::exportExcel", name = "导出 分类样例 Excel")
    @ApiOperation(value = "导出 分类样例 Excel", notes = "导出 分类样例 Excel")
    public Object exportExcel(ExampleCategory exampleCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ExampleCategory> lqw = new LambdaQueryWrapper<ExampleCategory>();
        lqw.eq(exampleCategory.getId() != null, ExampleCategory::getId ,exampleCategory.getId());
        lqw.like(StringUtils.isNotBlank(exampleCategory.getTitle()),ExampleCategory::getTitle ,exampleCategory.getTitle());
        lqw.eq(exampleCategory.getParentId() != null, ExampleCategory::getParentId ,exampleCategory.getParentId());
        lqw.eq(exampleCategory.getUpdatedTime() != null, ExampleCategory::getUpdatedTime ,exampleCategory.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExampleCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExampleCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(exampleCategory.getOnlyDate() != null, ExampleCategory::getOnlyDate ,exampleCategory.getOnlyDate());
        lqw.eq(exampleCategory.getPayDatetime() != null, ExampleCategory::getPayDatetime ,exampleCategory.getPayDatetime());
        lqw.like(StringUtils.isNotBlank(exampleCategory.getImg()),ExampleCategory::getImg ,exampleCategory.getImg());
        lqw.orderByDesc(ExampleCategory::getId);
        return exampleCategoryService.list(lqw);
    }

    /**
     * 导入 分类样例 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "exampleCategory::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 分类样例 Excel", notes = "导入 分类样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ExampleCategory.class, new ReadListener<ExampleCategory>() {
                @Override
                public void invoke(ExampleCategory exampleCategory, AnalysisContext analysisContext) {
                    exampleCategoryService.save(exampleCategory);
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