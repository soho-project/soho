package work.soho.express.biz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yundasys.request.CreateBillOrderRequest;
import com.yundasys.utils.OpenApiHttpUtils;
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
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.express.biz.apis.adapter.yunda.PdfCreater;
import work.soho.test.TestApp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
@AutoConfigureMockMvc
public class YundaTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testCreatePdfBill() throws Exception {
//        String apiRespJson = Files.readString(Path.of("resp.json"));
//        String apiRespJson = "{\"code\":\"0000\",\"message\":\"请求成功\",\"result\":true,\"data\":[{\"order_serial_no\":\"0000019876576897\",\"pdf_info\":\"[[{\"order_id\":\"5160533654\",\"order_serial_no\":\"test_191021002\",\"partner_id\":\"2017001068\",\"partner_orderid\":\"test_191021002\",\"order_type\":\"common\",\"mailno\":\"4060005469862\",\"customer_id\":\"\",\"sender_name\":\"\\u738b\\u5c0f\\u864e\",\"sender_company\":\"\\u51ef\\u5229\",\"sender_area_ids\":\"\",\"sender_area_names\":\"\\u6c5f\\u82cf\\u7701\\uff0c\\u5f90\\u5dde\\u5e02\\uff0c\\u65b0\\u6c82\\u5e02\",\"sender_address\":\"\\u6e56\\u4e1c\\u8def999\\u53f7\",\"sender_postcode\":\"221435\",\"sender_phone\":\"021-85926525\",\"sender_mobile\":\"13761960078\",\"sender_branch\":\"201700\",\"receiver_name\":\"\\u9646\\u5927\\u6709\",\"receiver_company\":\"\\u5343\\u5343\",\"receiver_area_ids\":\"310118\",\"receiver_area_names\":\"\\u4e0a\\u6d77\\u5e02,\\u4e0a\\u6d77\\u5e02,\\u9752\\u6d66\\u533a\",\"receiver_address\":\"\\u4e0a\\u6d77\\u5e02\\u9752\\u6d66\\u533a\\u76c8\\u6e2f\\u4e1c\\u8def6633\\u53f7\",\"receiver_postcode\":\"201700\",\"receiver_phone\":\"020-57720341\",\"receiver_mobile\":\"13761960075\",\"receiver_branch\":\"200230\",\"weight\":\"11.00\",\"remark\":\"\",\"status\":\"rs10\",\"time\":\"2019-10-2111:16:26\",\"position_no\":\"G096-0020\",\"position_zz\":\"0\",\"options\":\"\",\"send_num\":\"0\",\"nb_ckh\":\"2001123\",\"cus_area1\":\"\\u8ba2\\u5355\\u53f7:test_191021002\\n\\u8ba2\\u5355\\u53f7\\uff1a123\\n\\u6279\\u6b21\\u53f7\\uff1a456212\",\"cus_area2\":\"\",\"position\":\"300\",\"receiver_flag\":\"1\",\"package_wd\":\"J200000\",\"callback_id\":\"\",\"wave_no\":\"\",\"node_id\":\"\",\"ems_flag\":\"\",\"cus_area3\":\"\",\"trade_code\":\"\",\"shi1\":null,\"sheng1\":null,\"shi2\":\"310100\",\"sheng2\":\"310000\",\"collection_value\":\"100.00\",\"value\":\"20.00\",\"zffs\":\"0\",\"innerProvinceName\":\"\\u7701\\u5185\\u4ef6\",\"package_wdjc\":\"\\u96c6\\u5305\\u5730\\uff1a\\u4e0a\\u6d77\\u5206\\u62e8\\u5305\",\"sender_branch_jc\":\"\\u9752\\u6d66\\u533aYD\",\"bigpen_code\":\"G096-00\",\"lattice_mouth_no\":\"20\",\"mailno_barcode\":\"406000546986204240\",\"tname\":\"mailtmp_s12\",\"dispatch_code\":\"20\",\"qrcode\":\"4060005469862\\/300G096-0020\",\"privacy_receiver_name\":\"\\u9646**\",\"privacy_receiver_phone\":\"020-****0341\",\"privacy_receiver_mobile\":\"137****0075\"},[\"0424\",0]]]\",\"mail_no\":\"5300010219368\",\"status\":\"1\",\"remark\":null,\"msg\":\"订单创建成功\",\"orderId\":\"9876576897\"}]}";
//        JsonNode root = MAPPER.readTree(apiRespJson);
//

//        String pdfInfoRaw = "[[{\"order_id\":\"5160533654\",\"order_serial_no\":\"test_191021002\"} ,[\"0424\",0]]]";
//        JsonNode pdfInfoNode = MAPPER.readTree(pdfInfoRaw);
//
//        String pdfInfoStr = root.path("data").get(0).path("pdf_info").asText();

