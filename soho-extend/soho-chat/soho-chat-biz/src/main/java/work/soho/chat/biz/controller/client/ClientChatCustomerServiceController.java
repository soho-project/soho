package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.chat.biz.config.ImConfig;
import work.soho.chat.biz.domain.ChatCustomerService;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.service.ChatCustomerServiceService;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.chat.biz.service.ChatSessionUserService;
import work.soho.chat.biz.vo.CustomerServiceSessionVO;
import work.soho.common.core.result.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "客服系统")
@Log4j2
@RestController
@RequestMapping("/chat/chat/customer-service")
@RequiredArgsConstructor
public class ClientChatCustomerServiceController {
    private final ChatCustomerServiceService chatCustomerServiceService;

    private final ChatSessionService chatSessionService;

    private final ChatSessionUserService chatSessionUserService;

    private final ImConfig imConfig;

    @RequestMapping("/session-id")
    public R<CustomerServiceSessionVO> getSessionId(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, Long toUid) {
        log.info("login user: {}", sohoUserDetails);
        log.info(SecurityUtils.getLoginUser());
        Long uid = sohoUserDetails.getId();
        //获取客服信息，验证是否为客服
        LambdaQueryWrapper<ChatCustomerService> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatCustomerService::getUserId, toUid);

        ChatCustomerService chatCustomerService = chatCustomerServiceService.getOne(lambdaQueryWrapper);
        log.info(chatCustomerService);
        Assert.notNull(chatCustomerService, "客服不存在或已经删除");

        //创建会话token
        ChatSession chatSession = chatSessionService.findCustomerServiceSession(uid, toUid);
        if(chatSession == null) {
            //不存在会话，创建会话
            ArrayList<Long> toUids = new ArrayList<>();
            toUids.add(toUid);
            chatSession = chatSessionService.createSession(uid, toUids, ChatSessionEnums.Type.CUSTOMER_SERVICE);
        }
        CustomerServiceSessionVO customerServiceSessionVO = new CustomerServiceSessionVO();
        customerServiceSessionVO.setSessionId(chatSession.getId());
        //检查创建会话
        return R.success(customerServiceSessionVO);
    }

    /**
     * 客服系统配置信息获取接口
     *
     * @return
     */
    @GetMapping(value = "/config")
    public R<Map<String, Object>> getConfig() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(imConfig.AUTO_CHAT, imConfig.isAutoChat());
        return R.success(map);
    }
}
