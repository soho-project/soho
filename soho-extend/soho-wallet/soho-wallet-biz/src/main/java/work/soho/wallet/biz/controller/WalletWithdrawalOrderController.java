package work.soho.wallet.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.Asserts;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.security.utils.SecurityUtils;
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
import work.soho.wallet.biz.domain.WalletWithdrawalOrder;
import work.soho.wallet.biz.enums.WalletWithdrawalOrderEnums;
import work.soho.wallet.biz.service.WalletWithdrawalOrderService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.wallet.biz.vo.WalletWithdrawalOrderAliBankVo;
import work.soho.wallet.biz.vo.WalletWithdrawalOrderImportAliBankVo;
import work.soho.common.core.util.BeanUtils;

/**
 * 提现单Controller
 *
 * @author fang
 */
@Api(tags = "提现单")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletWithdrawalOrder")
public class WalletWithdrawalOrderController {

    private final WalletWithdrawalOrderService walletWithdrawalOrderService;
//    private final AdminDictApiService adminDictApiService;

    /**
     * 查询提现单列表
     */
    @GetMapping("/list")
    @Node(value = "walletWithdrawalOrder::list", name = "获取 提现单 列表")
    public R<PageSerializable<WalletWithdrawalOrder>> list(WalletWithdrawalOrder walletWithdrawalOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletWithdrawalOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletWithdrawalOrder.getId() != null, WalletWithdrawalOrder::getId ,walletWithdrawalOrder.getId());
        lqw.eq(walletWithdrawalOrder.getUserId() != null, WalletWithdrawalOrder::getUserId ,walletWithdrawalOrder.getUserId());
        lqw.eq(walletWithdrawalOrder.getWalletId() != null, WalletWithdrawalOrder::getWalletId ,walletWithdrawalOrder.getWalletId());
        lqw.eq(walletWithdrawalOrder.getAmount() != null, WalletWithdrawalOrder::getAmount ,walletWithdrawalOrder.getAmount());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getNotes()),WalletWithdrawalOrder::getNotes ,walletWithdrawalOrder.getNotes());
        lqw.eq(walletWithdrawalOrder.getStatus() != null, WalletWithdrawalOrder::getStatus ,walletWithdrawalOrder.getStatus());
        lqw.eq(walletWithdrawalOrder.getAdminId() != null, WalletWithdrawalOrder::getAdminId ,walletWithdrawalOrder.getAdminId());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getAdminNotes()),WalletWithdrawalOrder::getAdminNotes ,walletWithdrawalOrder.getAdminNotes());
        lqw.eq(walletWithdrawalOrder.getUpdatedTime() != null, WalletWithdrawalOrder::getUpdatedTime ,walletWithdrawalOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletWithdrawalOrder::getCreatedTime);
        List<WalletWithdrawalOrder> list = walletWithdrawalOrderService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取提现单详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletWithdrawalOrder::getInfo", name = "获取 提现单 详细信息")
    public R<WalletWithdrawalOrder> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletWithdrawalOrderService.getById(id));
    }

    /**
     * 新增提现单
     */
    @PostMapping
    @Node(value = "walletWithdrawalOrder::add", name = "新增 提现单")
    public R<Boolean> add(@RequestBody WalletWithdrawalOrder walletWithdrawalOrder) {
        walletWithdrawalOrder.setCode(IDGeneratorUtils.snowflake().toString());
        walletWithdrawalOrder.setCreatedTime(LocalDateTime.now());
        walletWithdrawalOrder.setUpdatedTime(LocalDateTime.now());
        return R.success(walletWithdrawalOrderService.save(walletWithdrawalOrder));
    }

    /**
     * 修改提现单
     */
    @PutMapping
    @Node(value = "walletWithdrawalOrder::edit", name = "修改 提现单")
    public R<Boolean> edit(@RequestBody WalletWithdrawalOrder walletWithdrawalOrder) {
        return R.success(walletWithdrawalOrderService.updateById(walletWithdrawalOrder));
    }

    /**
     * 删除提现单
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletWithdrawalOrder::remove", name = "删除 提现单")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletWithdrawalOrderService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 提现单 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletWithdrawalOrder.class)
    @Node(value = "walletWithdrawalOrder::exportExcel", name = "导出 提现单 Excel")
    public Object exportExcel(WalletWithdrawalOrder walletWithdrawalOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletWithdrawalOrder> lqw = new LambdaQueryWrapper<WalletWithdrawalOrder>();
        lqw.eq(walletWithdrawalOrder.getId() != null, WalletWithdrawalOrder::getId ,walletWithdrawalOrder.getId());
        lqw.eq(walletWithdrawalOrder.getUserId() != null, WalletWithdrawalOrder::getUserId ,walletWithdrawalOrder.getUserId());
        lqw.eq(walletWithdrawalOrder.getWalletId() != null, WalletWithdrawalOrder::getWalletId ,walletWithdrawalOrder.getWalletId());
        lqw.eq(walletWithdrawalOrder.getAmount() != null, WalletWithdrawalOrder::getAmount ,walletWithdrawalOrder.getAmount());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getNotes()),WalletWithdrawalOrder::getNotes ,walletWithdrawalOrder.getNotes());
        lqw.eq(walletWithdrawalOrder.getStatus() != null, WalletWithdrawalOrder::getStatus ,walletWithdrawalOrder.getStatus());
        lqw.eq(walletWithdrawalOrder.getAdminId() != null, WalletWithdrawalOrder::getAdminId ,walletWithdrawalOrder.getAdminId());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getAdminNotes()),WalletWithdrawalOrder::getAdminNotes ,walletWithdrawalOrder.getAdminNotes());
        lqw.eq(walletWithdrawalOrder.getUpdatedTime() != null, WalletWithdrawalOrder::getUpdatedTime ,walletWithdrawalOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletWithdrawalOrder::getCreatedTime);
        return walletWithdrawalOrderService.list(lqw);
    }

    /**
     * 导出 阿里提现单 Excel
     */
    @GetMapping("/exportAliBankExcel")
    @ExcelExport(
            fileName = "T(java.time.LocalDate).now().format(T(java.time.format.DateTimeFormatter).ofPattern('yyyy-MM-dd')) + '-WalletWithdrawal.xls'",
            modelClass = WalletWithdrawalOrderAliBankVo.class
    )
    @Node(value = "walletWithdrawalOrder::exportAliBankExcel", name = "导出 阿里提现单 Excel")
    public Object exportAliBankExcel(WalletWithdrawalOrder walletWithdrawalOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletWithdrawalOrder> lqw = new LambdaQueryWrapper<WalletWithdrawalOrder>();
        lqw.eq(walletWithdrawalOrder.getId() != null, WalletWithdrawalOrder::getId ,walletWithdrawalOrder.getId());
        lqw.eq(walletWithdrawalOrder.getUserId() != null, WalletWithdrawalOrder::getUserId ,walletWithdrawalOrder.getUserId());
        lqw.eq(walletWithdrawalOrder.getWalletId() != null, WalletWithdrawalOrder::getWalletId ,walletWithdrawalOrder.getWalletId());
        lqw.eq(walletWithdrawalOrder.getAmount() != null, WalletWithdrawalOrder::getAmount ,walletWithdrawalOrder.getAmount());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getNotes()),WalletWithdrawalOrder::getNotes ,walletWithdrawalOrder.getNotes());
