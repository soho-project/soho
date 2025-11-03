package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductLike;
import work.soho.shop.biz.mapper.ShopProductLikeMapper;
import work.soho.shop.biz.service.ShopProductLikeService;

@RequiredArgsConstructor
@Service
public class ShopProductLikeServiceImpl extends ServiceImpl<ShopProductLikeMapper, ShopProductLike>
    implements ShopProductLikeService{

}