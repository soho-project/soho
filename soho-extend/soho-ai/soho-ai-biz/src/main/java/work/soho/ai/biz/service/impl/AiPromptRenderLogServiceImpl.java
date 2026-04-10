package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiPromptRenderLog;
import work.soho.ai.biz.mapper.AiPromptRenderLogMapper;
import work.soho.ai.biz.service.AiPromptRenderLogService;

/**
 * 提示词渲染日志服务实现。
 */
@Service
public class AiPromptRenderLogServiceImpl extends ServiceImpl<AiPromptRenderLogMapper, AiPromptRenderLog>
        implements AiPromptRenderLogService {
}
