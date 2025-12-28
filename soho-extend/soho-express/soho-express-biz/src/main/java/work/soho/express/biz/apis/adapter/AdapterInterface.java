package work.soho.express.biz.apis.adapter;

import work.soho.express.biz.domain.ExpressOrder;

public interface AdapterInterface {
    void createOrder(ExpressOrder expressOrder);
    Boolean cancelOrder(ExpressOrder expressOrder);
    Boolean intercept(ExpressOrder expressOrder);
}
