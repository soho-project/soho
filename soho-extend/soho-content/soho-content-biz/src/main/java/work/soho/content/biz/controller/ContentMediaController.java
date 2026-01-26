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
import work.soho.content.biz.domain.ContentMedia;
import work.soho.content.biz.service.ContentMediaService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 内容媒体Controller
 *
 * @author fang
 */
@Api(value="内容媒体",tags = "内容媒体")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/contentMedia" )
public class ContentMediaController {

    private final ContentMediaService contentMediaService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询内容媒体列表
     */
    @GetMapping("/list")
    @Node(value = "contentMedia::list", name = "获取 内容媒体 列表")
    @ApiOperation(value = "获取 内容媒体 列表", notes = "获取 内容媒体 列表")
    public R<PageSerializable<ContentMedia>> list(ContentMedia contentMedia, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentMedia> lqw = new LambdaQueryWrapper<>();
        lqw.eq(contentMedia.getId() != null, ContentMedia::getId ,contentMedia.getId());
        lqw.eq(contentMedia.getExternalMediaId() != null, ContentMedia::getExternalMediaId ,contentMedia.getExternalMediaId());
        lqw.like(StringUtils.isNotBlank(contentMedia.getTitle()),ContentMedia::getTitle ,contentMedia.getTitle());
        lqw.like(StringUtils.isNotBlank(contentMedia.getUrl()),ContentMedia::getUrl ,contentMedia.getUrl());
        lqw.like(StringUtils.isNotBlank(contentMedia.getMimeType()),ContentMedia::getMimeType ,contentMedia.getMimeType());
        lqw.like(StringUtils.isNotBlank(contentMedia.getDescription()),ContentMedia::getDescription ,contentMedia.getDescription());
        lqw.like(StringUtils.isNotBlank(contentMedia.getAltText()),ContentMedia::getAltText ,contentMedia.getAltText());
        lqw.eq(contentMedia.getUserId() != null, ContentMedia::getUserId ,contentMedia.getUserId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentMedia::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentMedia::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentMedia.getUpdatedTime() != null, ContentMedia::getUpdatedTime ,contentMedia.getUpdatedTime());
        lqw.orderByDesc(ContentMedia::getId);
        List<ContentMedia> list = contentMediaService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容媒体详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "contentMedia::getInfo", name = "获取 内容媒体 详细信息")
    @ApiOperation(value = "获取 内容媒体 详细信息", notes = "获取 内容媒体 详细信息")
    public R<ContentMedia> getInfo(@PathVariable("id" ) Long id) {
        return R.success(contentMediaService.getById(id));
    }

    /**
     * 新增内容媒体
     */
    @PostMapping
    @Node(value = "contentMedia::add", name = "新增 内容媒体")
    @ApiOperation(value = "新增 内容媒体", notes = "新增 内容媒体")
    public R<Boolean> add(@RequestBody ContentMedia contentMedia) {
        return R.success(contentMediaService.save(contentMedia));
    }

    /**
     * 修改内容媒体
     */
    @PutMapping
    @Node(value = "contentMedia::edit", name = "修改 内容媒体")
    @ApiOperation(value = "修改 内容媒体", notes = "修改 内容媒体")
    public R<Boolean> edit(@RequestBody ContentMedia contentMedia) {
        return R.success(contentMediaService.updateById(contentMedia));
    }

    /**
     * 删除内容媒体
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "contentMedia::remove", name = "删除 内容媒体")
    @ApiOperation(value = "删除 内容媒体", notes = "删除 内容媒体")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(contentMediaService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 内容媒体 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ContentMedia.class)
    @Node(value = "contentMedia::exportExcel", name = "导出 内容媒体 Excel")
    @ApiOperation(value = "导出 内容媒体 Excel", notes = "导出 内容媒体 Excel")
    public Object exportExcel(ContentMedia contentMedia, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ContentMedia> lqw = new LambdaQueryWrapper<ContentMedia>();
        lqw.eq(contentMedia.getId() != null, ContentMedia::getId ,contentMedia.getId());
        lqw.eq(contentMedia.getExternalMediaId() != null, ContentMedia::getExternalMediaId ,contentMedia.getExternalMediaId());
        lqw.like(StringUtils.isNotBlank(contentMedia.getTitle()),ContentMedia::getTitle ,contentMedia.getTitle());
        lqw.like(StringUtils.isNotBlank(contentMedia.getUrl()),ContentMedia::getUrl ,contentMedia.getUrl());
        lqw.like(StringUtils.isNotBlank(contentMedia.getMimeType()),ContentMedia::getMimeType ,contentMedia.getMimeType());
        lqw.like(StringUtils.isNotBlank(contentMedia.getDescription()),ContentMedia::getDescription ,contentMedia.getDescription());
        lqw.like(StringUtils.isNotBlank(contentMedia.getAltText()),ContentMedia::getAltText ,contentMedia.getAltText());
        lqw.eq(contentMedia.getUserId() != null, ContentMedia::getUserId ,contentMedia.getUserId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentMedia::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentMedia::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentMedia.getUpdatedTime() != null, ContentMedia::getUpdatedTime ,contentMedia.getUpdatedTime());
        lqw.orderByDesc(ContentMedia::getId);
        return contentMediaService.list(lqw);
    }

    /**
     * 导入 内容媒体 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "contentMedia::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 内容媒体 Excel", notes = "导入 内容媒体 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ContentMedia.class, new ReadListener<ContentMedia>() {
                @Override
                public void invoke(ContentMedia contentMedia, AnalysisContext analysisContext) {
                    contentMediaService.save(contentMedia);
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