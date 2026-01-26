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
import work.soho.content.biz.domain.ContentTag;
import work.soho.content.biz.service.ContentTagService;
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
 * 内容标签Controller
 *
 * @author fang
 */
@Api(value = "user 内容标签", tags = "user 内容标签")
@RequiredArgsConstructor
@RestController
@RequestMapping("content/user/contentTag" )
public class UserContentTagController {

    private final ContentTagService contentTagService;

    /**
     * 查询内容标签列表
     */
    @GetMapping("/list")
    @Node(value = "user::contentTag::list", name = "获取 内容标签 列表")
    @ApiOperation(value = "user 获取 内容标签 列表", notes = "user 获取 内容标签 列表")
    public R<PageSerializable<ContentTag>> list(ContentTag contentTag, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentTag> lqw = new LambdaQueryWrapper<ContentTag>();
        lqw.eq(contentTag.getId() != null, ContentTag::getId ,contentTag.getId());
        lqw.like(StringUtils.isNotBlank(contentTag.getName()),ContentTag::getName ,contentTag.getName());
        lqw.like(StringUtils.isNotBlank(contentTag.getSlug()),ContentTag::getSlug ,contentTag.getSlug());
        lqw.like(StringUtils.isNotBlank(contentTag.getDescription()),ContentTag::getDescription ,contentTag.getDescription());
        lqw.eq(contentTag.getCount() != null, ContentTag::getCount ,contentTag.getCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentTag::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentTag::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(contentTag.getUpdatedTime() != null, ContentTag::getUpdatedTime ,contentTag.getUpdatedTime());
        List<ContentTag> list = contentTagService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容标签详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::contentTag::getInfo", name = "获取 内容标签 详细信息")
    @ApiOperation(value = "user 获取 内容标签 详细信息", notes = "user 获取 内容标签 详细信息")
    public R<ContentTag> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentTag contentTag = contentTagService.getById(id);
        return R.success(contentTag);
    }

    /**
     * 新增内容标签
     */
    @PostMapping
    @Node(value = "user::contentTag::add", name = "新增 内容标签")
    @ApiOperation(value = "user 新增 内容标签", notes = "user 新增 内容标签")
    public R<Boolean> add(@RequestBody ContentTag contentTag, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(contentTagService.save(contentTag));
    }

    /**
     * 修改内容标签
     */
    @PutMapping
    @Node(value = "user::contentTag::edit", name = "修改 内容标签")
    @ApiOperation(value = "user 修改 内容标签", notes = "user 修改 内容标签")
    public R<Boolean> edit(@RequestBody ContentTag contentTag, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentTag oldContentTag = contentTagService.getById(contentTag.getId());
        Assert.notNull(oldContentTag, "数据不存在");
        return R.success(contentTagService.updateById(contentTag));
    }

    /**
     * 删除内容标签
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::contentTag::remove", name = "删除 内容标签")
    @ApiOperation(value = "user 删除 内容标签", notes = "user 删除 内容标签")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(contentTagService.removeByIds(Arrays.asList(ids)));
    }
}