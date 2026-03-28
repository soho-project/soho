package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiMemberCard;
import work.soho.ai.biz.mapper.AiMemberCardMapper;
import work.soho.ai.biz.service.AiMemberCardService;

@Service
public class AiMemberCardServiceImpl extends ServiceImpl<AiMemberCardMapper, AiMemberCard> implements AiMemberCardService {
}
