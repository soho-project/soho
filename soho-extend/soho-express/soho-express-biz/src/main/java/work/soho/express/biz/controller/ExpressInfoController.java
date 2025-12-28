package work.soho.express.biz.controller;

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
import work.soho.express.biz.domain.ExpressInfo;
import work.soho.express.biz.service.ExpressInfoService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 快递信息Controller
 *
 * @author fang
 */
@Api(tags = "快递信息")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/express/admin/expressInfo" )
public class ExpressInfoController {

    private final ExpressInfoService expressInfoService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询快递信息列表
     */
    @GetMapping("/list")
    @Node(value = "expressInfo::list", name = "获取 快递信息 列表")
    public R<PageSerializable<ExpressInfo>> list(ExpressInfo expressInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExpressInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(expressInfo.getId() != null, ExpressInfo::getId ,expressInfo.getId());
        lqw.like(StringUtils.isNotBlank(expressInfo.getName()),ExpressInfo::getName ,expressInfo.getName());
        lqw.eq(expressInfo.getExpressType() != null, ExpressInfo::getExpressType ,expressInfo.getExpressType());
        lqw.eq(expressInfo.getStatus() != null, ExpressInfo::getStatus ,expressInfo.getStatus());
        lqw.like(StringUtils.isNotBlank(expressInfo.getAppKey()),ExpressInfo::getAppKey ,expressInfo.getAppKey());
        lqw.like(StringUtils.isNotBlank(expressInfo.getAppSecret()),ExpressInfo::getAppSecret ,expressInfo.getAppSecret());
        lqw.eq(expressInfo.getUpdatedTime() != null, ExpressInfo::getUpdatedTime ,expressInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExpressInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExpressInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ExpressInfo::getId);
        List<ExpressInfo> list = expressInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取快递信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "expressInfo::getInfo", name = "获取 快递信息 详细信息")
    public R<ExpressInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(expressInfoService.getById(id));
    }

    /**
     * 新增快递信息
     */
    @PostMapping
    @Node(value = "expressInfo::add", name = "新增 快递信息")
    public R<Boolean> add(@RequestBody ExpressInfo expressInfo) {
        return R.success(expressInfoService.save(expressInfo));
    }

    /**
     * 修改快递信息
     */
    @PutMapping
    @Node(value = "expressInfo::edit", name = "修改 快递信息")
    public R<Boolean> edit(@RequestBody ExpressInfo expressInfo) {
        return R.success(expressInfoService.updateById(expressInfo));
    }

    /**
     * 删除快递信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "expressInfo::remove", name = "删除 快递信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(expressInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 快递信息 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ExpressInfo.class)
    @Node(value = "expressInfo::exportExcel", name = "导出 快递信息 Excel")
    public Object exportExcel(ExpressInfo expressInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ExpressInfo> lqw = new LambdaQueryWrapper<ExpressInfo>();
        lqw.eq(expressInfo.getId() != null, ExpressInfo::getId ,expressInfo.getId());
        lqw.like(StringUtils.isNotBlank(expressInfo.getName()),ExpressInfo::getName ,expressInfo.getName());
        lqw.eq(expressInfo.getExpressType() != null, ExpressInfo::getExpressType ,expressInfo.getExpressType());
        lqw.eq(expressInfo.getStatus() != null, ExpressInfo::getStatus ,expressInfo.getStatus());
        lqw.like(StringUtils.isNotBlank(expressInfo.getAppKey()),ExpressInfo::getAppKey ,expressInfo.getAppKey());
        lqw.like(StringUtils.isNotBlank(expressInfo.getAppSecret()),ExpressInfo::getAppSecret ,expressInfo.getAppSecret());
        lqw.eq(expressInfo.getUpdatedTime() != null, ExpressInfo::getUpdatedTime ,expressInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExpressInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExpressInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ExpressInfo::getId);
        return expressInfoService.list(lqw);
    }

    /**
     * 导入 快递信息 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "expressInfo::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ExpressInfo.class, new ReadListener<ExpressInfo>() {
                @Override
                public void invoke(ExpressInfo expressInfo, AnalysisContext analysisContext) {
                    expressInfoService.save(expressInfo);
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