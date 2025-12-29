package work.soho.express.biz.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zto.zop.EncryptionType;
import com.zto.zop.ZopClient;
import com.zto.zop.ZopPublicRequest;
import com.zto.zop.dto.*;
import com.zto.zop.request.*;
import com.zto.zop.response.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import work.soho.common.core.util.JacksonUtils;
import work.soho.test.TestApp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
@AutoConfigureMockMvc
class ZtoTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

//    @BeforeEach
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
//                .apply(springSecurity())
//                .build();
//    }

    @Test
    void payList() throws Exception {
        String appKey = "d577e7b5024ad20446e10";
        String appSecret = "0e8e9457d493666ee2f5adb783e69abb";
        ZopClient client = new ZopClient(appKey, appSecret);
        ZopPublicRequest request = new ZopPublicRequest();
        request.setBody("{\"partnerType\":\"1\",\"orderType\":\"1\",\"partnerOrderCode\":\"商家自主定义\",\"accountInfo\":{\"accountId\":\"test\",\"accountPassword\":\"\",\"type\":1,\"customerId\":\"GPG1576724269\"},\"billCode\":\"\",\"senderInfo\":{\"senderId\":\"\",\"senderName\":\"张三\",\"senderPhone\":\"010-22226789\",\"senderMobile\":\"13900000000\",\"senderProvince\":\"上海\",\"senderCity\":\"上海市\",\"senderDistrict\":\"青浦区\",\"senderAddress\":\"华志路\"},\"receiveInfo\":{\"receiverName\":\"Jone Star\",\"receiverPhone\":\"021-87654321\",\"receiverMobile\":\"13500000000\",\"receiverProvince\":\"上海\",\"receiverCity\":\"上海市\",\"receiverDistrict\":\"闵行区\",\"receiverAddress\":\"申贵路1500号\"},\"orderVasList\":[{\"vasType\":\"COD\",\"vasAmount\":100000,\"vasPrice\":0,\"vasDetail\":\"\",\"accountNo\":\"\"}],\"hallCode\":\"S2044\",\"siteCode\":\"02100\",\"siteName\":\"上海\",\"summaryInfo\":{\"size\":\"\",\"quantity\":3,\"price\":30,\"freight\":20,\"premium\":10,\"startTime\":\"2020-12-10 12:00:00\",\"endTime\":\"2020-12-10 12:00:00\"},\"remark\":\"小吉下单\",\"orderItems\":[{\"name\":\"\",\"category\":\"\",\"material\":\"\",\"size\":\"\",\"weight\":0,\"unitprice\":0,\"quantity\":0,\"remark\":\"\"}],\"cabinet\":{\"address\":\"\",\"specification\":0,\"code\":\"\"}}");
        request.setUrl("https://japi-test.zto.com/zto.open.createOrder");
        request.setBase64(true);
        request.setUrl("https://japi-test.zto.com/zto.open.createOrder");
        request.setEncryptionType(EncryptionType.MD5);
        System.out.println(client.execute(request));
        System.out.println("payList");
    }

    @Test
    void testCreateOrder() throws IOException {
        CreateOrderRequest request = new CreateOrderRequest();

        request.setPartnerType("2");
        request.setOrderType("1");
        request.setPartnerOrderCode("商家自主定义");
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
        senderInfo.setSenderName("张三");
        senderInfo.setSenderPhone("010-22226789");
        senderInfo.setSenderMobile("13900000000");
        senderInfo.setSenderProvince("上海");
        senderInfo.setSenderCity("上海市");
        senderInfo.setSenderDistrict("青浦区");
        senderInfo.setSenderAddress("华志路");
        request.setSenderInfo(senderInfo);

        // ===== 收件人信息 =====
        ReceiveInfoDTO receiveInfo = new ReceiveInfoDTO();
        receiveInfo.setReceiverName("Jone Star");
        receiveInfo.setReceiverPhone("021-87654321");
        receiveInfo.setReceiverMobile("13500000000");
        receiveInfo.setReceiverProvince("上海");
        receiveInfo.setReceiverCity("上海市");
        receiveInfo.setReceiverDistrict("闵行区");
        receiveInfo.setReceiverAddress("申贵路1500号");
        request.setReceiveInfo(receiveInfo);

        // ===== 增值服务 =====
        OrderVasDTO vas = new OrderVasDTO();
        vas.setVasType("COD");
        vas.setVasAmount(100000);
        vas.setVasPrice(0);
        vas.setVasDetail("");
        vas.setAccountNo("");
        request.setOrderVasList(Collections.singletonList(vas));

        // ===== 网点信息 =====
        request.setHallCode("S2044");
        request.setSiteCode("02100");
        request.setSiteName("上海");

        // ===== 汇总信息 =====
        SummaryInfoDTO summaryInfo = new SummaryInfoDTO();
        summaryInfo.setSize("");
        summaryInfo.setQuantity(3);
        summaryInfo.setPrice(30);
        summaryInfo.setFreight(20);
        summaryInfo.setPremium(10);
        summaryInfo.setStartTime("2020-12-10 12:00:00");
        summaryInfo.setEndTime("2020-12-10 12:00:00");
        request.setSummaryInfo(summaryInfo);

        // ===== 订单明细 =====
        OrderItemDTO item = new OrderItemDTO();
        item.setName("");
        item.setCategory("");
        item.setMaterial("");
        item.setSize("");
        item.setWeight(0);
        item.setUnitprice(0);
        item.setQuantity(0);
        item.setRemark("");
        request.setOrderItems(Collections.singletonList(item));

        // ===== 智能柜 =====
        CabinetDTO cabinet = new CabinetDTO();
        cabinet.setAddress("");
        cabinet.setSpecification(0);
        cabinet.setCode("");
        request.setCabinet(cabinet);

        // ===== 备注 =====
        request.setRemark("小吉下单");

        String appKey = "d577e7b5024ad20446e10";
        String appSecret = "0e8e9457d493666ee2f5adb783e69abb";
        ZopClient client = new ZopClient(appKey, appSecret);
        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( request));
        publicRequest.setUrl("https://japi-test.zto.com/zto.open.createOrder");
        publicRequest.setBase64(true);
        publicRequest.setUrl("https://japi-test.zto.com/zto.open.createOrder");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        String r = client.execute(publicRequest);
        Response<CreateOrderResultDTO> response = JacksonUtils.toBean(r, Response.class);
        System.out.println(response.getResult());
        System.out.println(response);

