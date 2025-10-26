package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.PublishDeleteNotify;
import work.soho.shop.biz.domain.ShopInfo;
import work.soho.shop.biz.mapper.ShopInfoMapper;
import work.soho.shop.biz.service.ShopInfoService;

@PublishDeleteNotify(entityType = ShopInfo.class)
@RequiredArgsConstructor
@Service
public class ShopInfoServiceImpl extends ServiceImpl<ShopInfoMapper, ShopInfo>
    implements ShopInfoService{

}