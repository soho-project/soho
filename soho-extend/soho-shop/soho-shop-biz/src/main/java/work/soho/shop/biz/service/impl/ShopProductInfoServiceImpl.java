package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.mapper.ShopProductInfoMapper;
import work.soho.shop.biz.service.ShopProductInfoService;

@RequiredArgsConstructor
@Service
public class ShopProductInfoServiceImpl extends ServiceImpl<ShopProductInfoMapper, ShopProductInfo>
    implements ShopProductInfoService{

}