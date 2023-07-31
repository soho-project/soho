package work.soho.chat.biz.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatCustomerService;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.mapper.ChatCustomerServiceMapper;
import work.soho.chat.biz.mapper.ChatSessionMapper;
import work.soho.chat.biz.mapper.ChatSessionUserMapper;
import work.soho.chat.biz.service.ChatSessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession>
    implements ChatSessionService{

    private final ChatSessionUserMapper chatSessionUserMapper;

    private final ChatCustomerServiceMapper chatCustomerServiceMapper;

    @Value("#{@sohoConfig.getByKey('chat-default-customer-service-avatar')")
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

        //保存会话用户
        for (Long toUid: uids) {
            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setSessionId(chatSession.getId());
            chatSessionUser.setUserId(toUid);
            chatSessionUser.setUpdatedTime(LocalDateTime.now());
            chatSessionUser.setCreatedTime(LocalDateTime.now());
            chatSessionUserMapper.insert(chatSessionUser);
        }

        return chatSession;
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

}
