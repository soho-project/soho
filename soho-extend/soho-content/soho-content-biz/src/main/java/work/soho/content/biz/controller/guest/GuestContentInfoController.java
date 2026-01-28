package work.soho.content.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.AdminContentVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.AdminContentCategoryService;
import work.soho.content.biz.service.ContentInfoService;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;

import java.util.List;
import java.util.Map;

;

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

    private final AdminContentCategoryService adminContentCategoryService;

    private final UserApiService userApiService;

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

    @ApiOperation("获取内容")
    @GetMapping("content")
    public R<AdminContentVo> content(Long id) {
        ContentInfo adminContent = contentInfoService.getById(id);
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        AdminContentVo adminContentVo = BeanUtils.copy(adminContent, AdminContentVo.class);
        UserInfoDto user = userApiService.getUserById(adminContent.getUserId());
        if(user != null) {
            adminContentVo.setUsername(user.getUsername());
        }
        //获取文章导航信息
        List<ContentCategory> navaList = adminContentCategoryService.getCategorysBySonId(adminContent.getCategoryId());
        for (ContentCategory adminContentCategory: navaList) {
            AdminContentVo.NavItem nav = new AdminContentVo.NavItem();
            nav.setName(adminContentCategory.getName());
            nav.setId(adminContentCategory.getId());
            nav.setType(2);
            adminContentVo.getNavs().add(nav);
        }
        AdminContentVo.NavItem nav = new AdminContentVo.NavItem();
        nav.setName(adminContent.getTitle());
        nav.setId(adminContent.getId());
        nav.setType(3);
        adminContentVo.getNavs().add(nav);

        return R.success(adminContentVo);
    }

    @PostMapping("like")
    public R<Boolean> like(@RequestBody Map<String,Long> map) {
        ContentInfo adminContent = contentInfoService.getById(map.get("id"));
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        adminContent.setLikes(adminContent.getLikes()+1);
        contentInfoService.updateById(adminContent);
        return R.success();
    }

    @PostMapping("disLike")
    public R<Boolean> dislike(@RequestBody Map<String,Long> map) {
        ContentInfo adminContent = contentInfoService.getById(map.get("id"));
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        adminContent.setDisLikes(adminContent.getDisLikes()+1);
        contentInfoService.updateById(adminContent);
        return R.success();
    }

}