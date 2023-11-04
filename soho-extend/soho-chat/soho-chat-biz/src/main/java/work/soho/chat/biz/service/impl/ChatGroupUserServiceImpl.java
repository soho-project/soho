package work.soho.chat.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.enums.ChatSessionUserEnums;
import work.soho.chat.biz.mapper.ChatGroupUserMapper;
import work.soho.chat.biz.service.ChatGroupUserService;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.chat.biz.service.ChatSessionUserService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatGroupUserServiceImpl extends ServiceImpl<ChatGroupUserMapper, ChatGroupUser>
    implements ChatGroupUserService{

    final private ChatSessionService chatSessionService;

    final private ChatSessionUserService chatSessionUserService;

    /**
     * 获取指定用户组用户数据
     *
     * @param id
     * @param uid
     * @return
     */
    @Override
    public ChatGroupUser getByUid(Long id, Long uid) {
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, id)
                .eq(ChatGroupUser::getChatUid, uid).last("limit 1");
        return getOne(lambdaQueryWrapper);
    }

    /**
     * 恢复指定群用户聊天功能
     *
     * @param chatGroupUser
     */
    public void restoreUserBanned(ChatGroupUser chatGroupUser) {
        chatGroupUser.setBannedEndTime(null);
        updateById(chatGroupUser);

        getBaseMapper().update(chatGroupUser, Wrappers.<ChatGroupUser>lambdaUpdate().set(ChatGroupUser::getBannedEndTime, null).eq(ChatGroupUser::getId, chatGroupUser.getId()));

        //更新会话用户状态
        ChatSession chatSession = chatSessionService.findSession(ChatSessionEnums.Type.GROUP_CHAT, chatGroupUser.getGroupId());
        ChatSessionUser chatSessionUser = chatSessionUserService.getSessionUser(chatSession.getId(), chatGroupUser.getChatUid());
        chatSessionUser.setCanSend(ChatSessionUserEnums.CanSend.YES.getId());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        chatSessionUserService.updateById(chatSessionUser);
    }

    /**
     * 禁言恢复
     */
    public void restoreBanned() {
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.le(ChatGroupUser::getBannedEndTime, LocalDateTime.now())
                .last("limit 5000");
        List<ChatGroupUser> list = list(lambdaQueryWrapper);
        list.forEach(item->{
            restoreUserBanned(item);
        });
    }
}
