package work.soho.content.biz.controller.guest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.vo.AdminContentCategoryListVo;
import work.soho.admin.api.vo.AdminContentVo;
import work.soho.admin.api.vo.NavVo;
import work.soho.admin.api.vo.TreeContentCategoryVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.service.AdminContentCategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "前台内容分类管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/guest/contentCategory" )
public class GuestContentCategoryController {
    private final AdminContentCategoryService adminContentCategoryService;

    @ApiOperation("内容分类树")
    @GetMapping("/tree")
    public R<List<TreeContentCategoryVo>> tree() {
        List<ContentCategory> originList = adminContentCategoryService.list();
        //对象复制
        ArrayList<TreeContentCategoryVo> list = new ArrayList<>();
        for (ContentCategory adminContentCategory: originList) {
            TreeContentCategoryVo treeContentCategoryVo = new TreeContentCategoryVo();
            treeContentCategoryVo.setKey(adminContentCategory.getId());
            treeContentCategoryVo.setValue(adminContentCategory.getId());
            treeContentCategoryVo.setTitle(adminContentCategory.getName());
            treeContentCategoryVo.setParentId(adminContentCategory.getParentId());
            list.add(treeContentCategoryVo);
        }

        //hashmap
        HashMap<Long, TreeContentCategoryVo> map = new HashMap<>();
        for (TreeContentCategoryVo treeContentCategoryVo: list) {
            map.put(treeContentCategoryVo.getKey(), treeContentCategoryVo);
        }

        ArrayList<TreeContentCategoryVo> result = new ArrayList<>();
        //关联关系
        for (TreeContentCategoryVo treeContentCategoryVo: list) {
            if(0L == treeContentCategoryVo.getParentId()) {
                result.add(treeContentCategoryVo);
            } else {
                if(!map.containsKey(treeContentCategoryVo.getParentId())) {
                    continue;
                }
                map.get(treeContentCategoryVo.getParentId()).getChildren().add(treeContentCategoryVo);
            }
        }

        //获取parent id为0的所有数据
        return R.success(result);
    }

    @ApiOperation("获取内容分类")
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

    @ApiOperation("获取内容导航")
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
}
