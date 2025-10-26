package work.soho.pay.biz.controller;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.service.PayInfoService;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.excel.EasyExcelFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 支付表Controller
 *
 * @author i
 * @date 2022-11-11 16:36:32
 */
@Log4j2
@Api(tags = "客户端支付信息")
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay/admin/payInfo" )
public class PayInfoController {

    private final PayInfoService payInfoService;

    /**
     * 查询支付表列表
     */
    @GetMapping("/list")
    @Node(value = "payInfo::list", name = "支付表列表")
    public R<PageSerializable<PayInfo>> list(PayInfo payInfo)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<PayInfo> lqw = new LambdaQueryWrapper<PayInfo>();
        if (payInfo.getId() != null){
            lqw.eq(PayInfo::getId ,payInfo.getId());
        }
        if (StringUtils.isNotBlank(payInfo.getTitle())){
            lqw.like(PayInfo::getTitle ,payInfo.getTitle());
        }
        if (StringUtils.isNotBlank(payInfo.getName())){
            lqw.like(PayInfo::getName ,payInfo.getName());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountAppId())){
            lqw.like(PayInfo::getAccountAppId ,payInfo.getAccountAppId());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountId())){
            lqw.like(PayInfo::getAccountId ,payInfo.getAccountId());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountPrivateKey())){
            lqw.like(PayInfo::getAccountPrivateKey ,payInfo.getAccountPrivateKey());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountPublicKey())){
            lqw.like(PayInfo::getAccountPublicKey ,payInfo.getAccountPublicKey());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountImg())){
            lqw.like(PayInfo::getAccountImg ,payInfo.getAccountImg());
        }
        if (StringUtils.isNotBlank(payInfo.getClientType())){
            lqw.like(PayInfo::getClientType ,payInfo.getClientType());
        }
        if (payInfo.getCreatedTime() != null){
            lqw.eq(PayInfo::getCreatedTime ,payInfo.getCreatedTime());
        }
        if (payInfo.getUpdatedTime() != null){
            lqw.eq(PayInfo::getUpdatedTime ,payInfo.getUpdatedTime());
        }
        if (StringUtils.isNotBlank(payInfo.getPlatform())){
            lqw.like(PayInfo::getPlatform ,payInfo.getPlatform());
        }
        if (payInfo.getStatus() != null){
            lqw.eq(PayInfo::getStatus ,payInfo.getStatus());
        }
        if (StringUtils.isNotBlank(payInfo.getAdapterName())){
            lqw.like(PayInfo::getAdapterName ,payInfo.getAdapterName());
        }
        lqw.orderByDesc(PayInfo::getId);
        List<PayInfo> list = payInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取支付表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "payInfo::getInfo", name = "支付表详细信息")
    public R<PayInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(payInfoService.getById(id));
    }

    /**
     * 新增支付表
     */
    @PostMapping
    @Node(value = "payInfo::add", name = "支付表新增")
    public R<Boolean> add(@RequestBody PayInfo payInfo) {
       payInfo.setCreatedTime(LocalDateTime.now());
       payInfo.setUpdatedTime(LocalDateTime.now());
        return R.success(payInfoService.save(payInfo));
    }

    /**
     * 修改支付表
     */
    @PutMapping
    @Node(value = "payInfo::edit", name = "支付表修改")
    public R<Boolean> edit(@RequestBody PayInfo payInfo) {
       payInfo.setUpdatedTime(LocalDateTime.now());
        return R.success(payInfoService.updateById(payInfo));
    }

    /**
     * 删除支付表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "payInfo::remove", name = "支付表删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
//        new BigDecimal()
        return R.success(payInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该表options
     *
     * @return
     */
    @GetMapping("optionsV1")
    public R<HashMap<Integer, String>> optionsV1() {
        List<PayInfo> list = payInfoService.list();
        HashMap<Integer, String> map = new HashMap<>();
        for(PayInfo item: list) {

            map.put(item.getId(), item.getTitle());
        }
        return R.success(map);
    }

    /**
     * 获取该支付表 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "payInfo::options", name = "获取 支付表 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<PayInfo> list = payInfoService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(PayInfo item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getTitle());
            options.add(optionVo);
        }
        return R.success(options);
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
