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
import work.soho.ai.biz.domain.AiProviderModelRel;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(value = "AI提供商模型关联表", tags = "AI提供商模型关联表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/admin/aiProviderModelRel")
public class AiProviderModelRelController {
    private final AiProviderModelRelService aiProviderModelRelService;

    @GetMapping("/list")
    @Node(value = "aiProviderModelRel::list", name = "获取 AI提供商模型关联表 列表")
    @ApiOperation(value = "获取 AI提供商模型关联表 列表", notes = "获取 AI提供商模型关联表 列表")
    public R<PageSerializable<AiProviderModelRel>> list(AiProviderModelRel aiProviderModelRel, BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        PageUtils.startPage();
        LambdaQueryWrapper<AiProviderModelRel> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiProviderModelRel.getId() != null, AiProviderModelRel::getId, aiProviderModelRel.getId());
        lqw.eq(aiProviderModelRel.getProviderConfigId() != null, AiProviderModelRel::getProviderConfigId, aiProviderModelRel.getProviderConfigId());
        lqw.eq(aiProviderModelRel.getModelInfoId() != null, AiProviderModelRel::getModelInfoId, aiProviderModelRel.getModelInfoId());
        lqw.eq(aiProviderModelRel.getStatus() != null, AiProviderModelRel::getStatus, aiProviderModelRel.getStatus());
        lqw.eq(aiProviderModelRel.getSort() != null, AiProviderModelRel::getSort, aiProviderModelRel.getSort());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null, AiProviderModelRel::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null, AiProviderModelRel::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByAsc(AiProviderModelRel::getSort).orderByDesc(AiProviderModelRel::getId);
        return R.success(new PageSerializable<>(aiProviderModelRelService.list(lqw)));
    }

    @GetMapping("/{id}")
    @Node(value = "aiProviderModelRel::getInfo", name = "获取 AI提供商模型关联表 详细信息")
    @ApiOperation(value = "获取 AI提供商模型关联表 详细信息", notes = "获取 AI提供商模型关联表 详细信息")
    public R<AiProviderModelRel> getInfo(@PathVariable("id") Long id) {
        return R.success(aiProviderModelRelService.getById(id));
    }

    @PostMapping
    @Node(value = "aiProviderModelRel::add", name = "新增 AI提供商模型关联表")
    @ApiOperation(value = "新增 AI提供商模型关联表", notes = "新增 AI提供商模型关联表")
    public R<Boolean> add(@RequestBody AiProviderModelRel aiProviderModelRel) {
        return R.success(aiProviderModelRelService.save(aiProviderModelRel));
    }

    @PutMapping
    @Node(value = "aiProviderModelRel::edit", name = "修改 AI提供商模型关联表")
    @ApiOperation(value = "修改 AI提供商模型关联表", notes = "修改 AI提供商模型关联表")
    public R<Boolean> edit(@RequestBody AiProviderModelRel aiProviderModelRel) {
        return R.success(aiProviderModelRelService.updateById(aiProviderModelRel));
    }

    @DeleteMapping("/{ids}")
    @Node(value = "aiProviderModelRel::remove", name = "删除 AI提供商模型关联表")
    @ApiOperation(value = "删除 AI提供商模型关联表", notes = "删除 AI提供商模型关联表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiProviderModelRelService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("options")
    @Node(value = "aiProviderModelRel::options", name = "获取 AI提供商模型关联表 选项")
    @ApiOperation(value = "获取 AI提供商模型关联表 选项", notes = "获取 AI提供商模型关联表 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<OptionVo<Long, String>> options = new ArrayList<>();
        for (AiProviderModelRel item : aiProviderModelRelService.list()) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getProviderConfigId() + ":" + item.getModelInfoId());
            options.add(optionVo);
        }
        return R.success(options);
    }

    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = AiProviderModelRel.class)
    @Node(value = "aiProviderModelRel::exportExcel", name = "导出 AI提供商模型关联表 Excel")
    @ApiOperation(value = "导出 AI提供商模型关联表 Excel", notes = "导出 AI提供商模型关联表 Excel")
    public Object exportExcel(AiProviderModelRel aiProviderModelRel, BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        LambdaQueryWrapper<AiProviderModelRel> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiProviderModelRel.getId() != null, AiProviderModelRel::getId, aiProviderModelRel.getId());
        lqw.eq(aiProviderModelRel.getProviderConfigId() != null, AiProviderModelRel::getProviderConfigId, aiProviderModelRel.getProviderConfigId());
        lqw.eq(aiProviderModelRel.getModelInfoId() != null, AiProviderModelRel::getModelInfoId, aiProviderModelRel.getModelInfoId());
        lqw.eq(aiProviderModelRel.getStatus() != null, AiProviderModelRel::getStatus, aiProviderModelRel.getStatus());
        lqw.eq(aiProviderModelRel.getSort() != null, AiProviderModelRel::getSort, aiProviderModelRel.getSort());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null, AiProviderModelRel::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null, AiProviderModelRel::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByAsc(AiProviderModelRel::getSort).orderByDesc(AiProviderModelRel::getId);
        return aiProviderModelRelService.list(lqw);
    }

    @PostMapping("/importExcel")
    @Node(value = "aiProviderModelRel::importExcel", name = "导入 AI提供商模型关联表 Excel")
    @ApiOperation(value = "导入 AI提供商模型关联表 Excel", notes = "导入 AI提供商模型关联表 Excel")
    public R importExcel(@RequestParam(value = "file") MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), AiProviderModelRel.class, new ReadListener<AiProviderModelRel>() {
                @Override
                public void invoke(AiProviderModelRel aiProviderModelRel, AnalysisContext analysisContext) {
                    aiProviderModelRelService.save(aiProviderModelRel);
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
