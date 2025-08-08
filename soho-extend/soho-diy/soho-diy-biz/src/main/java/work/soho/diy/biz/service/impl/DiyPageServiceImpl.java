package work.soho.diy.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.diy.biz.domain.DiyPage;
import work.soho.diy.biz.mapper.DiyPageMapper;
import work.soho.diy.biz.service.DiyPageService;

@RequiredArgsConstructor
@Service
public class DiyPageServiceImpl extends ServiceImpl<DiyPageMapper, DiyPage>
    implements DiyPageService{

}