package work.soho.example.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.api.admin.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.example.biz.domain.ExampleCategory;
import work.soho.example.biz.service.ExampleCategoryService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:treeController
 *
 * @author fang
 * @date 2022-11-22 16:21:31
 */
@Api(tags = "自动化样例分类表;;")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/exampleCategory" )
public class ExampleCategoryController {

    private final ExampleCategoryService exampleCategoryService;

    /**
     * 查询自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree列表
     */
    @GetMapping("/list")
    @Node(value = "exampleCategory::list", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree列表")
    public R<PageSerializable<ExampleCategory>> list(ExampleCategory exampleCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExampleCategory> lqw = new LambdaQueryWrapper<ExampleCategory>();
        if (exampleCategory.getId() != null){
            lqw.eq(ExampleCategory::getId ,exampleCategory.getId());
        }
        if (StringUtils.isNotBlank(exampleCategory.getTitle())){
            lqw.like(ExampleCategory::getTitle ,exampleCategory.getTitle());
        }
        if (exampleCategory.getParentId() != null){
            lqw.eq(ExampleCategory::getParentId ,exampleCategory.getParentId());
        }
        if (exampleCategory.getUpdatedTime() != null){
            lqw.eq(ExampleCategory::getUpdatedTime ,exampleCategory.getUpdatedTime());
        }
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExampleCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExampleCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());

        if (exampleCategory.getOnlyDate() != null){
            lqw.eq(ExampleCategory::getOnlyDate ,exampleCategory.getOnlyDate());
        }
        if (exampleCategory.getPayDatetime() != null){
            lqw.eq(ExampleCategory::getPayDatetime ,exampleCategory.getPayDatetime());
        }
        if (StringUtils.isNotBlank(exampleCategory.getImg())){
            lqw.like(ExampleCategory::getImg ,exampleCategory.getImg());
        }
        List<ExampleCategory> list = exampleCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "exampleCategory::getInfo", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree详细信息")
    public R<ExampleCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleCategoryService.getById(id));
    }

    /**
     * 新增自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree
     */
    @PostMapping
    @Node(value = "exampleCategory::add", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree新增")
    public R<Boolean> add(@RequestBody ExampleCategory exampleCategory) {
       exampleCategory.setUpdatedTime(LocalDateTime.now());
       exampleCategory.setCreatedTime(LocalDateTime.now());
        return R.success(exampleCategoryService.save(exampleCategory));
    }

    /**
     * 修改自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree
     */
    @PutMapping
    @Node(value = "exampleCategory::edit", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree修改")
    public R<Boolean> edit(@RequestBody ExampleCategory exampleCategory) {
       exampleCategory.setUpdatedTime(LocalDateTime.now());
        return R.success(exampleCategoryService.updateById(exampleCategory));
    }

    /**
     * 删除自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "exampleCategory::remove", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(exampleCategoryService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该表options[name:id, capitalName:Id, capitalKeyName:Id, dbColName:id, type:long, frontType:number, frontMax:99999999999, frontStep:, dbType:int, dbUnsigned:false, comment:ID;;optionKey, annos:, length:11, scale:0, frontLength:12, defaultValue:null, isNotNull:true, specification:int(11), dbForeignName:, capitalForeignName:, javaForeignName:, javaType:Integer]
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "exampleCategory::options", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:treeOptions")
    public R<HashMap<Integer, String>> options() {
        List<ExampleCategory> list = exampleCategoryService.list();

        HashMap<Integer, String> map = new HashMap<>();
        for(ExampleCategory item: list) {
            map.put(item.getId(), item.getTitle());
        }
        return R.success(map);
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<ExampleCategory> list = exampleCategoryService.list();
        List<TreeNodeVo<Integer, Integer, Integer, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getTitle());
        }).collect(Collectors.toList());

        Map<Integer, List<TreeNodeVo>> mapVo = new HashMap<>();
        listVo.stream().forEach(item -> {
            if(mapVo.get(item.getParentId()) == null) {
                mapVo.put(item.getParentId(), new ArrayList<>());
            }
            mapVo.get(item.getParentId()).add(item);
        });

        listVo.forEach(item -> {
            if(mapVo.containsKey(item.getKey())) {
                item.setChildren(mapVo.get(item.getKey()));
            }
        });
        return R.success(mapVo.get(0));
    }
}
