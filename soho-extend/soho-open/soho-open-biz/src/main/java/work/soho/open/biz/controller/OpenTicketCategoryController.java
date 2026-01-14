package work.soho.open.biz.controller;

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
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.open.biz.domain.OpenTicketCategory;
import work.soho.open.biz.service.OpenTicketCategoryService;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 问题分类Controller
 *
 * @author fang
 */
@Api(tags = "问题分类")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openTicketCategory" )
public class OpenTicketCategoryController {

    private final OpenTicketCategoryService openTicketCategoryService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询问题分类列表
     */
    @GetMapping("/list")
    @Node(value = "openTicketCategory::list", name = "获取 问题分类 列表")
    public R<PageSerializable<OpenTicketCategory>> list(OpenTicketCategory openTicketCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenTicketCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openTicketCategory.getId() != null, OpenTicketCategory::getId ,openTicketCategory.getId());
        lqw.like(StringUtils.isNotBlank(openTicketCategory.getTitle()),OpenTicketCategory::getTitle ,openTicketCategory.getTitle());
        lqw.like(openTicketCategory.getParentId() != null,OpenTicketCategory::getParentId ,openTicketCategory.getParentId());
        lqw.eq(openTicketCategory.getUpdatedTime() != null, OpenTicketCategory::getUpdatedTime ,openTicketCategory.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicketCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicketCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenTicketCategory::getId);
        List<OpenTicketCategory> list = openTicketCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取问题分类详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openTicketCategory::getInfo", name = "获取 问题分类 详细信息")
    public R<OpenTicketCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openTicketCategoryService.getById(id));
    }

    /**
     * 新增问题分类
     */
    @PostMapping
    @Node(value = "openTicketCategory::add", name = "新增 问题分类")
    public R<Boolean> add(@RequestBody OpenTicketCategory openTicketCategory) {
        return R.success(openTicketCategoryService.save(openTicketCategory));
    }

    /**
     * 修改问题分类
     */
    @PutMapping
    @Node(value = "openTicketCategory::edit", name = "修改 问题分类")
    public R<Boolean> edit(@RequestBody OpenTicketCategory openTicketCategory) {
        return R.success(openTicketCategoryService.updateById(openTicketCategory));
    }

    /**
     * 删除问题分类
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openTicketCategory::remove", name = "删除 问题分类")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        List<OpenTicketCategory> oldData = openTicketCategoryService.listByIds(Arrays.asList(ids));
        if(oldData.size() != ids.length) {
            return R.error("数据不存在");
        }
        //检查是否有子节点
        LambdaQueryWrapper<OpenTicketCategory> lqw = new LambdaQueryWrapper<>();
        lqw.in(OpenTicketCategory::getParentId, Arrays.asList(ids));
        lqw.notIn(OpenTicketCategory::getId, Arrays.asList(ids));
        List<OpenTicketCategory> list = openTicketCategoryService.list(lqw);
        if (!list.isEmpty()) {
            return R.error("还有子节点，无法删除");
        }
        return R.success(openTicketCategoryService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<OpenTicketCategory> list = openTicketCategoryService.list();
        List<TreeNodeVo<Long, Long, Long, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getTitle());
        }).collect(Collectors.toList());

        Map<Long, List<TreeNodeVo>> mapVo = new HashMap<>();
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
        return R.success(mapVo.get(0L));
    }

    /**
     * 导出 问题分类 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenTicketCategory.class)
    @Node(value = "openTicketCategory::exportExcel", name = "导出 问题分类 Excel")
    public Object exportExcel(OpenTicketCategory openTicketCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenTicketCategory> lqw = new LambdaQueryWrapper<OpenTicketCategory>();
        lqw.eq(openTicketCategory.getId() != null, OpenTicketCategory::getId ,openTicketCategory.getId());
        lqw.like(StringUtils.isNotBlank(openTicketCategory.getTitle()),OpenTicketCategory::getTitle ,openTicketCategory.getTitle());
        lqw.like(openTicketCategory.getParentId() != null,OpenTicketCategory::getParentId ,openTicketCategory.getParentId());
        lqw.eq(openTicketCategory.getUpdatedTime() != null, OpenTicketCategory::getUpdatedTime ,openTicketCategory.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicketCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicketCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenTicketCategory::getId);
        return openTicketCategoryService.list(lqw);
    }

    /**
     * 导入 问题分类 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openTicketCategory::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenTicketCategory.class, new ReadListener<OpenTicketCategory>() {
                @Override
                public void invoke(OpenTicketCategory openTicketCategory, AnalysisContext analysisContext) {
                    openTicketCategoryService.save(openTicketCategory);
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