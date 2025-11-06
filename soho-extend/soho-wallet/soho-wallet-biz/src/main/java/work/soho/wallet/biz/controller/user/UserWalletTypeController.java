package work.soho.wallet.biz.controller.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.service.WalletTypeService;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 钱包类型Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletType" )
public class UserWalletTypeController {

    private final WalletTypeService walletTypeService;

    /**
     * 查询钱包类型列表
     */
    @GetMapping("/list")
    public R<PageSerializable<WalletType>> list(WalletType walletType, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletType> lqw = new LambdaQueryWrapper<WalletType>();
        lqw.eq(walletType.getId() != null, WalletType::getId ,walletType.getId());
        lqw.eq(walletType.getStatus() != null, WalletType::getStatus ,walletType.getStatus());
        lqw.like(StringUtils.isNotBlank(walletType.getName()),WalletType::getName ,walletType.getName());
        lqw.like(StringUtils.isNotBlank(walletType.getTitle()),WalletType::getTitle ,walletType.getTitle());
        lqw.like(StringUtils.isNotBlank(walletType.getNotes()),WalletType::getNotes ,walletType.getNotes());
        lqw.eq(walletType.getUpdatedTime() != null, WalletType::getUpdatedTime ,walletType.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletType::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletType::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletType> list = walletTypeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包类型详细信息
     */
    @GetMapping(value = "/{id}" )
    public R<WalletType> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletTypeService.getById(id));
    }

    /**
     * 计算提现金额到账金额
     *
     * @param id
     * @param amount
     * @return
     */
    @GetMapping(value = "/withdrawAmount/{id}")
    public R<BigDecimal> getWithdrawAmount(@PathVariable("id" ) Integer id,@RequestParam(value = "amount") BigDecimal amount) {
        BigDecimal commission = walletTypeService.getCommission(id, amount);
        return R.success(amount.subtract(commission));
    }

    /**
     * 新增钱包类型
     */
    @PostMapping
    public R<Boolean> add(@RequestBody WalletType walletType) {
        return R.success(walletTypeService.save(walletType));
    }

    /**
     * 修改钱包类型
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody WalletType walletType) {
        return R.success(walletTypeService.updateById(walletType));
    }

    /**
     * 删除钱包类型
     */
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletTypeService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该钱包类型 选项
     *
     * @return
     */
    @GetMapping("options")
    public R<List<OptionVo<Integer, String>>> options() {
        List<WalletType> list = walletTypeService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(WalletType item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getTitle());
            options.add(optionVo);
        }
        return R.success(options);
    }

//    /**
//     * 获取 钱包类型 枚举选项
//     *
//     * @return
//     */
//    @GetMapping("/queryStatusEnumOption")
//    public R<List<OptionVo<Integer, String>>> statusEnumOption() {
//        return R.success(adminDictApiService.getOptionsByCode("walletType-status"));
//    }
}