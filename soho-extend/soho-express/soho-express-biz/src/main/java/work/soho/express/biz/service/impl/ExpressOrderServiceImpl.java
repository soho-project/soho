package work.soho.express.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.express.biz.apis.adapter.AdapterInterface;
import work.soho.express.biz.apis.adapter.FactoryAdapter;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.domain.ExpressOrder;
import work.soho.express.biz.mapper.ExpressInfoMapper;
import work.soho.express.biz.mapper.ExpressOrderMapper;
import work.soho.express.biz.service.ExpressOrderService;

@RequiredArgsConstructor
@Service
public class ExpressOrderServiceImpl extends ServiceImpl<ExpressOrderMapper, ExpressOrder>
    implements ExpressOrderService{

    private final FactoryAdapter factoryAdapter;
    private final ExpressInfoMapper expressInfoMapper;


    @Override
    public void push(Long id, Long expressInfoId) {
        ExpressOrder expressOrder = this.getById(id);
        ExpressInfo expressInfo = expressInfoMapper.selectById(expressInfoId);

        Assert.notNull(expressOrder);
        Assert.notNull(expressInfo);

        AdapterInterface adapter = factoryAdapter.getAdapterByExpressInfo(expressInfo);
        adapter.createOrder(expressOrder);

    }

    @Override
    public Boolean cancel(Long id) {
        ExpressOrder expressOrder = this.getById(id);
        ExpressInfo expressInfo = expressInfoMapper.selectById(expressOrder.getExpressInfoId());
        Assert.notNull(expressOrder);

        AdapterInterface adapter = factoryAdapter.getAdapterByExpressInfo(expressInfo);
        Assert.notNull(adapter);
        return adapter.cancelOrder(expressOrder);
    }
}