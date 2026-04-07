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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.service.AiModelInfoService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(value = "AI模型信息表", tags = "AI模型信息表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/admin/aiModelInfo")
public class AiModelInfoController {
    private final AiModelInfoService aiModelInfoService;

    @GetMapping("/list")
    @Node(value = "aiModelInfo::list", name = "获取 AI模型信息表 列表")
    @ApiOperation(value = "获取 AI模型信息表 列表", notes = "获取 AI模型信息表 列表")
    public R<PageSerializable<AiModelInfo>> list(AiModelInfo aiModelInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        PageUtils.startPage();
        LambdaQueryWrapper<AiModelInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiModelInfo.getId() != null, AiModelInfo::getId, aiModelInfo.getId());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelName()), AiModelInfo::getModelName, aiModelInfo.getModelName());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelDesc()), AiModelInfo::getModelDesc, aiModelInfo.getModelDesc());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelDetail()), AiModelInfo::getModelDetail, aiModelInfo.getModelDetail());
        lqw.eq(StringUtils.isNotBlank(aiModelInfo.getModelTag()), AiModelInfo::getModelTag, aiModelInfo.getModelTag());
        lqw.eq(aiModelInfo.getStatus() != null, AiModelInfo::getStatus, aiModelInfo.getStatus());
        lqw.eq(aiModelInfo.getPromptPrice() != null, AiModelInfo::getPromptPrice, aiModelInfo.getPromptPrice());
        lqw.eq(aiModelInfo.getCompletionPrice() != null, AiModelInfo::getCompletionPrice, aiModelInfo.getCompletionPrice());
        lqw.eq(aiModelInfo.getSort() != null, AiModelInfo::getSort, aiModelInfo.getSort());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null, AiModelInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null, AiModelInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByAsc(AiModelInfo::getSort).orderByDesc(AiModelInfo::getId);
        return R.success(new PageSerializable<>(aiModelInfoService.list(lqw)));
    }

    @GetMapping("/{id}")
    @Node(value = "aiModelInfo::getInfo", name = "获取 AI模型信息表 详细信息")
    @ApiOperation(value = "获取 AI模型信息表 详细信息", notes = "获取 AI模型信息表 详细信息")
    public R<AiModelInfo> getInfo(@PathVariable("id") Long id) {
        return R.success(aiModelInfoService.getById(id));
    }

    @PostMapping
    @Node(value = "aiModelInfo::add", name = "新增 AI模型信息表")
    @ApiOperation(value = "新增 AI模型信息表", notes = "新增 AI模型信息表")
    public R<Boolean> add(@RequestBody AiModelInfo aiModelInfo) {
        return R.success(aiModelInfoService.save(aiModelInfo));
    }

    @PutMapping
    @Node(value = "aiModelInfo::edit", name = "修改 AI模型信息表")
    @ApiOperation(value = "修改 AI模型信息表", notes = "修改 AI模型信息表")
    public R<Boolean> edit(@RequestBody AiModelInfo aiModelInfo) {
        return R.success(aiModelInfoService.updateById(aiModelInfo));
    }

    @DeleteMapping("/{ids}")
    @Node(value = "aiModelInfo::remove", name = "删除 AI模型信息表")
    @ApiOperation(value = "删除 AI模型信息表", notes = "删除 AI模型信息表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiModelInfoService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("options")
    @Node(value = "aiModelInfo::options", name = "获取 AI模型信息表 选项")
    @ApiOperation(value = "获取 AI模型信息表 选项", notes = "获取 AI模型信息表 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<OptionVo<Long, String>> options = new ArrayList<>();
        for (AiModelInfo item : aiModelInfoService.list()) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getModelName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = AiModelInfo.class)
    @Node(value = "aiModelInfo::exportExcel", name = "导出 AI模型信息表 Excel")
    @ApiOperation(value = "导出 AI模型信息表 Excel", notes = "导出 AI模型信息表 Excel")
    public Object exportExcel(AiModelInfo aiModelInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        LambdaQueryWrapper<AiModelInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiModelInfo.getId() != null, AiModelInfo::getId, aiModelInfo.getId());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelName()), AiModelInfo::getModelName, aiModelInfo.getModelName());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelDesc()), AiModelInfo::getModelDesc, aiModelInfo.getModelDesc());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelDetail()), AiModelInfo::getModelDetail, aiModelInfo.getModelDetail());
        lqw.eq(StringUtils.isNotBlank(aiModelInfo.getModelTag()), AiModelInfo::getModelTag, aiModelInfo.getModelTag());
        lqw.eq(aiModelInfo.getStatus() != null, AiModelInfo::getStatus, aiModelInfo.getStatus());
        lqw.eq(aiModelInfo.getPromptPrice() != null, AiModelInfo::getPromptPrice, aiModelInfo.getPromptPrice());
        lqw.eq(aiModelInfo.getCompletionPrice() != null, AiModelInfo::getCompletionPrice, aiModelInfo.getCompletionPrice());
        lqw.eq(aiModelInfo.getSort() != null, AiModelInfo::getSort, aiModelInfo.getSort());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null, AiModelInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null, AiModelInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByAsc(AiModelInfo::getSort).orderByDesc(AiModelInfo::getId);
        return aiModelInfoService.list(lqw);
    }

    @PostMapping("/importExcel")
    @Node(value = "aiModelInfo::importExcel", name = "导入 AI模型信息表 Excel")
    @ApiOperation(value = "导入 AI模型信息表 Excel", notes = "导入 AI模型信息表 Excel")
    public R importExcel(@RequestParam(value = "file") MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), AiModelInfo.class, new ReadListener<AiModelInfo>() {
                @Override
                public void invoke(AiModelInfo aiModelInfo, AnalysisContext analysisContext) {
                    aiModelInfoService.save(aiModelInfo);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(e.getMessage());
        }
    }
}
