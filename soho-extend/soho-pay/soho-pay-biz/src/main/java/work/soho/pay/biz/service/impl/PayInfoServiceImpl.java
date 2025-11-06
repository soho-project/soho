package work.soho.pay.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.mapper.PayInfoMapper;
import work.soho.pay.biz.service.PayInfoService;

/**
* @author i
* @description 针对表【pay_info(支付表)】的数据库操作Service实现
* @createDate 2022-11-11 16:37:42
*/
@Service
public class PayInfoServiceImpl extends ServiceImpl<PayInfoMapper, PayInfo>
    implements PayInfoService{

}




