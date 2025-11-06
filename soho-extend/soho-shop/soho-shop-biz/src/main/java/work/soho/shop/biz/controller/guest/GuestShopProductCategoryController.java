package work.soho.shop.biz.controller.guest;

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
import work.soho.shop.biz.domain.ShopProductCategory;
import work.soho.shop.biz.service.ShopProductCategoryService;

import java.util.*;
import java.util.stream.Collectors;

;
/**
 * 商品分类Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/guest/shopProductCategory" )
public class GuestShopProductCategoryController {

    private final ShopProductCategoryService shopProductCategoryService;

    /**
     * 查询商品分类列表
     */
    @GetMapping("/list")
    @Node(value = "guest::shopProductCategory::list", name = "获取 商品分类 列表")
    public R<PageSerializable<ShopProductCategory>> list(ShopProductCategory shopProductCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductCategory> lqw = new LambdaQueryWrapper<ShopProductCategory>();
        lqw.eq(shopProductCategory.getId() != null, ShopProductCategory::getId ,shopProductCategory.getId());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getName()),ShopProductCategory::getName ,shopProductCategory.getName());
        lqw.eq(shopProductCategory.getParentId() != null, ShopProductCategory::getParentId ,shopProductCategory.getParentId());
        lqw.eq(shopProductCategory.getLevel() != null, ShopProductCategory::getLevel ,shopProductCategory.getLevel());
        lqw.eq(shopProductCategory.getSortOrder() != null, ShopProductCategory::getSortOrder ,shopProductCategory.getSortOrder());
        lqw.eq(shopProductCategory.getStatus() != null, ShopProductCategory::getStatus ,shopProductCategory.getStatus());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getIcon()),ShopProductCategory::getIcon ,shopProductCategory.getIcon());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getDescription()),ShopProductCategory::getDescription ,shopProductCategory.getDescription());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopProductCategory.getUpdatedTime() != null, ShopProductCategory::getUpdatedTime ,shopProductCategory.getUpdatedTime());
        List<ShopProductCategory> list = shopProductCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品分类详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::shopProductCategory::getInfo", name = "获取 商品分类 详细信息")
    public R<ShopProductCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductCategoryService.getById(id));
    }

    /**
     * 新增商品分类
     */
    @PostMapping
    @Node(value = "guest::shopProductCategory::add", name = "新增 商品分类")
    public R<Boolean> add(@RequestBody ShopProductCategory shopProductCategory) {
        return R.success(shopProductCategoryService.save(shopProductCategory));
    }

    /**
     * 修改商品分类
     */
    @PutMapping
    @Node(value = "guest::shopProductCategory::edit", name = "修改 商品分类")
    public R<Boolean> edit(@RequestBody ShopProductCategory shopProductCategory) {
        return R.success(shopProductCategoryService.updateById(shopProductCategory));
    }

    /**
     * 删除商品分类
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "guest::shopProductCategory::remove", name = "删除 商品分类")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductCategoryService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<ShopProductCategory> list = shopProductCategoryService.list();
        List<TreeNodeVo<Long, Long, Long, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getName());
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