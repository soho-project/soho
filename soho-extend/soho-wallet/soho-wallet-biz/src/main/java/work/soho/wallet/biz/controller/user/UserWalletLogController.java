package work.soho.wallet.biz.controller.user;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.wallet.api.request.WalletLogListRequest;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletLog;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletLogService;

/**
 * 钱包变更日志Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/user/walletLog" )
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
}