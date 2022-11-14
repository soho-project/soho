package work.soho.pay.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.pay.api.dto.OrderDetailsDto;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.pay.biz.service.PayOrderService;
import work.soho.pay.biz.vo.ClientPayInfoVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/client/api/pay" )
public class ClientApiPayController {
    private final PayInfoService payInfoService;
    private final PayOrderService payOrderService;

    /**
     * 客户端获取支付方式列表
     *
     * @param clientType
     * @return
     */
    @GetMapping("list")
    public R<List<ClientPayInfoVo>> payList(Integer clientType) {
        LambdaQueryWrapper<PayInfo> payInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        payInfoLambdaQueryWrapper.eq(PayInfo::getClientType, clientType);
        payInfoLambdaQueryWrapper.eq(PayInfo::getStatus, 1);
        List<PayInfo> list = payInfoService.list(payInfoLambdaQueryWrapper);
        return R.success(BeanUtils.copyList(list, ClientPayInfoVo.class));
    }
}
