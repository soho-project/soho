package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.mapper.OpenApiCallLogMapper;
import work.soho.open.biz.service.OpenApiCallLogService;

@RequiredArgsConstructor
@Service
public class OpenApiCallLogServiceImpl extends ServiceImpl<OpenApiCallLogMapper, OpenApiCallLog>
    implements OpenApiCallLogService{

}