//        System.out.println();
    }

    @Test
    public void testQueryOrder() throws IOException {
        QueryOrderInfoRequest request = new QueryOrderInfoRequest();
//        request.setBillCode("73100130019486");
//        request.setOrderCode("250913000004497101");
        request.setOrderCode("251229000012445107");
        request.setType(0);

        String appKey = "d577e7b5024ad20446e10";
        String appSecret = "0e8e9457d493666ee2f5adb783e69abb";
        ZopClient client = new ZopClient(appKey, appSecret);
        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( request));
        publicRequest.setBase64(true);
        publicRequest.setUrl("https://japi-test.zto.com/zto.open.getOrderInfo");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        String r = client.execute(publicRequest);
        System.out.println(r);

        Response<List<QueryOrderInfoResultDTO>> response = JacksonUtils.toBean(r, new TypeReference<Response<List<QueryOrderInfoResultDTO>>>() {});
        System.out.println(response.getResult());
        System.out.println(response);
    }

    @Test
    public void testCreateIntercept() throws IOException {
        CreateInterceptRequest req = new CreateInterceptRequest();

        req.setBillCode("73100130019486");
        req.setRequestId("73100130019486");
        req.setCustomerId("B17087481000");
        req.setCustomerName("客户名称");
        req.setReceivePhone("13000000000");
        req.setReceiveUsername("张三");
        req.setReceiveAddress("华新镇华志路1685号");
        req.setReceiveDistrict("青浦区");
        req.setReceiveCity("上海市");
        req.setReceiveProvince("上海");
        req.setDestinationType(3);
        req.setThirdBizNo("外部业务唯一标识");

        String appKey = "d577e7b5024ad20446e10";
        String appSecret = "0e8e9457d493666ee2f5adb783e69abb";
        ZopClient client = new ZopClient(appKey, appSecret);
        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( req));
        publicRequest.setBase64(true);
        publicRequest.setUrl("https://japi-test.zto.com/thirdcenter.createIntercept");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        String r = client.execute(publicRequest);
        System.out.println(r);
        Response<CreateInterceptResultDTO> response = JacksonUtils.toBean(r, Response.class);
        System.out.println(response.getData());
        System.out.println(response);
    }

    @Test
    public void testQueryInterceptAndReturnStatus() throws IOException {
        QueryInterceptAndReturnStatusRequest req = new QueryInterceptAndReturnStatusRequest();
        req.setBillCode("73100130019486");

        String appKey = "d577e7b5024ad20446e10";
        String appSecret = "0e8e9457d493666ee2f5adb783e69abb";
        ZopClient client = new ZopClient(appKey, appSecret);
        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( req));
        publicRequest.setBase64(true);
        publicRequest.setUrl("https://japi-test.zto.com/thirdcenter.queryInterceptAndReturnStatus");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        String r = client.execute(publicRequest);
        System.out.println(r);
        Response<QueryInterceptAndReturnStatusDTO> response = JacksonUtils.toBean(r, Response.class);
        System.out.println(response.getData());
        System.out.println(response);
    }

    @Test
    public void testQueryTrackBillRequest() throws IOException {
        QueryTrackBillRequest req = new QueryTrackBillRequest();
        req.setBillCode("73100130019486");

        String appKey = "d577e7b5024ad20446e10";
        String appSecret = "0e8e9457d493666ee2f5adb783e69abb";
        ZopClient client = new ZopClient(appKey, appSecret);
        ZopPublicRequest publicRequest = new ZopPublicRequest();
        publicRequest.setBody(JacksonUtils.toJson( req));
        publicRequest.setBase64(true);
        publicRequest.setUrl("https://japi-test.zto.com/zto.merchant.waybill.track.query");
        publicRequest.setEncryptionType(EncryptionType.MD5);

        String r = client.execute(publicRequest);
        System.out.println(r);
        Response<List<ScanTraceDTO>> response = JacksonUtils.toBean(r, new TypeReference<Response<List<ScanTraceDTO>>>() {});
        System.out.println(response.getResult());
//        System.out.println(response);

        response.getResult().forEach(System.out::println);
    }
}