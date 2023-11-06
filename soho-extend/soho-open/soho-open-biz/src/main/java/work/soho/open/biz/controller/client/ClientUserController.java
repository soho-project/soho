package work.soho.open.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.service.impl.TokenServiceImpl;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.chat.biz.vo.BaseUserVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.open.api.req.GetCodeReq;
import work.soho.open.api.req.GetTokenReq;
import work.soho.open.api.req.RegisterUserReq;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.domain.OpenCode;
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.service.OpenAppService;
import work.soho.open.biz.service.OpenCodeService;
import work.soho.open.biz.service.OpenUserService;
import work.soho.open.biz.utils.AuthUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client/api/open/user")
public class ClientUserController {
    private final ChatUserService chatUserService;

    private final OpenAppService openAppService;

    private final TokenServiceImpl tokenService;

    private final OpenCodeService openCodeService;

    private final OpenUserService openUserService;

    /**
     * 获取聊天token
     *
     * @return
     */
    @PostMapping("/createCode")
    public R<String> createChatCode(@RequestBody GetCodeReq getCodeReq) throws Exception {
        OpenApp openApp = openAppService.getById(getCodeReq.getAppId());
        Assert.notNull(openApp, "app不存在");
        //检查签名
        if(!AuthUtil.verifyMD5Signature(getCodeReq, openApp.getAppKey())) {
            return R.error("签名错误");
        }

        LambdaQueryWrapper<OpenUser> openUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        openUserLambdaQueryWrapper.eq(OpenUser::getOriginId, getCodeReq.getBody().getUid())
                .eq(OpenUser::getAppId, openApp.getId());
        OpenUser openUser = openUserService.getOne(openUserLambdaQueryWrapper);
        Assert.notNull(openUser, "用户不存在");

        ChatUser chatUser = chatUserService.getById(openUser.getUid());
        OpenCode openCode = new OpenCode();
        openCode.setCode(UUID.randomUUID().toString());
        openCode.setUid(chatUser.getId());
        openCode.setOriginUid(getCodeReq.getBody().getUid());
        openCode.setCreatedTime(LocalDateTime.now());
        openCode.setUpdatedTime(LocalDateTime.now());
        openCodeService.save(openCode);
        return R.success(openCode.getCode());
    }

    /**
     * 注册用户
     *
     * @param registerUserReq
     * @return
     * @throws Exception
     */
    @PostMapping("/registerUser")
    public R<BaseUserVO> registerUser(@RequestBody RegisterUserReq registerUserReq) throws Exception {
        OpenApp openApp = openAppService.getById(registerUserReq.getAppId());
        Assert.notNull(openApp, "app不存在");
        //检查签名
        if(!AuthUtil.verifyMD5Signature(registerUserReq, openApp.getAppKey())) {
            return R.error("签名错误");
        }

        //检查用户是否存在
        LambdaQueryWrapper<OpenUser> openUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        openUserLambdaQueryWrapper.eq(OpenUser::getAppId, openApp.getId());
        openUserLambdaQueryWrapper.eq(OpenUser::getOriginId, registerUserReq.getBody().getOriginId());
        OpenUser openUser = openUserService.getOne(openUserLambdaQueryWrapper);
        if(openUser != null) {
            return R.error("用户已经存在");
        }

        //检查传递主要参数，确定用户别的app是否已经上传
        LambdaQueryWrapper<ChatUser> chatUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatUserLambdaQueryWrapper.eq(ChatUser::getUsername, registerUserReq.getBody().getUsername())
                .or().eq(ChatUser::getPhone, registerUserReq.getBody().getPhone())
                .or().eq(ChatUser::getEmail, registerUserReq.getBody().getEmail());
        ChatUser chatUser = chatUserService.getOne(chatUserLambdaQueryWrapper);
        if(chatUser == null) {
            chatUser = new ChatUser();
            chatUser.setUsername(openApp.getCode() + "_" + registerUserReq.getBody().getUsername());
            chatUser.setNickname(registerUserReq.getBody().getNickname());
            chatUser.setPhone(registerUserReq.getBody().getPhone());
            chatUser.setEmail(registerUserReq.getBody().getEmail());
            chatUser.setAge(registerUserReq.getBody().getAge());
            chatUser.setOriginId(registerUserReq.getBody().getOriginId());
            chatUser.setOriginType(openApp.getCode());
        }

        openUser = new OpenUser();
        openUser.setUid(chatUser.getId());
        openUser.setOriginId(registerUserReq.getBody().getOriginId());
        openUser.setAppId(openApp.getId());
        openUser.setUpdatedTime(LocalDateTime.now());
        openUser.setCreateTime(LocalDateTime.now());
        openUserService.save(openUser);

        chatUserService.save(chatUser);
        BaseUserVO baseUserVO = BeanUtils.copy(chatUser, BaseUserVO.class);
        return R.success(baseUserVO);
    }

    /**
     * 获取聊天用户的token
     *
     * @param getTokenReq
     * @return
     */
    @PostMapping("/createToken")
    private R<Map<String,String>> getChatToken(@RequestBody GetTokenReq getTokenReq) {
        LambdaQueryWrapper<OpenCode> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OpenCode::getCode, getTokenReq.getCode());
        OpenCode openCode = openCodeService.getOne(lambdaQueryWrapper);
        //验证code有效性
        ChatUser chatUser = chatUserService.getById(openCode.getUid());
        SohoUserDetails sohoUserDetails = new SohoUserDetails();
        sohoUserDetails.setId(chatUser.getId());
        sohoUserDetails.setUsername(chatUser.getUsername());
        sohoUserDetails.setAuthorities(AuthorityUtils.createAuthorityList("chat"));
        Map<String, String> tokenMap = tokenService.createTokenInfo(sohoUserDetails);
        return R.success(tokenMap);
    }
}
