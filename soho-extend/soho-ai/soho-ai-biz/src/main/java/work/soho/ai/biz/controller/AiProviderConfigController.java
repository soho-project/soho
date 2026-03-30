package work.soho.ai.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.ai.biz.config.AiSysConfig;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.domain.AiProviderModelRel;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
    private static final String DEFAULT_PROVIDER_CODE = "chatgpt_codex_";
    private static final String DEFAULT_PROVIDER = "openai";
    private static final String DEFAULT_PROVIDER_UNIQUE_ID = null;
    private static final String DEFAULT_ENV = "prod";
    private static final String DEFAULT_BASE_URL = "https://chatgpt.com";
    private static final String DEFAULT_API_KEY_REF = "";
    private static final String DEFAULT_MODEL = "gpt-5.3-codex";
    private static final String DEFAULT_SUPPORTED_MODELS = "gpt-5.3-codex\n  gpt-5.2-codex\n  gpt-5.1-codex\n  gpt-5.1-codex-max\n  gpt-5-codex\n  codex-mini-latest";
    private static final Integer DEFAULT_RATE_LIMIT = 60;
    private static final Integer DEFAULT_TIMEOUT_MS = 60000;
    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer DEFAULT_WEIGHT = 1;
    private static final String DEFAULT_REMARK = "ChatGPT Codex Responses 适配";
    private static final List<Long> DEFAULT_MODEL_INFO_IDS = Arrays.asList(4L, 5L, 7L, 11L, 12L, 13L, 14L); // codex 默认关联模型


    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;
    private final AiSysConfig aiSysConfig;

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
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getProviderUniqueId()),AiProviderConfig::getProviderUniqueId ,aiProviderConfig.getProviderUniqueId());
        lqw.eq(aiProviderConfig.getRateLimit() != null, AiProviderConfig::getRateLimit ,aiProviderConfig.getRateLimit());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getRemark()),AiProviderConfig::getRemark ,aiProviderConfig.getRemark());
        lqw.eq(aiProviderConfig.getStatus() != null, AiProviderConfig::getStatus ,aiProviderConfig.getStatus());
        lqw.eq(aiProviderConfig.getTimeoutMs() != null, AiProviderConfig::getTimeoutMs ,aiProviderConfig.getTimeoutMs());
        lqw.eq(aiProviderConfig.getWeight() != null, AiProviderConfig::getWeight ,aiProviderConfig.getWeight());
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
        if (aiProviderConfig.getModelInfoIds() == null) {
            aiProviderConfig.setModelInfoIds(new ArrayList<>());
        }
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
     * 根据服务提供者唯一识别ID修改AI提供方配置表
     *
     * 主意 专供 codex api 同步
     */
    @PutMapping("/providerUniqueId/{providerUniqueId}")
    @Node(value = "aiProviderConfig::editByProviderUniqueId", name = "根据服务提供者唯一识别ID修改 AI提供方配置表")
    @ApiOperation(value = "根据服务提供者唯一识别ID修改 AI提供方配置表", notes = "根据服务提供者唯一识别ID修改 AI提供方配置表")
    public R<Boolean> editByProviderUniqueId(@PathVariable("providerUniqueId") String providerUniqueId,
                                             @RequestBody AiProviderConfig aiProviderConfig) {
        if (StringUtils.isBlank(providerUniqueId)) {
            return R.error("providerUniqueId不能为空");
        }
        AiProviderConfig existed = aiProviderConfigService.getOne(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getProviderUniqueId, providerUniqueId)
                .last("limit 1"));
        if (existed == null || existed.getId() == null) {
            AiProviderConfig toCreate = buildDefaultProviderConfig(providerUniqueId);
            mergeNonNullFields(toCreate, aiProviderConfig);
            toCreate.setProviderUniqueId(providerUniqueId);
            boolean saved = aiProviderConfigService.save(toCreate);
            if (saved && toCreate.getId() != null && toCreate.getModelInfoIds() != null) {
                aiProviderModelRelService.replaceRelations(toCreate.getId(), toCreate.getModelInfoIds());
            }
            return R.success(saved);
        }
        aiProviderConfig.setId(existed.getId());
        aiProviderConfig.setProviderUniqueId(providerUniqueId);
        boolean updated = aiProviderConfigService.updateById(aiProviderConfig);
        if (updated && aiProviderConfig.getModelInfoIds() != null) {
            aiProviderModelRelService.replaceRelations(existed.getId(), aiProviderConfig.getModelInfoIds());
        }
        return R.success(updated);
    }

    private AiProviderConfig buildDefaultProviderConfig(String providerUniqueId) {
        AiProviderConfig config = new AiProviderConfig();
        config.setCode(DEFAULT_PROVIDER_CODE + IDGeneratorUtils.uuid());
        config.setProvider(DEFAULT_PROVIDER);
        config.setProviderUniqueId(StringUtils.isBlank(providerUniqueId) ? DEFAULT_PROVIDER_UNIQUE_ID : providerUniqueId);
        config.setEnv(DEFAULT_ENV);
        config.setBaseUrl(DEFAULT_BASE_URL);
        config.setApiKeyRef(DEFAULT_API_KEY_REF);
        config.setDefaultModel(DEFAULT_MODEL);
        config.setSupportedModels(DEFAULT_SUPPORTED_MODELS);
        config.setConfigJson(buildDefaultConfigJson());
        config.setRateLimit(DEFAULT_RATE_LIMIT);
        config.setTimeoutMs(DEFAULT_TIMEOUT_MS);
        config.setStatus(DEFAULT_STATUS);
        config.setWeight(DEFAULT_WEIGHT);
        config.setRemark(DEFAULT_REMARK);
        config.setModelInfoIds(new ArrayList<>(DEFAULT_MODEL_INFO_IDS));
        return config;
    }

    private String buildDefaultConfigJson() {
        LinkedHashMap<String, Object> config = new LinkedHashMap<>();
        config.put("adapter", "codexResponses");
        config.put("codexResponsesPath", "/backend-api/codex/responses");
        config.put("store", false);
        config.put("streamSupported", true);
        config.put("billingEnabled", true);
        config.put("billingWalletTypeId", 1);
        config.put("promptPricePer1kTokens", 0.02);
        config.put("completionPricePer1kTokens", 0.08);

        String proxyType = aiSysConfig.getCodexProxyType();
        String proxyHost = aiSysConfig.getCodexProxyHost();
        Integer proxyPort = aiSysConfig.getCodexProxyPort();
        if (StringUtils.isNotBlank(proxyType) && StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
            config.put("proxyType", proxyType.trim());
            config.put("proxyHost", proxyHost.trim());
            config.put("proxyPort", proxyPort);
        }
        return JacksonUtils.toJson(config);
    }

    private void mergeNonNullFields(AiProviderConfig target, AiProviderConfig source) {
        if (target == null || source == null) {
            return;
        }
        if (StringUtils.isNotBlank(source.getApiKeyRef())) {
            target.setApiKeyRef(source.getApiKeyRef());
        }
        if (StringUtils.isNotBlank(source.getBaseUrl())) {
            target.setBaseUrl(source.getBaseUrl());
        }
        if (StringUtils.isNotBlank(source.getCode())) {
            target.setCode(source.getCode());
        }
        if (StringUtils.isNotBlank(source.getConfigJson())) {
            target.setConfigJson(source.getConfigJson());
        }
        if (StringUtils.isNotBlank(source.getDefaultModel())) {
            target.setDefaultModel(source.getDefaultModel());
        }
        if (StringUtils.isNotBlank(source.getSupportedModels())) {
            target.setSupportedModels(source.getSupportedModels());
        }
        if (StringUtils.isNotBlank(source.getEnv())) {
            target.setEnv(source.getEnv());
        }
        if (StringUtils.isNotBlank(source.getProvider())) {
            target.setProvider(source.getProvider());
        }
        if (source.getRateLimit() != null) {
            target.setRateLimit(source.getRateLimit());
        }
        if (StringUtils.isNotBlank(source.getRemark())) {
            target.setRemark(source.getRemark());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getTimeoutMs() != null) {
            target.setTimeoutMs(source.getTimeoutMs());
        }
        if (source.getWeight() != null) {
            target.setWeight(source.getWeight());
        }
        if (source.getModelInfoIds() != null) {
            target.setModelInfoIds(source.getModelInfoIds());
        }
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
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getProviderUniqueId()),AiProviderConfig::getProviderUniqueId ,aiProviderConfig.getProviderUniqueId());
        lqw.eq(aiProviderConfig.getRateLimit() != null, AiProviderConfig::getRateLimit ,aiProviderConfig.getRateLimit());
        lqw.like(StringUtils.isNotBlank(aiProviderConfig.getRemark()),AiProviderConfig::getRemark ,aiProviderConfig.getRemark());
        lqw.eq(aiProviderConfig.getStatus() != null, AiProviderConfig::getStatus ,aiProviderConfig.getStatus());
        lqw.eq(aiProviderConfig.getTimeoutMs() != null, AiProviderConfig::getTimeoutMs ,aiProviderConfig.getTimeoutMs());
        lqw.eq(aiProviderConfig.getWeight() != null, AiProviderConfig::getWeight ,aiProviderConfig.getWeight());
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
