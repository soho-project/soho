package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenCode;
import work.soho.open.biz.mapper.OpenCodeMapper;
import work.soho.open.biz.service.OpenCodeService;

@RequiredArgsConstructor
@Service
public class OpenCodeServiceImpl extends ServiceImpl<OpenCodeMapper, OpenCode>
    implements OpenCodeService{

}