package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopFreightRule;
import work.soho.shop.biz.mapper.ShopFreightRuleMapper;
import work.soho.shop.biz.service.ShopFreightRuleService;

@RequiredArgsConstructor
@Service
public class ShopFreightRuleServiceImpl extends ServiceImpl<ShopFreightRuleMapper, ShopFreightRule>
    implements ShopFreightRuleService{

}