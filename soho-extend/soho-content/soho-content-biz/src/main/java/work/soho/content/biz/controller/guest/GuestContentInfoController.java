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
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.ContentInfoService;
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
 * 系统内容表Controller
 *
 * @author fang
 */
@Api(value = "guest 系统内容表", tags = "guest 系统内容表")
@RequiredArgsConstructor
@RestController
@RequestMapping("content/guest/contentInfo" )
public class GuestContentInfoController {

    private final ContentInfoService contentInfoService;

    /**
     * 查询系统内容表列表
     */
    @GetMapping("/list")
    @Node(value = "guest::contentInfo::list", name = "获取 系统内容表 列表")
    @ApiOperation(value = "guest 获取 系统内容表 列表", notes = "guest 获取 系统内容表 列表")
    public R<PageSerializable<ContentInfo>> list(ContentInfo contentInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
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
        List<ContentInfo> list = contentInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取系统内容表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::contentInfo::getInfo", name = "获取 系统内容表 详细信息")
    @ApiOperation(value = "guest 获取 系统内容表 详细信息", notes = "guest 获取 系统内容表 详细信息")
    public R<ContentInfo> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentInfo contentInfo = contentInfoService.getById(id);
        return R.success(contentInfo);
    }

}