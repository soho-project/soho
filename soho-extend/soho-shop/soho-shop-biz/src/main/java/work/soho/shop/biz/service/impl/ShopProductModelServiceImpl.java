package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductModel;
import work.soho.shop.biz.mapper.ShopProductModelMapper;
import work.soho.shop.biz.service.ShopProductModelService;

@RequiredArgsConstructor
@Service
public class ShopProductModelServiceImpl extends ServiceImpl<ShopProductModelMapper, ShopProductModel>
    implements ShopProductModelService{

}