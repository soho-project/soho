package work.soho.express.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.express.api.dto.TrackDTO;
import work.soho.express.biz.domain.ExpressOrder;

import java.util.List;

public interface ExpressOrderService extends IService<ExpressOrder> {
    void push(Long id, Long expressInfoId);
    Boolean cancel(Long id);
    Boolean intercept(Long id);
    Boolean interceptSuccess(Long id);
    List<TrackDTO> queryTrack(Long id);
    void autoSyncBillCode();
    void syncBillCode(Long id);
    byte[] createBillPdf(Long id) throws Exception;
}