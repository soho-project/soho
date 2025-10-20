package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopInfo;
import work.soho.shop.biz.mapper.ShopInfoMapper;
import work.soho.shop.biz.service.ShopInfoService;

@RequiredArgsConstructor
@Service
public class ShopInfoServiceImpl extends ServiceImpl<ShopInfoMapper, ShopInfo>
    implements ShopInfoService{

}