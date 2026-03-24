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
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.service.AiApiCallLogService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * ai请求日志Controller
 *
 * @author fang
 */
@Api(value="ai请求日志",tags = "ai请求日志")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/admin/aiApiCallLog" )
public class AiApiCallLogController {

    private final AiApiCallLogService aiApiCallLogService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询ai请求日志列表
     */
    @GetMapping("/list")
    @Node(value = "aiApiCallLog::list", name = "获取 ai请求日志 列表")
    @ApiOperation(value = "获取 ai请求日志 列表", notes = "获取 ai请求日志 列表")
    public R<PageSerializable<AiApiCallLog>> list(AiApiCallLog aiApiCallLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AiApiCallLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiApiCallLog.getId() != null, AiApiCallLog::getId ,aiApiCallLog.getId());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getRequestId()),AiApiCallLog::getRequestId ,aiApiCallLog.getRequestId());
        lqw.eq(aiApiCallLog.getUserId() != null, AiApiCallLog::getUserId ,aiApiCallLog.getUserId());
        lqw.eq(aiApiCallLog.getApiKeyId() != null, AiApiCallLog::getApiKeyId ,aiApiCallLog.getApiKeyId());
        lqw.eq(aiApiCallLog.getProviderConfigId() != null, AiApiCallLog::getProviderConfigId ,aiApiCallLog.getProviderConfigId());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getEndpoint()),AiApiCallLog::getEndpoint ,aiApiCallLog.getEndpoint());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getModel()),AiApiCallLog::getModel ,aiApiCallLog.getModel());
        lqw.eq(aiApiCallLog.getPromptTokens() != null, AiApiCallLog::getPromptTokens ,aiApiCallLog.getPromptTokens());
        lqw.eq(aiApiCallLog.getCompletionTokens() != null, AiApiCallLog::getCompletionTokens ,aiApiCallLog.getCompletionTokens());
        lqw.eq(aiApiCallLog.getTotalTokens() != null, AiApiCallLog::getTotalTokens ,aiApiCallLog.getTotalTokens());
        lqw.eq(aiApiCallLog.getAmount() != null, AiApiCallLog::getAmount ,aiApiCallLog.getAmount());
        lqw.eq(aiApiCallLog.getStatus() != null, AiApiCallLog::getStatus ,aiApiCallLog.getStatus());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getErrorMessage()),AiApiCallLog::getErrorMessage ,aiApiCallLog.getErrorMessage());
        lqw.eq(aiApiCallLog.getWalletLogId() != null, AiApiCallLog::getWalletLogId ,aiApiCallLog.getWalletLogId());
        lqw.eq(aiApiCallLog.getUpdatedTime() != null, AiApiCallLog::getUpdatedTime ,aiApiCallLog.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiApiCallLog::getId);
        List<AiApiCallLog> list = aiApiCallLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取ai请求日志详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "aiApiCallLog::getInfo", name = "获取 ai请求日志 详细信息")
    @ApiOperation(value = "获取 ai请求日志 详细信息", notes = "获取 ai请求日志 详细信息")
    public R<AiApiCallLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(aiApiCallLogService.getById(id));
    }

    /**
     * 新增ai请求日志
     */
    @PostMapping
    @Node(value = "aiApiCallLog::add", name = "新增 ai请求日志")
    @ApiOperation(value = "新增 ai请求日志", notes = "新增 ai请求日志")
    public R<Boolean> add(@RequestBody AiApiCallLog aiApiCallLog) {
        return R.success(aiApiCallLogService.save(aiApiCallLog));
    }

    /**
     * 修改ai请求日志
     */
    @PutMapping
    @Node(value = "aiApiCallLog::edit", name = "修改 ai请求日志")
    @ApiOperation(value = "修改 ai请求日志", notes = "修改 ai请求日志")
    public R<Boolean> edit(@RequestBody AiApiCallLog aiApiCallLog) {
        return R.success(aiApiCallLogService.updateById(aiApiCallLog));
    }

    /**
     * 删除ai请求日志
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "aiApiCallLog::remove", name = "删除 ai请求日志")
    @ApiOperation(value = "删除 ai请求日志", notes = "删除 ai请求日志")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiApiCallLogService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 ai请求日志 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = AiApiCallLog.class)
    @Node(value = "aiApiCallLog::exportExcel", name = "导出 ai请求日志 Excel")
    @ApiOperation(value = "导出 ai请求日志 Excel", notes = "导出 ai请求日志 Excel")
    public Object exportExcel(AiApiCallLog aiApiCallLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<AiApiCallLog> lqw = new LambdaQueryWrapper<AiApiCallLog>();
        lqw.eq(aiApiCallLog.getId() != null, AiApiCallLog::getId ,aiApiCallLog.getId());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getRequestId()),AiApiCallLog::getRequestId ,aiApiCallLog.getRequestId());
        lqw.eq(aiApiCallLog.getUserId() != null, AiApiCallLog::getUserId ,aiApiCallLog.getUserId());
        lqw.eq(aiApiCallLog.getApiKeyId() != null, AiApiCallLog::getApiKeyId ,aiApiCallLog.getApiKeyId());
        lqw.eq(aiApiCallLog.getProviderConfigId() != null, AiApiCallLog::getProviderConfigId ,aiApiCallLog.getProviderConfigId());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getEndpoint()),AiApiCallLog::getEndpoint ,aiApiCallLog.getEndpoint());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getModel()),AiApiCallLog::getModel ,aiApiCallLog.getModel());
        lqw.eq(aiApiCallLog.getPromptTokens() != null, AiApiCallLog::getPromptTokens ,aiApiCallLog.getPromptTokens());
        lqw.eq(aiApiCallLog.getCompletionTokens() != null, AiApiCallLog::getCompletionTokens ,aiApiCallLog.getCompletionTokens());
        lqw.eq(aiApiCallLog.getTotalTokens() != null, AiApiCallLog::getTotalTokens ,aiApiCallLog.getTotalTokens());
        lqw.eq(aiApiCallLog.getAmount() != null, AiApiCallLog::getAmount ,aiApiCallLog.getAmount());
        lqw.eq(aiApiCallLog.getStatus() != null, AiApiCallLog::getStatus ,aiApiCallLog.getStatus());
        lqw.like(StringUtils.isNotBlank(aiApiCallLog.getErrorMessage()),AiApiCallLog::getErrorMessage ,aiApiCallLog.getErrorMessage());
        lqw.eq(aiApiCallLog.getWalletLogId() != null, AiApiCallLog::getWalletLogId ,aiApiCallLog.getWalletLogId());
        lqw.eq(aiApiCallLog.getUpdatedTime() != null, AiApiCallLog::getUpdatedTime ,aiApiCallLog.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiApiCallLog::getId);
        return aiApiCallLogService.list(lqw);
    }

    /**
     * 导入 ai请求日志 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "aiApiCallLog::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 ai请求日志 Excel", notes = "导入 ai请求日志 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), AiApiCallLog.class, new ReadListener<AiApiCallLog>() {
                @Override
                public void invoke(AiApiCallLog aiApiCallLog, AnalysisContext analysisContext) {
                    aiApiCallLogService.save(aiApiCallLog);
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