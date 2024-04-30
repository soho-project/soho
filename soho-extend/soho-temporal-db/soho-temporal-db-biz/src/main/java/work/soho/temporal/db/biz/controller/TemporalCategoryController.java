package work.soho.temporal.db.biz.controller;

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
import work.soho.temporal.db.biz.domain.TemporalCategory;
import work.soho.temporal.db.biz.service.TemporalCategoryService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
import work.soho.api.admin.service.AdminDictApiService;
/**
 * 时序数据分类Controller
 *
 * @author Administrator
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/temporalCategory" )
public class TemporalCategoryController {

    private final TemporalCategoryService temporalCategoryService;

    /**
     * 查询时序数据分类列表
     */
    @GetMapping("/list")
    @Node(value = "temporalCategory::list", name = "时序数据分类;;option:id~title,tree:id~title~parent_id,frontHome:tree列表")
    public R<PageSerializable<TemporalCategory>> list(TemporalCategory temporalCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<TemporalCategory> lqw = new LambdaQueryWrapper<TemporalCategory>();
        lqw.eq(temporalCategory.getId() != null, TemporalCategory::getId ,temporalCategory.getId());
        lqw.like(StringUtils.isNotBlank(temporalCategory.getName()),TemporalCategory::getName ,temporalCategory.getName());
        lqw.like(StringUtils.isNotBlank(temporalCategory.getTitle()),TemporalCategory::getTitle ,temporalCategory.getTitle());
        lqw.eq(temporalCategory.getParentId() != null, TemporalCategory::getParentId ,temporalCategory.getParentId());
        lqw.like(StringUtils.isNotBlank(temporalCategory.getNotes()),TemporalCategory::getNotes ,temporalCategory.getNotes());
        lqw.eq(temporalCategory.getUpdatedTime() != null, TemporalCategory::getUpdatedTime ,temporalCategory.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, TemporalCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, TemporalCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<TemporalCategory> list = temporalCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取时序数据分类详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "temporalCategory::getInfo", name = "时序数据分类;;option:id~title,tree:id~title~parent_id,frontHome:tree详细信息")
    public R<TemporalCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(temporalCategoryService.getById(id));
    }

    /**
     * 新增时序数据分类
     */
    @PostMapping
    @Node(value = "temporalCategory::add", name = "时序数据分类;;option:id~title,tree:id~title~parent_id,frontHome:tree新增")
    public R<Boolean> add(@RequestBody TemporalCategory temporalCategory) {
        return R.success(temporalCategoryService.save(temporalCategory));
    }

    /**
     * 修改时序数据分类
     */
    @PutMapping
    @Node(value = "temporalCategory::edit", name = "时序数据分类;;option:id~title,tree:id~title~parent_id,frontHome:tree修改")
    public R<Boolean> edit(@RequestBody TemporalCategory temporalCategory) {
        return R.success(temporalCategoryService.updateById(temporalCategory));
    }

    /**
     * 删除时序数据分类
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "temporalCategory::remove", name = "时序数据分类;;option:id~title,tree:id~title~parent_id,frontHome:tree删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(temporalCategoryService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该时序数据分类 options:id-title
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "temporalCategory::options", name = "时序数据分类;;option:id~title,tree:id~title~parent_id,frontHome:treeOptions")
    public R<HashMap<Integer, String>> options() {
        List<TemporalCategory> list = temporalCategoryService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(TemporalCategory item: list) {
            map.put(item.getId(), item.getTitle());
        }
        return R.success(map);
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<TemporalCategory> list = temporalCategoryService.list();
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