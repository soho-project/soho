package work.soho.express.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.mapper.ExpressInfoMapper;
import work.soho.express.biz.service.ExpressInfoService;

@RequiredArgsConstructor
@Service
public class ExpressInfoServiceImpl extends ServiceImpl<ExpressInfoMapper, ExpressInfo>
    implements ExpressInfoService{

}