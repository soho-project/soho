package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductFreight;
import work.soho.shop.biz.mapper.ShopProductFreightMapper;
import work.soho.shop.biz.service.ShopProductFreightService;

@RequiredArgsConstructor
@Service
public class ShopProductFreightServiceImpl extends ServiceImpl<ShopProductFreightMapper, ShopProductFreight>
    implements ShopProductFreightService{

}