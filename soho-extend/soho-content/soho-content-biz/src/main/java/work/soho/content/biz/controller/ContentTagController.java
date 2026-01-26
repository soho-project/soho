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
import work.soho.content.biz.domain.ContentTag;
import work.soho.content.biz.service.ContentTagService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 内容标签Controller
 *
 * @author fang
 */
@Api(value="内容标签",tags = "内容标签")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/contentTag" )
public class ContentTagController {

    private final ContentTagService contentTagService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询内容标签列表
     */
    @GetMapping("/list")
    @Node(value = "contentTag::list", name = "获取 内容标签 列表")
    @ApiOperation(value = "获取 内容标签 列表", notes = "获取 内容标签 列表")
    public R<PageSerializable<ContentTag>> list(ContentTag contentTag, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentTag> lqw = new LambdaQueryWrapper<>();
        lqw.eq(contentTag.getId() != null, ContentTag::getId ,contentTag.getId());
        lqw.like(StringUtils.isNotBlank(contentTag.getName()),ContentTag::getName ,contentTag.getName());
        lqw.like(StringUtils.isNotBlank(contentTag.getSlug()),ContentTag::getSlug ,contentTag.getSlug());
        lqw.like(StringUtils.isNotBlank(contentTag.getDescription()),ContentTag::getDescription ,contentTag.getDescription());
        lqw.eq(contentTag.getCount() != null, ContentTag::getCount ,contentTag.getCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentTag::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentTag::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentTag.getUpdatedTime() != null, ContentTag::getUpdatedTime ,contentTag.getUpdatedTime());
        lqw.orderByDesc(ContentTag::getId);
        List<ContentTag> list = contentTagService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容标签详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "contentTag::getInfo", name = "获取 内容标签 详细信息")
    @ApiOperation(value = "获取 内容标签 详细信息", notes = "获取 内容标签 详细信息")
    public R<ContentTag> getInfo(@PathVariable("id" ) Long id) {
        return R.success(contentTagService.getById(id));
    }

    /**
     * 新增内容标签
     */
    @PostMapping
    @Node(value = "contentTag::add", name = "新增 内容标签")
    @ApiOperation(value = "新增 内容标签", notes = "新增 内容标签")
    public R<Boolean> add(@RequestBody ContentTag contentTag) {
        return R.success(contentTagService.save(contentTag));
    }

    /**
     * 修改内容标签
     */
    @PutMapping
    @Node(value = "contentTag::edit", name = "修改 内容标签")
    @ApiOperation(value = "修改 内容标签", notes = "修改 内容标签")
    public R<Boolean> edit(@RequestBody ContentTag contentTag) {
        return R.success(contentTagService.updateById(contentTag));
    }

    /**
     * 删除内容标签
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "contentTag::remove", name = "删除 内容标签")
    @ApiOperation(value = "删除 内容标签", notes = "删除 内容标签")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(contentTagService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 内容标签 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ContentTag.class)
    @Node(value = "contentTag::exportExcel", name = "导出 内容标签 Excel")
    @ApiOperation(value = "导出 内容标签 Excel", notes = "导出 内容标签 Excel")
    public Object exportExcel(ContentTag contentTag, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ContentTag> lqw = new LambdaQueryWrapper<ContentTag>();
        lqw.eq(contentTag.getId() != null, ContentTag::getId ,contentTag.getId());
        lqw.like(StringUtils.isNotBlank(contentTag.getName()),ContentTag::getName ,contentTag.getName());
        lqw.like(StringUtils.isNotBlank(contentTag.getSlug()),ContentTag::getSlug ,contentTag.getSlug());
        lqw.like(StringUtils.isNotBlank(contentTag.getDescription()),ContentTag::getDescription ,contentTag.getDescription());
        lqw.eq(contentTag.getCount() != null, ContentTag::getCount ,contentTag.getCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentTag::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentTag::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentTag.getUpdatedTime() != null, ContentTag::getUpdatedTime ,contentTag.getUpdatedTime());
        lqw.orderByDesc(ContentTag::getId);
        return contentTagService.list(lqw);
    }

    /**
     * 导入 内容标签 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "contentTag::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 内容标签 Excel", notes = "导入 内容标签 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ContentTag.class, new ReadListener<ContentTag>() {
                @Override
                public void invoke(ContentTag contentTag, AnalysisContext analysisContext) {
                    contentTagService.save(contentTag);
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