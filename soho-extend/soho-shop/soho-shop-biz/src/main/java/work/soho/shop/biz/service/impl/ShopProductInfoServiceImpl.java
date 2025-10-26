package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.PublishDeleteNotify;
import work.soho.common.database.annotation.PublishSaveNotify;
import work.soho.common.database.annotation.PublishUpdateNotify;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.mapper.ShopProductInfoMapper;
import work.soho.shop.biz.service.ShopProductInfoService;

@PublishDeleteNotify
@PublishSaveNotify(entityType = ShopProductInfo.class)
@PublishUpdateNotify(entityType = ShopProductInfo.class)
@RequiredArgsConstructor
@Service
public class ShopProductInfoServiceImpl extends ServiceImpl<ShopProductInfoMapper, ShopProductInfo>
    implements ShopProductInfoService{

}