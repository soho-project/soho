package work.soho.shop.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopProductSpecValue;
import work.soho.shop.biz.service.ShopProductSpecValueService;

import java.util.Arrays;
import java.util.List;

;
/**
 * 商品规格值Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/guest/shopProductSpecValue" )
public class GuestShopProductSpecValueController {

    private final ShopProductSpecValueService shopProductSpecValueService;

    /**
     * 查询商品规格值列表
     */
    @GetMapping("/list")
    @Node(value = "guest::shopProductSpecValue::list", name = "获取 商品规格值 列表")
    public R<PageSerializable<ShopProductSpecValue>> list(ShopProductSpecValue shopProductSpecValue, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductSpecValue> lqw = new LambdaQueryWrapper<ShopProductSpecValue>();
        lqw.eq(shopProductSpecValue.getId() != null, ShopProductSpecValue::getId ,shopProductSpecValue.getId());
        lqw.eq(shopProductSpecValue.getProductId() != null, ShopProductSpecValue::getProductId ,shopProductSpecValue.getProductId());
        lqw.eq(shopProductSpecValue.getSkuId() != null, ShopProductSpecValue::getSkuId ,shopProductSpecValue.getSkuId());
        lqw.eq(shopProductSpecValue.getSpecId() != null, ShopProductSpecValue::getSpecId ,shopProductSpecValue.getSpecId());
        lqw.like(shopProductSpecValue.getSortOrder()!=null,ShopProductSpecValue::getSortOrder ,shopProductSpecValue.getSortOrder());
        lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getValue()),ShopProductSpecValue::getValue ,shopProductSpecValue.getValue());
        lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getExtend()),ShopProductSpecValue::getExtend ,shopProductSpecValue.getExtend());
        lqw.eq(shopProductSpecValue.getUpdatedTime() != null, ShopProductSpecValue::getUpdatedTime ,shopProductSpecValue.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductSpecValue::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductSpecValue::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ShopProductSpecValue> list = shopProductSpecValueService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品规格值详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::shopProductSpecValue::getInfo", name = "获取 商品规格值 详细信息")
    public R<ShopProductSpecValue> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductSpecValueService.getById(id));
    }

    /**
     * 新增商品规格值
     */
    @PostMapping
    @Node(value = "guest::shopProductSpecValue::add", name = "新增 商品规格值")
    public R<Boolean> add(@RequestBody ShopProductSpecValue shopProductSpecValue) {
        return R.success(shopProductSpecValueService.save(shopProductSpecValue));
    }

    /**
     * 修改商品规格值
     */
    @PutMapping
    @Node(value = "guest::shopProductSpecValue::edit", name = "修改 商品规格值")
    public R<Boolean> edit(@RequestBody ShopProductSpecValue shopProductSpecValue) {
        return R.success(shopProductSpecValueService.updateById(shopProductSpecValue));
    }

    /**
     * 删除商品规格值
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "guest::shopProductSpecValue::remove", name = "删除 商品规格值")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductSpecValueService.removeByIds(Arrays.asList(ids)));
    }
}