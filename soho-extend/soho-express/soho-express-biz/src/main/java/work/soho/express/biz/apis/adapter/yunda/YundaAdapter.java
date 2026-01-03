package work.soho.express.biz.apis.adapter.yunda;

import com.zto.zop.response.QueryOrderInfoResultDTO;
import com.zto.zop.response.ScanTraceDTO;
import work.soho.express.biz.apis.adapter.AdapterInterface;
import work.soho.express.biz.apis.dto.CreateOrderDTO;
import work.soho.express.biz.domain.ExpressOrder;

import java.util.List;

public class YundaAdapter implements AdapterInterface {
    @Override
    public CreateOrderDTO createOrder(ExpressOrder expressOrder) {
        return null;
    }

    @Override
    public Boolean cancelOrder(ExpressOrder expressOrder) {
        return null;
    }

    @Override
    public Boolean intercept(ExpressOrder expressOrder) {
        return null;
    }

    @Override
    public List<ScanTraceDTO> queryTrackBill(ExpressOrder expressOrder) {
        return List.of();
    }

    @Override
    public List<QueryOrderInfoResultDTO> queryOrderInfo(ExpressOrder expressOrder) {
        return List.of();
    }

    @Override
    public byte[] createPdfBill(ExpressOrder expressOrder) throws Exception {
        return new byte[0];
    }
}
