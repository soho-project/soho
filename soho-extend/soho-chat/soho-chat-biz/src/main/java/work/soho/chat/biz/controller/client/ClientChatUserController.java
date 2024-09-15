package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.admin.api.service.EmailApiService;
import work.soho.admin.api.service.SmsApiService;
import work.soho.chat.api.Constants;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.req.UpdateEmailReq;
import work.soho.chat.biz.req.UpdatePasswordReq;
import work.soho.chat.biz.req.UpdatePhoneReq;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.chat.biz.vo.DisplayUserVO;
import work.soho.common.core.result.R;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.longlink.api.sender.QueryLongLink;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Api(tags = "客户端用户接口")
@RestController
@Log4j2
@RequestMapping("/chat/chat/chatUser")
@RequiredArgsConstructor
public class ClientChatUserController {

    private final ChatUserService chatUserService;

    private final QueryLongLink queryLongLink;

    private final EmailApiService emailApiService;

    private final SmsApiService smsApiService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
        ChatUser chatUser = chatUserService.getById(sohoUserDetails.getId());
        chatUser.setPassword(null);
        return R.success(chatUser);
    }

    /**
     * 更新聊天用户信息
     *
     * @param sohoUserDetails
     * @param chatUser
     * @return
     */
    @PutMapping()
    public R<Boolean> updateUser(@AuthenticationPrincipal SohoUserDetails sohoUserDetails,@RequestBody ChatUser chatUser) {
        chatUser.setId(sohoUserDetails.getId());
        //检查配置密码
        if(StringUtils.isNotEmpty(chatUser.getPassword())) {
            chatUser.setPassword(new BCryptPasswordEncoder().encode(chatUser.getPassword()));
        }
        //TODO 处理邮箱，手机号更改
        chatUserService.updateById(chatUser);

        //TODO 更新会话相关信息
        SpringContextHolder.getApplicationContext().publishEvent(chatUser);

        return R.success(true);
    }

    /**
     * 获取其他用户详情
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/displayUser")
    public R<DisplayUserVO> displayUser(Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatUser chatUser = chatUserService.getById(id);
        DisplayUserVO displayUserVO = BeanUtils.copy(chatUser, DisplayUserVO.class);
        return R.success(displayUserVO);
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

    /**
     * 上传头像
     *
     * @param file
     * @return
     */
    @PostMapping("/avatar")
    public R<String> upload(@RequestParam(value = "upload") MultipartFile file, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
            if(!mimeType.getType().equals("image")) {
                return R.error("请传递正确的图片格式");
            }
            //TODO 更新删除原有头像
            //不做文件扩展名验证
            //TODO 配置正确的文件路径
            String url = UploadUtils.upload("user/avatar/"+sohoUserDetails.getId(), file);
            return R.success(url);
        } catch (Exception ioException) {
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }

    /**
     * 验证手机号
     *
     * @param chatUser
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/authPhone")
    public R<Boolean> authPhone(@RequestBody ChatUser chatUser, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) throws Exception {
        try {
            Assert.notNull(chatUser.getPhone(), "请传递手机号");
            HashMap<String, String> map  = new HashMap<>();
            Random random = new Random();
            Integer code = random.nextInt(8999) + 1000;
            redisTemplate.opsForValue().set("phone:" + chatUser.getPhone(), code);
            map.put("code", String.valueOf(code));
            smsApiService.sendSms(chatUser.getPhone(), "code", map);
            return R.success(true);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新手机号码
     *
     * @param updatePhoneReq
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/updatePhone")
    public R<Boolean> updatePhone(@RequestBody UpdatePhoneReq updatePhoneReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            Assert.notNull(updatePhoneReq.getPhone(), "手机号不能为空");
            Assert.notNull(updatePhoneReq.getCode(), "认证码不能为空");
            ChatUser chatUser = chatUserService.getById(sohoUserDetails.getId());
            if(updatePhoneReq.getPhone().equals(chatUser.getPhone())) {
                return R.success(true);
            }
            //验证code
            Integer storeCode = (Integer) redisTemplate.opsForValue().get("phone:" + updatePhoneReq.getPhone());
            log.info("验证码{},{}",storeCode, updatePhoneReq.getCode());
            if(storeCode.equals(updatePhoneReq.getCode())) {
                chatUser.setPhone(updatePhoneReq.getPhone());
                chatUser.setUpdatedTime(LocalDateTime.now());
                chatUserService.updateById(chatUser);
                return R.success(true);
            } else {
                return R.error("验证码错误");
            }
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/authEmail")
    public R<Boolean> authEmail(@RequestBody ChatUser chatUser, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            Assert.notNull(chatUser.getEmail(), "邮箱不能为空");
            Random random = new Random();
            Integer code = random.nextInt(8999) + 1000;
            redisTemplate.opsForValue().set("email:" + chatUser.getEmail(), code);
//            //TODO 检查邮箱是否重复
            Map<String, Object> model = new HashMap<>();
            model.put("code", code);
            emailApiService.sendEmail(chatUser.getEmail(), "code", model);

            return R.success(true);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新用户手机号
     *
     * @param updateEmailReq
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/updateEmail")
    public R<Boolean> updateEmail(@RequestBody UpdateEmailReq updateEmailReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            Assert.notNull(updateEmailReq.getEmail(), "邮箱不能为空");
            Assert.notNull(updateEmailReq.getCode(), "认证码不能为空");
            ChatUser chatUser = chatUserService.getById(sohoUserDetails.getId());
            Integer code  = (Integer) redisTemplate.opsForValue().get("email:" + updateEmailReq.getEmail());
            Assert.notNull(code, "请重新发送验证码");
            log.info("验证码,{},{}", code, updateEmailReq.getCode());
            if(code.equals(updateEmailReq.getCode())) {
                chatUser.setEmail(updateEmailReq.getEmail());
                chatUser.setUpdatedTime(LocalDateTime.now());
                chatUserService.updateById(chatUser);
                return R.success(true);
            } else {
                return R.error("验证码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新用户密码
     *
     * @param updatePasswordReq
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/updatePassword")
    public R<Boolean> updatePassword(@RequestBody UpdatePasswordReq updatePasswordReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            Assert.notNull(updatePasswordReq.getOldPassword(), "原密码不能为空");
            Assert.notEquals(updatePasswordReq.getPassword(), "新密码不能为空");
            ChatUser chatUser = chatUserService.getById(sohoUserDetails.getId());

            //检查老密码是否正确
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            if(bCryptPasswordEncoder.encode(updatePasswordReq.getOldPassword()).equals(sohoUserDetails.getPassword())) {
                chatUser.setPhone(bCryptPasswordEncoder.encode(updatePasswordReq.getPassword()));
                chatUser.setUpdatedTime(LocalDateTime.now());
                chatUserService.updateById(chatUser);
            }
            return R.success(true);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取用户当前状态
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/onlineStatus")
    public R<Integer> onlineStatus(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ArrayList<String> uids = new ArrayList<>();
        uids.add(String.valueOf(sohoUserDetails.getId()));
        Map<String, Integer> status = queryLongLink.getOnlineStatus(uids);
        return R.success(status.get(String.valueOf(sohoUserDetails.getId())));
    }
}
