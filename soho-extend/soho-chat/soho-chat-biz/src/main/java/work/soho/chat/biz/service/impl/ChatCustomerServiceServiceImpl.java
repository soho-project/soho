package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatCustomerService;
import work.soho.chat.biz.mapper.ChatCustomerServiceMapper;
import work.soho.chat.biz.service.ChatCustomerServiceService;

@RequiredArgsConstructor
@Service
public class ChatCustomerServiceServiceImpl extends ServiceImpl<ChatCustomerServiceMapper, ChatCustomerService>
    implements ChatCustomerServiceService{

}