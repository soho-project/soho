package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatGroup;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.enums.ChatGroupUserEnums;
import work.soho.chat.biz.mapper.ChatGroupMapper;
import work.soho.chat.biz.mapper.ChatGroupUserMapper;
import work.soho.chat.biz.mapper.ChatUserMapper;
import work.soho.chat.biz.service.ChatGroupService;
import work.soho.chat.biz.service.ChatGroupUserService;
import work.soho.chat.biz.vo.BaseUserVO;
import work.soho.chat.biz.vo.ChatGroupVO;
import work.soho.common.core.util.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroup>
    implements ChatGroupService{

    private final ChatGroupUserMapper chatGroupUserMapper;

    private final ChatUserMapper chatUserMapper;

    @Override
    public ChatGroup createGroup(ChatGroup chatGroup, List<Long> chatUserIds) {
        //TODO 配置群组头像
        chatGroup.setUpdatedTime(LocalDateTime.now());
        chatGroup.setCreatedTime(LocalDateTime.now());
        save(chatGroup);

        chatUserIds.forEach(uid->{
            ChatGroupUser chatGroupUser = new ChatGroupUser();
            chatGroupUser.setChatUid(uid);
            chatGroupUser.setGroupId(chatGroup.getId());
            chatGroupUser.setIsAdmin(ChatGroupUserEnums.IsAdmin.NO.getId());
            chatGroupUser.setUpdatedTime(LocalDateTime.now());
            chatGroupUser.setCreatedTime(LocalDateTime.now());
            chatGroupUserMapper.insert(chatGroupUser);
        });

        //TODO 创建群组会话

        return chatGroup;
    }

    @Override
    public void exitGroup(Long groupId, Long uid) {
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getChatUid, uid)
                .eq(ChatGroupUser::getGroupId, groupId);
        chatGroupUserMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public void joinGroup(Long groupId, List<Long> uids) {
        uids.forEach(uid->{
            ChatGroupUser chatGroupUser = new ChatGroupUser();
            chatGroupUser.setChatUid(uid);
            chatGroupUser.setGroupId(groupId);
            chatGroupUser.setIsAdmin(ChatGroupUserEnums.IsAdmin.NO.getId());
            chatGroupUser.setUpdatedTime(LocalDateTime.now());
            chatGroupUser.setCreatedTime(LocalDateTime.now());
            chatGroupUserMapper.insert(chatGroupUser);
        });
    }

    @Override
    public ChatGroupVO getDetails(Long id,Long uid) {
        ChatGroup chatGroup = getById(id);
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, id);
        List<ChatGroupUser> groupUsers = chatGroupUserMapper.selectList(lambdaQueryWrapper);
        List<Long> uids = groupUsers.stream().map(item->item.getChatUid()).collect(Collectors.toList());
        List<ChatUser> users = chatUserMapper.selectBatchIds(uids);

        List<BaseUserVO> userVOS = users.stream().map(item->{
            return BeanUtils.copy(item, BaseUserVO.class);
        }).collect(Collectors.toList());

        ChatGroupVO chatGroupVO = BeanUtils.copy(chatGroup, ChatGroupVO.class);
        chatGroupVO.setUserList(userVOS);
        //获取请求用户
        Optional<ChatGroupUser> foundUser = groupUsers.stream().filter(item->item.getChatUid().equals(uid)).findFirst();
        if(foundUser.isPresent()) {
            chatGroupVO.setNotesName(foundUser.get().getNotesName());
            chatGroupVO.setNickname(foundUser.get().getNickname());
        }

        return chatGroupVO;
    }

    @Override
    public ChatGroupUser getChatGroupUser(Long id, Long uid) {
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, id)
                .eq(ChatGroupUser::getChatUid, uid);
        return chatGroupUserMapper.selectOne(lambdaQueryWrapper);
    }
}
