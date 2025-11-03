package work.soho.shop.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.shop.api.vo.UserCartVo;
import work.soho.shop.biz.domain.ShopCartItems;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.domain.ShopProductSku;
import work.soho.shop.biz.service.ShopCartItemsService;
import work.soho.shop.biz.service.ShopProductInfoService;
import work.soho.shop.biz.service.ShopProductSkuService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 购物车表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/user/shopCartItems" )
public class UserShopCartItemsController {

    private final ShopCartItemsService shopCartItemsService;
    private final ShopProductInfoService shopProductInfoService;
    private final ShopProductSkuService shopProductSkuService;

    /**
     * 查询购物车表列表
     */
    @GetMapping("/list")
    @Node(value = "user::shopCartItems::list", name = "获取 购物车表 列表")
    public R<PageSerializable<ShopCartItems>> list(ShopCartItems shopCartItems, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopCartItems> lqw = new LambdaQueryWrapper<ShopCartItems>();
        lqw.eq(shopCartItems.getId() != null, ShopCartItems::getId ,shopCartItems.getId());
        lqw.eq(shopCartItems.getUserId() != null, ShopCartItems::getUserId ,shopCartItems.getUserId());
        lqw.like(StringUtils.isNotBlank(shopCartItems.getSessionId()),ShopCartItems::getSessionId ,shopCartItems.getSessionId());
        lqw.eq(shopCartItems.getProductId() != null, ShopCartItems::getProductId ,shopCartItems.getProductId());
        lqw.eq(shopCartItems.getSkuId() != null, ShopCartItems::getSkuId ,shopCartItems.getSkuId());
        lqw.eq(shopCartItems.getQty() != null, ShopCartItems::getQty ,shopCartItems.getQty());
        lqw.eq(shopCartItems.getIsSelected() != null, ShopCartItems::getIsSelected ,shopCartItems.getIsSelected());
        lqw.eq(shopCartItems.getPrice() != null, ShopCartItems::getPrice ,shopCartItems.getPrice());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopCartItems::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopCartItems::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopCartItems.getUpdatedTime() != null, ShopCartItems::getUpdatedTime ,shopCartItems.getUpdatedTime());
        List<ShopCartItems> list = shopCartItemsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取购物车表列表
     */
    @GetMapping("/getUserCart")
    public R<UserCartVo> getUserCart(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(shopCartItemsService.getUserCart(userDetails.getId()));
    }

    /**
     * 获取购物车表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::shopCartItems::getInfo", name = "获取 购物车表 详细信息")
    public R<ShopCartItems> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopCartItemsService.getById(id));
    }

    /**
     * 新增购物车表
     */
    @PostMapping
    @Node(value = "user::shopCartItems::add", name = "新增 购物车表")
    public R<Boolean> add(@RequestBody ShopCartItems shopCartItems, @AuthenticationPrincipal SohoUserDetails userDetails) {
        // 获取是否有提交产品
        ShopCartItems cartItem = shopCartItemsService.getOne(
                new LambdaQueryWrapper<ShopCartItems>()
                        .eq(ShopCartItems::getUserId, userDetails.getId())
                        .eq(ShopCartItems::getProductId, shopCartItems.getProductId())
                        .eq(ShopCartItems::getSkuId, shopCartItems.getSkuId())
        );
        if (cartItem != null) {
            cartItem.setQty(cartItem.getQty() + shopCartItems.getQty());
            return R.success(shopCartItemsService.updateById(cartItem));
        }

        //TODO 获取产品价格
        ShopProductInfo shopProductInfo = shopProductInfoService.getById(shopCartItems.getProductId());
        ShopProductSku shopProductSku = shopProductSkuService.getById(shopCartItems.getSkuId());
        if(shopProductSku != null) {
            shopCartItems.setPrice(shopProductSku.getSellingPrice());
        } else {
            shopCartItems.setPrice(shopProductInfo.getSellingPrice());
        }
        shopCartItems.setUserId(userDetails.getId());
        return R.success(shopCartItemsService.save(shopCartItems));
    }

    /**
     * 修改购物车表
     */
    @PutMapping
    @Node(value = "user::shopCartItems::edit", name = "修改 购物车表")
    public R<Boolean> edit(@RequestBody ShopCartItems shopCartItems) {
        return R.success(shopCartItemsService.updateById(shopCartItems));
    }

    /**
     * 删除购物车表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::shopCartItems::remove", name = "删除 购物车表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopCartItemsService.removeByIds(Arrays.asList(ids)));
    }
}