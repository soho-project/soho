package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopLogisticsCompany;
import work.soho.shop.biz.mapper.ShopLogisticsCompanyMapper;
import work.soho.shop.biz.service.ShopLogisticsCompanyService;

@RequiredArgsConstructor
@Service
public class ShopLogisticsCompanyServiceImpl extends ServiceImpl<ShopLogisticsCompanyMapper, ShopLogisticsCompany>
    implements ShopLogisticsCompanyService{

}