package work.soho.ai.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.ai.biz.domain.AiApp;
import work.soho.ai.biz.mapper.AiAppMapper;
import work.soho.ai.biz.service.AiAppService;

@RequiredArgsConstructor
@Service
public class AiAppServiceImpl extends ServiceImpl<AiAppMapper, AiApp>
    implements AiAppService{

}