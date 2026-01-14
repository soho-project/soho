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
import work.soho.open.biz.domain.OpenUserPersonalAuth;
import work.soho.open.biz.service.OpenUserPersonalAuthService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 个人实名认证详情表Controller
 *
 * @author fang
 */
@Api(tags = "个人实名认证详情表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openUserPersonalAuth" )
public class OpenUserPersonalAuthController {

    private final OpenUserPersonalAuthService openUserPersonalAuthService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询个人实名认证详情表列表
     */
    @GetMapping("/list")
    @Node(value = "openUserPersonalAuth::list", name = "获取 个人实名认证详情表 列表")
    public R<PageSerializable<OpenUserPersonalAuth>> list(OpenUserPersonalAuth openUserPersonalAuth, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenUserPersonalAuth> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openUserPersonalAuth.getId() != null, OpenUserPersonalAuth::getId ,openUserPersonalAuth.getId());
        lqw.eq(openUserPersonalAuth.getUserId() != null, OpenUserPersonalAuth::getUserId ,openUserPersonalAuth.getUserId());
        lqw.eq(openUserPersonalAuth.getIdCardType() != null, OpenUserPersonalAuth::getIdCardType ,openUserPersonalAuth.getIdCardType());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIdCardNoEncrypt()),OpenUserPersonalAuth::getIdCardNoEncrypt ,openUserPersonalAuth.getIdCardNoEncrypt());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIdCardFrontImg()),OpenUserPersonalAuth::getIdCardFrontImg ,openUserPersonalAuth.getIdCardFrontImg());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIdCardBackImg()),OpenUserPersonalAuth::getIdCardBackImg ,openUserPersonalAuth.getIdCardBackImg());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getRealName()),OpenUserPersonalAuth::getRealName ,openUserPersonalAuth.getRealName());
        lqw.eq(openUserPersonalAuth.getGender() != null, OpenUserPersonalAuth::getGender ,openUserPersonalAuth.getGender());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getNation()),OpenUserPersonalAuth::getNation ,openUserPersonalAuth.getNation());
        lqw.eq(openUserPersonalAuth.getBirthDate() != null, OpenUserPersonalAuth::getBirthDate ,openUserPersonalAuth.getBirthDate());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getAddress()),OpenUserPersonalAuth::getAddress ,openUserPersonalAuth.getAddress());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIssuedBy()),OpenUserPersonalAuth::getIssuedBy ,openUserPersonalAuth.getIssuedBy());
        lqw.eq(openUserPersonalAuth.getValidDateStart() != null, OpenUserPersonalAuth::getValidDateStart ,openUserPersonalAuth.getValidDateStart());
        lqw.eq(openUserPersonalAuth.getValidDateEnd() != null, OpenUserPersonalAuth::getValidDateEnd ,openUserPersonalAuth.getValidDateEnd());
        lqw.eq(openUserPersonalAuth.getFaceCompareScore() != null, OpenUserPersonalAuth::getFaceCompareScore ,openUserPersonalAuth.getFaceCompareScore());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getFaceImageUrl()),OpenUserPersonalAuth::getFaceImageUrl ,openUserPersonalAuth.getFaceImageUrl());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getLiveDetectResult()),OpenUserPersonalAuth::getLiveDetectResult ,openUserPersonalAuth.getLiveDetectResult());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getAuthScene()),OpenUserPersonalAuth::getAuthScene ,openUserPersonalAuth.getAuthScene());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getExtInfo()),OpenUserPersonalAuth::getExtInfo ,openUserPersonalAuth.getExtInfo());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenUserPersonalAuth::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenUserPersonalAuth::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenUserPersonalAuth::getId);
        List<OpenUserPersonalAuth> list = openUserPersonalAuthService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取个人实名认证详情表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openUserPersonalAuth::getInfo", name = "获取 个人实名认证详情表 详细信息")
    public R<OpenUserPersonalAuth> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openUserPersonalAuthService.getById(id));
    }

    /**
     * 新增个人实名认证详情表
     */
    @PostMapping
    @Node(value = "openUserPersonalAuth::add", name = "新增 个人实名认证详情表")
    public R<Boolean> add(@RequestBody OpenUserPersonalAuth openUserPersonalAuth) {
        return R.success(openUserPersonalAuthService.save(openUserPersonalAuth));
    }

    /**
     * 修改个人实名认证详情表
     */
    @PutMapping
    @Node(value = "openUserPersonalAuth::edit", name = "修改 个人实名认证详情表")
    public R<Boolean> edit(@RequestBody OpenUserPersonalAuth openUserPersonalAuth) {
        return R.success(openUserPersonalAuthService.updateById(openUserPersonalAuth));
    }

    /**
     * 删除个人实名认证详情表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openUserPersonalAuth::remove", name = "删除 个人实名认证详情表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openUserPersonalAuthService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 个人实名认证详情表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenUserPersonalAuth.class)
    @Node(value = "openUserPersonalAuth::exportExcel", name = "导出 个人实名认证详情表 Excel")
    public Object exportExcel(OpenUserPersonalAuth openUserPersonalAuth, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenUserPersonalAuth> lqw = new LambdaQueryWrapper<OpenUserPersonalAuth>();
        lqw.eq(openUserPersonalAuth.getId() != null, OpenUserPersonalAuth::getId ,openUserPersonalAuth.getId());
        lqw.eq(openUserPersonalAuth.getUserId() != null, OpenUserPersonalAuth::getUserId ,openUserPersonalAuth.getUserId());
        lqw.eq(openUserPersonalAuth.getIdCardType() != null, OpenUserPersonalAuth::getIdCardType ,openUserPersonalAuth.getIdCardType());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIdCardNoEncrypt()),OpenUserPersonalAuth::getIdCardNoEncrypt ,openUserPersonalAuth.getIdCardNoEncrypt());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIdCardFrontImg()),OpenUserPersonalAuth::getIdCardFrontImg ,openUserPersonalAuth.getIdCardFrontImg());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIdCardBackImg()),OpenUserPersonalAuth::getIdCardBackImg ,openUserPersonalAuth.getIdCardBackImg());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getRealName()),OpenUserPersonalAuth::getRealName ,openUserPersonalAuth.getRealName());
        lqw.eq(openUserPersonalAuth.getGender() != null, OpenUserPersonalAuth::getGender ,openUserPersonalAuth.getGender());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getNation()),OpenUserPersonalAuth::getNation ,openUserPersonalAuth.getNation());
        lqw.eq(openUserPersonalAuth.getBirthDate() != null, OpenUserPersonalAuth::getBirthDate ,openUserPersonalAuth.getBirthDate());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getAddress()),OpenUserPersonalAuth::getAddress ,openUserPersonalAuth.getAddress());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getIssuedBy()),OpenUserPersonalAuth::getIssuedBy ,openUserPersonalAuth.getIssuedBy());
        lqw.eq(openUserPersonalAuth.getValidDateStart() != null, OpenUserPersonalAuth::getValidDateStart ,openUserPersonalAuth.getValidDateStart());
        lqw.eq(openUserPersonalAuth.getValidDateEnd() != null, OpenUserPersonalAuth::getValidDateEnd ,openUserPersonalAuth.getValidDateEnd());
        lqw.eq(openUserPersonalAuth.getFaceCompareScore() != null, OpenUserPersonalAuth::getFaceCompareScore ,openUserPersonalAuth.getFaceCompareScore());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getFaceImageUrl()),OpenUserPersonalAuth::getFaceImageUrl ,openUserPersonalAuth.getFaceImageUrl());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getLiveDetectResult()),OpenUserPersonalAuth::getLiveDetectResult ,openUserPersonalAuth.getLiveDetectResult());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getAuthScene()),OpenUserPersonalAuth::getAuthScene ,openUserPersonalAuth.getAuthScene());
        lqw.like(StringUtils.isNotBlank(openUserPersonalAuth.getExtInfo()),OpenUserPersonalAuth::getExtInfo ,openUserPersonalAuth.getExtInfo());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenUserPersonalAuth::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenUserPersonalAuth::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenUserPersonalAuth::getId);
        return openUserPersonalAuthService.list(lqw);
    }

    /**
     * 导入 个人实名认证详情表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openUserPersonalAuth::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenUserPersonalAuth.class, new ReadListener<OpenUserPersonalAuth>() {
                @Override
                public void invoke(OpenUserPersonalAuth openUserPersonalAuth, AnalysisContext analysisContext) {
                    openUserPersonalAuthService.save(openUserPersonalAuth);
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