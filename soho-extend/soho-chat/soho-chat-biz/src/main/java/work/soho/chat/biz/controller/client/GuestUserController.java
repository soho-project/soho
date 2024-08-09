package work.soho.chat.biz.controller.client;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.req.LoginReq;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.common.core.result.R;

import java.time.LocalDateTime;
import java.util.Map;

@Api(tags = "客户端游客用户")
@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/guest/chat/user")
public class GuestUserController {
    private final ChatUserService chatUserService;

    /**
     * 聊天用户登录
     *
     * @return
     */
    @PostMapping("/login")
    public R<Map<String,String>> login(@RequestBody LoginReq loginReq) {
        //获取登录用户
        Map<String, String> tokenMap = chatUserService.login(loginReq.getId(), loginReq.getPassword(), loginReq.getClientId());
        return R.success(tokenMap);
    }

    /**
     * 创建注册用户
     *
     * @param chatUser
     * @return
     */
    @PostMapping("/register")
    public R<Boolean> create(@RequestBody ChatUser chatUser) {
        ChatUser exitsUser = chatUserService.getByUsername(chatUser.getUsername());
        if(exitsUser != null) {
            return R.error("用户已经存在");
        }
        chatUser.setCreatedTime(LocalDateTime.now());
        chatUser.setUpdatedTime(LocalDateTime.now());
        chatUser.setNickname(chatUser.getUsername());
        chatUser.setOriginType("chat");
        //set default avatar
        chatUser.setAvatar("https://randomuser.me/api/portraits/med/men/32.jpg");
        chatUser.setPassword(new BCryptPasswordEncoder().encode(chatUser.getPassword()));
        chatUserService.save(chatUser);
        return R.success();
    }

    @GetMapping("/test")
    public String test() {
        return "hello";
    }
}
