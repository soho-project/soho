package work.soho.chat.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/client/api/chatUser")
@RequiredArgsConstructor
public class ClientChatUserController {

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
        ChatUser chatUser = chatUserService.getOne(lambdaQueryWrapper);
        if(chatUser == null) {
            chatUser = new ChatUser();
            chatUser.setOriginId(clientId);
            chatUser.setOriginType(Constants.ROLE_NAME);
            chatUser.setCreatedTime(LocalDateTime.now());
            chatUser.setUpdatedTime(LocalDateTime.now());
            chatUser.setNickname(IDGeneratorUtils.snowflake().toString());
            chatUser.setUsername(IDGeneratorUtils.snowflake().toString());
            chatUserService.save(chatUser);
        }

        return R.success(chatUserService.getTokenInfoByUserId(chatUser.getId()));
    }
}
