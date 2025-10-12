package work.soho.content.biz.controller;


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
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.AdminContentRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.AdminContentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * 系统内容表Controller
 *
 * @author i
 * @date 2022-09-03 01:59:15
 */
@Api(tags = "系统内容表API")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/adminContent" )
public class AdminContentController {

    private final AdminContentService adminContentService;

    /**
     * 查询系统内容表列表
     */
    @GetMapping("/list")
    @Node(value = "adminContent::list", name = "系统内容表列表")
    public R<PageSerializable<ContentInfo>> list(AdminContentRequest adminContentRequest)
    {
        LambdaQueryWrapper<ContentInfo> lqw = new LambdaQueryWrapper<>();
        PageUtils.startPage();
        lqw.between(adminContentRequest.getStartTime()!=null, ContentInfo::getCreatedTime, adminContentRequest.getStartTime(), adminContentRequest.getEndTime());
        lqw.like(adminContentRequest.getTitle()!=null, ContentInfo::getTitle, adminContentRequest.getTitle());
        lqw.eq(adminContentRequest.getCategoryId()!=null, ContentInfo::getCategoryId, adminContentRequest.getCategoryId());

        List<ContentInfo> list = adminContentService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取系统内容表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminContent::getInfo", name = "系统内容表详细信息")
    public R<ContentInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminContentService.getById(id));
    }

    /**
     * 新增系统内容表
     */
    @PostMapping
    @Node(value = "adminContent::add", name = "系统内容表新增")
    public R<Boolean> add(@RequestBody ContentInfo adminContent) {
        adminContent.setCreatedTime(LocalDateTime.now());
        adminContent.setUpdatedTime(LocalDateTime.now());
        adminContent.setUserId(SecurityUtils.getLoginUserId());
        return R.success(adminContentService.save(adminContent));
    }

    /**
     * 修改系统内容表
     */
    @PutMapping
    @Node(value = "adminContent::edit", name = "系统内容表修改")
    public R<Boolean> edit(@RequestBody ContentInfo adminContent) {
        adminContent.setUpdatedTime(LocalDateTime.now());
        return R.success(adminContentService.updateById(adminContent));
    }

    /**
     * 删除系统内容表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminContent::remove", name = "系统内容表删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminContentService.removeByIds(Arrays.asList(ids)));
    }

    @PostMapping("upload")
    @Node(value = "adminContent::upload", name = "附件上传")
    public R<String> upload(@RequestParam(value = "thumbnail")MultipartFile file) {
        String filePath = UploadUtils.upload("test", file);
        return R.success(filePath);
    }

    /**
     * 导出 系统内容表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ContentInfo.class)
    @Node(value = "contentInfo::exportExcel", name = "导出 系统内容表 Excel")
    public Object exportExcel(ContentInfo contentInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ContentInfo> lqw = new LambdaQueryWrapper<ContentInfo>();
        lqw.eq(contentInfo.getId() != null, ContentInfo::getId ,contentInfo.getId());
        lqw.like(StringUtils.isNotBlank(contentInfo.getTitle()), ContentInfo::getTitle ,contentInfo.getTitle());
        lqw.like(StringUtils.isNotBlank(contentInfo.getDescription()), ContentInfo::getDescription ,contentInfo.getDescription());
        lqw.like(StringUtils.isNotBlank(contentInfo.getKeywords()), ContentInfo::getKeywords ,contentInfo.getKeywords());
        lqw.like(StringUtils.isNotBlank(contentInfo.getThumbnail()), ContentInfo::getThumbnail ,contentInfo.getThumbnail());
        lqw.like(StringUtils.isNotBlank(contentInfo.getBody()), ContentInfo::getBody ,contentInfo.getBody());
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
        return adminContentService.list(lqw);
    }

    /**
     * 导入 系统内容表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "contentInfo::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ContentInfo.class, new ReadListener<ContentInfo>() {
                @Override
                public void invoke(ContentInfo contentInfo, AnalysisContext analysisContext) {
                    adminContentService.save(contentInfo);
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
