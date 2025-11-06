package work.soho.wallet.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.wallet.biz.domain.WalletLog;
import work.soho.wallet.biz.mapper.WalletLogMapper;
import work.soho.wallet.biz.service.WalletLogService;

@RequiredArgsConstructor
@Service
public class WalletLogServiceImpl extends ServiceImpl<WalletLogMapper, WalletLog>
    implements WalletLogService{

}