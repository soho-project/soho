package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiChatSessionMessage;
import work.soho.ai.biz.mapper.AiChatSessionMessageMapper;
import work.soho.ai.biz.service.AiChatSessionMessageService;

@Service
public class AiChatSessionMessageServiceImpl extends ServiceImpl<AiChatSessionMessageMapper, AiChatSessionMessage>
        implements AiChatSessionMessageService {
}
