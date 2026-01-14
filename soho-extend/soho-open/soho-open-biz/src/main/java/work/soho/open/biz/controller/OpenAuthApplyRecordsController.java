package work.soho.open.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenAuthApplyRecords;
import work.soho.open.biz.service.OpenAuthApplyRecordsService;

import java.util.Arrays;
import java.util.List;
/**
 * 认证申请记录表Controller
 *
 * @author fang
 */
@Api(tags = "认证申请记录表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openAuthApplyRecords" )
public class OpenAuthApplyRecordsController {

    private final OpenAuthApplyRecordsService openAuthApplyRecordsService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询认证申请记录表列表
     */
    @GetMapping("/list")
    @Node(value = "openAuthApplyRecords::list", name = "获取 认证申请记录表 列表")
    public R<PageSerializable<OpenAuthApplyRecords>> list(OpenAuthApplyRecords openAuthApplyRecords, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenAuthApplyRecords> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openAuthApplyRecords.getId() != null, OpenAuthApplyRecords::getId ,openAuthApplyRecords.getId());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getApplyNo()),OpenAuthApplyRecords::getApplyNo ,openAuthApplyRecords.getApplyNo());
        lqw.eq(openAuthApplyRecords.getUserId() != null, OpenAuthApplyRecords::getUserId ,openAuthApplyRecords.getUserId());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getAppId()),OpenAuthApplyRecords::getAppId ,openAuthApplyRecords.getAppId());
        lqw.eq(openAuthApplyRecords.getAuthType() != null, OpenAuthApplyRecords::getAuthType ,openAuthApplyRecords.getAuthType());
        lqw.eq(openAuthApplyRecords.getApplyStatus() != null, OpenAuthApplyRecords::getApplyStatus ,openAuthApplyRecords.getApplyStatus());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getFailReason()),OpenAuthApplyRecords::getFailReason ,openAuthApplyRecords.getFailReason());
        lqw.like(openAuthApplyRecords.getAdminId() != null,OpenAuthApplyRecords::getAdminId ,openAuthApplyRecords.getAdminId());
        lqw.eq(openAuthApplyRecords.getAuditTime() != null, OpenAuthApplyRecords::getAuditTime ,openAuthApplyRecords.getAuditTime());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getAuditRemark()),OpenAuthApplyRecords::getAuditRemark ,openAuthApplyRecords.getAuditRemark());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getSourceIp()),OpenAuthApplyRecords::getSourceIp ,openAuthApplyRecords.getSourceIp());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getUserAgent()),OpenAuthApplyRecords::getUserAgent ,openAuthApplyRecords.getUserAgent());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getRequestData()),OpenAuthApplyRecords::getRequestData ,openAuthApplyRecords.getRequestData());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getResponseData()),OpenAuthApplyRecords::getResponseData ,openAuthApplyRecords.getResponseData());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getCallbackUrl()),OpenAuthApplyRecords::getCallbackUrl ,openAuthApplyRecords.getCallbackUrl());
        lqw.eq(openAuthApplyRecords.getCallbackStatus() != null, OpenAuthApplyRecords::getCallbackStatus ,openAuthApplyRecords.getCallbackStatus());
        lqw.eq(openAuthApplyRecords.getCallbackTime() != null, OpenAuthApplyRecords::getCallbackTime ,openAuthApplyRecords.getCallbackTime());
        lqw.eq(openAuthApplyRecords.getRetryCount() != null, OpenAuthApplyRecords::getRetryCount ,openAuthApplyRecords.getRetryCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAuthApplyRecords::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAuthApplyRecords::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openAuthApplyRecords.getUpdatedTime() != null, OpenAuthApplyRecords::getUpdatedTime ,openAuthApplyRecords.getUpdatedTime());
        lqw.orderByDesc(OpenAuthApplyRecords::getId);
        List<OpenAuthApplyRecords> list = openAuthApplyRecordsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取认证申请记录表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openAuthApplyRecords::getInfo", name = "获取 认证申请记录表 详细信息")
    public R<OpenAuthApplyRecords> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAuthApplyRecordsService.getById(id));
    }

    /**
     * 新增认证申请记录表
     */
    @PostMapping
    @Node(value = "openAuthApplyRecords::add", name = "新增 认证申请记录表")
    public R<Boolean> add(@RequestBody OpenAuthApplyRecords openAuthApplyRecords) {
        return R.success(openAuthApplyRecordsService.save(openAuthApplyRecords));
    }

    /**
     * 修改认证申请记录表
     */
    @PutMapping
    @Node(value = "openAuthApplyRecords::edit", name = "修改 认证申请记录表")
    public R<Boolean> edit(@RequestBody OpenAuthApplyRecords openAuthApplyRecords) {
        return R.success(openAuthApplyRecordsService.updateById(openAuthApplyRecords));
    }

    /**
     * 删除认证申请记录表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openAuthApplyRecords::remove", name = "删除 认证申请记录表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAuthApplyRecordsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 认证申请记录表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenAuthApplyRecords.class)
    @Node(value = "openAuthApplyRecords::exportExcel", name = "导出 认证申请记录表 Excel")
    public Object exportExcel(OpenAuthApplyRecords openAuthApplyRecords, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenAuthApplyRecords> lqw = new LambdaQueryWrapper<OpenAuthApplyRecords>();
        lqw.eq(openAuthApplyRecords.getId() != null, OpenAuthApplyRecords::getId ,openAuthApplyRecords.getId());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getApplyNo()),OpenAuthApplyRecords::getApplyNo ,openAuthApplyRecords.getApplyNo());
        lqw.eq(openAuthApplyRecords.getUserId() != null, OpenAuthApplyRecords::getUserId ,openAuthApplyRecords.getUserId());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getAppId()),OpenAuthApplyRecords::getAppId ,openAuthApplyRecords.getAppId());
        lqw.eq(openAuthApplyRecords.getAuthType() != null, OpenAuthApplyRecords::getAuthType ,openAuthApplyRecords.getAuthType());
        lqw.eq(openAuthApplyRecords.getApplyStatus() != null, OpenAuthApplyRecords::getApplyStatus ,openAuthApplyRecords.getApplyStatus());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getFailReason()),OpenAuthApplyRecords::getFailReason ,openAuthApplyRecords.getFailReason());
        lqw.like(openAuthApplyRecords.getAdminId() != null,OpenAuthApplyRecords::getAdminId ,openAuthApplyRecords.getAdminId());
        lqw.eq(openAuthApplyRecords.getAuditTime() != null, OpenAuthApplyRecords::getAuditTime ,openAuthApplyRecords.getAuditTime());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getAuditRemark()),OpenAuthApplyRecords::getAuditRemark ,openAuthApplyRecords.getAuditRemark());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getSourceIp()),OpenAuthApplyRecords::getSourceIp ,openAuthApplyRecords.getSourceIp());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getUserAgent()),OpenAuthApplyRecords::getUserAgent ,openAuthApplyRecords.getUserAgent());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getRequestData()),OpenAuthApplyRecords::getRequestData ,openAuthApplyRecords.getRequestData());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getResponseData()),OpenAuthApplyRecords::getResponseData ,openAuthApplyRecords.getResponseData());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getCallbackUrl()),OpenAuthApplyRecords::getCallbackUrl ,openAuthApplyRecords.getCallbackUrl());
        lqw.eq(openAuthApplyRecords.getCallbackStatus() != null, OpenAuthApplyRecords::getCallbackStatus ,openAuthApplyRecords.getCallbackStatus());
        lqw.eq(openAuthApplyRecords.getCallbackTime() != null, OpenAuthApplyRecords::getCallbackTime ,openAuthApplyRecords.getCallbackTime());
        lqw.eq(openAuthApplyRecords.getRetryCount() != null, OpenAuthApplyRecords::getRetryCount ,openAuthApplyRecords.getRetryCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAuthApplyRecords::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAuthApplyRecords::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openAuthApplyRecords.getUpdatedTime() != null, OpenAuthApplyRecords::getUpdatedTime ,openAuthApplyRecords.getUpdatedTime());
        lqw.orderByDesc(OpenAuthApplyRecords::getId);
        return openAuthApplyRecordsService.list(lqw);
    }

    /**
     * 导入 认证申请记录表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openAuthApplyRecords::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenAuthApplyRecords.class, new ReadListener<OpenAuthApplyRecords>() {
                @Override
                public void invoke(OpenAuthApplyRecords openAuthApplyRecords, AnalysisContext analysisContext) {
                    openAuthApplyRecordsService.save(openAuthApplyRecords);
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

    /**
     * 审核通过认证申请
     */
    @PutMapping("/audit/{id}")
    @Node(value = "openAuthApplyRecords::audit", name = "审核通过认证申请")
    public R<Boolean> audit(@PathVariable Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenAuthApplyRecords openAuthApplyRecords = openAuthApplyRecordsService.getById(id);
        if (openAuthApplyRecords == null) {
            return R.error("认证申请记录表不存在");
        }
        openAuthApplyRecords.setAdminId(userDetails.getId());
        return R.success(openAuthApplyRecordsService.audit(openAuthApplyRecords));
    }

    /**
     * 拒绝认证申请
     */
    @PutMapping("/reject/{id}")
    @Node(value = "openAuthApplyRecords::reject", name = "拒绝认证申请")
    public R<Boolean> reject(@PathVariable Long id, @RequestParam String reason, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenAuthApplyRecords openAuthApplyRecords = openAuthApplyRecordsService.getById(id);
        if (openAuthApplyRecords == null) {
            return R.error("认证申请记录表不存在");
        }
        openAuthApplyRecords.setAdminId(userDetails.getId());
        return R.success(openAuthApplyRecordsService.reject(openAuthApplyRecords, reason));
    }
}