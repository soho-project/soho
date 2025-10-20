package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopUserAddresses;
import work.soho.shop.biz.mapper.ShopUserAddressesMapper;
import work.soho.shop.biz.service.ShopUserAddressesService;

@RequiredArgsConstructor
@Service
public class ShopUserAddressesServiceImpl extends ServiceImpl<ShopUserAddressesMapper, ShopUserAddresses>
    implements ShopUserAddressesService{

}