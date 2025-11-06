package work.soho.pay.biz.platform.adapay.adapter;

import work.soho.pay.biz.platform.PayConfig;

public class AbstractAdapayApis {
    protected PayConfig payConfig;
    public AbstractAdapayApis(PayConfig payConfig)
    {
        this.payConfig = payConfig;
    }
}
