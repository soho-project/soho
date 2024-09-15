package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.admin.api.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 * 聊天用户Controller
 *
 * @author fang
 */
@Api(tags = "聊天用户")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatUser" )
public class ChatUserController {

    private final ChatUserService chatUserService;

    /**
     * 查询聊天用户列表
     */
    @GetMapping("/list")
    @Node(value = "chatUser::list", name = "聊天用户列表")
    public R<PageSerializable<ChatUser>> list(ChatUser chatUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatUser> lqw = new LambdaQueryWrapper<ChatUser>();
        lqw.eq(chatUser.getId() != null, ChatUser::getId ,chatUser.getId());
        lqw.like(StringUtils.isNotBlank(chatUser.getUsername()),ChatUser::getUsername ,chatUser.getUsername());
        lqw.like(StringUtils.isNotBlank(chatUser.getNickname()),ChatUser::getNickname ,chatUser.getNickname());
        lqw.eq(chatUser.getUpdatedTime() != null, ChatUser::getUpdatedTime ,chatUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.like(StringUtils.isNotBlank(chatUser.getOriginType()),ChatUser::getOriginType ,chatUser.getOriginType());
        lqw.eq(chatUser.getOriginId() != null, ChatUser::getOriginId ,chatUser.getOriginId());
        lqw.orderByDesc(ChatUser::getId);
        List<ChatUser> list = chatUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取聊天用户详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatUser::getInfo", name = "聊天用户详细信息")
    public R<ChatUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatUserService.getById(id));
    }

    /**
     * 新增聊天用户
     */
    @PostMapping
    @Node(value = "chatUser::add", name = "聊天用户新增")
    public R<Boolean> add(@RequestBody ChatUser chatUser) {
        chatUser.setCreatedTime(LocalDateTime.now());
        chatUser.setUpdatedTime(LocalDateTime.now());
        return R.success(chatUserService.save(chatUser));
    }

    /**
     * 修改聊天用户
     */
    @PutMapping
    @Node(value = "chatUser::edit", name = "聊天用户修改")
    public R<Boolean> edit(@RequestBody ChatUser chatUser) {
        chatUser.setUpdatedTime(LocalDateTime.now());
        return R.success(chatUserService.updateById(chatUser));
    }

    /**
     * 删除聊天用户
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatUser::remove", name = "聊天用户删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatUserService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 创建Token
     *
     * @param id
     * @return
     */
    @GetMapping("/token")
    public R<Map<String,String>> createToken(Long id) {
        return R.success(chatUserService.getTokenInfoByUserId(id));
    }

    /**
     * 获取管理员用户聊天TOKEN
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/token-from-admin")
    public R<Map<String, String>> loginToken(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        //检查用户是否存在
        LambdaQueryWrapper<ChatUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUser::getOriginId, sohoUserDetails.getId());
        lambdaQueryWrapper.eq(ChatUser::getOriginType, "admin");
        ChatUser chatUser = chatUserService.getOne(lambdaQueryWrapper);
        if(chatUser == null) {
            chatUser = new ChatUser();
            chatUser.setUsername("admin-" + sohoUserDetails.getId());
            chatUser.setNickname("admin-" + sohoUserDetails.getId());
            chatUser.setOriginType("admin");
            chatUser.setOriginId(String.valueOf(sohoUserDetails.getId()));
            chatUser.setAvatar("https://randomuser.me/api/portraits/med/men/32.jpg");
            chatUser.setIntroduction("管理用户");
            chatUser.setUpdatedTime(LocalDateTime.now());
            chatUser.setCreatedTime(LocalDateTime.now());
            chatUserService.save(chatUser);
        }
        return R.success(chatUserService.getTokenInfo(String.valueOf(sohoUserDetails.getId()), "admin"));
    }
}
