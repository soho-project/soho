package work.soho.express.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.express.api.dto.SimpleExpressOrderDTO;
import work.soho.express.biz.apis.adapter.AdapterInterface;
import work.soho.express.biz.apis.adapter.FactoryAdapter;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.domain.ExpressOrder;
import work.soho.express.biz.enums.ExpressOrderEnums;
import work.soho.express.biz.mapper.ExpressInfoMapper;
import work.soho.express.biz.mapper.ExpressOrderMapper;
import work.soho.express.biz.service.ExpressOrderService;
import work.soho.express.api.service.ExpressOrderApiService;

@RequiredArgsConstructor
@Service
public class ExpressOrderServiceImpl extends ServiceImpl<ExpressOrderMapper, ExpressOrder>
    implements ExpressOrderService, ExpressOrderApiService {

    private final FactoryAdapter factoryAdapter;
    private final ExpressInfoMapper expressInfoMapper;

    @Override
    public Boolean createExpressOrder(SimpleExpressOrderDTO simpleExpressOrderDTO) {
        ExpressOrder expressOrder = BeanUtils.copy(simpleExpressOrderDTO, ExpressOrder.class);

        expressOrder.setNo(IDGeneratorUtils.snowflake().toString());
        expressOrder.setStatus(ExpressOrderEnums.Status.PENDING.getId());
        return save(expressOrder);
    }

    @Override
    public void push(Long id, Long expressInfoId) {
        ExpressOrder expressOrder = this.getById(id);
        ExpressInfo expressInfo = expressInfoMapper.selectById(expressInfoId);

        Assert.notNull(expressOrder);
        Assert.notNull(expressInfo);

        AdapterInterface adapter = factoryAdapter.getAdapterByExpressInfo(expressInfo);
        adapter.createOrder(expressOrder);
        expressOrder.setStatus(ExpressOrderEnums.Status.SENT.getId());
        updateById(expressOrder);
    }

    @Override
    public Boolean cancel(Long id) {
        ExpressOrder expressOrder = this.getById(id);
        ExpressInfo expressInfo = expressInfoMapper.selectById(expressOrder.getExpressInfoId());
        Assert.notNull(expressOrder);

        AdapterInterface adapter = factoryAdapter.getAdapterByExpressInfo(expressInfo);
        Assert.notNull(adapter);
        if(adapter.cancelOrder(expressOrder)) {
            expressOrder.setStatus(ExpressOrderEnums.Status.CANCEL.getId());
            return updateById(expressOrder);
        }
        return false;
    }

    @Override
    public Boolean intercept(Long id) {
        ExpressOrder expressOrder = this.getById(id);
        ExpressInfo expressInfo = expressInfoMapper.selectById(expressOrder.getExpressInfoId());
        Assert.notNull(expressOrder);

        AdapterInterface adapter = factoryAdapter.getAdapterByExpressInfo(expressInfo);
        Assert.notNull(adapter);
        if(adapter.intercept(expressOrder)) {
            expressOrder.setStatus(ExpressOrderEnums.Status.INTERCEPT_PENDING.getId());
            return updateById(expressOrder);
        }
        // 更新状态
        return false;
    }

    @Override
    public Boolean interceptSuccess(Long id) {
        ExpressOrder expressOrder = this.getById(id);
        expressOrder.setStatus(ExpressOrderEnums.Status.INTERCEPT_SUCCESS.getId());
        return updateById(expressOrder);
    }
}