package work.soho.chat.biz.controller.client;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.service.AdminConfigApiService;
import work.soho.chat.biz.domain.ChatUserEmote;
import work.soho.chat.biz.service.ChatUserEmoteService;
import work.soho.common.core.result.R;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chat/chat/chatUserEmote")
@RequiredArgsConstructor
public class ClientChatUserEmoteController {

    private final ChatUserEmoteService chatUserEmoteService;

    private final AdminConfigApiService adminConfigApiService;

    /**
     * 用户表情列表
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/list")
    public R<List<ChatUserEmote>> list(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        Integer emoteCountLimit = adminConfigApiService.getByKey("chat-user-emote-limit", Integer.class, 100);
        LambdaQueryWrapper<ChatUserEmote> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserEmote::getUid, sohoUserDetails.getId());
        lambdaQueryWrapper.last("limit " + emoteCountLimit);
        return R.success(chatUserEmoteService.list(lambdaQueryWrapper));
    }

    /**
     * 添加用户表情
     *
     * @param chatUserEmote
     * @param sohoUserDetails
     * @return
     */
    @PostMapping
    public R<Boolean> create(@RequestBody ChatUserEmote chatUserEmote, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        chatUserEmote.setUid(sohoUserDetails.getId());
        chatUserEmote.setCreatedTime(LocalDateTime.now());
        //TODO 创建复制表情
        return R.success(chatUserEmoteService.save(chatUserEmote));
    }

    /**
     * 产出用户表情
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatUserEmote> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserEmote::getId, id)
                .eq(ChatUserEmote::getUid, sohoUserDetails.getId());
        ChatUserEmote chatUserEmote = chatUserEmoteService.getOne(lambdaQueryWrapper);
        Assert.notNull(chatUserEmote, "表情不存在");
        return R.success(chatUserEmoteService.removeById(chatUserEmote));
    }
}
