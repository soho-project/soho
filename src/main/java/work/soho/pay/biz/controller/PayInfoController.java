package work.soho.pay.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import work.soho.common.core.util.PageUtils;
import work.soho.admin.common.security.utils.SecurityUtils;
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
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.service.AdminDictApiService;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.service.PayInfoService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
import work.soho.api.admin.service.AdminDictApiService;
/**
 * 支付表Controller
 *
 * @author fang
 */
@Api(tags = "支付表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/payInfo" )
public class PayInfoController {

    private final PayInfoService payInfoService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询支付表列表
     */
    @GetMapping("/list")
    @Node(value = "payInfo::list", name = "获取 支付表 列表")
    public R<PageSerializable<PayInfo>> list(PayInfo payInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<PayInfo> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(payInfo.getPlatform()),PayInfo::getPlatform ,payInfo.getPlatform());
        lqw.eq(payInfo.getStatus() != null, PayInfo::getStatus ,payInfo.getStatus());
        lqw.like(StringUtils.isNotBlank(payInfo.getAdapterName()),PayInfo::getAdapterName ,payInfo.getAdapterName());
        lqw.eq(payInfo.getId() != null, PayInfo::getId ,payInfo.getId());
        lqw.like(StringUtils.isNotBlank(payInfo.getTitle()),PayInfo::getTitle ,payInfo.getTitle());
        lqw.like(StringUtils.isNotBlank(payInfo.getName()),PayInfo::getName ,payInfo.getName());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountAppId()),PayInfo::getAccountAppId ,payInfo.getAccountAppId());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountId()),PayInfo::getAccountId ,payInfo.getAccountId());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountPrivateKey()),PayInfo::getAccountPrivateKey ,payInfo.getAccountPrivateKey());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountSerialNumber()),PayInfo::getAccountSerialNumber ,payInfo.getAccountSerialNumber());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountPublicKey()),PayInfo::getAccountPublicKey ,payInfo.getAccountPublicKey());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountImg()),PayInfo::getAccountImg ,payInfo.getAccountImg());
        lqw.like(StringUtils.isNotBlank(payInfo.getClientType()),PayInfo::getClientType ,payInfo.getClientType());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, PayInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, PayInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(payInfo.getUpdatedTime() != null, PayInfo::getUpdatedTime ,payInfo.getUpdatedTime());
        lqw.orderByDesc(PayInfo::getId);
        List<PayInfo> list = payInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取支付表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "payInfo::getInfo", name = "获取 支付表 详细信息")
    public R<PayInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(payInfoService.getById(id));
    }

    /**
     * 新增支付表
     */
    @PostMapping
    @Node(value = "payInfo::add", name = "新增 支付表")
    public R<Boolean> add(@RequestBody PayInfo payInfo) {
        return R.success(payInfoService.save(payInfo));
    }

    /**
     * 修改支付表
     */
    @PutMapping
    @Node(value = "payInfo::edit", name = "修改 支付表")
    public R<Boolean> edit(@RequestBody PayInfo payInfo) {
        return R.success(payInfoService.updateById(payInfo));
    }

    /**
     * 删除支付表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "payInfo::remove", name = "删除 支付表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(payInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 支付表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = PayInfo.class)
    @Node(value = "payInfo::exportExcel", name = "导出 支付表 Excel")
    public Object exportExcel(PayInfo payInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<PayInfo> lqw = new LambdaQueryWrapper<PayInfo>();
        lqw.like(StringUtils.isNotBlank(payInfo.getPlatform()),PayInfo::getPlatform ,payInfo.getPlatform());
        lqw.eq(payInfo.getStatus() != null, PayInfo::getStatus ,payInfo.getStatus());
        lqw.like(StringUtils.isNotBlank(payInfo.getAdapterName()),PayInfo::getAdapterName ,payInfo.getAdapterName());
        lqw.eq(payInfo.getId() != null, PayInfo::getId ,payInfo.getId());
        lqw.like(StringUtils.isNotBlank(payInfo.getTitle()),PayInfo::getTitle ,payInfo.getTitle());
        lqw.like(StringUtils.isNotBlank(payInfo.getName()),PayInfo::getName ,payInfo.getName());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountAppId()),PayInfo::getAccountAppId ,payInfo.getAccountAppId());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountId()),PayInfo::getAccountId ,payInfo.getAccountId());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountPrivateKey()),PayInfo::getAccountPrivateKey ,payInfo.getAccountPrivateKey());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountSerialNumber()),PayInfo::getAccountSerialNumber ,payInfo.getAccountSerialNumber());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountPublicKey()),PayInfo::getAccountPublicKey ,payInfo.getAccountPublicKey());
        lqw.like(StringUtils.isNotBlank(payInfo.getAccountImg()),PayInfo::getAccountImg ,payInfo.getAccountImg());
        lqw.like(StringUtils.isNotBlank(payInfo.getClientType()),PayInfo::getClientType ,payInfo.getClientType());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, PayInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, PayInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(payInfo.getUpdatedTime() != null, PayInfo::getUpdatedTime ,payInfo.getUpdatedTime());
        lqw.orderByDesc(PayInfo::getId);
        return payInfoService.list(lqw);
    }

    /**
     * 导入 支付表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "payInfo::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), PayInfo.class, new ReadListener<PayInfo>() {
                @Override
                public void invoke(PayInfo payInfo, AnalysisContext analysisContext) {
                    payInfoService.save(payInfo);
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