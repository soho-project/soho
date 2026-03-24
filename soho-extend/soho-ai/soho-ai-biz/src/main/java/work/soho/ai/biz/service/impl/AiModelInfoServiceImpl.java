package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.mapper.AiModelInfoMapper;
import work.soho.ai.biz.service.AiModelInfoService;

@Service
public class AiModelInfoServiceImpl extends ServiceImpl<AiModelInfoMapper, AiModelInfo>
        implements AiModelInfoService {
}
