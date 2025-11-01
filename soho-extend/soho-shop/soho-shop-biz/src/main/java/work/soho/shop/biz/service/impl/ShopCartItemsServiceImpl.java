package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopCartItems;
import work.soho.shop.biz.mapper.ShopCartItemsMapper;
import work.soho.shop.biz.service.ShopCartItemsService;

@RequiredArgsConstructor
@Service
public class ShopCartItemsServiceImpl extends ServiceImpl<ShopCartItemsMapper, ShopCartItems>
    implements ShopCartItemsService{

}