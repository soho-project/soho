package work.soho.express.biz.service;

import com.zto.zop.response.ScanTraceDTO;
import work.soho.express.biz.domain.ExpressOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ExpressOrderService extends IService<ExpressOrder> {
    void push(Long id, Long expressInfoId);
    Boolean cancel(Long id);
    Boolean intercept(Long id);
    Boolean interceptSuccess(Long id);
    List<ScanTraceDTO> queryTrack(Long id);
    void autoSyncBillCode();
    void syncBillCode(Long id);
    byte[] createBillPdf(Long id) throws Exception;
}