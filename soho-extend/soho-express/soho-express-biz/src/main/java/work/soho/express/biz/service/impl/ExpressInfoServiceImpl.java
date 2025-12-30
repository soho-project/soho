package work.soho.express.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.mapper.ExpressInfoMapper;
import work.soho.express.biz.service.ExpressInfoService;

@RequiredArgsConstructor
@Service
public class ExpressInfoServiceImpl extends ServiceImpl<ExpressInfoMapper, ExpressInfo>
    implements ExpressInfoService{

}