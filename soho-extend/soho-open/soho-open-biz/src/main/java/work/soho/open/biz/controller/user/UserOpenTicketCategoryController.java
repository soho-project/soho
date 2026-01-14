package work.soho.open.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.open.biz.domain.OpenTicketCategory;
import work.soho.open.biz.service.OpenTicketCategoryService;

import java.util.*;
import java.util.stream.Collectors;

;
/**
 * 问题分类Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user/openTicketCategory" )
public class UserOpenTicketCategoryController {

    private final OpenTicketCategoryService openTicketCategoryService;

    /**
     * 查询问题分类列表
     */
    @GetMapping("/list")
    @Node(value = "user::openTicketCategory::list", name = "获取 问题分类 列表")
    public R<PageSerializable<OpenTicketCategory>> list(OpenTicketCategory openTicketCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenTicketCategory> lqw = new LambdaQueryWrapper<OpenTicketCategory>();
        lqw.eq(openTicketCategory.getId() != null, OpenTicketCategory::getId ,openTicketCategory.getId());
        lqw.like(StringUtils.isNotBlank(openTicketCategory.getTitle()),OpenTicketCategory::getTitle ,openTicketCategory.getTitle());
        lqw.like(openTicketCategory.getParentId() != null,OpenTicketCategory::getParentId ,openTicketCategory.getParentId());
        lqw.eq(openTicketCategory.getUpdatedTime() != null, OpenTicketCategory::getUpdatedTime ,openTicketCategory.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicketCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicketCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<OpenTicketCategory> list = openTicketCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取问题分类详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openTicketCategory::getInfo", name = "获取 问题分类 详细信息")
    public R<OpenTicketCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openTicketCategoryService.getById(id));
    }

    /**
     * 新增问题分类
     */
    @PostMapping
    @Node(value = "user::openTicketCategory::add", name = "新增 问题分类")
    public R<Boolean> add(@RequestBody OpenTicketCategory openTicketCategory) {
        return R.success(openTicketCategoryService.save(openTicketCategory));
    }

    /**
     * 修改问题分类
     */
    @PutMapping
    @Node(value = "user::openTicketCategory::edit", name = "修改 问题分类")
    public R<Boolean> edit(@RequestBody OpenTicketCategory openTicketCategory) {
        return R.success(openTicketCategoryService.updateById(openTicketCategory));
    }

    /**
     * 删除问题分类
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openTicketCategory::remove", name = "删除 问题分类")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openTicketCategoryService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<OpenTicketCategory> list = openTicketCategoryService.list();
        List<TreeNodeVo<Long, Long, Long, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getTitle());
        }).collect(Collectors.toList());

        Map<Long, List<TreeNodeVo>> mapVo = new HashMap<>();
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
        return R.success(mapVo.get(0L));
    }
}