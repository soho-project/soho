package work.soho.open.biz.controller;

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
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.service.OpenAppService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * Controller
 *
 * @author fang
 */
@Api(tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openApp" )
public class OpenAppController {

    private final OpenAppService openAppService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "openApp::list", name = "获取  列表")
    public R<PageSerializable<OpenApp>> list(OpenApp openApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApp> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openApp.getId() != null, OpenApp::getId ,openApp.getId());
        lqw.eq(openApp.getUserId() != null, OpenApp::getUserId ,openApp.getUserId());
        lqw.like(StringUtils.isNotBlank(openApp.getAppName()),OpenApp::getAppName ,openApp.getAppName());
        lqw.like(StringUtils.isNotBlank(openApp.getAppKey()),OpenApp::getAppKey ,openApp.getAppKey());
        lqw.like(StringUtils.isNotBlank(openApp.getAppSecret()),OpenApp::getAppSecret ,openApp.getAppSecret());
        lqw.eq(openApp.getStatus() != null, OpenApp::getStatus ,openApp.getStatus());
        lqw.eq(openApp.getQpsLimit() != null, OpenApp::getQpsLimit ,openApp.getQpsLimit());
        lqw.like(StringUtils.isNotBlank(openApp.getRemark()),OpenApp::getRemark ,openApp.getRemark());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openApp.getUpdatedTime() != null, OpenApp::getUpdatedTime ,openApp.getUpdatedTime());
        lqw.orderByDesc(OpenApp::getId);
        List<OpenApp> list = openAppService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openApp::getInfo", name = "获取  详细信息")
    public R<OpenApp> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "openApp::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenApp openApp) {
        return R.success(openAppService.save(openApp));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "openApp::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenApp openApp) {
        return R.success(openAppService.updateById(openApp));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openApp::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "openApp::options", name = "获取  选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<OpenApp> list = openAppService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(OpenApp item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getAppName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenApp.class)
    @Node(value = "openApp::exportExcel", name = "导出  Excel")
    public Object exportExcel(OpenApp openApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenApp> lqw = new LambdaQueryWrapper<OpenApp>();
        lqw.eq(openApp.getId() != null, OpenApp::getId ,openApp.getId());
        lqw.eq(openApp.getUserId() != null, OpenApp::getUserId ,openApp.getUserId());
        lqw.like(StringUtils.isNotBlank(openApp.getAppName()),OpenApp::getAppName ,openApp.getAppName());
        lqw.like(StringUtils.isNotBlank(openApp.getAppKey()),OpenApp::getAppKey ,openApp.getAppKey());
        lqw.like(StringUtils.isNotBlank(openApp.getAppSecret()),OpenApp::getAppSecret ,openApp.getAppSecret());
        lqw.eq(openApp.getStatus() != null, OpenApp::getStatus ,openApp.getStatus());
        lqw.eq(openApp.getQpsLimit() != null, OpenApp::getQpsLimit ,openApp.getQpsLimit());
        lqw.like(StringUtils.isNotBlank(openApp.getRemark()),OpenApp::getRemark ,openApp.getRemark());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openApp.getUpdatedTime() != null, OpenApp::getUpdatedTime ,openApp.getUpdatedTime());
        lqw.orderByDesc(OpenApp::getId);
        return openAppService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openApp::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenApp.class, new ReadListener<OpenApp>() {
                @Override
                public void invoke(OpenApp openApp, AnalysisContext analysisContext) {
                    openAppService.save(openApp);
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