package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.PublishDeleteNotify;
import work.soho.shop.biz.domain.ShopFreightTemplate;
import work.soho.shop.biz.mapper.ShopFreightTemplateMapper;
import work.soho.shop.biz.service.ShopFreightTemplateService;

@PublishDeleteNotify
@RequiredArgsConstructor
@Service
public class ShopFreightTemplateServiceImpl extends ServiceImpl<ShopFreightTemplateMapper, ShopFreightTemplate>
    implements ShopFreightTemplateService{

}