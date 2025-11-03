package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.BeanUtils;
import work.soho.shop.api.vo.ShopVo;
import work.soho.shop.api.vo.UserCartVo;
import work.soho.shop.biz.domain.ShopCartItems;
import work.soho.shop.biz.domain.ShopInfo;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.domain.ShopProductSpecValue;
import work.soho.shop.biz.mapper.*;
import work.soho.shop.biz.service.ShopCartItemsService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShopCartItemsServiceImpl extends ServiceImpl<ShopCartItemsMapper, ShopCartItems>
    implements ShopCartItemsService{

    private final ShopProductInfoMapper shopProductInfoMapper;
    private final ShopInfoMapper shopInfoMapper;
    private final ShopProductSkuMapper shopProductSkuMapper;
    private final ShopProductSpecValueMapper shopProductSpecValueMapper;

    @Override
    public UserCartVo getUserCart(Long userId) {
        UserCartVo userCartVo = new UserCartVo();
        List<ShopCartItems> shopCartItems = this.list(new LambdaQueryWrapper<ShopCartItems>().eq(ShopCartItems::getUserId, userId));
        if(shopCartItems.isEmpty()) {
            return userCartVo;
        }
        // 获取所有的商品ID
        List<Long> productIds = shopCartItems.stream().map(ShopCartItems::getProductId).collect(Collectors.toList());
        Map<Long,ShopProductInfo> shopProductInfos = shopProductInfoMapper.selectBatchIds(productIds).stream().collect(Collectors.toMap(ShopProductInfo::getId, item->item));
        // 获取所有商品所属店铺ID
        List<Integer> shopIds = shopProductInfos.values().stream().map(ShopProductInfo::getShopId).collect(Collectors.toList());
        List<ShopInfo> shopInfos = shopInfoMapper.selectBatchIds(shopIds);

        List<UserCartVo.ShopGroup> shopGroups = shopInfos.stream().map(shopInfo -> {
            ShopVo shopVo = BeanUtils.copy(shopInfo, ShopVo.class);
            UserCartVo.ShopGroup shopGroup = new UserCartVo.ShopGroup();
            shopGroup.setShop(shopVo);

            shopCartItems.forEach(shopCartItem -> {
                UserCartVo.ShopCartItems item = new UserCartVo.ShopCartItems();
                item.setId(shopCartItem.getId());
                item.setProductId(shopCartItem.getProductId());
                item.setSkuId(shopCartItem.getSkuId());
                item.setPrice(shopCartItem.getPrice());
                item.setSellingPrice(shopProductInfos.get(shopCartItem.getProductId()).getSellingPrice());
                item.setQty(shopCartItem.getQty());
                item.setName(shopProductInfos.get(shopCartItem.getProductId()).getName());
                item.setMainImage(shopProductInfos.get(shopCartItem.getProductId()).getMainImage());
                if(shopCartItem.getSkuId() != null && shopCartItem.getSkuId() > 0) {
                    item.setSpceString("");
                    // 查询sku属性值
                    shopProductSpecValueMapper.selectList(
                            new LambdaQueryWrapper<ShopProductSpecValue>().eq(ShopProductSpecValue::getSkuId, shopCartItem.getSkuId())
                    ).forEach(shopProductSpecValue -> {
                        item.setSpceString(item.getSpceString() + shopProductSpecValue.getValue() + " ");
                    });

                    item.setSpceString(item.getSpceString().trim());
                }
                shopGroup.getCartItems().add(item);
            });
            return shopGroup;
        }).collect(Collectors.toList());

        userCartVo.setShopGroups(shopGroups);

        return userCartVo;
    }
}