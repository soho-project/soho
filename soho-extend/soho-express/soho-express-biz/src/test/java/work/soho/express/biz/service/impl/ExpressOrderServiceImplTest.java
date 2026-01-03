package work.soho.express.biz.service.impl;

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
import work.soho.express.api.dto.OrderItem;
import work.soho.express.api.dto.SimpleExpressOrderDTO;
import work.soho.test.TestApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
@AutoConfigureMockMvc
class ExpressOrderServiceImplTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ExpressOrderServiceImpl expressOrderService;

    @Test
    void createExpressOrder() {
        SimpleExpressOrderDTO simpleExpressOrderDTO = new SimpleExpressOrderDTO();
        simpleExpressOrderDTO.setTrackingNo(IDGeneratorUtils.snowflake().toString());
        simpleExpressOrderDTO.setPartnerOrderNo("1234567890");
        simpleExpressOrderDTO.setReceiverName("张三");
        simpleExpressOrderDTO.setReceiverPhone("13800000000");
        simpleExpressOrderDTO.setReceiverMobile("13800000000");
        simpleExpressOrderDTO.setReceiverProvince("广东省");
        simpleExpressOrderDTO.setReceiverCity("广州市");
        simpleExpressOrderDTO.setReceiverDistrict("天河区");
        simpleExpressOrderDTO.setReceiverAddress("天河路");
        simpleExpressOrderDTO.setSummaryInfo("商品信息");
        // 发件人信息
        simpleExpressOrderDTO.setSenderName("张三");
        simpleExpressOrderDTO.setSenderPhone("13800000000");
        simpleExpressOrderDTO.setSenderMobile("13800000000");
        simpleExpressOrderDTO.setSenderProvince("广东省");
        simpleExpressOrderDTO.setSenderCity("广州市");
        simpleExpressOrderDTO.setSenderDistrict("天河区");
        simpleExpressOrderDTO.setSenderAddress("天河路");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem()
                .setName("商品1")
                .setQty(1)
                .setWeight(BigDecimal.valueOf(0.5))
//                        .setFreight(1)
                .setRemark("商品1备注"));
        simpleExpressOrderDTO.setOrderItems(items);

        Boolean result = expressOrderService.createExpressOrder(simpleExpressOrderDTO);
        System.out.println( result);
    }
}