package work.soho.content.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.ContentInfoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

;

/**
 * 系统内容表Controller
 *
 * @author fang
 */
@Api(value = "user 系统内容表", tags = "user 系统内容表")
@RequiredArgsConstructor
@RestController
@RequestMapping("content/user/contentInfo" )
public class UserContentInfoController {

    private final ContentInfoService contentInfoService;

    /**
     * 查询系统内容表列表
     */
    @GetMapping("/list")
    @Node(value = "user::contentInfo::list", name = "获取 系统内容表 列表")
    @ApiOperation(value = "user 获取 系统内容表 列表", notes = "user 获取 系统内容表 列表")
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
        lqw.eq(ContentInfo::getUserId, userDetails.getId());
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
    @Node(value = "user::contentInfo::getInfo", name = "获取 系统内容表 详细信息")
    @ApiOperation(value = "user 获取 系统内容表 详细信息", notes = "user 获取 系统内容表 详细信息")
    public R<ContentInfo> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentInfo contentInfo = contentInfoService.getById(id);
        if (!contentInfo.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(contentInfo);
    }

    /**
     * 新增系统内容表
     */
    @PostMapping
    @Node(value = "user::contentInfo::add", name = "新增 系统内容表")
    @ApiOperation(value = "user 新增 系统内容表", notes = "user 新增 系统内容表")
    public R<Boolean> add(@RequestBody ContentInfo contentInfo, @AuthenticationPrincipal SohoUserDetails userDetails) {
        contentInfo.setUserId(userDetails.getId());
        return R.success(contentInfoService.save(contentInfo));
    }

    /**
     * 修改系统内容表
     */
    @PutMapping
    @Node(value = "user::contentInfo::edit", name = "修改 系统内容表")
    @ApiOperation(value = "user 修改 系统内容表", notes = "user 修改 系统内容表")
    public R<Boolean> edit(@RequestBody ContentInfo contentInfo, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ContentInfo oldContentInfo = contentInfoService.getById(contentInfo.getId());
        Assert.notNull(oldContentInfo, "数据不存在");

        if(!oldContentInfo.getUserId().equals(userDetails.getId())) {
            return R.error("无权限");
        }
        return R.success(contentInfoService.updateById(contentInfo));
    }

    /**
     * 删除系统内容表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::contentInfo::remove", name = "删除 系统内容表")
    @ApiOperation(value = "user 删除 系统内容表", notes = "user 删除 系统内容表")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {

        List<ContentInfo> oldList = contentInfoService.listByIds(Arrays.asList(ids));
        // 检查是否为当前登录用户的数据
        for(ContentInfo item: oldList) {
            if(!item.getUserId().equals(userDetails.getId())) {
                return R.error("无权限");
            }
        }
        return R.success(contentInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该系统内容表 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "user::contentInfo::options", name = "获取 系统内容表 选项")
    @ApiOperation(value = "user 获取 系统内容表 选项", notes = "user 获取 系统内容表 选项")
    public R<List<OptionVo<Long, String>>> options(@AuthenticationPrincipal SohoUserDetails userDetails) {
        LambdaQueryWrapper<ContentInfo> lqw = new LambdaQueryWrapper<ContentInfo>();
        lqw.eq(ContentInfo::getUserId, userDetails.getId());
        List<ContentInfo> list = contentInfoService.list(lqw);
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(ContentInfo item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getTitle());
            options.add(optionVo);
        }
        return R.success(options);
    }
}