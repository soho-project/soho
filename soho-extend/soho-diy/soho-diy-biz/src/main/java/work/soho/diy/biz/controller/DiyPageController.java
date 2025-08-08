package work.soho.diy.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.diy.biz.domain.DiyPage;
import work.soho.diy.biz.service.DiyPageService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
/**
 * DIY页面Controller
 *
 * @author fang
 */
@Api(tags = "DIY页面")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/diy/admin/diyPage" )
public class DiyPageController {
    private final DiyPageService diyPageService;

    /**
     * 查询DIY页面列表
     */
    @GetMapping("/list")
    @Node(value = "diyPage::list", name = "获取 DIY页面 列表")
    public R<PageSerializable<DiyPage>> list(DiyPage diyPage, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<DiyPage> lqw = new LambdaQueryWrapper<>();
        lqw.eq(diyPage.getId() != null, DiyPage::getId ,diyPage.getId());
        lqw.like(StringUtils.isNotBlank(diyPage.getRoute()),DiyPage::getRoute ,diyPage.getRoute());
        lqw.like(StringUtils.isNotBlank(diyPage.getTitle()),DiyPage::getTitle ,diyPage.getTitle());
        lqw.like(StringUtils.isNotBlank(diyPage.getNotes()),DiyPage::getNotes ,diyPage.getNotes());
        lqw.eq(diyPage.getVersion() != null, DiyPage::getVersion ,diyPage.getVersion());
        lqw.eq(diyPage.getStatus() != null, DiyPage::getStatus ,diyPage.getStatus());
        lqw.eq(diyPage.getUpdatedTime() != null, DiyPage::getUpdatedTime ,diyPage.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, DiyPage::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, DiyPage::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(DiyPage::getId);
        List<DiyPage> list = diyPageService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取DIY页面详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "diyPage::getInfo", name = "获取 DIY页面 详细信息")
    public R<DiyPage> getInfo(@PathVariable("id" ) Long id) {
        return R.success(diyPageService.getById(id));
    }

    /**
     * 新增DIY页面
     */
    @PostMapping
    @Node(value = "diyPage::add", name = "新增 DIY页面")
    public R<Boolean> add(@RequestBody DiyPage diyPage) {
        diyPage.setUpdatedTime(LocalDateTime.now());
        diyPage.setCreatedTime(LocalDateTime.now());
        diyPage.setVersion(System.currentTimeMillis());
        return R.success(diyPageService.save(diyPage));
    }

    /**
     * 修改DIY页面
     */
    @PutMapping
    @Node(value = "diyPage::edit", name = "修改 DIY页面")
    public R<Boolean> edit(@RequestBody DiyPage diyPage) {
        return R.success(diyPageService.updateById(diyPage));
    }

    /**
     * 删除DIY页面
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "diyPage::remove", name = "删除 DIY页面")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(diyPageService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 DIY页面 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = DiyPage.class)
    @Node(value = "diyPage::exportExcel", name = "导出 DIY页面 Excel")
    public Object exportExcel(DiyPage diyPage, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<DiyPage> lqw = new LambdaQueryWrapper<DiyPage>();
        lqw.eq(diyPage.getId() != null, DiyPage::getId ,diyPage.getId());
        lqw.like(StringUtils.isNotBlank(diyPage.getRoute()),DiyPage::getRoute ,diyPage.getRoute());
        lqw.like(StringUtils.isNotBlank(diyPage.getTitle()),DiyPage::getTitle ,diyPage.getTitle());
        lqw.like(StringUtils.isNotBlank(diyPage.getNotes()),DiyPage::getNotes ,diyPage.getNotes());
        lqw.eq(diyPage.getVersion() != null, DiyPage::getVersion ,diyPage.getVersion());
        lqw.eq(diyPage.getStatus() != null, DiyPage::getStatus ,diyPage.getStatus());
        lqw.eq(diyPage.getUpdatedTime() != null, DiyPage::getUpdatedTime ,diyPage.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, DiyPage::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, DiyPage::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(DiyPage::getId);
        return diyPageService.list(lqw);
    }

    /**
     * 导入 DIY页面 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "diyPage::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), DiyPage.class, new ReadListener<DiyPage>() {
                @Override
                public void invoke(DiyPage diyPage, AnalysisContext analysisContext) {
                    diyPageService.save(diyPage);
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