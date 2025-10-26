package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.OnBeforeDelete;
import work.soho.common.database.event.DeleteEvent;
import work.soho.shop.biz.domain.ShopFreightRule;
import work.soho.shop.biz.domain.ShopFreightTemplate;
import work.soho.shop.biz.mapper.ShopFreightRuleMapper;
import work.soho.shop.biz.service.ShopFreightRuleService;

@RequiredArgsConstructor
@Service
public class ShopFreightRuleServiceImpl extends ServiceImpl<ShopFreightRuleMapper, ShopFreightRule>
    implements ShopFreightRuleService{
    @OnBeforeDelete(entityType = ShopFreightTemplate.class)
    public void onBeforeRemove(DeleteEvent event) {
        remove(new LambdaQueryWrapper<ShopFreightRule>().in(ShopFreightRule::getTemplateId, event.getEntityIds()));
    }
}