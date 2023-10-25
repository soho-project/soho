package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatUserEmote;
import work.soho.chat.biz.mapper.ChatUserEmoteMapper;
import work.soho.chat.biz.service.ChatUserEmoteService;

@RequiredArgsConstructor
@Service
public class ChatUserEmoteServiceImpl extends ServiceImpl<ChatUserEmoteMapper, ChatUserEmote>
    implements ChatUserEmoteService{

}