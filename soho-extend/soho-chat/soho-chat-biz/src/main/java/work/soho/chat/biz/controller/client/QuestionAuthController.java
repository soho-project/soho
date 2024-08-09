package work.soho.chat.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.chat.api.Constants;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Api(tags = "客户端访客接口")
@RestController
@RequestMapping("/guest/chat/auth")
@RequiredArgsConstructor
public class QuestionAuthController {
    /**
     * token密钥
     */
    @Value("${token.secret:defaultValue}")
    private String secret;

    private final ChatUserService chatUserService;

    /**
     * 获取访客TOKEN
     *
     * @param clientId
     * @return
     */
    @GetMapping("/token")
    public R<Map<String,String>> createToken(String clientId) {
        LambdaQueryWrapper<ChatUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUser::getOriginId, clientId);
        lambdaQueryWrapper.eq(ChatUser::getOriginType, Constants.ORIGIN_ROLE_GUEST);
        ChatUser chatUser = chatUserService.getOne(lambdaQueryWrapper);
        if(chatUser == null) {
            chatUser = new ChatUser();
            chatUser.setOriginId(clientId);
            chatUser.setOriginType(Constants.ORIGIN_ROLE_GUEST);
            chatUser.setCreatedTime(LocalDateTime.now());
            chatUser.setUpdatedTime(LocalDateTime.now());
            chatUser.setNickname(IDGeneratorUtils.snowflake().toString());
            chatUser.setUsername(IDGeneratorUtils.snowflake().toString());
            chatUserService.save(chatUser);
        }

        return R.success(chatUserService.getTokenInfoByUserId(chatUser.getId()));
    }
}
