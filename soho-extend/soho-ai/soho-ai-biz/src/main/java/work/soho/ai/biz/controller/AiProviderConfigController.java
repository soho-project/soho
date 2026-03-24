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
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.domain.AiProviderModelRel;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * AI提供方配置表Controller
 *
 * @author i
 */
@Api(value="AI提供方配置表",tags = "AI提供方配置表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/admin/aiProviderConfig" )
public class AiProviderConfigController {

    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;

    /**
     * 查询AI提供方配置表列表
     */
    @GetMapping("/list")
    @Node(value = "aiProviderConfig::list", name = "获取 AI提供方配置表 列表")
    @ApiOperation(value = "获取 AI提供方配置表 列表", notes = "获取 AI提供方配置表 列表")
    public R<PageSerializable<AiProviderConfig>> list(AiProviderConfig aiProviderConfig, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AiProviderConfig> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getApiKeyRef()),AiProviderConfig::getApiKeyRef ,aiProviderConfig.getApiKeyRef());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getBaseUrl()),AiProviderConfig::getBaseUrl ,aiProviderConfig.getBaseUrl());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getCode()),AiProviderConfig::getCode ,aiProviderConfig.getCode());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getConfigJson()),AiProviderConfig::getConfigJson ,aiProviderConfig.getConfigJson());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiProviderConfig::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiProviderConfig::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getDefaultModel()),AiProviderConfig::getDefaultModel ,aiProviderConfig.getDefaultModel());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getSupportedModels()),AiProviderConfig::getSupportedModels ,aiProviderConfig.getSupportedModels());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getEnv()),AiProviderConfig::getEnv ,aiProviderConfig.getEnv());
        lqw.eq(aiProviderConfig.getId() != null, AiProviderConfig::getId ,aiProviderConfig.getId());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getProvider()),AiProviderConfig::getProvider ,aiProviderConfig.getProvider());
        lqw.eq(aiProviderConfig.getRateLimit() != null, AiProviderConfig::getRateLimit ,aiProviderConfig.getRateLimit());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getRemark()),AiProviderConfig::getRemark ,aiProviderConfig.getRemark());
        lqw.eq(aiProviderConfig.getStatus() != null, AiProviderConfig::getStatus ,aiProviderConfig.getStatus());
        lqw.eq(aiProviderConfig.getTimeoutMs() != null, AiProviderConfig::getTimeoutMs ,aiProviderConfig.getTimeoutMs());
        lqw.eq(aiProviderConfig.getUpdatedTime() != null, AiProviderConfig::getUpdatedTime ,aiProviderConfig.getUpdatedTime());
        lqw.orderByDesc(AiProviderConfig::getId);
        List<AiProviderConfig> list = aiProviderConfigService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取AI提供方配置表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "aiProviderConfig::getInfo", name = "获取 AI提供方配置表 详细信息")
    @ApiOperation(value = "获取 AI提供方配置表 详细信息", notes = "获取 AI提供方配置表 详细信息")
    public R<AiProviderConfig> getInfo(@PathVariable("id" ) Long id) {
        AiProviderConfig aiProviderConfig = aiProviderConfigService.getById(id);
        if (aiProviderConfig != null) {
            aiProviderConfig.setModelInfoIds(aiProviderModelRelService.listEnabledModelIdsByProviderConfigId(id));
        }
        return R.success(aiProviderConfig);
    }

    /**
     * 新增AI提供方配置表
     */
    @PostMapping
    @Node(value = "aiProviderConfig::add", name = "新增 AI提供方配置表")
    @ApiOperation(value = "新增 AI提供方配置表", notes = "新增 AI提供方配置表")
    public R<Boolean> add(@RequestBody AiProviderConfig aiProviderConfig) {
        boolean saved = aiProviderConfigService.save(aiProviderConfig);
        if (saved && aiProviderConfig.getId() != null && aiProviderConfig.getModelInfoIds() != null) {
            aiProviderModelRelService.replaceRelations(aiProviderConfig.getId(), aiProviderConfig.getModelInfoIds());
        }
        return R.success(saved);
    }

    /**
     * 修改AI提供方配置表
     */
    @PutMapping
    @Node(value = "aiProviderConfig::edit", name = "修改 AI提供方配置表")
    @ApiOperation(value = "修改 AI提供方配置表", notes = "修改 AI提供方配置表")
    public R<Boolean> edit(@RequestBody AiProviderConfig aiProviderConfig) {
        boolean updated = aiProviderConfigService.updateById(aiProviderConfig);
        if (updated && aiProviderConfig.getId() != null && aiProviderConfig.getModelInfoIds() != null) {
            aiProviderModelRelService.replaceRelations(aiProviderConfig.getId(), aiProviderConfig.getModelInfoIds());
        }
        return R.success(updated);
    }

    /**
     * 删除AI提供方配置表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "aiProviderConfig::remove", name = "删除 AI提供方配置表")
    @ApiOperation(value = "删除 AI提供方配置表", notes = "删除 AI提供方配置表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        aiProviderModelRelService.remove(new LambdaQueryWrapper<AiProviderModelRel>()
                .in(AiProviderModelRel::getProviderConfigId, idList));
        return R.success(aiProviderConfigService.removeByIds(idList));
    }

    /**
     * 获取该AI提供方配置表 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "aiProviderConfig::options", name = "获取 AI提供方配置表 选项")
    @ApiOperation(value = "获取 AI提供方配置表 选项", notes = "获取 AI提供方配置表 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<AiProviderConfig> list = aiProviderConfigService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(AiProviderConfig item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getCode());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 AI提供方配置表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = AiProviderConfig.class)
    @Node(value = "aiProviderConfig::exportExcel", name = "导出 AI提供方配置表 Excel")
    @ApiOperation(value = "导出 AI提供方配置表 Excel", notes = "导出 AI提供方配置表 Excel")
    public Object exportExcel(AiProviderConfig aiProviderConfig, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<AiProviderConfig> lqw = new LambdaQueryWrapper<AiProviderConfig>();
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getApiKeyRef()),AiProviderConfig::getApiKeyRef ,aiProviderConfig.getApiKeyRef());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getBaseUrl()),AiProviderConfig::getBaseUrl ,aiProviderConfig.getBaseUrl());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getCode()),AiProviderConfig::getCode ,aiProviderConfig.getCode());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getConfigJson()),AiProviderConfig::getConfigJson ,aiProviderConfig.getConfigJson());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiProviderConfig::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiProviderConfig::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getDefaultModel()),AiProviderConfig::getDefaultModel ,aiProviderConfig.getDefaultModel());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getSupportedModels()),AiProviderConfig::getSupportedModels ,aiProviderConfig.getSupportedModels());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getEnv()),AiProviderConfig::getEnv ,aiProviderConfig.getEnv());
        lqw.eq(aiProviderConfig.getId() != null, AiProviderConfig::getId ,aiProviderConfig.getId());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getProvider()),AiProviderConfig::getProvider ,aiProviderConfig.getProvider());
        lqw.eq(aiProviderConfig.getRateLimit() != null, AiProviderConfig::getRateLimit ,aiProviderConfig.getRateLimit());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getRemark()),AiProviderConfig::getRemark ,aiProviderConfig.getRemark());
        lqw.eq(aiProviderConfig.getStatus() != null, AiProviderConfig::getStatus ,aiProviderConfig.getStatus());
        lqw.eq(aiProviderConfig.getTimeoutMs() != null, AiProviderConfig::getTimeoutMs ,aiProviderConfig.getTimeoutMs());
        lqw.eq(aiProviderConfig.getUpdatedTime() != null, AiProviderConfig::getUpdatedTime ,aiProviderConfig.getUpdatedTime());
        lqw.orderByDesc(AiProviderConfig::getId);
        return aiProviderConfigService.list(lqw);
    }

    /**
     * 导入 AI提供方配置表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "aiProviderConfig::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 AI提供方配置表 Excel", notes = "导入 AI提供方配置表 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), AiProviderConfig.class, new ReadListener<AiProviderConfig>() {
                @Override
                public void invoke(AiProviderConfig aiProviderConfig, AnalysisContext analysisContext) {
                    aiProviderConfigService.save(aiProviderConfig);
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
