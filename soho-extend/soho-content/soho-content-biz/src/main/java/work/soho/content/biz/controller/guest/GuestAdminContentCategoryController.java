package work.soho.content.biz.controller.guest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.vo.TreeContentCategoryVo;
import work.soho.common.core.result.R;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.service.AdminContentCategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/content/guest/adminContentCategory" )
public class GuestAdminContentCategoryController {
    private final AdminContentCategoryService adminContentCategoryService;

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
            if(0l == treeContentCategoryVo.getParentId()) {
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
}
