package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatGroup;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.domain.ChatSessionUser;
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
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.avatar.utils.NinePatchAvatarGeneratorUtils;
import work.soho.common.data.upload.utils.UploadUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroup>
    implements ChatGroupService{

    private final ChatGroupUserMapper chatGroupUserMapper;

    private final ChatUserMapper chatUserMapper;

    /**
     * 创建群组
     *
     * @param chatGroup
     * @param chatUserIds
     * @return
     */
    @Override
    public ChatGroup createGroup(ChatGroup chatGroup, List<Long> chatUserIds) {
        chatGroup.setUpdatedTime(LocalDateTime.now());
        chatGroup.setCreatedTime(LocalDateTime.now());
        save(chatGroup);
        //添加创建者并且去重复
        chatUserIds.add(chatGroup.getMasterChatUid());
        chatUserIds = chatUserIds.stream().distinct().collect(Collectors.toList());
        List<ChatUser> users = chatUserMapper.selectBatchIds(chatUserIds);
        String[] avatars = (String[]) users.stream().limit(9).map(ChatUser::getAvatar).toArray(String[]::new);
        String title = users.stream().limit(3).map(ChatUser::getUsername).collect(Collectors.joining(","));
        Integer gridCount = avatars.length <= 4 ? 2 : 3;
        String groupAvatar = UploadUtils.upload("group/"+chatGroup.getId()+"/avatar.png", NinePatchAvatarGeneratorUtils.create(150, gridCount, avatars));
        chatGroup.setAvatar(groupAvatar);
        chatGroup.setTitle(title);
        saveOrUpdate(chatGroup);

        users.forEach(user->{
            ChatGroupUser chatGroupUser = new ChatGroupUser();
            chatGroupUser.setGroupId(chatGroup.getId());
            chatGroupUser.setChatUid(user.getId());
            chatGroupUser.setIsAdmin(chatGroup.getMasterChatUid().equals(user.getId()) ? ChatGroupUserEnums.IsAdmin.YES.getId() :  ChatGroupUserEnums.IsAdmin.NO.getId());
            chatGroupUser.setNickname(StringUtils.isEmpty(user.getNickname()) ? user.getUsername() : user.getNickname());
            chatGroupUser.setUpdatedTime(LocalDateTime.now());
            chatGroupUser.setCreatedTime(LocalDateTime.now());
            chatGroupUserMapper.insert(chatGroupUser);
        });

        return chatGroup;
    }

    @Override
    public void exitGroup(Long groupId, Long uid) {
        //获取群用户
        ChatGroup chatGroup = getById(groupId);
        if(chatGroup.getMasterChatUid().equals(uid)) {
            throw new RuntimeException("群主，不允许退出");
        }

        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getChatUid, uid)
                .eq(ChatGroupUser::getGroupId, groupId);
        chatGroupUserMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public void joinGroup(Long groupId, List<Long> uids) {
        //获取用户信息
        Map<Long,ChatUser> mapUsers = chatUserMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(ChatUser::getId, item->item));
        uids.forEach(uid->{
            ChatGroupUser chatGroupUser = new ChatGroupUser();
            chatGroupUser.setChatUid(uid);
            chatGroupUser.setGroupId(groupId);
            chatGroupUser.setIsAdmin(ChatGroupUserEnums.IsAdmin.NO.getId());
            chatGroupUser.setUpdatedTime(LocalDateTime.now());
            chatGroupUser.setCreatedTime(LocalDateTime.now());
            ChatUser user = mapUsers.get(uid);
            if(StringUtils.isNotBlank(user.getNickname())) {
                chatGroupUser.setNickname(user.getNickname());
            } else {
                chatGroupUser.setNickname(user.getUsername());
            }
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
            chatGroupVO.setIsAdmin(foundUser.get().getIsAdmin());
            if(chatGroupVO.getIsAdmin() == 0 && uid.equals(chatGroup.getMasterChatUid())) {
                chatGroupVO.setIsAdmin(ChatGroupUserEnums.IsAdmin.YES.getId());
            }
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

    @Override
    public Boolean isAdmin(Long id, Long uid) {
        ChatGroup chatGroup = getById(id);
        if(chatGroup == null) {
            return Boolean.FALSE;
        }
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, id);
        lambdaQueryWrapper.eq(ChatGroupUser::getIsAdmin, ChatGroupUserEnums.IsAdmin.YES.getId());
        List<ChatGroupUser> list = chatGroupUserMapper.selectList(lambdaQueryWrapper);
        List<Long> uids = list.stream().map(ChatGroupUser::getChatUid).collect(Collectors.toList());
        uids.add(chatGroup.getMasterChatUid());
        return uids.contains(uid);
    }
}
