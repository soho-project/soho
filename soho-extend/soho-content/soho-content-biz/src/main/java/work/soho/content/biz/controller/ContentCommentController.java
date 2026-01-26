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
import work.soho.content.biz.domain.ContentComment;
import work.soho.content.biz.service.ContentCommentService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 内容评论Controller
 *
 * @author fang
 */
@Api(value="内容评论",tags = "内容评论")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/contentComment" )
public class ContentCommentController {

    private final ContentCommentService contentCommentService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询内容评论列表
     */
    @GetMapping("/list")
    @Node(value = "contentComment::list", name = "获取 内容评论 列表")
    @ApiOperation(value = "获取 内容评论 列表", notes = "获取 内容评论 列表")
    public R<PageSerializable<ContentComment>> list(ContentComment contentComment, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentComment> lqw = new LambdaQueryWrapper<>();
        lqw.eq(contentComment.getId() != null, ContentComment::getId ,contentComment.getId());
        lqw.eq(contentComment.getExternalCommentId() != null, ContentComment::getExternalCommentId ,contentComment.getExternalCommentId());
        lqw.eq(contentComment.getContentId() != null, ContentComment::getContentId ,contentComment.getContentId());
        lqw.eq(contentComment.getParentId() != null, ContentComment::getParentId ,contentComment.getParentId());
        lqw.eq(contentComment.getUserId() != null, ContentComment::getUserId ,contentComment.getUserId());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorName()),ContentComment::getAuthorName ,contentComment.getAuthorName());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorEmail()),ContentComment::getAuthorEmail ,contentComment.getAuthorEmail());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorUrl()),ContentComment::getAuthorUrl ,contentComment.getAuthorUrl());
        lqw.like(StringUtils.isNotBlank(contentComment.getContent()),ContentComment::getContent ,contentComment.getContent());
        lqw.like(StringUtils.isNotBlank(contentComment.getStatus()),ContentComment::getStatus ,contentComment.getStatus());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentComment::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentComment::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentComment.getUpdatedTime() != null, ContentComment::getUpdatedTime ,contentComment.getUpdatedTime());
        lqw.orderByDesc(ContentComment::getId);
        List<ContentComment> list = contentCommentService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容评论详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "contentComment::getInfo", name = "获取 内容评论 详细信息")
    @ApiOperation(value = "获取 内容评论 详细信息", notes = "获取 内容评论 详细信息")
    public R<ContentComment> getInfo(@PathVariable("id" ) Long id) {
        return R.success(contentCommentService.getById(id));
    }

    /**
     * 新增内容评论
     */
    @PostMapping
    @Node(value = "contentComment::add", name = "新增 内容评论")
    @ApiOperation(value = "新增 内容评论", notes = "新增 内容评论")
    public R<Boolean> add(@RequestBody ContentComment contentComment) {
        return R.success(contentCommentService.save(contentComment));
    }

    /**
     * 修改内容评论
     */
    @PutMapping
    @Node(value = "contentComment::edit", name = "修改 内容评论")
    @ApiOperation(value = "修改 内容评论", notes = "修改 内容评论")
    public R<Boolean> edit(@RequestBody ContentComment contentComment) {
        return R.success(contentCommentService.updateById(contentComment));
    }

    /**
     * 删除内容评论
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "contentComment::remove", name = "删除 内容评论")
    @ApiOperation(value = "删除 内容评论", notes = "删除 内容评论")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(contentCommentService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 内容评论 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ContentComment.class)
    @Node(value = "contentComment::exportExcel", name = "导出 内容评论 Excel")
    @ApiOperation(value = "导出 内容评论 Excel", notes = "导出 内容评论 Excel")
    public Object exportExcel(ContentComment contentComment, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ContentComment> lqw = new LambdaQueryWrapper<ContentComment>();
        lqw.eq(contentComment.getId() != null, ContentComment::getId ,contentComment.getId());
        lqw.eq(contentComment.getExternalCommentId() != null, ContentComment::getExternalCommentId ,contentComment.getExternalCommentId());
        lqw.eq(contentComment.getContentId() != null, ContentComment::getContentId ,contentComment.getContentId());
        lqw.eq(contentComment.getParentId() != null, ContentComment::getParentId ,contentComment.getParentId());
        lqw.eq(contentComment.getUserId() != null, ContentComment::getUserId ,contentComment.getUserId());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorName()),ContentComment::getAuthorName ,contentComment.getAuthorName());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorEmail()),ContentComment::getAuthorEmail ,contentComment.getAuthorEmail());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorUrl()),ContentComment::getAuthorUrl ,contentComment.getAuthorUrl());
        lqw.like(StringUtils.isNotBlank(contentComment.getContent()),ContentComment::getContent ,contentComment.getContent());
        lqw.like(StringUtils.isNotBlank(contentComment.getStatus()),ContentComment::getStatus ,contentComment.getStatus());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentComment::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentComment::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentComment.getUpdatedTime() != null, ContentComment::getUpdatedTime ,contentComment.getUpdatedTime());
        lqw.orderByDesc(ContentComment::getId);
        return contentCommentService.list(lqw);
    }

    /**
     * 导入 内容评论 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "contentComment::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 内容评论 Excel", notes = "导入 内容评论 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ContentComment.class, new ReadListener<ContentComment>() {
                @Override
                public void invoke(ContentComment contentComment, AnalysisContext analysisContext) {
                    contentCommentService.save(contentComment);
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