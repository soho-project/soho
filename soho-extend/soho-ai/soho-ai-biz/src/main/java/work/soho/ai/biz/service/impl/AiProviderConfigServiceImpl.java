package work.soho.ai.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.mapper.AiProviderConfigMapper;
import work.soho.ai.biz.service.AiProviderConfigService;

@RequiredArgsConstructor
@Service
public class AiProviderConfigServiceImpl extends ServiceImpl<AiProviderConfigMapper, AiProviderConfig>
    implements AiProviderConfigService{

}