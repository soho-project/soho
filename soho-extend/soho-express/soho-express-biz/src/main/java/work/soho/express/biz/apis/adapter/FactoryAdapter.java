package work.soho.express.biz.apis.adapter;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.express.biz.apis.adapter.yunda.YundaAdapter;
import work.soho.express.biz.apis.adapter.zto.ZtoAdapter;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.enums.ExpressInfoEnums;

@Service
public class FactoryAdapter {
    public AdapterInterface getAdapterByExpressInfo(ExpressInfo expressInfo) {
        Assert.notNull(expressInfo, "快递信息不能为空");
        Assert.notNull(expressInfo.getExpressType(), "请选择快递公司");

        if(expressInfo.getExpressType() == ExpressInfoEnums.ExpressType.ZTO_EXPRESS.getId()) {
            return new ZtoAdapter(expressInfo);
        } else if(expressInfo.getExpressType() == ExpressInfoEnums.ExpressType.YTO_EXPRESS.getId()) {
            return new YundaAdapter(expressInfo);
        }

        return null;
    }
}
