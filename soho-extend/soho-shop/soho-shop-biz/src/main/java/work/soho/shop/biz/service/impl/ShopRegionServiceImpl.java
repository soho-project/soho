package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopRegion;
import work.soho.shop.biz.mapper.ShopRegionMapper;
import work.soho.shop.biz.service.ShopRegionService;

@RequiredArgsConstructor
@Service
public class ShopRegionServiceImpl extends ServiceImpl<ShopRegionMapper, ShopRegion>
    implements ShopRegionService{

}