package work.soho.express.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zto.zop.response.QueryOrderInfoResultDTO;
import com.zto.zop.response.ScanTraceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.express.api.dto.SimpleExpressOrderDTO;
import work.soho.express.api.service.ExpressOrderApiService;
import work.soho.express.biz.apis.adapter.AdapterInterface;
import work.soho.express.biz.apis.adapter.FactoryAdapter;
import work.soho.express.biz.apis.dto.CreateOrderDTO;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.domain.ExpressOrder;
import work.soho.express.biz.enums.ExpressOrderEnums;
import work.soho.express.biz.mapper.ExpressInfoMapper;
import work.soho.express.biz.mapper.ExpressOrderMapper;
import work.soho.express.biz.service.ExpressOrderService;

import java.util.List;

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
        CreateOrderDTO createOrderResultDTO = adapter.createOrder(expressOrder);
        expressOrder.setStatus(ExpressOrderEnums.Status.SENT.getId());
        expressOrder.setPartnerOrderNo(createOrderResultDTO.getOrderNo());
        if(createOrderResultDTO.getBillCode() != null && StringUtils.isNotBlank(createOrderResultDTO.getBillCode())) {
            expressOrder.setBillCode(createOrderResultDTO.getBillCode());
        }
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

    @Override
    public List<ScanTraceDTO> queryTrack(Long id) {
        ExpressOrder expressOrder = this.getById(id);
        AdapterInterface adapter = factoryAdapter.getAdapterByExpressInfo(expressInfoMapper.selectById(expressOrder.getExpressInfoId()));
        return adapter.queryTrackBill(expressOrder);
    }

    @Override
    public void autoSyncBillCode() {
        Long lastId = 0L;
        LambdaQueryWrapper<ExpressOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExpressOrder::getStatus, ExpressOrderEnums.Status.SENT.getId());
        queryWrapper.isNull(ExpressOrder::getBillCode);
        queryWrapper.orderByAsc(ExpressOrder::getId);
        do {
            queryWrapper.gt(ExpressOrder::getId, lastId);
            queryWrapper.last("limit 100");
            List<ExpressOrder> expressOrders = this.list(queryWrapper);

            // 数据处理
            for (ExpressOrder expressOrder : expressOrders) {
                syncBillCode(expressOrder);
                lastId = expressOrder.getId();
            }

            if(expressOrders.size() <= 0) {
                break;
            }
        } while (true);
    }

    /**
     * 同步订单快递单号
     *
     * @param expressOrder
     */
    private void syncBillCode(ExpressOrder expressOrder) {
        AdapterInterface adapter = factoryAdapter.getAdapterByExpressInfo(expressInfoMapper.selectById(expressOrder.getExpressInfoId()));
        List<QueryOrderInfoResultDTO> queryOrderInfo = adapter.queryOrderInfo(expressOrder);
        if(queryOrderInfo != null && queryOrderInfo.size() > 0) {
            expressOrder.setBillCode(queryOrderInfo.get(0).getBillCode());
            updateById(expressOrder);
        }
    }

    @Override
    public void syncBillCode(Long id) {
        ExpressOrder expressOrder = this.getById(id);
        syncBillCode(expressOrder);
    }
}