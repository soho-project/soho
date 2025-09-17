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
import work.soho.content.biz.domain.AdminContent;
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
    public R<PageSerializable<AdminContent>> list(AdminContent adminContent, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AdminContent> lqw = new LambdaQueryWrapper<AdminContent>();
        lqw.eq(adminContent.getId() != null, AdminContent::getId ,adminContent.getId());
        lqw.like(StringUtils.isNotBlank(adminContent.getTitle()),AdminContent::getTitle ,adminContent.getTitle());
        lqw.like(StringUtils.isNotBlank(adminContent.getDescription()),AdminContent::getDescription ,adminContent.getDescription());
        lqw.like(StringUtils.isNotBlank(adminContent.getKeywords()),AdminContent::getKeywords ,adminContent.getKeywords());
        lqw.like(StringUtils.isNotBlank(adminContent.getThumbnail()),AdminContent::getThumbnail ,adminContent.getThumbnail());
        lqw.like(StringUtils.isNotBlank(adminContent.getBody()),AdminContent::getBody ,adminContent.getBody());
        lqw.eq(adminContent.getUpdatedTime() != null, AdminContent::getUpdatedTime ,adminContent.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AdminContent::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AdminContent::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(adminContent.getCategoryId() != null, AdminContent::getCategoryId ,adminContent.getCategoryId());
        lqw.eq(adminContent.getUserId() != null, AdminContent::getUserId ,adminContent.getUserId());
        lqw.eq(adminContent.getStatus() != null, AdminContent::getStatus ,adminContent.getStatus());
        lqw.eq(adminContent.getIsTop() != null, AdminContent::getIsTop ,adminContent.getIsTop());
        lqw.eq(adminContent.getStar() != null, AdminContent::getStar ,adminContent.getStar());
        lqw.eq(adminContent.getLikes() != null, AdminContent::getLikes ,adminContent.getLikes());
        lqw.eq(adminContent.getDisLikes() != null, AdminContent::getDisLikes ,adminContent.getDisLikes());
        lqw.eq(adminContent.getCommentsCount() != null, AdminContent::getCommentsCount ,adminContent.getCommentsCount());
        lqw.orderByDesc(AdminContent::getId);
        List<AdminContent> list = adminContentService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取系统内容表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::adminContent::getInfo", name = "获取 系统内容表 详细信息")
    public R<AdminContent> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminContentService.getById(id));
    }

}