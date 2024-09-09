package work.soho.example.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.api.admin.service.AdminDictApiService;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.example.biz.domain.ExampleCategory;
import work.soho.example.biz.service.ExampleCategoryService;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 自动化样例分类表Controller
 *
 * @author fang
 */
@Api(tags = "自动化样例分类表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/example/admin/exampleCategory" )
public class ExampleCategoryController {

    private final ExampleCategoryService exampleCategoryService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询自动化样例分类表列表
     */
    @GetMapping("/list")
    @Node(value = "exampleCategory::list", name = "获取 自动化样例分类表 列表")
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
        List<ExampleCategory> list = exampleCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取自动化样例分类表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "exampleCategory::getInfo", name = "获取 自动化样例分类表 详细信息")
    public R<ExampleCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleCategoryService.getById(id));
    }

    /**
     * 新增自动化样例分类表
     */
    @PostMapping
    @Node(value = "exampleCategory::add", name = "新增 自动化样例分类表")
    public R<Boolean> add(@RequestBody ExampleCategory exampleCategory) {
        return R.success(exampleCategoryService.save(exampleCategory));
    }

    /**
     * 修改自动化样例分类表
     */
    @PutMapping
    @Node(value = "exampleCategory::edit", name = "修改 自动化样例分类表")
    public R<Boolean> edit(@RequestBody ExampleCategory exampleCategory) {
        return R.success(exampleCategoryService.updateById(exampleCategory));
    }

    /**
     * 删除自动化样例分类表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "exampleCategory::remove", name = "删除 自动化样例分类表")
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
     * 获取该自动化样例分类表 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "exampleCategory::options", name = "获取 自动化样例分类表 选项")
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
    public R<List<TreeNodeVo>> tree() {
        List<ExampleCategory> list = exampleCategoryService.list();
        List<TreeNodeVo<Integer, Integer, Integer, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getTitle());
        }).collect(Collectors.toList());

        Map<Integer, List<TreeNodeVo>> mapVo = new HashMap<>();
        listVo.stream().forEach(item -> {
            mapVo.computeIfAbsent(item.getParentId(), k -> new ArrayList<>());
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
     * 导出 自动化样例分类表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xsl", modelClass = ExampleCategory.class)
    @Node(value = "exampleCategory::exportExcel", name = "导出 自动化样例分类表 Excel")
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
        return exampleCategoryService.list(lqw);
    }

    /**
     * 导入 自动化样例分类表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "exampleCategory::importExcel", name = "导入 自动化样例 Excel")
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