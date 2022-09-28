package work.soho.admin.controller;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.PageObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.domain.AdminContent;
import work.soho.admin.domain.AdminContentCategory;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminContentCategoryService;
import work.soho.admin.service.AdminContentService;
import work.soho.admin.service.AdminUserService;
import work.soho.api.admin.vo.AdminContentCategoryListVo;
import work.soho.api.admin.vo.AdminContentVo;
import work.soho.api.admin.vo.NavVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api("内容接口")
@RestController
@RequestMapping("/client/api/content")
@RequiredArgsConstructor
public class ClientContentController {
    private final AdminContentService adminContentService;
    private final AdminUserService adminUserService;
    private final AdminContentCategoryService adminContentCategoryService;

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("list")
    public R<List<AdminContent>> list() {
        LambdaQueryWrapper<AdminContent> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AdminContent::getStatus, 1);
        PageUtils.startPage();
        List<AdminContent> list = adminContentService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    @GetMapping("category")
    public R<AdminContentCategoryListVo> category(Long id) {
        AdminContentCategoryListVo adminContentCategoryListVo;
        AdminContentCategory adminContentCategory = adminContentCategoryService.getById(id);
        adminContentCategoryListVo = BeanUtils.copy(adminContentCategory, AdminContentCategoryListVo.class);
        //获取文章导航信息
        List<AdminContentCategory> navaList = adminContentCategoryService.getCategorysBySonId(adminContentCategory.getParentId());
        for (AdminContentCategory adminContentCategory1: navaList) {
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
        List<AdminContentCategory> list = adminContentCategoryService.list();
        Map<Long, List<AdminContentCategory>> map = list.stream().collect(Collectors.groupingBy(AdminContentCategory::getParentId));
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
    private void loopNav(Map<Long, List<AdminContentCategory>> map,NavVo navVo) {
        List<AdminContentCategory> listTmp = null;
        if((listTmp = map.get(Long.parseLong(navVo.getKey()))) != null) {
            for(AdminContentCategory item: listTmp) {
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
        AdminContent adminContent = adminContentService.getById(id);
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        AdminContentVo adminContentVo = BeanUtils.copy(adminContent, AdminContentVo.class);
        //获取用户信息
        AdminUser adminUser = adminUserService.getById(adminContent.getUserId());
        if(adminUser!=null) {
            adminContentVo.setUsername(adminUser.getUsername());
        }
        //获取文章导航信息
        List<AdminContentCategory> navaList = adminContentCategoryService.getCategorysBySonId(adminContent.getCategoryId());
        for (AdminContentCategory adminContentCategory: navaList) {
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
        AdminContent adminContent = adminContentService.getById(map.get("id"));
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
        AdminContent adminContent = adminContentService.getById(map.get("id"));
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        adminContent.setDisLikes(adminContent.getDisLikes()+1);
        adminContentService.updateById(adminContent);
        return R.success();
    }

}
