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
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.ContentInfoService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 系统内容表Controller
 *
 * @author fang
 */
@Api(value="系统内容表",tags = "系统内容表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/contentInfo" )
public class ContentInfoController {

    private final ContentInfoService contentInfoService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询系统内容表列表
     */
    @GetMapping("/list")
    @Node(value = "contentInfo::list", name = "获取 系统内容表 列表")
    @ApiOperation(value = "获取 系统内容表 列表", notes = "获取 系统内容表 列表")
    public R<PageSerializable<ContentInfo>> list(ContentInfo contentInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(contentInfo.getId() != null, ContentInfo::getId ,contentInfo.getId());
        lqw.like(StringUtils.isNotBlank(contentInfo.getTitle()),ContentInfo::getTitle ,contentInfo.getTitle());
        lqw.like(StringUtils.isNotBlank(contentInfo.getDescription()),ContentInfo::getDescription ,contentInfo.getDescription());
        lqw.like(StringUtils.isNotBlank(contentInfo.getKeywords()),ContentInfo::getKeywords ,contentInfo.getKeywords());
        lqw.like(StringUtils.isNotBlank(contentInfo.getThumbnail()),ContentInfo::getThumbnail ,contentInfo.getThumbnail());
        lqw.like(StringUtils.isNotBlank(contentInfo.getBody()),ContentInfo::getBody ,contentInfo.getBody());
        lqw.eq(contentInfo.getUpdatedTime() != null, ContentInfo::getUpdatedTime ,contentInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentInfo.getCategoryId() != null, ContentInfo::getCategoryId ,contentInfo.getCategoryId());
        lqw.eq(contentInfo.getUserId() != null, ContentInfo::getUserId ,contentInfo.getUserId());
        lqw.eq(contentInfo.getStatus() != null, ContentInfo::getStatus ,contentInfo.getStatus());
        lqw.eq(contentInfo.getIsTop() != null, ContentInfo::getIsTop ,contentInfo.getIsTop());
        lqw.eq(contentInfo.getStar() != null, ContentInfo::getStar ,contentInfo.getStar());
        lqw.eq(contentInfo.getLikes() != null, ContentInfo::getLikes ,contentInfo.getLikes());
        lqw.eq(contentInfo.getDisLikes() != null, ContentInfo::getDisLikes ,contentInfo.getDisLikes());
        lqw.eq(contentInfo.getCommentsCount() != null, ContentInfo::getCommentsCount ,contentInfo.getCommentsCount());
        lqw.orderByDesc(ContentInfo::getId);
        List<ContentInfo> list = contentInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取系统内容表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "contentInfo::getInfo", name = "获取 系统内容表 详细信息")
    @ApiOperation(value = "获取 系统内容表 详细信息", notes = "获取 系统内容表 详细信息")
    public R<ContentInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(contentInfoService.getById(id));
    }

    /**
     * 新增系统内容表
     */
    @PostMapping
    @Node(value = "contentInfo::add", name = "新增 系统内容表")
    @ApiOperation(value = "新增 系统内容表", notes = "新增 系统内容表")
    public R<Boolean> add(@RequestBody ContentInfo contentInfo) {
        return R.success(contentInfoService.save(contentInfo));
    }

    /**
     * 修改系统内容表
     */
    @PutMapping
    @Node(value = "contentInfo::edit", name = "修改 系统内容表")
    @ApiOperation(value = "修改 系统内容表", notes = "修改 系统内容表")
    public R<Boolean> edit(@RequestBody ContentInfo contentInfo) {
        return R.success(contentInfoService.updateById(contentInfo));
    }

    /**
     * 删除系统内容表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "contentInfo::remove", name = "删除 系统内容表")
    @ApiOperation(value = "删除 系统内容表", notes = "删除 系统内容表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(contentInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该系统内容表 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "contentInfo::options", name = "获取 系统内容表 选项")
    @ApiOperation(value = "获取 系统内容表 选项", notes = "获取 系统内容表 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<ContentInfo> list = contentInfoService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(ContentInfo item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getTitle());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 系统内容表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ContentInfo.class)
    @Node(value = "contentInfo::exportExcel", name = "导出 系统内容表 Excel")
    @ApiOperation(value = "导出 系统内容表 Excel", notes = "导出 系统内容表 Excel")
    public Object exportExcel(ContentInfo contentInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ContentInfo> lqw = new LambdaQueryWrapper<ContentInfo>();
        lqw.eq(contentInfo.getId() != null, ContentInfo::getId ,contentInfo.getId());
        lqw.like(StringUtils.isNotBlank(contentInfo.getTitle()),ContentInfo::getTitle ,contentInfo.getTitle());
        lqw.like(StringUtils.isNotBlank(contentInfo.getDescription()),ContentInfo::getDescription ,contentInfo.getDescription());
        lqw.like(StringUtils.isNotBlank(contentInfo.getKeywords()),ContentInfo::getKeywords ,contentInfo.getKeywords());
        lqw.like(StringUtils.isNotBlank(contentInfo.getThumbnail()),ContentInfo::getThumbnail ,contentInfo.getThumbnail());
        lqw.like(StringUtils.isNotBlank(contentInfo.getBody()),ContentInfo::getBody ,contentInfo.getBody());
        lqw.eq(contentInfo.getUpdatedTime() != null, ContentInfo::getUpdatedTime ,contentInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentInfo.getCategoryId() != null, ContentInfo::getCategoryId ,contentInfo.getCategoryId());
        lqw.eq(contentInfo.getUserId() != null, ContentInfo::getUserId ,contentInfo.getUserId());
        lqw.eq(contentInfo.getStatus() != null, ContentInfo::getStatus ,contentInfo.getStatus());
        lqw.eq(contentInfo.getIsTop() != null, ContentInfo::getIsTop ,contentInfo.getIsTop());
        lqw.eq(contentInfo.getStar() != null, ContentInfo::getStar ,contentInfo.getStar());
        lqw.eq(contentInfo.getLikes() != null, ContentInfo::getLikes ,contentInfo.getLikes());
        lqw.eq(contentInfo.getDisLikes() != null, ContentInfo::getDisLikes ,contentInfo.getDisLikes());
        lqw.eq(contentInfo.getCommentsCount() != null, ContentInfo::getCommentsCount ,contentInfo.getCommentsCount());
        lqw.orderByDesc(ContentInfo::getId);
        return contentInfoService.list(lqw);
    }

    /**
     * 导入 系统内容表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "contentInfo::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 系统内容表 Excel", notes = "导入 系统内容表 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ContentInfo.class, new ReadListener<ContentInfo>() {
                @Override
                public void invoke(ContentInfo contentInfo, AnalysisContext analysisContext) {
                    contentInfoService.save(contentInfo);
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