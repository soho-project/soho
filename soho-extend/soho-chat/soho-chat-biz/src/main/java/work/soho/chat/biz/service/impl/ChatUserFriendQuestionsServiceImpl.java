package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatUserFriendQuestions;
import work.soho.chat.biz.mapper.ChatUserFriendQuestionsMapper;
import work.soho.chat.biz.service.ChatUserFriendQuestionsService;

@RequiredArgsConstructor
@Service
public class ChatUserFriendQuestionsServiceImpl extends ServiceImpl<ChatUserFriendQuestionsMapper, ChatUserFriendQuestions>
    implements ChatUserFriendQuestionsService{

}