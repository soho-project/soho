package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.mapper.AiApiCallLogMapper;
import work.soho.ai.biz.service.AiApiCallLogService;

@Service
public class AiApiCallLogServiceImpl extends ServiceImpl<AiApiCallLogMapper, AiApiCallLog>
        implements AiApiCallLogService {
}
