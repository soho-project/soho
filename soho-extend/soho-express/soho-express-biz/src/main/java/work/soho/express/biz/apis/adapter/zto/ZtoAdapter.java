package work.soho.express.biz.apis.adapter.zto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zto.zop.EncryptionType;
import com.zto.zop.ZopClient;
import com.zto.zop.ZopPublicRequest;
import com.zto.zop.dto.AccountInfoDTO;
import com.zto.zop.dto.ReceiveInfoDTO;
import com.zto.zop.dto.SenderInfoDTO;
import com.zto.zop.request.*;
import com.zto.zop.response.*;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.express.biz.apis.adapter.AdapterInterface;
import work.soho.express.biz.apis.dto.CreateOrderDTO;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.domain.ExpressOrder;

import java.util.List;

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
    public CreateOrderDTO createOrder(ExpressOrder expressOrder) {
        CreateOrderRequest request = new CreateOrderRequest();

        request.setPartnerType("2");
        request.setOrderType("3"); // 2 预约件 返回单号  3  预约件 不反回单号
        request.setPartnerOrderCode(expressOrder.getNo());
        request.setBillCode("");

        // ===== 账户信息 =====
        AccountInfoDTO accountInfo = new AccountInfoDTO();
        accountInfo.setAccountId(expressInfo.getBillAccount());
        accountInfo.setAccountPassword(expressInfo.getBillAccountPassword());
        accountInfo.setType(2);
//        accountInfo.setCustomerId("GPG1576724269");
        request.setAccountInfo(accountInfo);

        // ===== 寄件人信息 =====
        SenderInfoDTO senderInfo = new SenderInfoDTO();
        senderInfo.setSenderId("");
        if(expressOrder.getSenderName() == null || expressOrder.getSenderName().isEmpty()) {
            senderInfo.setSenderName(expressInfo.getSenderName());
            senderInfo.setSenderPhone(expressInfo.getSenderPhone());
            senderInfo.setSenderMobile(expressInfo.getSenderMobile());
            senderInfo.setSenderProvince(expressInfo.getSenderProvince());
            senderInfo.setSenderCity(expressInfo.getSenderCity());
            senderInfo.setSenderDistrict(expressInfo.getSenderDistrict());
            senderInfo.setSenderAddress(expressInfo.getSenderAddress());
        } else {
            senderInfo.setSenderName(expressOrder.getSenderName());
            senderInfo.setSenderPhone(expressOrder.getSenderPhone());
            senderInfo.setSenderMobile(expressOrder.getSenderMobile());
            senderInfo.setSenderProvince(expressOrder.getSenderProvince());
            senderInfo.setSenderCity(expressOrder.getSenderCity());
            senderInfo.setSenderDistrict(expressOrder.getSenderDistrict());
            senderInfo.setSenderAddress(expressOrder.getSenderAddress());
        }

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
            Response<CreateOrderResultDTO> response = JacksonUtils.toBean(result, new TypeReference<Response<CreateOrderResultDTO>>() {});
            CreateOrderResultDTO r = response.getResult();
            CreateOrderDTO createOrderDTO = new CreateOrderDTO();
            createOrderDTO.setOrderNo(r.getOrderCode());
            createOrderDTO.setBillCode(r.getBillCode());
            return createOrderDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    @Override
    public Boolean intercept(ExpressOrder expressOrder) {
        CreateInterceptRequest req = new CreateInterceptRequest();

        req.setBillCode(expressOrder.getBillCode());
        req.setRequestId(IDGeneratorUtils.snowflake().toString());
//        req.setCustomerId("B17087481000");
//        req.setCustomerName("客户名称");
//        req.setReceivePhone("13000000000");
//        req.setReceiveUsername(expressOrder.getReceiverName());
//        req.setReceiveAddress(expressOrder.getReceiverAddress());
//        req.setReceiveDistrict(expressOrder.getReceiverDistrict());
//        req.setReceiveCity(expressOrder.getReceiverCity());
//        req.setReceiveProvince(expressOrder.getReceiverProvince());
        // 拦截类型:1 返回收件网点；2 返回寄件人地址；3 修改派送地址；4 退回指定地址（必填）
        req.setDestinationType(2);
        req.setThirdBizNo(expressOrder.getNo());

        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( req));
        publicRequest.setBase64(true);
        publicRequest.setUrl(baseUrl + "thirdcenter.createIntercept");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        try {
            String r = getClient().execute(publicRequest);
            Response<CreateInterceptResultDTO> response = JacksonUtils.toBean(r, Response.class);
            return response.getStatus();
        } catch (Exception e) {
            // ignore
            return false;
        }
    }

    @Override
    public List<ScanTraceDTO> queryTrackBill(ExpressOrder expressOrder) {
        QueryTrackBillRequest req = new QueryTrackBillRequest();
        req.setBillCode(expressOrder.getBillCode());

        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( req));
        publicRequest.setBase64(true);
        publicRequest.setUrl("https://japi-test.zto.com/zto.merchant.waybill.track.query");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        try {
            String r = getClient().execute(publicRequest);
            Response<List<ScanTraceDTO>> response = JacksonUtils.toBean(r, new TypeReference<Response<List<ScanTraceDTO>>>() {});
            System.out.println(response.getResult());
            return response.getResult();
        } catch (Exception e) {
            // ignore
            return null;
        }
    }

    @Override
    public List<QueryOrderInfoResultDTO> queryOrderInfo(ExpressOrder expressOrder) {
        QueryOrderInfoRequest request = new QueryOrderInfoRequest();
        request.setOrderCode(expressOrder.getNo());
        request.setType(0);

        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( request));
        publicRequest.setBase64(true);
        publicRequest.setUrl(baseUrl + "zto.open.getOrderInfo");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        try {
            String r = getClient().execute(publicRequest);
//            System.out.println(r);

            Response<List<QueryOrderInfoResultDTO>> response = JacksonUtils.toBean(r, new TypeReference<Response<List<QueryOrderInfoResultDTO>>>() {});
            return response.getResult();
        } catch (Exception e) {
            return null;
        }
    }
}
