package work.soho.wallet.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.wallet.biz.domain.WalletLog;
import work.soho.wallet.biz.mapper.WalletLogMapper;
import work.soho.wallet.biz.service.WalletLogService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WalletLogServiceImpl extends ServiceImpl<WalletLogMapper, WalletLog>
    implements WalletLogService{

    @Override
    public BigDecimal sumAmount(Long walletId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Object> list = getBaseMapper().selectObjs(Wrappers.<WalletLog>query().select("SUM(amount)")
                .eq("wallet_id", walletId)
                .between("created_time", startTime, endTime));
        if (list != null && list.size() > 0) {
            BigDecimal sum = (BigDecimal) list.get(0);
            return sum == null ? BigDecimal.ZERO : sum;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumIncomeAmount(Long walletId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Object> list = getBaseMapper().selectObjs(Wrappers.<WalletLog>query().select("SUM(amount)")
                .eq("wallet_id", walletId)
                        .ge("amount", 0)
                .between("created_time", startTime, endTime));
        if (list != null && list.size() > 0) {
            BigDecimal sum = (BigDecimal) list.get(0);
            return sum == null ? BigDecimal.ZERO : sum;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumOutcomeAmount(Long walletId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Object> list = getBaseMapper().selectObjs(Wrappers.<WalletLog>query().select("SUM(amount)")
                .eq("wallet_id", walletId)
                .le("amount", 0)
                .between("created_time", startTime, endTime));
        if (list != null && list.size() > 0) {
            BigDecimal sum = (BigDecimal) list.get(0);
            return sum == null ? BigDecimal.ZERO : sum;
        }
        return BigDecimal.ZERO;
    }
}