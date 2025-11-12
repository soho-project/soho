package work.soho.wallet.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import work.soho.admin.api.vo.OptionVo;
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
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.service.WalletTypeService;
import java.util.ArrayList;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;

/**
 * 钱包类型Controller
 *
 * @author fang
 */
@Api(tags = "钱包类型")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletType" )
public class WalletTypeController {

    private final WalletTypeService walletTypeService;

    /**
     * 查询钱包类型列表
     */
    @GetMapping("/list")
    @Node(value = "walletType::list", name = "获取 钱包类型 列表")
    public R<PageSerializable<WalletType>> list(WalletType walletType, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletType> lqw = new LambdaQueryWrapper<>();
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
    @Node(value = "walletType::getInfo", name = "获取 钱包类型 详细信息")
    public R<WalletType> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletTypeService.getById(id));
    }

    /**
     * 新增钱包类型
     */
    @PostMapping
    @Node(value = "walletType::add", name = "新增 钱包类型")
    public R<Boolean> add(@RequestBody WalletType walletType) {
        return R.success(walletTypeService.save(walletType));
    }

    /**
     * 修改钱包类型
     */
    @PutMapping
    @Node(value = "walletType::edit", name = "修改 钱包类型")
    public R<Boolean> edit(@RequestBody WalletType walletType) {
        return R.success(walletTypeService.updateById(walletType));
    }

    /**
     * 删除钱包类型
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletType::remove", name = "删除 钱包类型")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletTypeService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该钱包类型 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "walletType::options", name = "获取 钱包类型 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<WalletType> list = walletTypeService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

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

    /**
     * 导出 钱包类型 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletType.class)
    @Node(value = "walletType::exportExcel", name = "导出 钱包类型 Excel")
    public Object exportExcel(WalletType walletType, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletType> lqw = new LambdaQueryWrapper<WalletType>();
        lqw.eq(walletType.getId() != null, WalletType::getId ,walletType.getId());
        lqw.eq(walletType.getStatus() != null, WalletType::getStatus ,walletType.getStatus());
        lqw.like(StringUtils.isNotBlank(walletType.getName()),WalletType::getName ,walletType.getName());
        lqw.like(StringUtils.isNotBlank(walletType.getTitle()),WalletType::getTitle ,walletType.getTitle());
        lqw.like(StringUtils.isNotBlank(walletType.getNotes()),WalletType::getNotes ,walletType.getNotes());
        lqw.eq(walletType.getUpdatedTime() != null, WalletType::getUpdatedTime ,walletType.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletType::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletType::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return walletTypeService.list(lqw);
    }

    /**
     * 导入 钱包类型 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletType::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletType.class, new ReadListener<WalletType>() {
                @Override
                public void invoke(WalletType walletType, AnalysisContext analysisContext) {
                    walletTypeService.save(walletType);
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