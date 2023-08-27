package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.mapper.ChatUserNoticeMapper;
import work.soho.chat.biz.service.ChatUserNoticeService;

@RequiredArgsConstructor
@Service
public class ChatUserNoticeServiceImpl extends ServiceImpl<ChatUserNoticeMapper, ChatUserNotice>
    implements ChatUserNoticeService{

}