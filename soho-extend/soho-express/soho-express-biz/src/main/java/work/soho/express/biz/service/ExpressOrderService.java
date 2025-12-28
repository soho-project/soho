package work.soho.express.biz.service;

import work.soho.express.biz.domain.ExpressOrder;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ExpressOrderService extends IService<ExpressOrder> {
    void push(Long id, Long expressInfoId);
    Boolean cancel(Long id);
}