package work.soho.content.biz.controller;

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
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.service.ContentCategoryService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 内容分类Controller
 *
 * @author fang
 */
@Api(value="内容分类",tags = "内容分类")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/contentCategory" )
public class ContentCategoryController {

    private final ContentCategoryService contentCategoryService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询内容分类列表
     */
    @GetMapping("/list")
    @Node(value = "contentCategory::list", name = "获取 内容分类 列表")
    @ApiOperation(value = "获取 内容分类 列表", notes = "获取 内容分类 列表")
    public R<PageSerializable<ContentCategory>> list(ContentCategory contentCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(contentCategory.getId() != null, ContentCategory::getId ,contentCategory.getId());
        lqw.like(StringUtils.isNotBlank(contentCategory.getName()),ContentCategory::getName ,contentCategory.getName());
        lqw.eq(contentCategory.getParentId() != null, ContentCategory::getParentId ,contentCategory.getParentId());
        lqw.like(StringUtils.isNotBlank(contentCategory.getDescription()),ContentCategory::getDescription ,contentCategory.getDescription());
        lqw.like(StringUtils.isNotBlank(contentCategory.getKeyword()),ContentCategory::getKeyword ,contentCategory.getKeyword());
        lqw.like(StringUtils.isNotBlank(contentCategory.getContent()),ContentCategory::getContent ,contentCategory.getContent());
        lqw.eq(contentCategory.getOrder() != null, ContentCategory::getOrder ,contentCategory.getOrder());
        lqw.eq(contentCategory.getIsDisplay() != null, ContentCategory::getIsDisplay ,contentCategory.getIsDisplay());
        lqw.eq(contentCategory.getUpdateTime() != null, ContentCategory::getUpdateTime ,contentCategory.getUpdateTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ContentCategory::getId);
        List<ContentCategory> list = contentCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容分类详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "contentCategory::getInfo", name = "获取 内容分类 详细信息")
    @ApiOperation(value = "获取 内容分类 详细信息", notes = "获取 内容分类 详细信息")
    public R<ContentCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(contentCategoryService.getById(id));
    }

    /**
     * 新增内容分类
     */
    @PostMapping
    @Node(value = "contentCategory::add", name = "新增 内容分类")
    @ApiOperation(value = "新增 内容分类", notes = "新增 内容分类")
    public R<Boolean> add(@RequestBody ContentCategory contentCategory) {
        return R.success(contentCategoryService.save(contentCategory));
    }

    /**
     * 修改内容分类
     */
    @PutMapping
    @Node(value = "contentCategory::edit", name = "修改 内容分类")
    @ApiOperation(value = "修改 内容分类", notes = "修改 内容分类")
    public R<Boolean> edit(@RequestBody ContentCategory contentCategory) {
        return R.success(contentCategoryService.updateById(contentCategory));
    }

    /**
     * 删除内容分类
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "contentCategory::remove", name = "删除 内容分类")
    @ApiOperation(value = "删除 内容分类", notes = "删除 内容分类")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        List<ContentCategory> oldData = contentCategoryService.listByIds(Arrays.asList(ids));
        if(oldData.size() != ids.length) {
            return R.error("数据不存在");
        }
        //检查是否有子节点
        LambdaQueryWrapper<ContentCategory> lqw = new LambdaQueryWrapper<>();
        lqw.in(ContentCategory::getParentId, Arrays.asList(ids));
        lqw.notIn(ContentCategory::getId, Arrays.asList(ids));
        List<ContentCategory> list = contentCategoryService.list(lqw);
        if (!list.isEmpty()) {
            return R.error("还有子节点，无法删除");
        }
        return R.success(contentCategoryService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("tree")
    @Node(value = "contentCategory::tree", name = "获取 内容分类 树")
    @ApiOperation(value = "获取 内容分类 树", notes = "获取 内容分类 树")
    public R<List<TreeNodeVo>> tree() {
        List<ContentCategory> list = contentCategoryService.list();
        List<TreeNodeVo<Long, Long, Long, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getName());
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
     * 导出 内容分类 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ContentCategory.class)
    @Node(value = "contentCategory::exportExcel", name = "导出 内容分类 Excel")
    @ApiOperation(value = "导出 内容分类 Excel", notes = "导出 内容分类 Excel")
    public Object exportExcel(ContentCategory contentCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ContentCategory> lqw = new LambdaQueryWrapper<ContentCategory>();
        lqw.eq(contentCategory.getId() != null, ContentCategory::getId ,contentCategory.getId());
        lqw.like(StringUtils.isNotBlank(contentCategory.getName()),ContentCategory::getName ,contentCategory.getName());
        lqw.eq(contentCategory.getParentId() != null, ContentCategory::getParentId ,contentCategory.getParentId());
        lqw.like(StringUtils.isNotBlank(contentCategory.getDescription()),ContentCategory::getDescription ,contentCategory.getDescription());
        lqw.like(StringUtils.isNotBlank(contentCategory.getKeyword()),ContentCategory::getKeyword ,contentCategory.getKeyword());
        lqw.like(StringUtils.isNotBlank(contentCategory.getContent()),ContentCategory::getContent ,contentCategory.getContent());
        lqw.eq(contentCategory.getOrder() != null, ContentCategory::getOrder ,contentCategory.getOrder());
        lqw.eq(contentCategory.getIsDisplay() != null, ContentCategory::getIsDisplay ,contentCategory.getIsDisplay());
        lqw.eq(contentCategory.getUpdateTime() != null, ContentCategory::getUpdateTime ,contentCategory.getUpdateTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ContentCategory::getId);
        return contentCategoryService.list(lqw);
    }

    /**
     * 导入 内容分类 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "contentCategory::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 内容分类 Excel", notes = "导入 内容分类 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ContentCategory.class, new ReadListener<ContentCategory>() {
                @Override
                public void invoke(ContentCategory contentCategory, AnalysisContext analysisContext) {
                    contentCategoryService.save(contentCategory);
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