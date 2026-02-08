package work.soho.ai.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import work.soho.ai.biz.domain.AiApp;
import work.soho.ai.biz.service.AiAppService;
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
 * @author i
 */
@Api(value="",tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/admin/aiApp" )
public class AiAppController {

    private final AiAppService aiAppService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "aiApp::list", name = "获取  列表")
    @ApiOperation(value = "获取  列表", notes = "获取  列表")
    public R<PageSerializable<AiApp>> list(AiApp aiApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AiApp> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiApp.getId() != null, AiApp::getId ,aiApp.getId());
        lqw.like(StringUtils.isNotBlank(aiApp.getCode()),AiApp::getCode ,aiApp.getCode());
        lqw.like(StringUtils.isNotBlank(aiApp.getTitle()),AiApp::getTitle ,aiApp.getTitle());
        lqw.like(StringUtils.isNotBlank(aiApp.getDescription()),AiApp::getDescription ,aiApp.getDescription());
        lqw.like(StringUtils.isNotBlank(aiApp.getSystemPrompt()),AiApp::getSystemPrompt ,aiApp.getSystemPrompt());
        lqw.eq(aiApp.getStatus() != null, AiApp::getStatus ,aiApp.getStatus());
        lqw.eq(aiApp.getProviderId() != null, AiApp::getProviderId ,aiApp.getProviderId());
        lqw.eq(aiApp.getUpdatedTime() != null, AiApp::getUpdatedTime ,aiApp.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiApp::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiApp::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiApp::getId);
        List<AiApp> list = aiAppService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "aiApp::getInfo", name = "获取  详细信息")
    @ApiOperation(value = "获取  详细信息", notes = "获取  详细信息")
    public R<AiApp> getInfo(@PathVariable("id" ) Long id) {
        return R.success(aiAppService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "aiApp::add", name = "新增 ")
    @ApiOperation(value = "新增 ", notes = "新增 ")
    public R<Boolean> add(@RequestBody AiApp aiApp) {
        return R.success(aiAppService.save(aiApp));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "aiApp::edit", name = "修改 ")
    @ApiOperation(value = "修改 ", notes = "修改 ")
    public R<Boolean> edit(@RequestBody AiApp aiApp) {
        return R.success(aiAppService.updateById(aiApp));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "aiApp::remove", name = "删除 ")
    @ApiOperation(value = "删除 ", notes = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiAppService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = AiApp.class)
    @Node(value = "aiApp::exportExcel", name = "导出  Excel")
    @ApiOperation(value = "导出  Excel", notes = "导出  Excel")
    public Object exportExcel(AiApp aiApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<AiApp> lqw = new LambdaQueryWrapper<AiApp>();
        lqw.eq(aiApp.getId() != null, AiApp::getId ,aiApp.getId());
        lqw.like(StringUtils.isNotBlank(aiApp.getCode()),AiApp::getCode ,aiApp.getCode());
        lqw.like(StringUtils.isNotBlank(aiApp.getTitle()),AiApp::getTitle ,aiApp.getTitle());
        lqw.like(StringUtils.isNotBlank(aiApp.getDescription()),AiApp::getDescription ,aiApp.getDescription());
        lqw.like(StringUtils.isNotBlank(aiApp.getSystemPrompt()),AiApp::getSystemPrompt ,aiApp.getSystemPrompt());
        lqw.eq(aiApp.getStatus() != null, AiApp::getStatus ,aiApp.getStatus());
        lqw.eq(aiApp.getProviderId() != null, AiApp::getProviderId ,aiApp.getProviderId());
        lqw.eq(aiApp.getUpdatedTime() != null, AiApp::getUpdatedTime ,aiApp.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiApp::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiApp::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiApp::getId);
        return aiAppService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "aiApp::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入  Excel", notes = "导入  Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), AiApp.class, new ReadListener<AiApp>() {
                @Override
                public void invoke(AiApp aiApp, AnalysisContext analysisContext) {
                    aiAppService.save(aiApp);
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