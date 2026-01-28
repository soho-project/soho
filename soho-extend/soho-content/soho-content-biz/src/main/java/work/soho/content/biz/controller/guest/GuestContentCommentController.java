package work.soho.content.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.content.biz.domain.ContentComment;
import work.soho.content.biz.enums.ContentCommentEnums;
import work.soho.content.biz.service.ContentCommentService;

import java.util.List;

;

/**
 * 内容评论Controller
 *
 * @author fang
 */
@Api(value = "guest 内容评论", tags = "guest 内容评论")
@RequiredArgsConstructor
@RestController
@RequestMapping("content/guest/contentComment" )
public class GuestContentCommentController {

    private final ContentCommentService contentCommentService;

    /**
     * 查询内容评论列表
     */
    @GetMapping("/list")
    @Node(value = "guest::contentComment::list", name = "获取 内容评论 列表")
    @ApiOperation(value = "guest 获取 内容评论 列表", notes = "guest 获取 内容评论 列表")
    public R<PageSerializable<ContentComment>> list(ContentComment contentComment, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
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
//        lqw.like(StringUtils.isNotBlank(contentComment.getStatus()),ContentComment::getStatus ,contentComment.getStatus());
        lqw.eq(ContentComment::getStatus , ContentCommentEnums.Status.ACTIVE);
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentComment::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentComment::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentComment.getUpdatedTime() != null, ContentComment::getUpdatedTime ,contentComment.getUpdatedTime());
        List<ContentComment> list = contentCommentService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容评论详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::contentComment::getInfo", name = "获取 内容评论 详细信息")
    @ApiOperation(value = "guest 获取 内容评论 详细信息", notes = "guest 获取 内容评论 详细信息")
    public R<ContentComment> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentComment contentComment = contentCommentService.getById(id);
        return R.success(contentComment);
    }

    /**
     * 新增内容评论
     */
    @PostMapping
    @Node(value = "guest::contentComment::add", name = "新增 内容评论")
    @ApiOperation(value = "guest 新增 内容评论", notes = "guest 新增 内容评论")
    public R<Boolean> add(@RequestBody ContentComment contentComment, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(contentCommentService.save(contentComment));
    }

}