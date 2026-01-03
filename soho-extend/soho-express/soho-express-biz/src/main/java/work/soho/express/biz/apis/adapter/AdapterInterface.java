package work.soho.express.biz.apis.adapter;

import com.zto.zop.response.QueryOrderInfoResultDTO;
import com.zto.zop.response.ScanTraceDTO;
import work.soho.express.biz.apis.dto.CreateOrderDTO;
import work.soho.express.biz.domain.ExpressOrder;

import java.util.List;

public interface AdapterInterface {
    CreateOrderDTO createOrder(ExpressOrder expressOrder);
    Boolean cancelOrder(ExpressOrder expressOrder);
    Boolean intercept(ExpressOrder expressOrder);
    List<ScanTraceDTO> queryTrackBill(ExpressOrder expressOrder);
    List<QueryOrderInfoResultDTO> queryOrderInfo(ExpressOrder expressOrder);
    byte[] createPdfBill(ExpressOrder expressOrder) throws Exception;
}
