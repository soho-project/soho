package work.soho.diy.biz.controller.guest;

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
import work.soho.diy.biz.domain.DiyPage;
import work.soho.diy.biz.service.DiyPageService;

import java.util.List;

/**
 * Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/diy/guest/diyPage" )
public class GuestDiyPageController {

    private final DiyPageService diyPageService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "guest::diyPage::list", name = "获取  列表")
    public R<PageSerializable<DiyPage>> list(DiyPage diyPage, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<DiyPage> lqw = new LambdaQueryWrapper<DiyPage>();
        lqw.eq(diyPage.getId() != null, DiyPage::getId ,diyPage.getId());
        lqw.like(StringUtils.isNotBlank(diyPage.getRoute()),DiyPage::getRoute ,diyPage.getRoute());
        lqw.like(StringUtils.isNotBlank(diyPage.getTitle()),DiyPage::getTitle ,diyPage.getTitle());
        lqw.like(StringUtils.isNotBlank(diyPage.getNotes()),DiyPage::getNotes ,diyPage.getNotes());
        lqw.eq(diyPage.getVersion() != null, DiyPage::getVersion ,diyPage.getVersion());
        lqw.eq(diyPage.getStatus() != null, DiyPage::getStatus ,diyPage.getStatus());
        lqw.like(StringUtils.isNotBlank(diyPage.getData()),DiyPage::getData ,diyPage.getData());
        lqw.eq(diyPage.getUpdatedTime() != null, DiyPage::getUpdatedTime ,diyPage.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, DiyPage::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, DiyPage::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<DiyPage> list = diyPageService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::diyPage::getInfo", name = "获取  详细信息")
    public R<DiyPage> getInfo(@PathVariable("id" ) Long id) {
        return R.success(diyPageService.getById(id));
    }

    /**
     * 根据路径获取详情
     *
     * @param path
     * @return
     */
    @GetMapping("/detailsByPath")
    @Node(value = "guest::diyPage::details", name = "获取  详情")
    public R<DiyPage> getByPath(String path) {
        LambdaQueryWrapper<DiyPage> lqw = new LambdaQueryWrapper<DiyPage>();
        lqw.eq(DiyPage::getRoute, path);
        return R.success(diyPageService.getOne(lqw));
    }
}