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
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.service.AiUserApiKeyService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * API KEYController
 *
 * @author fang
 */
@Api(value="API KEY",tags = "API KEY")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/admin/aiUserApiKey" )
public class AiUserApiKeyController {

    private final AiUserApiKeyService aiUserApiKeyService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询API KEY列表
     */
    @GetMapping("/list")
    @Node(value = "aiUserApiKey::list", name = "获取 API KEY 列表")
    @ApiOperation(value = "获取 API KEY 列表", notes = "获取 API KEY 列表")
    public R<PageSerializable<AiUserApiKey>> list(AiUserApiKey aiUserApiKey, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AiUserApiKey> lqw = new LambdaQueryWrapper<>();
        lqw.eq(aiUserApiKey.getId() != null, AiUserApiKey::getId ,aiUserApiKey.getId());
        lqw.eq(aiUserApiKey.getUserId() != null, AiUserApiKey::getUserId ,aiUserApiKey.getUserId());
        lqw.like(StringUtils.isNotBlank(aiUserApiKey.getName()),AiUserApiKey::getName ,aiUserApiKey.getName());
        lqw.like(StringUtils.isNotBlank(aiUserApiKey.getApiKeyPrefix()),AiUserApiKey::getApiKeyPrefix ,aiUserApiKey.getApiKeyPrefix());
        lqw.like(StringUtils.isNotBlank(aiUserApiKey.getApiKeyHash()),AiUserApiKey::getApiKeyHash ,aiUserApiKey.getApiKeyHash());
        lqw.eq(aiUserApiKey.getStatus() != null, AiUserApiKey::getStatus ,aiUserApiKey.getStatus());
        lqw.eq(aiUserApiKey.getLastUsedTime() != null, AiUserApiKey::getLastUsedTime ,aiUserApiKey.getLastUsedTime());
        lqw.eq(aiUserApiKey.getUpdatedTime() != null, AiUserApiKey::getUpdatedTime ,aiUserApiKey.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiUserApiKey::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiUserApiKey::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiUserApiKey::getId);
        List<AiUserApiKey> list = aiUserApiKeyService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取API KEY详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "aiUserApiKey::getInfo", name = "获取 API KEY 详细信息")
    @ApiOperation(value = "获取 API KEY 详细信息", notes = "获取 API KEY 详细信息")
    public R<AiUserApiKey> getInfo(@PathVariable("id" ) Long id) {
        return R.success(aiUserApiKeyService.getById(id));
    }

    /**
     * 新增API KEY
     */
    @PostMapping
    @Node(value = "aiUserApiKey::add", name = "新增 API KEY")
    @ApiOperation(value = "新增 API KEY", notes = "新增 API KEY")
    public R<Boolean> add(@RequestBody AiUserApiKey aiUserApiKey) {
        return R.success(aiUserApiKeyService.save(aiUserApiKey));
    }

    /**
     * 修改API KEY
     */
    @PutMapping
    @Node(value = "aiUserApiKey::edit", name = "修改 API KEY")
    @ApiOperation(value = "修改 API KEY", notes = "修改 API KEY")
    public R<Boolean> edit(@RequestBody AiUserApiKey aiUserApiKey) {
        return R.success(aiUserApiKeyService.updateById(aiUserApiKey));
    }

    /**
     * 删除API KEY
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "aiUserApiKey::remove", name = "删除 API KEY")
    @ApiOperation(value = "删除 API KEY", notes = "删除 API KEY")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiUserApiKeyService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该API KEY 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "aiUserApiKey::options", name = "获取 API KEY 选项")
    @ApiOperation(value = "获取 API KEY 选项", notes = "获取 API KEY 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<AiUserApiKey> list = aiUserApiKeyService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(AiUserApiKey item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 API KEY Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = AiUserApiKey.class)
    @Node(value = "aiUserApiKey::exportExcel", name = "导出 API KEY Excel")
    @ApiOperation(value = "导出 API KEY Excel", notes = "导出 API KEY Excel")
    public Object exportExcel(AiUserApiKey aiUserApiKey, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<AiUserApiKey> lqw = new LambdaQueryWrapper<AiUserApiKey>();
        lqw.eq(aiUserApiKey.getId() != null, AiUserApiKey::getId ,aiUserApiKey.getId());
        lqw.eq(aiUserApiKey.getUserId() != null, AiUserApiKey::getUserId ,aiUserApiKey.getUserId());
        lqw.like(StringUtils.isNotBlank(aiUserApiKey.getName()),AiUserApiKey::getName ,aiUserApiKey.getName());
        lqw.like(StringUtils.isNotBlank(aiUserApiKey.getApiKeyPrefix()),AiUserApiKey::getApiKeyPrefix ,aiUserApiKey.getApiKeyPrefix());
        lqw.like(StringUtils.isNotBlank(aiUserApiKey.getApiKeyHash()),AiUserApiKey::getApiKeyHash ,aiUserApiKey.getApiKeyHash());
        lqw.eq(aiUserApiKey.getStatus() != null, AiUserApiKey::getStatus ,aiUserApiKey.getStatus());
        lqw.eq(aiUserApiKey.getLastUsedTime() != null, AiUserApiKey::getLastUsedTime ,aiUserApiKey.getLastUsedTime());
        lqw.eq(aiUserApiKey.getUpdatedTime() != null, AiUserApiKey::getUpdatedTime ,aiUserApiKey.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiUserApiKey::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiUserApiKey::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiUserApiKey::getId);
        return aiUserApiKeyService.list(lqw);
    }

    /**
     * 导入 API KEY Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "aiUserApiKey::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 API KEY Excel", notes = "导入 API KEY Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), AiUserApiKey.class, new ReadListener<AiUserApiKey>() {
                @Override
                public void invoke(AiUserApiKey aiUserApiKey, AnalysisContext analysisContext) {
                    aiUserApiKeyService.save(aiUserApiKey);
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
