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
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.service.OpenApiCallLogService;
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
@RequestMapping("/open/admin/openApiCallLog" )
public class OpenApiCallLogController {

    private final OpenApiCallLogService openApiCallLogService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "openApiCallLog::list", name = "获取  列表")
    public R<PageSerializable<OpenApiCallLog>> list(OpenApiCallLog openApiCallLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApiCallLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openApiCallLog.getId() != null, OpenApiCallLog::getId ,openApiCallLog.getId());
        lqw.eq(openApiCallLog.getAppId() != null, OpenApiCallLog::getAppId ,openApiCallLog.getAppId());
        lqw.eq(openApiCallLog.getApiId() != null, OpenApiCallLog::getApiId ,openApiCallLog.getApiId());
        lqw.like(StringUtils.isNotBlank(openApiCallLog.getRequestId()),OpenApiCallLog::getRequestId ,openApiCallLog.getRequestId());
        lqw.like(StringUtils.isNotBlank(openApiCallLog.getClientIp()),OpenApiCallLog::getClientIp ,openApiCallLog.getClientIp());
        lqw.eq(openApiCallLog.getResponseCode() != null, OpenApiCallLog::getResponseCode ,openApiCallLog.getResponseCode());
        lqw.eq(openApiCallLog.getCostMs() != null, OpenApiCallLog::getCostMs ,openApiCallLog.getCostMs());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenApiCallLog::getId);
        List<OpenApiCallLog> list = openApiCallLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openApiCallLog::getInfo", name = "获取  详细信息")
    public R<OpenApiCallLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openApiCallLogService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "openApiCallLog::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenApiCallLog openApiCallLog) {
        return R.success(openApiCallLogService.save(openApiCallLog));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "openApiCallLog::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenApiCallLog openApiCallLog) {
        return R.success(openApiCallLogService.updateById(openApiCallLog));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openApiCallLog::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openApiCallLogService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenApiCallLog.class)
    @Node(value = "openApiCallLog::exportExcel", name = "导出  Excel")
    public Object exportExcel(OpenApiCallLog openApiCallLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenApiCallLog> lqw = new LambdaQueryWrapper<OpenApiCallLog>();
        lqw.eq(openApiCallLog.getId() != null, OpenApiCallLog::getId ,openApiCallLog.getId());
        lqw.eq(openApiCallLog.getAppId() != null, OpenApiCallLog::getAppId ,openApiCallLog.getAppId());
        lqw.eq(openApiCallLog.getApiId() != null, OpenApiCallLog::getApiId ,openApiCallLog.getApiId());
        lqw.like(StringUtils.isNotBlank(openApiCallLog.getRequestId()),OpenApiCallLog::getRequestId ,openApiCallLog.getRequestId());
        lqw.like(StringUtils.isNotBlank(openApiCallLog.getClientIp()),OpenApiCallLog::getClientIp ,openApiCallLog.getClientIp());
        lqw.eq(openApiCallLog.getResponseCode() != null, OpenApiCallLog::getResponseCode ,openApiCallLog.getResponseCode());
        lqw.eq(openApiCallLog.getCostMs() != null, OpenApiCallLog::getCostMs ,openApiCallLog.getCostMs());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApiCallLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenApiCallLog::getId);
        return openApiCallLogService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openApiCallLog::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenApiCallLog.class, new ReadListener<OpenApiCallLog>() {
                @Override
                public void invoke(OpenApiCallLog openApiCallLog, AnalysisContext analysisContext) {
                    openApiCallLogService.save(openApiCallLog);
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