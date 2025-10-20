package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopFreightTemplate;
import work.soho.shop.biz.mapper.ShopFreightTemplateMapper;
import work.soho.shop.biz.service.ShopFreightTemplateService;

@RequiredArgsConstructor
@Service
public class ShopFreightTemplateServiceImpl extends ServiceImpl<ShopFreightTemplateMapper, ShopFreightTemplate>
    implements ShopFreightTemplateService{

}