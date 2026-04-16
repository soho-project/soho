package work.soho.wallet.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import work.soho.wallet.biz.domain.WalletInfo;

import java.math.BigDecimal;

public interface WalletInfoMapper extends BaseMapper<WalletInfo> {

    int atomicUpdateAmount(@Param("id") Long id, @Param("delta") BigDecimal delta);
}