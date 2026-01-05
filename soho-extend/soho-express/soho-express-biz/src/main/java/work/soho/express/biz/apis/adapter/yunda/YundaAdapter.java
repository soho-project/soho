package work.soho.express.biz.apis.adapter.yunda;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yundasys.request.CancelBillOrderRequest;
import com.yundasys.request.CreateBillOrderRequest;
import com.yundasys.request.QueryTrackBillRequest;
import com.yundasys.response.CancelBillOrderResultResponse;
import com.yundasys.response.CreateBillOrderResultResponse;
import com.yundasys.response.QueryTrackBillResultResponse;
import com.yundasys.response.Response;
import com.yundasys.utils.OpenApiHttpUtils;
import com.zto.zop.response.QueryOrderInfoResultDTO;
import work.soho.common.core.util.JacksonUtils;
import work.soho.express.api.dto.PrintInfoDTO;
import work.soho.express.api.dto.TrackDTO;
import work.soho.express.biz.apis.adapter.AdapterInterface;
import work.soho.express.biz.apis.dto.CreateOrderDTO;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.domain.ExpressOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YundaAdapter implements AdapterInterface {
    private ExpressInfo expressInfo;

    private String baseUrl = "https://u-openapi.yundasys.com/";

    public YundaAdapter(ExpressInfo expressInfo) {
        this.expressInfo = expressInfo;
    }

    /**
     * 执行请求
     *
     * @return
     */
    private <T> T execute(String url, Object json, TypeReference<T> typeReference) {
        String r = OpenApiHttpUtils.doPostJson(baseUrl + url, JacksonUtils.toJson( json), expressInfo.getAppKey(), expressInfo.getAppSecret());
        System.out.println(r);
        return JacksonUtils.toBean(r, typeReference);
    }


    @Override
    public CreateOrderDTO createOrder(ExpressOrder expressOrder) {
        CreateBillOrderRequest dto = new CreateBillOrderRequest();
        CreateBillOrderRequest.Order order = new CreateBillOrderRequest.Order();
        order.setOrder_serial_no(expressOrder.getNo());
        order.setKhddh(expressOrder.getNo());
        order.setNode_id("350"); // 默认固定值
        order.setOrder_type("common"); // 默认固定值
        order.setCollection_value(126.5);   // 未知字段
        order.setValue(126.5); //
//        order.setSize("0.12,0.23,0.11"); // 长宽高
//        order.setWeight(0.0);
//        order.setSpecial(0);
        order.setRemark(expressOrder.getRemark());

        // 创建收件人
        CreateBillOrderRequest.Receiver receiver = new CreateBillOrderRequest.Receiver();
        receiver.setName(expressOrder.getReceiverName());
        receiver.setMobile(expressOrder.getReceiverMobile());
        receiver.setProvince(expressOrder.getReceiverProvince());
        receiver.setCity(expressOrder.getReceiverCity());
        receiver.setCounty(expressOrder.getReceiverDistrict());
        receiver.setAddress(expressOrder.getReceiverAddress());
        order.setReceiver(receiver);

        // 创建寄件人
        CreateBillOrderRequest.Sender sender = new CreateBillOrderRequest.Sender();
        sender.setName(expressOrder.getSenderName());
        sender.setMobile(expressOrder.getSenderMobile());
        sender.setProvince(expressOrder.getSenderProvince());
        sender.setCity(expressOrder.getSenderCity());
        sender.setCounty(expressOrder.getSenderDistrict());
        sender.setAddress(expressOrder.getSenderAddress());
        order.setSender(sender);

        // TODO 解析物品信息
        // 创建商品
        CreateBillOrderRequest.Item item = new CreateBillOrderRequest.Item();
        item.setName("衣服");
        item.setNumber(1);
        item.setRemark("袜子");
        order.setItems(Arrays.asList(item));

        // 创建多件打包信息
        CreateBillOrderRequest.MultiPack multiPack = new CreateBillOrderRequest.MultiPack();
        multiPack.setMulpck("");
        multiPack.setTotal(1);
        multiPack.setEndmark(1);
//        order.setMulti_pack(multiPack);

        // 创建标记信息
//        CreateBillOrderRequest.MarkingInfo insured = new CreateBillOrderRequest.MarkingInfo();
//        insured.setType("INSURED");
//        CreateBillOrderRequest.MarkingValue insuredValue = new CreateBillOrderRequest.MarkingValue();
//        insuredValue.setValue(2100);
//        insured.setMarkingValue(insuredValue);
//
//        CreateBillOrderRequest.MarkingInfo df = new CreateBillOrderRequest.MarkingInfo();
//        df.setType("DF");
//        CreateBillOrderRequest.MarkingValue dfValue = new CreateBillOrderRequest.MarkingValue();
//        dfValue.setValue(15);
//        df.setMarkingValue(dfValue);

//        CreateBillOrderRequest.MarkingInfo cod = new CreateBillOrderRequest.MarkingInfo();
//        cod.setType("COD");
//        CreateBillOrderRequest.MarkingValue codValue = new CreateBillOrderRequest.MarkingValue();
//        codValue.setValue(15);
//        cod.setMarkingValue(codValue);

//        CreateBillOrderRequest.MarkingInfo returnMark = new CreateBillOrderRequest.MarkingInfo();
//        returnMark.setType("RETURN");
//        CreateBillOrderRequest.MarkingValue returnValue = new CreateBillOrderRequest.MarkingValue();
//        returnValue.setValue("1,2");
//        returnMark.setMarkingValue(returnValue);
//
//        CreateBillOrderRequest.MarkingInfo yxz = new CreateBillOrderRequest.MarkingInfo();
//        yxz.setType("YXZ");
//
//        CreateBillOrderRequest.MarkingInfo mul = new CreateBillOrderRequest.MarkingInfo();
//        mul.setType("MUL");
//        CreateBillOrderRequest.MarkingValue mulValue = new CreateBillOrderRequest.MarkingValue();
//        mulValue.setValue(1);
//        mul.setMarkingValue(mulValue);

        CreateBillOrderRequest.MarkingInfo contact = new CreateBillOrderRequest.MarkingInfo();
        contact.setType("CONTACT");

//        order.setMarkingInfos(Arrays.asList(insured, yxz));

        dto.setOrders(Arrays.asList(order));
//        System.out.println(dto);

//        String url = "https://u-openapi.yundasys.com/openapi-api/v1/accountOrder/createBmOrder";
//        String appKey = "999999";
//        String appSecret = "04d4ad40eeec11e9bad2d962f53dda9d";
//        String partnerId = "529951202001";
//        String secret = "Y4TQ3WBar9hpnw7As8xUZEReSuDdf2";

//        String appKey = "518553900309";
//        String appSecret = "qWkXMnTpmwhDaAcv9gJRrFxS7fKHtQ";
//        String partnerId = "518553900309";
//        String secret = "qWkXMnTpmwhDaAcv9gJRrFxS7fKHtQ";

//        String partnerId = "201700101001";
//        String secret = "123456789";
        dto.setAppid(expressInfo.getAppKey());
        dto.setPartnerId(expressInfo.getBillAccount());
        dto.setSecret(expressInfo.getBillAccountPassword());

        Response<List<CreateBillOrderResultResponse>> response = execute("openapi-api/v1/accountOrder/createBmOrder", dto, new TypeReference<Response<List<CreateBillOrderResultResponse>>>(){});
        List<CreateBillOrderResultResponse> resultList = response.getData();
        CreateBillOrderResultResponse result = resultList.get(0);
        expressOrder.setBillCode(result.getMailNo());
        expressOrder.setPartnerOrderNo(result.getOrderSerialNo());

        CreateOrderDTO createOrderResultDTO = new CreateOrderDTO();
        createOrderResultDTO.setOrderNo(result.getOrderSerialNo());
        createOrderResultDTO.setBillCode(result.getMailNo());

        try {
            ObjectMapper MAPPER = new ObjectMapper();
            JsonNode pdfInfo = MAPPER.readTree(result.getPdfInfo());
            JsonNode label = pdfInfo.get(0).get(0);
            if(label != null) {
                expressOrder.setBagAddr(text(label,"package_wdjc"));
                expressOrder.setMark(text(label,"position") + " " + text(label,"position_no"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return createOrderResultDTO;
    }

    private static String text(JsonNode n, String f) { return n.path(f).asText(""); }

    @Override
    public Boolean cancelOrder(ExpressOrder expressOrder) {
        CancelBillOrderRequest dto = new CancelBillOrderRequest();
        dto.setAppid(expressInfo.getAppKey());
        dto.setPartnerId(expressInfo.getBillAccount());
        dto.setSecret(expressInfo.getBillAccountPassword());
        dto.setOrders(Arrays.asList(new CancelBillOrderRequest.Order()
                .setOrderSerialNo(expressOrder.getPartnerOrderNo())
                .setMailNo(expressOrder.getBillCode())
        ));
        Response<List<CancelBillOrderResultResponse>> response = execute("openapi-api/v1/accountOrder/cancelBmOrder", dto, new TypeReference<Response<List<CancelBillOrderResultResponse>>>(){});
        return response.getResult();
    }

    @Override
    public Boolean intercept(ExpressOrder expressOrder) {
        return null;
    }

    @Override
    public List<TrackDTO> queryTrackBill(ExpressOrder expressOrder) {
        QueryTrackBillRequest dto = new QueryTrackBillRequest();
        dto.setMailNo(expressOrder.getBillCode());
        Response<QueryTrackBillResultResponse> response = execute("openapi-api/v1/accountOrder/queryTrackBill", dto, new TypeReference<Response<QueryTrackBillResultResponse>>(){});
        List<TrackDTO> steps = new ArrayList<>();
        for (QueryTrackBillResultResponse.Step item : response.getData().getSteps()) {
            TrackDTO step = new TrackDTO();
            step.setStatus(item.getStatus());
            step.setDescription(item.getDescription());
            step.setLocation(item.getCity());
            step.setScanDate(String.valueOf(item.getTime().getTime()));
            step.setScanSiteCity(item.getCity());
            steps.add(step);
        }
        return steps;
    }

    @Override
    public List<QueryOrderInfoResultDTO> queryOrderInfo(ExpressOrder expressOrder) {
        return List.of();
    }

    @Override
    public byte[] createPdfBill(ExpressOrder expressOrder) throws Exception {
        PrintInfoDTO printInfo = new PrintInfoDTO();
        PrintInfoDTO.Sender sender = new PrintInfoDTO.Sender();
        sender.setName(expressOrder.getSenderName());
        sender.setMobile(expressOrder.getSenderMobile());
        sender.setProv(expressOrder.getSenderProvince());
        sender.setCity(expressOrder.getSenderCity());
        sender.setCounty(expressOrder.getSenderDistrict());
        sender.setAddress(expressOrder.getSenderAddress());
        printInfo.setSender(sender);
        PrintInfoDTO.Receiver receiver = new PrintInfoDTO.Receiver();
        receiver.setName(expressOrder.getReceiverName());
        receiver.setMobile(expressOrder.getReceiverMobile());
        receiver.setProv(expressOrder.getReceiverProvince());
        receiver.setCity(expressOrder.getReceiverCity());
        receiver.setCounty(expressOrder.getReceiverDistrict());
        receiver.setAddress(expressOrder.getReceiverAddress());
        printInfo.setReceiver(receiver);
        PrintInfoDTO.PrintParam printParam = new PrintInfoDTO.PrintParam();
        printParam.setMailNo(expressOrder.getBillCode());
        printParam.setPrintMark(expressOrder.getMark());
        printParam.setPrintBagaddr(expressOrder.getBagAddr());
        printParam.setRemark(expressOrder.getRemark());
        printInfo.setPrintParam(printParam);
        // 配置产品信息
        printInfo.setGoods(new ArrayList<>());
        if(expressOrder.getOrderItems() != null) {
            List<PrintInfoDTO.Goods> goods = JacksonUtils.toBean(expressOrder.getOrderItems(), new TypeReference<List<PrintInfoDTO.Goods>>() {});
            printInfo.setGoods(goods);
        }
        return PdfCreater.buildPdf(printInfo);
    }
}
