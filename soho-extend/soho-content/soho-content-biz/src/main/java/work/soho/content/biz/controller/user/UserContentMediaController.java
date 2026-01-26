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
import work.soho.content.biz.domain.ContentMedia;
import work.soho.content.biz.service.ContentMediaService;
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
 * 内容媒体Controller
 *
 * @author fang
 */
@Api(value = "user 内容媒体", tags = "user 内容媒体")
@RequiredArgsConstructor
@RestController
@RequestMapping("content/user/contentMedia" )
public class UserContentMediaController {

    private final ContentMediaService contentMediaService;

    /**
     * 查询内容媒体列表
     */
    @GetMapping("/list")
    @Node(value = "user::contentMedia::list", name = "获取 内容媒体 列表")
    @ApiOperation(value = "user 获取 内容媒体 列表", notes = "user 获取 内容媒体 列表")
    public R<PageSerializable<ContentMedia>> list(ContentMedia contentMedia, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentMedia> lqw = new LambdaQueryWrapper<ContentMedia>();
        lqw.eq(contentMedia.getId() != null, ContentMedia::getId ,contentMedia.getId());
        lqw.eq(contentMedia.getExternalMediaId() != null, ContentMedia::getExternalMediaId ,contentMedia.getExternalMediaId());
        lqw.like(StringUtils.isNotBlank(contentMedia.getTitle()),ContentMedia::getTitle ,contentMedia.getTitle());
        lqw.like(StringUtils.isNotBlank(contentMedia.getUrl()),ContentMedia::getUrl ,contentMedia.getUrl());
        lqw.like(StringUtils.isNotBlank(contentMedia.getMimeType()),ContentMedia::getMimeType ,contentMedia.getMimeType());
        lqw.like(StringUtils.isNotBlank(contentMedia.getDescription()),ContentMedia::getDescription ,contentMedia.getDescription());
        lqw.like(StringUtils.isNotBlank(contentMedia.getAltText()),ContentMedia::getAltText ,contentMedia.getAltText());
        lqw.eq(ContentMedia::getUserId, userDetails.getId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentMedia::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentMedia::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentMedia.getUpdatedTime() != null, ContentMedia::getUpdatedTime ,contentMedia.getUpdatedTime());
        List<ContentMedia> list = contentMediaService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容媒体详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::contentMedia::getInfo", name = "获取 内容媒体 详细信息")
    @ApiOperation(value = "user 获取 内容媒体 详细信息", notes = "user 获取 内容媒体 详细信息")
    public R<ContentMedia> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentMedia contentMedia = contentMediaService.getById(id);
        if (!contentMedia.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(contentMedia);
    }

    /**
     * 新增内容媒体
     */
    @PostMapping
    @Node(value = "user::contentMedia::add", name = "新增 内容媒体")
    @ApiOperation(value = "user 新增 内容媒体", notes = "user 新增 内容媒体")
    public R<Boolean> add(@RequestBody ContentMedia contentMedia, @AuthenticationPrincipal SohoUserDetails userDetails) {
        contentMedia.setUserId(userDetails.getId());
        return R.success(contentMediaService.save(contentMedia));
    }

    /**
     * 修改内容媒体
     */
    @PutMapping
    @Node(value = "user::contentMedia::edit", name = "修改 内容媒体")
    @ApiOperation(value = "user 修改 内容媒体", notes = "user 修改 内容媒体")
    public R<Boolean> edit(@RequestBody ContentMedia contentMedia, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentMedia oldContentMedia = contentMediaService.getById(contentMedia.getId());
        Assert.notNull(oldContentMedia, "数据不存在");
  
        if(!oldContentMedia.getUserId().equals(userDetails.getId())) {
            return R.error("无权限");
        }
        return R.success(contentMediaService.updateById(contentMedia));
    }

    /**
     * 删除内容媒体
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::contentMedia::remove", name = "删除 内容媒体")
    @ApiOperation(value = "user 删除 内容媒体", notes = "user 删除 内容媒体")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {

        List<ContentMedia> oldList = contentMediaService.listByIds(Arrays.asList(ids));
        // 检查是否为当前登录用户的数据
        for(ContentMedia item: oldList) {
            if(!item.getUserId().equals(userDetails.getId())) {
                return R.error("无权限");
            }
        }
        return R.success(contentMediaService.removeByIds(Arrays.asList(ids)));
    }
}