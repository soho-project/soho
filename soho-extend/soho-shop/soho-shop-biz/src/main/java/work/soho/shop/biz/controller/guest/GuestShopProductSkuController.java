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
import work.soho.shop.biz.domain.ShopProductSku;
import work.soho.shop.biz.service.ShopProductSkuService;

import java.util.Arrays;
import java.util.List;

;
/**
 * Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/guest/shopProductSku" )
public class GuestShopProductSkuController {

    private final ShopProductSkuService shopProductSkuService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "guest::shopProductSku::list", name = "获取  列表")
    public R<PageSerializable<ShopProductSku>> list(ShopProductSku shopProductSku, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductSku> lqw = new LambdaQueryWrapper<ShopProductSku>();
        lqw.eq(shopProductSku.getId() != null, ShopProductSku::getId ,shopProductSku.getId());
        lqw.like(shopProductSku.getProductId() != null,ShopProductSku::getProductId ,shopProductSku.getProductId());
        lqw.like(StringUtils.isNotBlank(shopProductSku.getCode()),ShopProductSku::getCode ,shopProductSku.getCode());
        lqw.eq(shopProductSku.getQty() != null, ShopProductSku::getQty ,shopProductSku.getQty());
        lqw.eq(shopProductSku.getOriginalPrice() != null, ShopProductSku::getOriginalPrice ,shopProductSku.getOriginalPrice());
        lqw.eq(shopProductSku.getSellingPrice() != null, ShopProductSku::getSellingPrice ,shopProductSku.getSellingPrice());
        lqw.like(StringUtils.isNotBlank(shopProductSku.getMainImage()),ShopProductSku::getMainImage ,shopProductSku.getMainImage());
        lqw.eq(shopProductSku.getUpdatedTime() != null, ShopProductSku::getUpdatedTime ,shopProductSku.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductSku::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductSku::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ShopProductSku> list = shopProductSkuService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::shopProductSku::getInfo", name = "获取  详细信息")
    public R<ShopProductSku> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductSkuService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "guest::shopProductSku::add", name = "新增 ")
    public R<Boolean> add(@RequestBody ShopProductSku shopProductSku) {
        return R.success(shopProductSkuService.save(shopProductSku));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "guest::shopProductSku::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody ShopProductSku shopProductSku) {
        return R.success(shopProductSkuService.updateById(shopProductSku));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "guest::shopProductSku::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductSkuService.removeByIds(Arrays.asList(ids)));
    }
}