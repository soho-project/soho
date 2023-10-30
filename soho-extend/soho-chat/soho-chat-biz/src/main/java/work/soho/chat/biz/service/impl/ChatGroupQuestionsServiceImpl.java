package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatGroupQuestions;
import work.soho.chat.biz.mapper.ChatGroupQuestionsMapper;
import work.soho.chat.biz.service.ChatGroupQuestionsService;

@RequiredArgsConstructor
@Service
public class ChatGroupQuestionsServiceImpl extends ServiceImpl<ChatGroupQuestionsMapper, ChatGroupQuestions>
    implements ChatGroupQuestionsService{

}