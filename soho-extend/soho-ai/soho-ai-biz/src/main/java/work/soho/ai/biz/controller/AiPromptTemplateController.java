package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.ai.biz.domain.AiPromptTemplate;
import work.soho.ai.biz.service.AiPromptTemplateService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import java.util.Arrays;
import java.util.List;

/**
 * AI 提示词模板管理。
 */
@Api(tags = "AI提示词模板")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin/aiPromptTemplate")
public class AiPromptTemplateController {
    private static final String PROMPT_TEMPLATE_CODE_PREFIX = "prompt_tpl_";

    private final AiPromptTemplateService aiPromptTemplateService;

    /**
     * 查询提示词模板列表。
     *
     * @param aiPromptTemplate 查询条件
     * @param betweenCreatedTimeRequest 时间范围
     * @return 模板分页列表
     */
    @GetMapping("/list")
    @Node(value = "aiPromptTemplate::list", name = "获取 AI提示词模板 列表")
    @ApiOperation(value = "获取 AI提示词模板 列表", notes = "获取 AI提示词模板 列表")
    public R<PageSerializable<AiPromptTemplate>> list(AiPromptTemplate aiPromptTemplate,
                                                      BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        PageUtils.startPage();
        LambdaQueryWrapper<AiPromptTemplate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiPromptTemplate.getId() != null, AiPromptTemplate::getId, aiPromptTemplate.getId());
        lqw.like(StringUtils.isNotBlank(aiPromptTemplate.getCode()), AiPromptTemplate::getCode, aiPromptTemplate.getCode());
        lqw.like(StringUtils.isNotBlank(aiPromptTemplate.getName()), AiPromptTemplate::getName, aiPromptTemplate.getName());
        lqw.eq(StringUtils.isNotBlank(aiPromptTemplate.getSceneCode()), AiPromptTemplate::getSceneCode, aiPromptTemplate.getSceneCode());
        lqw.eq(StringUtils.isNotBlank(aiPromptTemplate.getProviderCode()), AiPromptTemplate::getProviderCode, aiPromptTemplate.getProviderCode());
        lqw.eq(StringUtils.isNotBlank(aiPromptTemplate.getModelPattern()), AiPromptTemplate::getModelPattern, aiPromptTemplate.getModelPattern());
        lqw.eq(aiPromptTemplate.getVersion() != null, AiPromptTemplate::getVersion, aiPromptTemplate.getVersion());
        lqw.eq(aiPromptTemplate.getStatus() != null, AiPromptTemplate::getStatus, aiPromptTemplate.getStatus());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null,
                AiPromptTemplate::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null,
                AiPromptTemplate::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiPromptTemplate::getVersion).orderByDesc(AiPromptTemplate::getId);
        List<AiPromptTemplate> list = aiPromptTemplateService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取提示词模板详情。
     *
     * @param id 模板ID
     * @return 模板详情
     */
    @GetMapping("/{id}")
    @Node(value = "aiPromptTemplate::getInfo", name = "获取 AI提示词模板 详细信息")
    @ApiOperation(value = "获取 AI提示词模板 详细信息", notes = "获取 AI提示词模板 详细信息")
    public R<AiPromptTemplate> getInfo(@PathVariable("id") Long id) {
        return R.success(aiPromptTemplateService.getById(id));
    }

    /**
     * 新增提示词模板。
     *
     * @param aiPromptTemplate 模板数据
     * @return 是否成功
     */
    @PostMapping
    @Node(value = "aiPromptTemplate::add", name = "新增 AI提示词模板")
    @ApiOperation(value = "新增 AI提示词模板", notes = "新增 AI提示词模板")
    public R<Boolean> add(@RequestBody AiPromptTemplate aiPromptTemplate) {
        fillGeneratedCode(aiPromptTemplate);
        return R.success(aiPromptTemplateService.save(aiPromptTemplate));
    }

    /**
     * 修改提示词模板。
     *
     * @param aiPromptTemplate 模板数据
     * @return 是否成功
     */
    @PutMapping
    @Node(value = "aiPromptTemplate::edit", name = "修改 AI提示词模板")
    @ApiOperation(value = "修改 AI提示词模板", notes = "修改 AI提示词模板")
    public R<Boolean> edit(@RequestBody AiPromptTemplate aiPromptTemplate) {
        fillGeneratedCode(aiPromptTemplate);
        return R.success(aiPromptTemplateService.updateById(aiPromptTemplate));
    }

    /**
     * 删除提示词模板。
     *
     * @param ids 模板ID数组
     * @return 是否成功
     */
    @DeleteMapping("/{ids}")
    @Node(value = "aiPromptTemplate::remove", name = "删除 AI提示词模板")
    @ApiOperation(value = "删除 AI提示词模板", notes = "删除 AI提示词模板")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiPromptTemplateService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 当模板编码为空时自动生成编码。
     *
     * @param aiPromptTemplate 提示词模板
     */
    private void fillGeneratedCode(AiPromptTemplate aiPromptTemplate) {
        if (aiPromptTemplate == null || StringUtils.isNotBlank(aiPromptTemplate.getCode())) {
            return;
        }
        if (aiPromptTemplate.getId() != null) {
            AiPromptTemplate existed = aiPromptTemplateService.getById(aiPromptTemplate.getId());
            if (existed != null && StringUtils.isNotBlank(existed.getCode())) {
                aiPromptTemplate.setCode(existed.getCode());
                return;
            }
        }
        aiPromptTemplate.setCode(PROMPT_TEMPLATE_CODE_PREFIX + IDGeneratorUtils.snowflake());
    }
}
