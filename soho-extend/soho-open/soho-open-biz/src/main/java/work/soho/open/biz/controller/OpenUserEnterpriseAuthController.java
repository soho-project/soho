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
import work.soho.open.biz.domain.OpenUserEnterpriseAuth;
import work.soho.open.biz.service.OpenUserEnterpriseAuthService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 企业实名认证详情表Controller
 *
 * @author fang
 */
@Api(tags = "企业实名认证详情表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openUserEnterpriseAuth" )
public class OpenUserEnterpriseAuthController {

    private final OpenUserEnterpriseAuthService openUserEnterpriseAuthService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询企业实名认证详情表列表
     */
    @GetMapping("/list")
    @Node(value = "openUserEnterpriseAuth::list", name = "获取 企业实名认证详情表 列表")
    public R<PageSerializable<OpenUserEnterpriseAuth>> list(OpenUserEnterpriseAuth openUserEnterpriseAuth, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenUserEnterpriseAuth> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openUserEnterpriseAuth.getId() != null, OpenUserEnterpriseAuth::getId ,openUserEnterpriseAuth.getId());
        lqw.eq(openUserEnterpriseAuth.getUserId() != null, OpenUserEnterpriseAuth::getUserId ,openUserEnterpriseAuth.getUserId());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanyName()),OpenUserEnterpriseAuth::getCompanyName ,openUserEnterpriseAuth.getCompanyName());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanyNameEn()),OpenUserEnterpriseAuth::getCompanyNameEn ,openUserEnterpriseAuth.getCompanyNameEn());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCreditCode()),OpenUserEnterpriseAuth::getCreditCode ,openUserEnterpriseAuth.getCreditCode());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanyType()),OpenUserEnterpriseAuth::getCompanyType ,openUserEnterpriseAuth.getCompanyType());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLegalPerson()),OpenUserEnterpriseAuth::getLegalPerson ,openUserEnterpriseAuth.getLegalPerson());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLegalPersonIdCard()),OpenUserEnterpriseAuth::getLegalPersonIdCard ,openUserEnterpriseAuth.getLegalPersonIdCard());
        lqw.eq(openUserEnterpriseAuth.getRegisteredCapital() != null, OpenUserEnterpriseAuth::getRegisteredCapital ,openUserEnterpriseAuth.getRegisteredCapital());
        lqw.eq(openUserEnterpriseAuth.getEstablishDate() != null, OpenUserEnterpriseAuth::getEstablishDate ,openUserEnterpriseAuth.getEstablishDate());
        lqw.eq(openUserEnterpriseAuth.getBusinessTermStart() != null, OpenUserEnterpriseAuth::getBusinessTermStart ,openUserEnterpriseAuth.getBusinessTermStart());
        lqw.eq(openUserEnterpriseAuth.getBusinessTermEnd() != null, OpenUserEnterpriseAuth::getBusinessTermEnd ,openUserEnterpriseAuth.getBusinessTermEnd());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getBusinessScope()),OpenUserEnterpriseAuth::getBusinessScope ,openUserEnterpriseAuth.getBusinessScope());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getRegisteredAddress()),OpenUserEnterpriseAuth::getRegisteredAddress ,openUserEnterpriseAuth.getRegisteredAddress());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getBusinessAddress()),OpenUserEnterpriseAuth::getBusinessAddress ,openUserEnterpriseAuth.getBusinessAddress());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLicenseImageUrl()),OpenUserEnterpriseAuth::getLicenseImageUrl ,openUserEnterpriseAuth.getLicenseImageUrl());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLicenseImageNo()),OpenUserEnterpriseAuth::getLicenseImageNo ,openUserEnterpriseAuth.getLicenseImageNo());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getTaxCertificateImage()),OpenUserEnterpriseAuth::getTaxCertificateImage ,openUserEnterpriseAuth.getTaxCertificateImage());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getOrganizationCertificateImage()),OpenUserEnterpriseAuth::getOrganizationCertificateImage ,openUserEnterpriseAuth.getOrganizationCertificateImage());
        lqw.eq(openUserEnterpriseAuth.getStatus() != null, OpenUserEnterpriseAuth::getStatus ,openUserEnterpriseAuth.getStatus());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getIndustry()),OpenUserEnterpriseAuth::getIndustry ,openUserEnterpriseAuth.getIndustry());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanySize()),OpenUserEnterpriseAuth::getCompanySize ,openUserEnterpriseAuth.getCompanySize());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getAuthChannel()),OpenUserEnterpriseAuth::getAuthChannel ,openUserEnterpriseAuth.getAuthChannel());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getExtInfo()),OpenUserEnterpriseAuth::getExtInfo ,openUserEnterpriseAuth.getExtInfo());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenUserEnterpriseAuth::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenUserEnterpriseAuth::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openUserEnterpriseAuth.getUpdatedTime() != null, OpenUserEnterpriseAuth::getUpdatedTime ,openUserEnterpriseAuth.getUpdatedTime());
        lqw.orderByDesc(OpenUserEnterpriseAuth::getId);
        List<OpenUserEnterpriseAuth> list = openUserEnterpriseAuthService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取企业实名认证详情表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openUserEnterpriseAuth::getInfo", name = "获取 企业实名认证详情表 详细信息")
    public R<OpenUserEnterpriseAuth> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openUserEnterpriseAuthService.getById(id));
    }

    /**
     * 新增企业实名认证详情表
     */
    @PostMapping
    @Node(value = "openUserEnterpriseAuth::add", name = "新增 企业实名认证详情表")
    public R<Boolean> add(@RequestBody OpenUserEnterpriseAuth openUserEnterpriseAuth) {
        return R.success(openUserEnterpriseAuthService.save(openUserEnterpriseAuth));
    }

    /**
     * 修改企业实名认证详情表
     */
    @PutMapping
    @Node(value = "openUserEnterpriseAuth::edit", name = "修改 企业实名认证详情表")
    public R<Boolean> edit(@RequestBody OpenUserEnterpriseAuth openUserEnterpriseAuth) {
        return R.success(openUserEnterpriseAuthService.updateById(openUserEnterpriseAuth));
    }

    /**
     * 删除企业实名认证详情表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openUserEnterpriseAuth::remove", name = "删除 企业实名认证详情表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openUserEnterpriseAuthService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 企业实名认证详情表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenUserEnterpriseAuth.class)
    @Node(value = "openUserEnterpriseAuth::exportExcel", name = "导出 企业实名认证详情表 Excel")
    public Object exportExcel(OpenUserEnterpriseAuth openUserEnterpriseAuth, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenUserEnterpriseAuth> lqw = new LambdaQueryWrapper<OpenUserEnterpriseAuth>();
        lqw.eq(openUserEnterpriseAuth.getId() != null, OpenUserEnterpriseAuth::getId ,openUserEnterpriseAuth.getId());
        lqw.eq(openUserEnterpriseAuth.getUserId() != null, OpenUserEnterpriseAuth::getUserId ,openUserEnterpriseAuth.getUserId());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanyName()),OpenUserEnterpriseAuth::getCompanyName ,openUserEnterpriseAuth.getCompanyName());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanyNameEn()),OpenUserEnterpriseAuth::getCompanyNameEn ,openUserEnterpriseAuth.getCompanyNameEn());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCreditCode()),OpenUserEnterpriseAuth::getCreditCode ,openUserEnterpriseAuth.getCreditCode());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanyType()),OpenUserEnterpriseAuth::getCompanyType ,openUserEnterpriseAuth.getCompanyType());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLegalPerson()),OpenUserEnterpriseAuth::getLegalPerson ,openUserEnterpriseAuth.getLegalPerson());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLegalPersonIdCard()),OpenUserEnterpriseAuth::getLegalPersonIdCard ,openUserEnterpriseAuth.getLegalPersonIdCard());
        lqw.eq(openUserEnterpriseAuth.getRegisteredCapital() != null, OpenUserEnterpriseAuth::getRegisteredCapital ,openUserEnterpriseAuth.getRegisteredCapital());
        lqw.eq(openUserEnterpriseAuth.getEstablishDate() != null, OpenUserEnterpriseAuth::getEstablishDate ,openUserEnterpriseAuth.getEstablishDate());
        lqw.eq(openUserEnterpriseAuth.getBusinessTermStart() != null, OpenUserEnterpriseAuth::getBusinessTermStart ,openUserEnterpriseAuth.getBusinessTermStart());
        lqw.eq(openUserEnterpriseAuth.getBusinessTermEnd() != null, OpenUserEnterpriseAuth::getBusinessTermEnd ,openUserEnterpriseAuth.getBusinessTermEnd());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getBusinessScope()),OpenUserEnterpriseAuth::getBusinessScope ,openUserEnterpriseAuth.getBusinessScope());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getRegisteredAddress()),OpenUserEnterpriseAuth::getRegisteredAddress ,openUserEnterpriseAuth.getRegisteredAddress());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getBusinessAddress()),OpenUserEnterpriseAuth::getBusinessAddress ,openUserEnterpriseAuth.getBusinessAddress());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLicenseImageUrl()),OpenUserEnterpriseAuth::getLicenseImageUrl ,openUserEnterpriseAuth.getLicenseImageUrl());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getLicenseImageNo()),OpenUserEnterpriseAuth::getLicenseImageNo ,openUserEnterpriseAuth.getLicenseImageNo());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getTaxCertificateImage()),OpenUserEnterpriseAuth::getTaxCertificateImage ,openUserEnterpriseAuth.getTaxCertificateImage());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getOrganizationCertificateImage()),OpenUserEnterpriseAuth::getOrganizationCertificateImage ,openUserEnterpriseAuth.getOrganizationCertificateImage());
        lqw.eq(openUserEnterpriseAuth.getStatus() != null, OpenUserEnterpriseAuth::getStatus ,openUserEnterpriseAuth.getStatus());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getIndustry()),OpenUserEnterpriseAuth::getIndustry ,openUserEnterpriseAuth.getIndustry());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getCompanySize()),OpenUserEnterpriseAuth::getCompanySize ,openUserEnterpriseAuth.getCompanySize());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getAuthChannel()),OpenUserEnterpriseAuth::getAuthChannel ,openUserEnterpriseAuth.getAuthChannel());
        lqw.like(StringUtils.isNotBlank(openUserEnterpriseAuth.getExtInfo()),OpenUserEnterpriseAuth::getExtInfo ,openUserEnterpriseAuth.getExtInfo());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenUserEnterpriseAuth::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenUserEnterpriseAuth::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openUserEnterpriseAuth.getUpdatedTime() != null, OpenUserEnterpriseAuth::getUpdatedTime ,openUserEnterpriseAuth.getUpdatedTime());
        lqw.orderByDesc(OpenUserEnterpriseAuth::getId);
        return openUserEnterpriseAuthService.list(lqw);
    }

    /**
     * 导入 企业实名认证详情表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openUserEnterpriseAuth::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenUserEnterpriseAuth.class, new ReadListener<OpenUserEnterpriseAuth>() {
                @Override
                public void invoke(OpenUserEnterpriseAuth openUserEnterpriseAuth, AnalysisContext analysisContext) {
                    openUserEnterpriseAuthService.save(openUserEnterpriseAuth);
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