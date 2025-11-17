package work.soho.wallet.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
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
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.wallet.biz.domain.WalletUser;
import work.soho.wallet.biz.service.WalletUserService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 钱包用户Controller
 *
 * @author fang
 */
@Api(tags = "钱包用户")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletUser" )
public class WalletUserController {

    private final WalletUserService walletUserService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询钱包用户列表
     */
    @GetMapping("/list")
    @Node(value = "walletUser::list", name = "获取 钱包用户 列表")
    public R<PageSerializable<WalletUser>> list(WalletUser walletUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletUser.getId() != null, WalletUser::getId ,walletUser.getId());
        lqw.eq(walletUser.getUserId() != null, WalletUser::getUserId ,walletUser.getUserId());
        lqw.like(StringUtils.isNotBlank(walletUser.getPayPassword()),WalletUser::getPayPassword ,walletUser.getPayPassword());
        lqw.eq(walletUser.getUpdatedTime() != null, WalletUser::getUpdatedTime ,walletUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletUser::getId);
        List<WalletUser> list = walletUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包用户详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletUser::getInfo", name = "获取 钱包用户 详细信息")
    public R<WalletUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletUserService.getById(id));
    }

    /**
     * 新增钱包用户
     */
    @PostMapping
    @Node(value = "walletUser::add", name = "新增 钱包用户")
    public R<Boolean> add(@RequestBody WalletUser walletUser) {
        return R.success(walletUserService.save(walletUser));
    }

    /**
     * 修改钱包用户
     */
    @PutMapping
    @Node(value = "walletUser::edit", name = "修改 钱包用户")
    public R<Boolean> edit(@RequestBody WalletUser walletUser) {
        return R.success(walletUserService.updateById(walletUser));
    }

    /**
     * 删除钱包用户
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletUser::remove", name = "删除 钱包用户")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletUserService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 钱包用户 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletUser.class)
    @Node(value = "walletUser::exportExcel", name = "导出 钱包用户 Excel")
    public Object exportExcel(WalletUser walletUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletUser> lqw = new LambdaQueryWrapper<WalletUser>();
        lqw.eq(walletUser.getId() != null, WalletUser::getId ,walletUser.getId());
        lqw.eq(walletUser.getUserId() != null, WalletUser::getUserId ,walletUser.getUserId());
        lqw.like(StringUtils.isNotBlank(walletUser.getPayPassword()),WalletUser::getPayPassword ,walletUser.getPayPassword());
        lqw.eq(walletUser.getUpdatedTime() != null, WalletUser::getUpdatedTime ,walletUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletUser::getId);
        return walletUserService.list(lqw);
    }

    /**
     * 导入 钱包用户 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletUser::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletUser.class, new ReadListener<WalletUser>() {
                @Override
                public void invoke(WalletUser walletUser, AnalysisContext analysisContext) {
                    walletUserService.save(walletUser);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    //nothing todo
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(e.getMessage());
        }
    }
}