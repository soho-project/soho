package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductModelSpec;
import work.soho.shop.biz.mapper.ShopProductModelSpecMapper;
import work.soho.shop.biz.service.ShopProductModelSpecService;

@RequiredArgsConstructor
@Service
public class ShopProductModelSpecServiceImpl extends ServiceImpl<ShopProductModelSpecMapper, ShopProductModelSpec>
    implements ShopProductModelSpecService{

}