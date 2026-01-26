package work.soho.content.biz.controller.user;

import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import work.soho.common.security.annotation.Node;;
import work.soho.content.biz.domain.ContentComment;
import work.soho.content.biz.service.ContentCommentService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import work.soho.common.security.userdetails.SohoUserDetails;
import org.springframework.util.Assert;

/**
 * 内容评论Controller
 *
 * @author fang
 */
@Api(value = "user 内容评论", tags = "user 内容评论")
@RequiredArgsConstructor
@RestController
@RequestMapping("content/user/contentComment" )
public class UserContentCommentController {

    private final ContentCommentService contentCommentService;

    /**
     * 查询内容评论列表
     */
    @GetMapping("/list")
    @Node(value = "user::contentComment::list", name = "获取 内容评论 列表")
    @ApiOperation(value = "user 获取 内容评论 列表", notes = "user 获取 内容评论 列表")
    public R<PageSerializable<ContentComment>> list(ContentComment contentComment, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentComment> lqw = new LambdaQueryWrapper<ContentComment>();
        lqw.eq(contentComment.getId() != null, ContentComment::getId ,contentComment.getId());
        lqw.eq(contentComment.getExternalCommentId() != null, ContentComment::getExternalCommentId ,contentComment.getExternalCommentId());
        lqw.eq(contentComment.getContentId() != null, ContentComment::getContentId ,contentComment.getContentId());
        lqw.eq(contentComment.getParentId() != null, ContentComment::getParentId ,contentComment.getParentId());
        lqw.eq(ContentComment::getUserId, userDetails.getId());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorName()),ContentComment::getAuthorName ,contentComment.getAuthorName());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorEmail()),ContentComment::getAuthorEmail ,contentComment.getAuthorEmail());
        lqw.like(StringUtils.isNotBlank(contentComment.getAuthorUrl()),ContentComment::getAuthorUrl ,contentComment.getAuthorUrl());
        lqw.like(StringUtils.isNotBlank(contentComment.getContent()),ContentComment::getContent ,contentComment.getContent());
        lqw.like(StringUtils.isNotBlank(contentComment.getStatus()),ContentComment::getStatus ,contentComment.getStatus());
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
    @Node(value = "user::contentComment::getInfo", name = "获取 内容评论 详细信息")
    @ApiOperation(value = "user 获取 内容评论 详细信息", notes = "user 获取 内容评论 详细信息")
    public R<ContentComment> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentComment contentComment = contentCommentService.getById(id);
        if (!contentComment.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(contentComment);
    }

    /**
     * 新增内容评论
     */
    @PostMapping
    @Node(value = "user::contentComment::add", name = "新增 内容评论")
    @ApiOperation(value = "user 新增 内容评论", notes = "user 新增 内容评论")
    public R<Boolean> add(@RequestBody ContentComment contentComment, @AuthenticationPrincipal SohoUserDetails userDetails) {
        contentComment.setUserId(userDetails.getId());
        return R.success(contentCommentService.save(contentComment));
    }

    /**
     * 修改内容评论
     */
    @PutMapping
    @Node(value = "user::contentComment::edit", name = "修改 内容评论")
    @ApiOperation(value = "user 修改 内容评论", notes = "user 修改 内容评论")
    public R<Boolean> edit(@RequestBody ContentComment contentComment, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentComment oldContentComment = contentCommentService.getById(contentComment.getId());
        Assert.notNull(oldContentComment, "数据不存在");
  
        if(!oldContentComment.getUserId().equals(userDetails.getId())) {
            return R.error("无权限");
        }
        return R.success(contentCommentService.updateById(contentComment));
    }

    /**
     * 删除内容评论
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::contentComment::remove", name = "删除 内容评论")
    @ApiOperation(value = "user 删除 内容评论", notes = "user 删除 内容评论")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {

        List<ContentComment> oldList = contentCommentService.listByIds(Arrays.asList(ids));
        // 检查是否为当前登录用户的数据
        for(ContentComment item: oldList) {
            if(!item.getUserId().equals(userDetails.getId())) {
                return R.error("无权限");
            }
        }
        return R.success(contentCommentService.removeByIds(Arrays.asList(ids)));
    }
}