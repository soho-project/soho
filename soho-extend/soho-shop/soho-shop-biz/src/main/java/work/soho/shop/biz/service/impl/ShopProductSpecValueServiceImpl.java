package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductSpecValue;
import work.soho.shop.biz.mapper.ShopProductSpecValueMapper;
import work.soho.shop.biz.service.ShopProductSpecValueService;

@RequiredArgsConstructor
@Service
public class ShopProductSpecValueServiceImpl extends ServiceImpl<ShopProductSpecValueMapper, ShopProductSpecValue>
    implements ShopProductSpecValueService{

}