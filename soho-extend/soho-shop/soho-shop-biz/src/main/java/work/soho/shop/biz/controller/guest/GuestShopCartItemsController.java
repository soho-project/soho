package work.soho.shop.biz.controller.guest;

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
import work.soho.common.security.annotation.Node;;
import work.soho.shop.biz.domain.ShopCartItems;
import work.soho.shop.biz.service.ShopCartItemsService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 购物车表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/guest/shopCartItems" )
public class GuestShopCartItemsController {

    private final ShopCartItemsService shopCartItemsService;

    /**
     * 查询购物车表列表
     */
    @GetMapping("/list")
    @Node(value = "guest::shopCartItems::list", name = "获取 购物车表 列表")
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
     * 获取购物车表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::shopCartItems::getInfo", name = "获取 购物车表 详细信息")
    public R<ShopCartItems> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopCartItemsService.getById(id));
    }

    /**
     * 新增购物车表
     */
    @PostMapping
    @Node(value = "guest::shopCartItems::add", name = "新增 购物车表")
    public R<Boolean> add(@RequestBody ShopCartItems shopCartItems) {
        return R.success(shopCartItemsService.save(shopCartItems));
    }

    /**
     * 修改购物车表
     */
    @PutMapping
    @Node(value = "guest::shopCartItems::edit", name = "修改 购物车表")
    public R<Boolean> edit(@RequestBody ShopCartItems shopCartItems) {
        return R.success(shopCartItemsService.updateById(shopCartItems));
    }

    /**
     * 删除购物车表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "guest::shopCartItems::remove", name = "删除 购物车表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopCartItemsService.removeByIds(Arrays.asList(ids)));
    }
}