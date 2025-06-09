package work.soho.quartz.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.quartz.biz.domain.AdminJobLog;
import work.soho.quartz.biz.service.AdminJobLogService;

import java.util.Arrays;
import java.util.List;
/**
 * 计划任务执行日志Controller
 *
 * @author fang
 */
@Api(tags = "计划任务执行日志")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/quartz/admin/adminJobLog" )
public class AdminJobLogController {

    private final AdminJobLogService adminJobLogService;

    /**
     * 查询计划任务执行日志列表
     */
    @GetMapping("/list")
    @Node(value = "adminJobLog::list", name = "获取 计划任务执行日志 列表")
    public R<PageSerializable<AdminJobLog>> list(AdminJobLog adminJobLog)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AdminJobLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(adminJobLog.getId() != null, AdminJobLog::getId ,adminJobLog.getId());
        lqw.eq(adminJobLog.getJobId() != null, AdminJobLog::getJobId ,adminJobLog.getJobId());
        lqw.eq(adminJobLog.getStartTime() != null, AdminJobLog::getStartTime ,adminJobLog.getStartTime());
        lqw.eq(adminJobLog.getStatus() != null, AdminJobLog::getStatus ,adminJobLog.getStatus());
        lqw.like(StringUtils.isNotBlank(adminJobLog.getResult()),AdminJobLog::getResult ,adminJobLog.getResult());
        lqw.eq(adminJobLog.getEndTime() != null, AdminJobLog::getEndTime ,adminJobLog.getEndTime());
        lqw.eq(adminJobLog.getAdminId() != null, AdminJobLog::getAdminId ,adminJobLog.getAdminId());
        lqw.orderByDesc(AdminJobLog::getId);
        List<AdminJobLog> list = adminJobLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取计划任务执行日志详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminJobLog::getInfo", name = "获取 计划任务执行日志 详细信息")
    public R<AdminJobLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminJobLogService.getById(id));
    }

    /**
     * 新增计划任务执行日志
     */
    @PostMapping
    @Node(value = "adminJobLog::add", name = "新增 计划任务执行日志")
    public R<Boolean> add(@RequestBody AdminJobLog adminJobLog) {
        return R.success(adminJobLogService.save(adminJobLog));
    }

    /**
     * 修改计划任务执行日志
     */
    @PutMapping
    @Node(value = "adminJobLog::edit", name = "修改 计划任务执行日志")
    public R<Boolean> edit(@RequestBody AdminJobLog adminJobLog) {
        return R.success(adminJobLogService.updateById(adminJobLog));
    }

    /**
     * 删除计划任务执行日志
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminJobLog::remove", name = "删除 计划任务执行日志")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminJobLogService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 计划任务执行日志 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xsl", modelClass = AdminJobLog.class)
    @Node(value = "adminJobLog::exportExcel", name = "导出 计划任务执行日志 Excel")
    public Object exportExcel(AdminJobLog adminJobLog)
    {
        LambdaQueryWrapper<AdminJobLog> lqw = new LambdaQueryWrapper<AdminJobLog>();
        lqw.eq(adminJobLog.getId() != null, AdminJobLog::getId ,adminJobLog.getId());
        lqw.eq(adminJobLog.getJobId() != null, AdminJobLog::getJobId ,adminJobLog.getJobId());
        lqw.eq(adminJobLog.getStartTime() != null, AdminJobLog::getStartTime ,adminJobLog.getStartTime());
        lqw.eq(adminJobLog.getStatus() != null, AdminJobLog::getStatus ,adminJobLog.getStatus());
        lqw.like(StringUtils.isNotBlank(adminJobLog.getResult()),AdminJobLog::getResult ,adminJobLog.getResult());
        lqw.eq(adminJobLog.getEndTime() != null, AdminJobLog::getEndTime ,adminJobLog.getEndTime());
        lqw.eq(adminJobLog.getAdminId() != null, AdminJobLog::getAdminId ,adminJobLog.getAdminId());
        return adminJobLogService.list(lqw);
    }

    /**
     * 导入 计划任务执行日志 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "adminJobLog::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), AdminJobLog.class, new ReadListener<AdminJobLog>() {
                @Override
                public void invoke(AdminJobLog adminJobLog, AnalysisContext analysisContext) {
                    adminJobLogService.save(adminJobLog);
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