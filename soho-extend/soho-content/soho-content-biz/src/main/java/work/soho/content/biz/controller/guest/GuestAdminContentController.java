package work.soho.content.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.AdminContentService;

import java.util.List;

;
/**
 * 系统内容表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/guest/adminContent" )
public class GuestAdminContentController {

    private final AdminContentService adminContentService;

    /**
     * 查询系统内容表列表
     */
    @GetMapping("/list")
    @Node(value = "guest::adminContent::list", name = "获取 系统内容表 列表")
    public R<PageSerializable<ContentInfo>> list(ContentInfo adminContent, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentInfo> lqw = new LambdaQueryWrapper<ContentInfo>();
        lqw.eq(adminContent.getId() != null, ContentInfo::getId ,adminContent.getId());
        lqw.like(StringUtils.isNotBlank(adminContent.getTitle()), ContentInfo::getTitle ,adminContent.getTitle());
        lqw.like(StringUtils.isNotBlank(adminContent.getDescription()), ContentInfo::getDescription ,adminContent.getDescription());
        lqw.like(StringUtils.isNotBlank(adminContent.getKeywords()), ContentInfo::getKeywords ,adminContent.getKeywords());
        lqw.like(StringUtils.isNotBlank(adminContent.getThumbnail()), ContentInfo::getThumbnail ,adminContent.getThumbnail());
        lqw.like(StringUtils.isNotBlank(adminContent.getBody()), ContentInfo::getBody ,adminContent.getBody());
        lqw.eq(adminContent.getUpdatedTime() != null, ContentInfo::getUpdatedTime ,adminContent.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ContentInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ContentInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(adminContent.getCategoryId() != null, ContentInfo::getCategoryId ,adminContent.getCategoryId());
        lqw.eq(adminContent.getUserId() != null, ContentInfo::getUserId ,adminContent.getUserId());
        lqw.eq(adminContent.getStatus() != null, ContentInfo::getStatus ,adminContent.getStatus());
        lqw.eq(adminContent.getIsTop() != null, ContentInfo::getIsTop ,adminContent.getIsTop());
        lqw.eq(adminContent.getStar() != null, ContentInfo::getStar ,adminContent.getStar());
        lqw.eq(adminContent.getLikes() != null, ContentInfo::getLikes ,adminContent.getLikes());
        lqw.eq(adminContent.getDisLikes() != null, ContentInfo::getDisLikes ,adminContent.getDisLikes());
        lqw.eq(adminContent.getCommentsCount() != null, ContentInfo::getCommentsCount ,adminContent.getCommentsCount());
        lqw.orderByDesc(ContentInfo::getId);
        List<ContentInfo> list = adminContentService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取系统内容表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::adminContent::getInfo", name = "获取 系统内容表 详细信息")
    public R<ContentInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminContentService.getById(id));
    }

}