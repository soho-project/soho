package work.soho.shop.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopOrderSku;
import work.soho.shop.biz.service.ShopOrderSkuService;

import java.util.Arrays;
import java.util.List;

;
/**
 * 订单SKUController
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/user/shopOrderSku" )
public class UserShopOrderSkuController {

    private final ShopOrderSkuService shopOrderSkuService;

    /**
     * 查询订单SKU列表
     */
    @GetMapping("/list")
    @Node(value = "user::shopOrderSku::list", name = "获取 订单SKU 列表")
    public R<PageSerializable<ShopOrderSku>> list(ShopOrderSku shopOrderSku, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopOrderSku> lqw = new LambdaQueryWrapper<ShopOrderSku>();
        lqw.eq(shopOrderSku.getId() != null, ShopOrderSku::getId ,shopOrderSku.getId());
        lqw.eq(shopOrderSku.getOrderId() != null, ShopOrderSku::getOrderId ,shopOrderSku.getOrderId());
        lqw.eq(shopOrderSku.getSkuId() != null, ShopOrderSku::getSkuId ,shopOrderSku.getSkuId());
        lqw.like(StringUtils.isNotBlank(shopOrderSku.getName()),ShopOrderSku::getName ,shopOrderSku.getName());
        lqw.like(StringUtils.isNotBlank(shopOrderSku.getMainImage()),ShopOrderSku::getMainImage ,shopOrderSku.getMainImage());
        lqw.eq(shopOrderSku.getAmount() != null, ShopOrderSku::getAmount ,shopOrderSku.getAmount());
        lqw.eq(shopOrderSku.getQty() != null, ShopOrderSku::getQty ,shopOrderSku.getQty());
        lqw.eq(shopOrderSku.getTotalAmount() != null, ShopOrderSku::getTotalAmount ,shopOrderSku.getTotalAmount());
        lqw.eq(shopOrderSku.getUpdatedTime() != null, ShopOrderSku::getUpdatedTime ,shopOrderSku.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopOrderSku::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopOrderSku::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ShopOrderSku> list = shopOrderSkuService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取订单SKU详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::shopOrderSku::getInfo", name = "获取 订单SKU 详细信息")
    public R<ShopOrderSku> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopOrderSkuService.getById(id));
    }

    /**
     * 新增订单SKU
     */
    @PostMapping
    @Node(value = "user::shopOrderSku::add", name = "新增 订单SKU")
    public R<Boolean> add(@RequestBody ShopOrderSku shopOrderSku) {
        return R.success(shopOrderSkuService.save(shopOrderSku));
    }

    /**
     * 修改订单SKU
     */
    @PutMapping
    @Node(value = "user::shopOrderSku::edit", name = "修改 订单SKU")
    public R<Boolean> edit(@RequestBody ShopOrderSku shopOrderSku) {
        return R.success(shopOrderSkuService.updateById(shopOrderSku));
    }

    /**
     * 删除订单SKU
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::shopOrderSku::remove", name = "删除 订单SKU")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopOrderSkuService.removeByIds(Arrays.asList(ids)));
    }
}