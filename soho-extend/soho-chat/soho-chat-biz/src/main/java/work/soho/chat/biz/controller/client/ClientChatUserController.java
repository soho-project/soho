package work.soho.chat.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.Constants;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.chat.biz.vo.DisplayUserVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.upload.utils.UploadUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat/chat/chatUser")
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
            //不做文件扩展名验证
            //TODO 配置正确的文件路径
            String url = UploadUtils.upload("user/avatar/"+sohoUserDetails.getId(), file);
            return R.success(url);
        } catch (Exception ioException) {
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }
}
