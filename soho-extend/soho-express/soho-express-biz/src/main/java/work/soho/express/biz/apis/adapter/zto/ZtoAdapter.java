package work.soho.express.biz.apis.adapter.zto;

import com.zto.zop.EncryptionType;
import com.zto.zop.ZopClient;
import com.zto.zop.ZopPublicRequest;
import com.zto.zop.dto.AccountInfoDTO;
import com.zto.zop.dto.ReceiveInfoDTO;
import com.zto.zop.dto.SenderInfoDTO;
import com.zto.zop.request.CancelOrderRequest;
import com.zto.zop.request.CreateOrderRequest;
import com.zto.zop.response.CancelOrderResultDTO;
import com.zto.zop.response.CreateOrderResultDTO;
import com.zto.zop.response.Response;
import work.soho.common.core.util.JacksonUtils;
import work.soho.express.biz.apis.adapter.AdapterInterface;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.domain.ExpressOrder;

public class ZtoAdapter implements AdapterInterface {
    private ExpressInfo expressInfo;

    private String baseUrl = "https://japi-test.zto.com/";

    public ZtoAdapter(ExpressInfo expressInfo) {
        this.expressInfo = expressInfo;
    }

    private ZopClient getClient() {
        ZopClient client = new ZopClient(expressInfo.getAppKey(), expressInfo.getAppSecret());
        return client;
    }

    @Override
    public void createOrder(ExpressOrder expressOrder) {
        CreateOrderRequest request = new CreateOrderRequest();

        request.setPartnerType("2");
        request.setOrderType("1");
        request.setPartnerOrderCode(expressOrder.getNo());
        request.setBillCode("");

        // ===== 账户信息 =====
        AccountInfoDTO accountInfo = new AccountInfoDTO();
        accountInfo.setAccountId("test");
        accountInfo.setAccountPassword("");
        accountInfo.setType(1);
        accountInfo.setCustomerId("GPG1576724269");
        request.setAccountInfo(accountInfo);

        // ===== 寄件人信息 =====
        SenderInfoDTO senderInfo = new SenderInfoDTO();
        senderInfo.setSenderId("");
        senderInfo.setSenderName(expressOrder.getSenderName());
        senderInfo.setSenderPhone(expressOrder.getSenderPhone());
        senderInfo.setSenderMobile(expressOrder.getSenderMobile());
        senderInfo.setSenderProvince(expressOrder.getSenderProvince());
        senderInfo.setSenderCity(expressOrder.getSenderCity());
        senderInfo.setSenderDistrict(expressOrder.getSenderDistrict());
        senderInfo.setSenderAddress(expressOrder.getSenderAddress());
        request.setSenderInfo(senderInfo);

        // ===== 收件人信息 =====
        ReceiveInfoDTO receiveInfo = new ReceiveInfoDTO();
        receiveInfo.setReceiverName(expressOrder.getReceiverName());
        receiveInfo.setReceiverPhone(expressOrder.getReceiverPhone());
        receiveInfo.setReceiverMobile(expressOrder.getReceiverMobile());
        receiveInfo.setReceiverProvince(expressOrder.getReceiverProvince());
        receiveInfo.setReceiverCity(expressOrder.getReceiverCity());
        receiveInfo.setReceiverDistrict(expressOrder.getReceiverDistrict());
        receiveInfo.setReceiverAddress(expressOrder.getReceiverDistrict());
        request.setReceiveInfo(receiveInfo);

        // ===== 增值服务 =====
//        OrderVasDTO vas = new OrderVasDTO();
//        vas.setVasType("COD");
//        vas.setVasAmount(100000);
//        vas.setVasPrice(0);
//        vas.setVasDetail("");
//        vas.setAccountNo("");
//        request.setOrderVasList(Collections.singletonList(vas));

        // ===== 网点信息 =====
//        request.setHallCode("S2044");
//        request.setSiteCode("02100");
//        request.setSiteName("上海");

        // ===== 汇总信息 =====
//        SummaryInfoDTO summaryInfo = new SummaryInfoDTO();
//        summaryInfo.setSize("");
//        summaryInfo.setQuantity(3);
//        summaryInfo.setPrice(30);
//        summaryInfo.setFreight(20);
//        summaryInfo.setPremium(10);
//        summaryInfo.setStartTime("2020-12-10 12:00:00");
//        summaryInfo.setEndTime("2020-12-10 12:00:00");
//        request.setSummaryInfo(summaryInfo);

        // ===== 订单明细 =====
//        OrderItemDTO item = new OrderItemDTO();
//        item.setName("");
//        item.setCategory("");
//        item.setMaterial("");
//        item.setSize("");
//        item.setWeight(0);
//        item.setUnitprice(0);
//        item.setQuantity(0);
//        item.setRemark("");
//        request.setOrderItems(Collections.singletonList(item));

        // ===== 智能柜 =====
//        CabinetDTO cabinet = new CabinetDTO();
//        cabinet.setAddress("");
//        cabinet.setSpecification(0);
//        cabinet.setCode("");
//        request.setCabinet(cabinet);

        // ===== 备注 =====
        request.setRemark("小吉下单");

        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( request));
        publicRequest.setBase64(true);
        publicRequest.setUrl(baseUrl + "zto.open.createOrder");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        try {
            String result = getClient().execute(publicRequest);
            Response<CreateOrderResultDTO> response = JacksonUtils.toBean(result, Response.class);
            System.out.println(response.getResult());
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public Boolean cancelOrder(ExpressOrder expressOrder) {
        CancelOrderRequest request = new CancelOrderRequest();
        request.setOrderCode(expressOrder.getNo());
        request.setCancelType("1");
        request.setBillCode(expressOrder.getBillCode());

        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( request));
        publicRequest.setBase64(true);
        publicRequest.setUrl(baseUrl + "zto.open.cancelPreOrder");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        try {
            String result = getClient().execute(publicRequest);
            Response<CancelOrderResultDTO> response = JacksonUtils.toBean(result, Response.class);
            return response.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
