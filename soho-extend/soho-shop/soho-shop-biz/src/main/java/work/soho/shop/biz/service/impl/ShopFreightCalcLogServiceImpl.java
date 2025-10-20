package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopFreightCalcLog;
import work.soho.shop.biz.mapper.ShopFreightCalcLogMapper;
import work.soho.shop.biz.service.ShopFreightCalcLogService;

@RequiredArgsConstructor
@Service
public class ShopFreightCalcLogServiceImpl extends ServiceImpl<ShopFreightCalcLogMapper, ShopFreightCalcLog>
    implements ShopFreightCalcLogService{

}