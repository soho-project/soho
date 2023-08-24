package work.soho.chat.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.Constants;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat/chat/chatUser")
@RequiredArgsConstructor
public class ClientChatUserController {

    private final ChatUserService chatUserService;

//    private final ChatSessionUser cha;

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

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @GetMapping()
    public R<ChatUser> userInfo(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        return R.success(chatUserService.getById(sohoUserDetails.getId()));
    }

    /**
     * 获取好友列表
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/getFriendList")
    public R<HashMap> friendList(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        //TODO 获取所有私聊会话ID

        //TODO 获取好友用户信息

        return null;
    }

    /**
     * 获取群组列表
     */
    @GetMapping("/getGroupList")
    public R<HashMap> groupList(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {

        return null;
    }
}