//        lqw.eq(walletWithdrawalOrder.getStatus() != null, WalletWithdrawalOrder::getStatus ,walletWithdrawalOrder.getStatus());
        lqw.eq(walletWithdrawalOrder.getAdminId() != null, WalletWithdrawalOrder::getAdminId ,walletWithdrawalOrder.getAdminId());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getAdminNotes()),WalletWithdrawalOrder::getAdminNotes ,walletWithdrawalOrder.getAdminNotes());
        lqw.eq(walletWithdrawalOrder.getUpdatedTime() != null, WalletWithdrawalOrder::getUpdatedTime ,walletWithdrawalOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());

        // 强制设置状态为待处理
        lqw.eq(WalletWithdrawalOrder::getStatus , WalletWithdrawalOrderEnums.Status.PENDING_PROCESSING.getId());

        lqw.orderByDesc(WalletWithdrawalOrder::getCreatedTime);

        // 转换为阿里模板view
        List<WalletWithdrawalOrderAliBankVo> list = walletWithdrawalOrderService.list(lqw).stream().map(item -> {
            WalletWithdrawalOrderAliBankVo vo = new WalletWithdrawalOrderAliBankVo();
            vo.setId(item.getId());
            vo.setCardName(item.getCardName());
            vo.setCardCode(item.getCardCode());
            vo.setAmount(item.getPayAmount());
            vo.setPurpose("服务费;NO:" + item.getCode());
            vo.setBankName("");
            vo.setInterbankNumber("");

//            WalletWithdrawalOrderAliBankVo vo = BeanUtils.copy(item, WalletWithdrawalOrderAliBankVo.class);
//            vo.setPurpose("服务费;NO" + item.getCode());
            return vo;
        }).collect(Collectors.toList());

        return list;
    }

    /**
     * 导入 网商银行提现单处理结果 Excel
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/importAliBankExcel")
    @Node(value = "walletWithdrawalOrder::importAliBankExcel", name = "导入 自动化样例 Excel")
    public R importAliBankExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletWithdrawalOrderImportAliBankVo.class, new ReadListener<WalletWithdrawalOrderImportAliBankVo>() {
                @Override
                public void invoke(WalletWithdrawalOrderImportAliBankVo vo, AnalysisContext analysisContext) {
//                    walletWithdrawalOrderService.save(walletWithdrawalOrder);
                    log.info("==========================================");
                    log.info(vo);
                    String[] parts = vo.getPurpose().split(":");
                    if(parts.length > 1) {
                        String orderCode = parts[parts.length - 1];
                        log.info("当前单号: {}", orderCode);
                        WalletWithdrawalOrder walletWithdrawalOrder = walletWithdrawalOrderService.getOne(new LambdaQueryWrapper<WalletWithdrawalOrder>().eq(WalletWithdrawalOrder::getCode, orderCode));
                        Asserts.notNull(walletWithdrawalOrder, "提现单不存在");
                        walletWithdrawalOrder.setStatus(WalletWithdrawalOrderEnums.Status.WITHDRAWN.getId());
                        walletWithdrawalOrder.setUpdatedTime(LocalDateTime.now());
                        walletWithdrawalOrderService.updateById(walletWithdrawalOrder);
                    } else {
                        throw new RuntimeException("解析失败");
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    //nothing todo
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
//            return R.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 导入 提现单 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletWithdrawalOrder::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletWithdrawalOrderImportAliBankVo.class, new ReadListener<WalletWithdrawalOrder>() {
                @Override
                public void invoke(WalletWithdrawalOrder walletWithdrawalOrder, AnalysisContext analysisContext) {
                    walletWithdrawalOrderService.save(walletWithdrawalOrder);
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