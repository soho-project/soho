package work.soho.wallet.biz.controller.user;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.wallet.api.request.WalletLogListRequest;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletLog;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletLogService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 钱包变更日志Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletLog" )
public class UserWalletLogController {

    private final WalletLogService walletLogService;

    private final WalletInfoService walletInfoService;

    /**
     * 查询钱包变更日志列表
     public R<PageSerializable<WalletLog>> list(WalletLogListRequest walletLogListRequest
     */
    @GetMapping("/list")
    public R<PageSerializable<WalletLog>> list(WalletLogListRequest walletLogListRequest
        , @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        // 获取对应类型的钱包ID
        WalletInfo walletInfo = walletInfoService.getByUserIdAndType(sohoUserDetails.getId(), walletLogListRequest.getType());
        Assert.notNull(walletInfo, "钱包不存在");

        PageUtils.startPage();
        LambdaQueryWrapper<WalletLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletLog::getWalletId , walletInfo.getId());
//        lqw.like(StringUtils.isNotBlank(walletLog.getNotes()),WalletLog::getNotes ,walletLog.getNotes());
        lqw.ge(StringUtils.isNotBlank(walletLogListRequest.getStartTime()), WalletLog::getCreatedTime, walletLogListRequest.getStartTime());
        lqw.lt(StringUtils.isNotEmpty(walletLogListRequest.getEndTime()), WalletLog::getCreatedTime, walletLogListRequest.getEndTime());

        if(walletLogListRequest.getChangeType() != null) {
            if(walletLogListRequest.getChangeType().intValue() == 1) {
                // 增加
                lqw.gt(WalletLog::getAmount, 0);
            } else if(walletLogListRequest.getChangeType().intValue() == 2) {
                // 减少
                lqw.lt(WalletLog::getAmount, 0);
            }
        }

        lqw.orderByDesc(WalletLog::getId);
        List<WalletLog> list = walletLogService.list(lqw);

        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包变更日志详细信息
     */
    @GetMapping(value = "/{id}" )
    public R<WalletLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletLogService.getById(id));
    }

    /**
     * 新增钱包变更日志
     */
    @PostMapping
    public R<Boolean> add(@RequestBody WalletLog walletLog) {
        return R.success(walletLogService.save(walletLog));
    }

    /**
     * 修改钱包变更日志
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody WalletLog walletLog) {
        return R.success(walletLogService.updateById(walletLog));
    }

    /**
     * 删除钱包变更日志
     */
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletLogService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 统计昨天钱包进出账总额
     */
    @ApiOperation(value = "统计昨天钱包进出账总额")
    @GetMapping("/statistics/yesterday")
    public R<HashMap<String, Object>> statisticsYesterday(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, Integer typeId) {
        WalletInfo walletInfo = walletInfoService.getByUserIdAndType(sohoUserDetails.getId(), typeId);
        HashMap<String, Object> result = new HashMap<>();

        // 获取今天的凌晨时间
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        // 获取昨天的凌晨时间
        LocalDateTime yesterdayStart = todayStart.minusDays(1);

        result.put("income", walletLogService.sumIncomeAmount(walletInfo.getId(), yesterdayStart, todayStart));
        result.put("outcome", walletLogService.sumOutcomeAmount(walletInfo.getId(), yesterdayStart, todayStart));
        return R.success(result);
    }
}