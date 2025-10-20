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
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.service.ShopProductInfoService;

import java.util.Arrays;
import java.util.List;

;
/**
 * 商品信息Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/guest/shopProductInfo" )
public class GuestShopProductInfoController {

    private final ShopProductInfoService shopProductInfoService;

    /**
     * 查询商品信息列表
     */
    @GetMapping("/list")
    @Node(value = "guest::shopProductInfo::list", name = "获取 商品信息 列表")
    public R<PageSerializable<ShopProductInfo>> list(ShopProductInfo shopProductInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductInfo> lqw = new LambdaQueryWrapper<ShopProductInfo>();
        lqw.eq(shopProductInfo.getId() != null, ShopProductInfo::getId ,shopProductInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSpuCode()),ShopProductInfo::getSpuCode ,shopProductInfo.getSpuCode());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getName()),ShopProductInfo::getName ,shopProductInfo.getName());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSubTitle()),ShopProductInfo::getSubTitle ,shopProductInfo.getSubTitle());
        lqw.like(shopProductInfo.getQty() != null,ShopProductInfo::getQty ,shopProductInfo.getQty());
        lqw.eq(shopProductInfo.getOriginalPrice() != null, ShopProductInfo::getOriginalPrice ,shopProductInfo.getOriginalPrice());
        lqw.eq(shopProductInfo.getSellingPrice() != null, ShopProductInfo::getSellingPrice ,shopProductInfo.getSellingPrice());
        lqw.eq(shopProductInfo.getShopId() != null, ShopProductInfo::getShopId ,shopProductInfo.getShopId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getMainImage()),ShopProductInfo::getMainImage ,shopProductInfo.getMainImage());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getThumbnails()),ShopProductInfo::getThumbnails ,shopProductInfo.getThumbnails());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getDetailHtml()),ShopProductInfo::getDetailHtml ,shopProductInfo.getDetailHtml());
        lqw.eq(shopProductInfo.getCommentCount() != null, ShopProductInfo::getCommentCount ,shopProductInfo.getCommentCount());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getCategoryId()),ShopProductInfo::getCategoryId ,shopProductInfo.getCategoryId());
        lqw.like(shopProductInfo.getShelfStatus() != null,ShopProductInfo::getShelfStatus ,shopProductInfo.getShelfStatus());
        lqw.eq(shopProductInfo.getAuditStatus() != null, ShopProductInfo::getAuditStatus ,shopProductInfo.getAuditStatus());
        lqw.eq(shopProductInfo.getUpdatedTime() != null, ShopProductInfo::getUpdatedTime ,shopProductInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ShopProductInfo> list = shopProductInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::shopProductInfo::getInfo", name = "获取 商品信息 详细信息")
    public R<ShopProductInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductInfoService.getById(id));
    }

    /**
     * 新增商品信息
     */
    @PostMapping
    @Node(value = "guest::shopProductInfo::add", name = "新增 商品信息")
    public R<Boolean> add(@RequestBody ShopProductInfo shopProductInfo) {
        return R.success(shopProductInfoService.save(shopProductInfo));
    }

    /**
     * 修改商品信息
     */
    @PutMapping
    @Node(value = "guest::shopProductInfo::edit", name = "修改 商品信息")
    public R<Boolean> edit(@RequestBody ShopProductInfo shopProductInfo) {
        return R.success(shopProductInfoService.updateById(shopProductInfo));
    }

    /**
     * 删除商品信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "guest::shopProductInfo::remove", name = "删除 商品信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductInfoService.removeByIds(Arrays.asList(ids)));
    }
}