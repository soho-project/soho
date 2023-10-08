package work.soho.chat.biz.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.api.payload.ChatMessage;
import work.soho.chat.biz.domain.*;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.mapper.ChatCustomerServiceMapper;
import work.soho.chat.biz.mapper.ChatSessionMapper;
import work.soho.chat.biz.mapper.ChatSessionUserMapper;
import work.soho.chat.biz.mapper.ChatUserMapper;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.data.avatar.utils.NinePatchAvatarGeneratorUtils;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.longlink.api.sender.Sender;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession>
    implements ChatSessionService{

    private final ChatSessionUserMapper chatSessionUserMapper;

    private final ChatCustomerServiceMapper chatCustomerServiceMapper;

    private final ChatUserMapper chatUserMapper;

    /**
     * 消息发送接口
     */
    private final Sender sender;

    @Value("#{@sohoConfig.getByKey('chat-default-customer-service-avatar')}")
    private String defaultCustomerAvatar;

    /**
     * 查询客服会话
     *
     * @param uid
     * @param toUid
     * @return
     */
    @Override
    public ChatSession findCustomerServiceSession(Long uid, Long toUid) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, uid);
        List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectList(lambdaQueryWrapper);
        List<Long> sessionIdList = chatSessionUserList.stream().map(ChatSessionUser::getSessionId).collect(Collectors.toList());
        if(sessionIdList == null || sessionIdList.size() == 0) {
            return null;
        }

        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(ChatSessionUser::getSessionId, sessionIdList);
        lambdaQueryWrapper1.eq(ChatSessionUser::getUserId, toUid);
        List<ChatSessionUser> chatSessionUserList1 = chatSessionUserMapper.selectList(lambdaQueryWrapper1);
        List<Long> sessionIdList1 = chatSessionUserList1.stream().map(ChatSessionUser::getSessionId).collect(Collectors.toList());
        if(sessionIdList1 == null || sessionIdList1.size() == 0) {
            return null;
        }

        //查询客服会话
        LambdaQueryWrapper<ChatSession> chatSessionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatSessionLambdaQueryWrapper.in(ChatSession::getId, sessionIdList1);
        chatSessionLambdaQueryWrapper.eq(ChatSession::getType, ChatSessionEnums.Type.CUSTOMER_SERVICE.getId());
        chatSessionLambdaQueryWrapper.eq(ChatSession::getStatus, ChatSessionEnums.Status.ACTIVE.getId());
        chatSessionLambdaQueryWrapper.orderByDesc(ChatSession::getId);
        chatSessionLambdaQueryWrapper.last(" limit 1");
        return getOne(chatSessionLambdaQueryWrapper);
    }

    /**
     * 查询好友会话
     *
     * @param uid
     * @param toUid
     * @return
     */
    public ChatSession findFriendSession(Long uid, Long toUid) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, uid);
        List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectList(lambdaQueryWrapper);
        List<Long> sessionIdList = chatSessionUserList.stream().map(ChatSessionUser::getSessionId).collect(Collectors.toList());
        if(sessionIdList == null || sessionIdList.size() == 0) {
            return createSession(uid, new ArrayList<>(Arrays.asList(toUid)), ChatSessionEnums.Type.PRIVATE_CHAT);
        }

        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(ChatSessionUser::getSessionId, sessionIdList);
        lambdaQueryWrapper1.eq(ChatSessionUser::getUserId, toUid);
        List<ChatSessionUser> chatSessionUserList1 = chatSessionUserMapper.selectList(lambdaQueryWrapper1);
        List<Long> sessionIdList1 = chatSessionUserList1.stream().map(ChatSessionUser::getSessionId).collect(Collectors.toList());
        if(sessionIdList1 == null || sessionIdList1.size() == 0) {
            //没有相同的会话则不存在私聊会话
            return createSession(uid, new ArrayList<>(Arrays.asList(toUid)), ChatSessionEnums.Type.PRIVATE_CHAT);
        }


        //查询客服会话
        LambdaQueryWrapper<ChatSession> chatSessionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatSessionLambdaQueryWrapper.in(ChatSession::getId, sessionIdList1);
        chatSessionLambdaQueryWrapper.eq(ChatSession::getType, ChatSessionEnums.Type.PRIVATE_CHAT.getId());
        chatSessionLambdaQueryWrapper.eq(ChatSession::getStatus, ChatSessionEnums.Status.ACTIVE.getId());
        chatSessionLambdaQueryWrapper.orderByDesc(ChatSession::getId);
        chatSessionLambdaQueryWrapper.last(" limit 1");
        ChatSession chatSession = getOne(chatSessionLambdaQueryWrapper);
        //会话不存在择创建会话
        if(chatSession == null) {
            return createSession(uid, new ArrayList<>(Arrays.asList(toUid)), ChatSessionEnums.Type.PRIVATE_CHAT);
        }

        return chatSession;
    }

    /**
     * 查询创建群会话
     *
     * @param chatGroup
     * @param userList
     * @return
     */
    @Override
    public ChatSession groupSession(ChatGroup chatGroup, List<ChatGroupUser> userList) {
        LambdaQueryWrapper<ChatSession> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSession::getType, ChatSessionEnums.Type.GROUP_CHAT.getId())
                .eq(ChatSession::getTrackId, chatGroup.getId());
        ChatSession chatSession = getOne(lambdaQueryWrapper);
        if(chatSession == null) {
            //TODO 根据传递信息创建群组会话
            chatSession = new ChatSession();
            chatSession.setAvatar(chatGroup.getAvatar());
            chatSession.setStatus(ChatSessionEnums.Status.ACTIVE.getId());
            chatSession.setType(ChatSessionEnums.Type.GROUP_CHAT.getId());
            chatSession.setTitle(chatGroup.getTitle());
            chatSession.setUpdatedTime(LocalDateTime.now());
            chatSession.setCreatedTime(LocalDateTime.now());
            chatSession.setTrackId(chatGroup.getId());
            save(chatSession);

        }

        //添加群组用户
        for(ChatGroupUser chatGroupUser: userList) {
            //检查用户是否已经加入群聊
            LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(ChatSessionUser::getSessionId, chatSession.getId());
            lambdaQueryWrapper1.eq(ChatSessionUser::getUserId, chatGroupUser.getChatUid());
            ChatSessionUser chatSessionUser = chatSessionUserMapper.selectOne(lambdaQueryWrapper1);
            if(chatSessionUser == null) {
                ChatUser chatUser = chatUserMapper.selectById(chatGroupUser.getChatUid());
                chatSessionUser = new ChatSessionUser();
                chatSessionUser.setSessionId(chatSession.getId());
                chatSessionUser.setUserId(chatGroupUser.getChatUid());
//                chatSessionUser.setAvatar(chatUser.getAvatar());
                chatSessionUser.setUpdatedTime(LocalDateTime.now());
                chatSessionUser.setCreatedTime(LocalDateTime.now());
//                chatSessionUser.setTitle(chatGroupUser.getNickname());
                chatSessionUserMapper.insert(chatSessionUser);
            }
        }

        return chatSession;
    }

    /**
     * 创建会话
     *
     * @param uid
     * @param uids
     * @param type
     * @return
     */
    public ChatSession createSession(Long uid, List<Long> uids, ChatSessionEnums.Type type) {
        ChatSession chatSession = new ChatSession();
        chatSession.setStatus(ChatSessionEnums.Status.ACTIVE.getId());
        chatSession.setCreatedTime(LocalDateTime.now());
        chatSession.setUpdatedTime(LocalDateTime.now());
        chatSession.setType(type.getId());
        chatSession.setTitle("会话"); //生成会话名字
        chatSession.setAvatar(defaultCustomerAvatar);
        save(chatSession);

        uids.add(uid);
        //查询用户信息
        List<ChatUser> userList = chatUserMapper.selectBatchIds(uids);
        Map<Long, ChatUser> mapUsers = userList.stream().collect(Collectors.toMap(ChatUser::getId, item->item));
        //非好友会话拼装九宫格会话头像
        String sessionAvatar = defaultCustomerAvatar;
        if(type.getId() == ChatSessionEnums.Type.GROUP_CHAT.getId()
            || type.getId() == ChatSessionEnums.Type.GROUP.getId()
        ) {
            String[] imageUrls = (String[]) userList.stream().limit(9).map(ChatUser::getAvatar).toArray();
            //生成九宫格头像
            sessionAvatar = UploadUtils.upload("/session/"+chatSession.getId()+"/avatar.png", NinePatchAvatarGeneratorUtils.create(150, 3, imageUrls));
        } else if (type.getId() == ChatSessionEnums.Type.SELF.getId()) {
            //自己的头像
            sessionAvatar = mapUsers.get(uid).getAvatar();
        }

        //更新会话头像
        chatSession.setAvatar(sessionAvatar);
        updateById(chatSession);

        //保存会话用户
        for (Long toUid: uids) {
            //获取会话对方昵称
            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setSessionId(chatSession.getId());
            chatSessionUser.setUserId(toUid);
            chatSessionUser.setUpdatedTime(LocalDateTime.now());
            chatSessionUser.setCreatedTime(LocalDateTime.now());
            chatSessionUser.setIsTop(0);
            chatSessionUser.setIsNotDisturb(0);
            //配置好友头像
            if(type.getId() == ChatSessionEnums.Type.PRIVATE_CHAT.getId()) {
                Long otherUid = uids.stream().filter(id -> id != toUid).findFirst().orElse(toUid);
                chatSessionUser.setAvatar(mapUsers.get(otherUid).getAvatar());
                chatSessionUser.setSessionNickname(mapUsers.get(otherUid).getNickname());
            }
            chatSessionUserMapper.insert(chatSessionUser);
        }

        return chatSession;
    }

    @Override
    public ChatSession findSession(ChatSessionEnums.Type type, Long trackId) {
        LambdaQueryWrapper<ChatSession> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSession::getType, type.getId());
        lambdaQueryWrapper.eq(ChatSession::getTrackId, trackId);
        return getOne(lambdaQueryWrapper);
    }

    /**
     * 查找会话客服ID
     *
     * @param sessionId
     * @return
     */
    @Override
    public ChatSessionUser findCustomerService(Long sessionId) {
        //获取所有客服
        List<ChatCustomerService> chatCustomerServiceList = chatCustomerServiceMapper.selectList(new LambdaQueryWrapper<>());
        List<Long> customerServiceUids = chatCustomerServiceList.stream().map(ChatCustomerService::getUserId).collect(Collectors.toList());

        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
        List<ChatSessionUser> list = chatSessionUserMapper.selectList(lambdaQueryWrapper);
        AtomicReference<ChatSessionUser> chatSessionUser = new AtomicReference<>();
        list.forEach(item->{
            if(customerServiceUids.contains(item.getUserId())) {
                chatSessionUser.set(item);
            }
        });
        return chatSessionUser.get();
    }

    /**
     * 查询指定会话的用户信息
     *
     * @param sessionId
     * @return
     */
    public List<ChatSessionUser> getSessionUser(String sessionId) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
        return chatSessionUserMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public ChatSessionUser getSessionUser(Long sessionId, Long uid) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, uid);
        return chatSessionUserMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 聊天处理
     *
     * @param inputChatMessage
     */
    public void chat(ChatMessage inputChatMessage) {
       List<ChatSessionUser> sessionUsers = getSessionUser(inputChatMessage.getToSessionId());
       for(ChatSessionUser chatSessionUser: sessionUsers) {
           log.info("当前转发消息用户信息： {}", chatSessionUser);
           //检查是否为发送用户
           if(chatSessionUser.getUserId().equals(Long.valueOf(inputChatMessage.getFromUid()))) {
               //这是发送人
               //ignore
           } else {
               //推送消息到信道给到指定用户
               sender.sendToUid(String.valueOf(chatSessionUser.getUserId()), JacksonUtils.toJson(inputChatMessage));
           }
       }
    }
}
