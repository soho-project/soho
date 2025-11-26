package work.soho.content.biz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.vo.TreeContentCategoryVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.service.AdminContentCategoryService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 内容分类Controller
 *
 * @author i
 * @date 2022-09-03 01:59:15
 */
@Api(tags = "内容分类API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/adminContentCategory" )
public class AdminContentCategoryController {

    private final AdminContentCategoryService adminContentCategoryService;

    /**
     * 查询内容分类列表
     */
    @GetMapping("/list")
    @Node(value = "adminContentCategory::list", name = "内容分类列表")
    public R<PageSerializable<ContentCategory>> list(ContentCategory adminContentCategory)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ContentCategory> lqw = new LambdaQueryWrapper<ContentCategory>();

        if (adminContentCategory.getId() != null){
            lqw.eq(ContentCategory::getId ,adminContentCategory.getId());
        }
        if (StringUtils.isNotBlank(adminContentCategory.getName())){
            lqw.like(ContentCategory::getName ,adminContentCategory.getName());
        }
        if (adminContentCategory.getParentId() != null){
            lqw.eq(ContentCategory::getParentId ,adminContentCategory.getParentId());
        }
        if (StringUtils.isNotBlank(adminContentCategory.getDescription())){
            lqw.like(ContentCategory::getDescription ,adminContentCategory.getDescription());
        }
        if (StringUtils.isNotBlank(adminContentCategory.getKeyword())){
            lqw.like(ContentCategory::getKeyword ,adminContentCategory.getKeyword());
        }
        if (StringUtils.isNotBlank(adminContentCategory.getContent())){
            lqw.like(ContentCategory::getContent ,adminContentCategory.getContent());
        }
        if (adminContentCategory.getOrder() != null){
            lqw.eq(ContentCategory::getOrder ,adminContentCategory.getOrder());
        }
        if (adminContentCategory.getIsDisplay() != null){
            lqw.eq(ContentCategory::getIsDisplay ,adminContentCategory.getIsDisplay());
        }
        if (adminContentCategory.getUpdateTime() != null){
            lqw.eq(ContentCategory::getUpdateTime ,adminContentCategory.getUpdateTime());
        }
        if (adminContentCategory.getCreatedTime() != null){
            lqw.eq(ContentCategory::getCreatedTime ,adminContentCategory.getCreatedTime());
        }
        List<ContentCategory> list = adminContentCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取内容分类详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminContentCategory::getInfo", name = "内容分类详细信息")
    public R<ContentCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminContentCategoryService.getById(id));
    }

    /**
     * 新增内容分类
     */
    @PostMapping
    @Node(value = "adminContentCategory::add", name = "内容分类新增")
    public R<Boolean> add(@RequestBody ContentCategory adminContentCategory) {
        adminContentCategory.setUpdateTime(LocalDateTime.now());
        adminContentCategory.setCreatedTime(LocalDateTime.now());
        return R.success(adminContentCategoryService.save(adminContentCategory));
    }

    /**
     * 修改内容分类
     */
    @PutMapping
    @Node(value = "adminContentCategory::edit", name = "内容分类修改")
    public R<Boolean> edit(@RequestBody ContentCategory adminContentCategory) {
        adminContentCategory.setUpdateTime(LocalDateTime.now());
        return R.success(adminContentCategoryService.updateById(adminContentCategory));
    }

    /**
     * 删除内容分类
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminContentCategory::remove", name = "内容分类删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminContentCategoryService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("")
    @Node(value = "adminContentCategory::details", name = "内容分类详情")
    public R<ContentCategory> details(Long id) {
        return R.success(adminContentCategoryService.getById(id));
    }

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

    @GetMapping("/options")
    public R<Map<Long, String>> getOptions() {
        LambdaQueryWrapper<ContentCategory> lqw = new LambdaQueryWrapper<ContentCategory>();
        Map<Long, String> map = adminContentCategoryService.list(lqw).stream().collect(Collectors.toMap(ContentCategory::getId, ContentCategory::getName));
        return R.success(map);
    }
}