        String pdfInfoStr = "[[{\"order_id\":\"5160533654\",\"order_serial_no\":\"test_191021002\",\"partner_id\":\"2017001068\",\"partner_orderid\":\"test_191021002\",\"order_type\":\"common\",\"mailno\":\"4060005469862\",\"customer_id\":\"\",\"sender_name\":\"\\u738b\\u5c0f\\u864e\",\"sender_company\":\"\\u51ef\\u5229\",\"sender_area_ids\":\"\",\"sender_area_names\":\"\\u6c5f\\u82cf\\u7701\\uff0c\\u5f90\\u5dde\\u5e02\\uff0c\\u65b0\\u6c82\\u5e02\",\"sender_address\":\"\\u6e56\\u4e1c\\u8def999\\u53f7\",\"sender_postcode\":\"221435\",\"sender_phone\":\"021-85926525\",\"sender_mobile\":\"13761960078\",\"sender_branch\":\"201700\",\"receiver_name\":\"\\u9646\\u5927\\u6709\",\"receiver_company\":\"\\u5343\\u5343\",\"receiver_area_ids\":\"310118\",\"receiver_area_names\":\"\\u4e0a\\u6d77\\u5e02,\\u4e0a\\u6d77\\u5e02,\\u9752\\u6d66\\u533a\",\"receiver_address\":\"\\u4e0a\\u6d77\\u5e02\\u9752\\u6d66\\u533a\\u76c8\\u6e2f\\u4e1c\\u8def6633\\u53f7\",\"receiver_postcode\":\"201700\",\"receiver_phone\":\"020-57720341\",\"receiver_mobile\":\"13761960075\",\"receiver_branch\":\"200230\",\"weight\":\"11.00\",\"remark\":\"\",\"status\":\"rs10\",\"time\":\"2019-10-21 11:16:26\",\"position_no\":\"G096-00 20\",\"position_zz\":\"0\",\"options\":\"\",\"send_num\":\"0\",\"nb_ckh\":\"2001123\",\"cus_area1\":\"\\u8ba2\\u5355\\u53f7:test_191021002\\n\\u8ba2\\u5355\\u53f7\\uff1a123 \\n\\u6279\\u6b21\\u53f7\\uff1a456212\",\"cus_area2\":\"\",\"position\":\"300\",\"receiver_flag\":\"1\",\"package_wd\":\"J200000\",\"callback_id\":\"\",\"wave_no\":\"\",\"node_id\":\"\",\"ems_flag\":\"\",\"cus_area3\":\"\",\"trade_code\":\"\",\"shi1\":null,\"sheng1\":null,\"shi2\":\"310100\",\"sheng2\":\"310000\",\"collection_value\":\"100.00\",\"value\":\"20.00\",\"zffs\":\"0\",\"innerProvinceName\":\"\\u7701\\u5185\\u4ef6\",\"package_wdjc\":\"\\u96c6\\u5305\\u5730\\uff1a\\u4e0a\\u6d77\\u5206\\u62e8\\u5305  \",\"sender_branch_jc\":\"\\u9752\\u6d66\\u533aYD\",\"bigpen_code\":\"G096-00\",\"lattice_mouth_no\":\"20\",\"mailno_barcode\":\"406000546986204240\",\"tname\":\"mailtmp_s12\",\"dispatch_code\":\"20\",\"qrcode\":\"4060005469862\\/300 G096-00 20\",\"privacy_receiver_name\":\"\\u9646**\",\"privacy_receiver_phone\":\"020-****0341\",\"privacy_receiver_mobile\":\"137****0075\"},[\"0424\",0]]]";
        JsonNode pdfInfo = MAPPER.readTree(pdfInfoStr);

        // 你返回结构：[[ {fields...}, ["0424",0] ]]
        JsonNode label = pdfInfo.get(0).get(0);

        System.out.println(label);

