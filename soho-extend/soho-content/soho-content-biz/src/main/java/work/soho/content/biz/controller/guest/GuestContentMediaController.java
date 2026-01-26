package work.soho.content.biz.controller.guest;

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
@Api(value = "guest 内容媒体", tags = "guest 内容媒体")
@RequiredArgsConstructor
@RestController
@RequestMapping("content/guest/contentMedia" )
public class GuestContentMediaController {

    private final ContentMediaService contentMediaService;

    /**
     * 查询内容媒体列表
     */
    @GetMapping("/list")
    @Node(value = "guest::contentMedia::list", name = "获取 内容媒体 列表")
    @ApiOperation(value = "guest 获取 内容媒体 列表", notes = "guest 获取 内容媒体 列表")
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
        lqw.eq(contentMedia.getUserId() != null, ContentMedia::getUserId ,contentMedia.getUserId());
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
    @Node(value = "guest::contentMedia::getInfo", name = "获取 内容媒体 详细信息")
    @ApiOperation(value = "guest 获取 内容媒体 详细信息", notes = "guest 获取 内容媒体 详细信息")
    public R<ContentMedia> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentMedia contentMedia = contentMediaService.getById(id);
        return R.success(contentMedia);
    }

}