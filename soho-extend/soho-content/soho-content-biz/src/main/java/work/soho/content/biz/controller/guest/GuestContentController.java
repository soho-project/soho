package work.soho.content.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.vo.AdminContentCategoryListVo;
import work.soho.admin.api.vo.AdminContentVo;
import work.soho.admin.api.vo.NavVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.service.AdminContentCategoryService;
import work.soho.content.biz.service.AdminContentService;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "客户端内容接口")
@RestController
@RequestMapping("/content/guest/api/content")
@RequiredArgsConstructor
public class GuestContentController {
    private final AdminContentService adminContentService;
    private final UserApiService userApiService ;
    private final AdminContentCategoryService adminContentCategoryService;

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("list")
    public R<List<ContentInfo>> list() {
        LambdaQueryWrapper<ContentInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ContentInfo::getStatus, 1);
        PageUtils.startPage();
        List<ContentInfo> list = adminContentService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    @GetMapping("category")
    public R<AdminContentCategoryListVo> category(Long id) {
        AdminContentCategoryListVo adminContentCategoryListVo;
        ContentCategory adminContentCategory = adminContentCategoryService.getById(id);
        adminContentCategoryListVo = BeanUtils.copy(adminContentCategory, AdminContentCategoryListVo.class);
        //获取文章导航信息
        List<ContentCategory> navaList = adminContentCategoryService.getCategorysBySonId(adminContentCategory.getParentId());
        for (ContentCategory adminContentCategory1: navaList) {
            AdminContentVo.NavItem nav = new AdminContentVo.NavItem();
            nav.setName(adminContentCategory1.getName());
            nav.setId(adminContentCategory1.getId());
            nav.setType(2);
            adminContentCategoryListVo.getNavs().add(nav);
        }

        return R.success(adminContentCategoryListVo);
    }

    @GetMapping("nav")
    public R<List<NavVo>> nav() {
        List<ContentCategory> list = adminContentCategoryService.list();
        Map<Long, List<ContentCategory>> map = list.stream().collect(Collectors.groupingBy(ContentCategory::getParentId));
        NavVo tmpNavVo = new NavVo();
        tmpNavVo.setKey("0");
        loopNav(map, tmpNavVo);
        return R.success(tmpNavVo.getChildren());
    }

    /**
     * 递归导航
     *
     * @param map
     * @param navVo
     * @return
     */
    private void loopNav(Map<Long, List<ContentCategory>> map, NavVo navVo) {
        List<ContentCategory> listTmp = null;
        if((listTmp = map.get(Long.parseLong(navVo.getKey()))) != null) {
            for(ContentCategory item: listTmp) {
                NavVo nav = new NavVo();
                nav.setKey(String.valueOf(item.getId()));
                nav.setLabel(item.getName());
                navVo.getChildren().add(nav);
                loopNav(map, nav);
            }
        }
    }

    @GetMapping("content")
    public R<AdminContentVo> content(Long id) {
        ContentInfo adminContent = adminContentService.getById(id);
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
        ContentInfo adminContent = adminContentService.getById(map.get("id"));
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        adminContent.setLikes(adminContent.getLikes()+1);
        adminContentService.updateById(adminContent);
        return R.success();
    }

    @PostMapping("disLike")
    public R<Boolean> dislike(@RequestBody Map<String,Long> map) {
        ContentInfo adminContent = adminContentService.getById(map.get("id"));
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        adminContent.setDisLikes(adminContent.getDisLikes()+1);
        adminContentService.updateById(adminContent);
        return R.success();
    }

}
