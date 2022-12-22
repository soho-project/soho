import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.domain.ExampleCategory;
import work.soho.service.ExampleCategoryService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 自动化样例分类表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/exampleCategory" )
public class ExampleCategoryController {

    private final ExampleCategoryService exampleCategoryService;

    /**
     * 查询自动化样例分类表列表
     */
    @GetMapping("/list")
    @Node(value = "exampleCategory::list", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree列表")
    public R<PageSerializable<ExampleCategory>> list(ExampleCategory exampleCategory)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExampleCategory> lqw = new LambdaQueryWrapper<ExampleCategory>();        if (exampleCategory.getCreatedTime() != null){
            lqw.eq(ExampleCategory::getCreatedTime ,exampleCategory.getCreatedTime());
        }        if (exampleCategory.getId() != null){
            lqw.eq(ExampleCategory::getId ,exampleCategory.getId());
        }        if (exampleCategory.getImg() != null){
            lqw.eq(ExampleCategory::getImg ,exampleCategory.getImg());
        }        if (exampleCategory.getOnlyDate() != null){
            lqw.eq(ExampleCategory::getOnlyDate ,exampleCategory.getOnlyDate());
        }        if (exampleCategory.getParentId() != null){
            lqw.eq(ExampleCategory::getParentId ,exampleCategory.getParentId());
        }        if (exampleCategory.getPayDatetime() != null){
            lqw.eq(ExampleCategory::getPayDatetime ,exampleCategory.getPayDatetime());
        }        if (exampleCategory.getTitle() != null){
            lqw.eq(ExampleCategory::getTitle ,exampleCategory.getTitle());
        }        if (exampleCategory.getUpdatedTime() != null){
            lqw.eq(ExampleCategory::getUpdatedTime ,exampleCategory.getUpdatedTime());
        }        List<ExampleCategory> list = exampleCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取自动化样例分类表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "exampleCategory::getInfo", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree详细信息")
    public R<ExampleCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleCategoryService.getById(id));
    }

    /**
     * 新增自动化样例分类表
     */
    @PostMapping
    @Node(value = "exampleCategory::add", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree新增")
    public R<Boolean> add(@RequestBody ExampleCategory exampleCategory) {
        return R.success(exampleCategoryService.save(exampleCategory));
    }

    /**
     * 修改自动化样例分类表
     */
    @PutMapping
    @Node(value = "exampleCategory::edit", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree修改")
    public R<Boolean> edit(@RequestBody ExampleCategory exampleCategory) {
        return R.success(exampleCategoryService.updateById(exampleCategory));
    }

    /**
     * 删除自动化样例分类表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "exampleCategory::remove", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:tree删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(exampleCategoryService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该自动化样例分类表 options:id-title
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "exampleCategory::options", name = "自动化样例分类表;;option:id~title,tree:id~title~parent_id,frontHome:treeOptions")
    public R<HashMap<Integer, String>> options() {
        List<ExampleCategory> list = exampleCategoryService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(ExampleCategory item: list) {
            map.put(item.getnull(), item.getnull());
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