        byte[] pdf = PdfCreater.buildPdf(label);
        Files.write(Path.of("yunda_76x130.pdf"), pdf);
        System.out.println("OK: yunda_76x130.pdf");
    }

    @Test
    public void testCreateBillOrder() {
        CreateBillOrderRequest dto = new CreateBillOrderRequest();
//        dto.setAppid("999999");
//        dto.setPartner_id("201700101001");
//        dto.setSecret("123456789");

        // 创建订单
        Long id = IDGeneratorUtils.snowflake().longValue();
        CreateBillOrderRequest.Order order = new CreateBillOrderRequest.Order();
        order.setOrder_serial_no(id);
        order.setKhddh(id);
        order.setNode_id("350");
        order.setOrder_type("common");
        order.setCollection_value(126.5);
        order.setValue(126.5);
        order.setSize("0.12,0.23,0.11");
        order.setWeight(0.0);
        order.setSpecial(0);
        order.setRemark("");

        // 创建收件人
        CreateBillOrderRequest.Receiver receiver = new CreateBillOrderRequest.Receiver();
        receiver.setName("李四");
        receiver.setMobile("17601206977");
        receiver.setProvince("上海市");
        receiver.setCity("上海市");
        receiver.setCounty("青浦区");
        receiver.setAddress("上海市，青浦区，盈港东路 6679 号");
        order.setReceiver(receiver);

        // 创建寄件人
        CreateBillOrderRequest.Sender sender = new CreateBillOrderRequest.Sender();
        sender.setName("张三");
        sender.setMobile("17601206977");
        sender.setProvince("上海市");
        sender.setCity("上海市");
        sender.setCounty("青浦区");
        sender.setAddress("上海市，青浦区，盈港东路 7766 号");
        order.setSender(sender);

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
        CreateBillOrderRequest.MarkingInfo insured = new CreateBillOrderRequest.MarkingInfo();
        insured.setType("INSURED");
        CreateBillOrderRequest.MarkingValue insuredValue = new CreateBillOrderRequest.MarkingValue();
        insuredValue.setValue(2100);
        insured.setMarkingValue(insuredValue);

        CreateBillOrderRequest.MarkingInfo df = new CreateBillOrderRequest.MarkingInfo();
        df.setType("DF");
        CreateBillOrderRequest.MarkingValue dfValue = new CreateBillOrderRequest.MarkingValue();
        dfValue.setValue(15);
        df.setMarkingValue(dfValue);

        CreateBillOrderRequest.MarkingInfo cod = new CreateBillOrderRequest.MarkingInfo();
        cod.setType("COD");
        CreateBillOrderRequest.MarkingValue codValue = new CreateBillOrderRequest.MarkingValue();
        codValue.setValue(15);
        cod.setMarkingValue(codValue);

        CreateBillOrderRequest.MarkingInfo returnMark = new CreateBillOrderRequest.MarkingInfo();
        returnMark.setType("RETURN");
        CreateBillOrderRequest.MarkingValue returnValue = new CreateBillOrderRequest.MarkingValue();
        returnValue.setValue("1,2");
        returnMark.setMarkingValue(returnValue);

        CreateBillOrderRequest.MarkingInfo yxz = new CreateBillOrderRequest.MarkingInfo();
        yxz.setType("YXZ");

        CreateBillOrderRequest.MarkingInfo mul = new CreateBillOrderRequest.MarkingInfo();
        mul.setType("MUL");
//        CreateBillOrderRequest.MarkingValue mulValue = new CreateBillOrderRequest.MarkingValue();
//        mulValue.setValue(1);
//        mul.setMarkingValue(mulValue);

        CreateBillOrderRequest.MarkingInfo contact = new CreateBillOrderRequest.MarkingInfo();
        contact.setType("CONTACT");

        order.setMarkingInfos(Arrays.asList(insured, df, cod, returnMark, yxz, contact));

        dto.setOrders(Arrays.asList(order));
        System.out.println(dto);

        String url = "https://u-openapi.yundasys.com/openapi-api/v1/accountOrder/createBmOrder";
        String appKey = "999999";
        String appSecret = "04d4ad40eeec11e9bad2d962f53dda9d";
        String partnerId = "529951202001";
        String secret = "Y4TQ3WBar9hpnw7As8xUZEReSuDdf2";
        dto.setAppid(appKey);
        dto.setPartner_id(partnerId);
        dto.setSecret(secret);

        System.out.println(JacksonUtils.toJson(dto));

        String r = OpenApiHttpUtils.doPostJson(url, JacksonUtils.toJson(dto), appKey, appSecret);
        System.out.println(r);
    }
}
