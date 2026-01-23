package work.soho.user.biz.controller;

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
import work.soho.user.biz.domain.UserOauthType;
import work.soho.user.biz.service.UserOauthTypeService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 三方认证类型Controller
 *
 * @author fang
 */
@Api(value="三方认证类型",tags = "三方认证类型")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/admin/userOauthType" )
public class UserOauthTypeController {

    private final UserOauthTypeService userOauthTypeService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询三方认证类型列表
     */
    @GetMapping("/list")
    @Node(value = "userOauthType::list", name = "获取 三方认证类型 列表")
    @ApiOperation(value = "获取 三方认证类型 列表", notes = "获取 三方认证类型 列表")
    public R<PageSerializable<UserOauthType>> list(UserOauthType userOauthType, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<UserOauthType> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userOauthType.getId() != null, UserOauthType::getId ,userOauthType.getId());
        lqw.like(StringUtils.isNotBlank(userOauthType.getName()),UserOauthType::getName ,userOauthType.getName());
        lqw.like(StringUtils.isNotBlank(userOauthType.getTitle()),UserOauthType::getTitle ,userOauthType.getTitle());
        lqw.like(StringUtils.isNotBlank(userOauthType.getLogo()),UserOauthType::getLogo ,userOauthType.getLogo());
        lqw.like(StringUtils.isNotBlank(userOauthType.getClientId()),UserOauthType::getClientId ,userOauthType.getClientId());
        lqw.like(StringUtils.isNotBlank(userOauthType.getClientSecret()),UserOauthType::getClientSecret ,userOauthType.getClientSecret());
        lqw.like(StringUtils.isNotBlank(userOauthType.getKid()),UserOauthType::getKid ,userOauthType.getKid());
        lqw.like(StringUtils.isNotBlank(userOauthType.getTeamId()),UserOauthType::getTeamId ,userOauthType.getTeamId());
        lqw.eq(userOauthType.getStatus() != null, UserOauthType::getStatus ,userOauthType.getStatus());
        lqw.eq(userOauthType.getAdapter() != null, UserOauthType::getAdapter ,userOauthType.getAdapter());
        lqw.eq(userOauthType.getUpdatedTime() != null, UserOauthType::getUpdatedTime ,userOauthType.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserOauthType::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserOauthType::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(UserOauthType::getId);
        List<UserOauthType> list = userOauthTypeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取三方认证类型详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "userOauthType::getInfo", name = "获取 三方认证类型 详细信息")
    @ApiOperation(value = "获取 三方认证类型 详细信息", notes = "获取 三方认证类型 详细信息")
    public R<UserOauthType> getInfo(@PathVariable("id" ) Long id) {
        return R.success(userOauthTypeService.getById(id));
    }

    /**
     * 新增三方认证类型
     */
    @PostMapping
    @Node(value = "userOauthType::add", name = "新增 三方认证类型")
    @ApiOperation(value = "新增 三方认证类型", notes = "新增 三方认证类型")
    public R<Boolean> add(@RequestBody UserOauthType userOauthType) {
        return R.success(userOauthTypeService.save(userOauthType));
    }

    /**
     * 修改三方认证类型
     */
    @PutMapping
    @Node(value = "userOauthType::edit", name = "修改 三方认证类型")
    @ApiOperation(value = "修改 三方认证类型", notes = "修改 三方认证类型")
    public R<Boolean> edit(@RequestBody UserOauthType userOauthType) {
        return R.success(userOauthTypeService.updateById(userOauthType));
    }

    /**
     * 删除三方认证类型
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "userOauthType::remove", name = "删除 三方认证类型")
    @ApiOperation(value = "删除 三方认证类型", notes = "删除 三方认证类型")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(userOauthTypeService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 三方认证类型 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = UserOauthType.class)
    @Node(value = "userOauthType::exportExcel", name = "导出 三方认证类型 Excel")
    @ApiOperation(value = "导出 三方认证类型 Excel", notes = "导出 三方认证类型 Excel")
    public Object exportExcel(UserOauthType userOauthType, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<UserOauthType> lqw = new LambdaQueryWrapper<UserOauthType>();
        lqw.eq(userOauthType.getId() != null, UserOauthType::getId ,userOauthType.getId());
        lqw.like(StringUtils.isNotBlank(userOauthType.getName()),UserOauthType::getName ,userOauthType.getName());
        lqw.like(StringUtils.isNotBlank(userOauthType.getTitle()),UserOauthType::getTitle ,userOauthType.getTitle());
        lqw.like(StringUtils.isNotBlank(userOauthType.getLogo()),UserOauthType::getLogo ,userOauthType.getLogo());
        lqw.like(StringUtils.isNotBlank(userOauthType.getClientId()),UserOauthType::getClientId ,userOauthType.getClientId());
        lqw.like(StringUtils.isNotBlank(userOauthType.getClientSecret()),UserOauthType::getClientSecret ,userOauthType.getClientSecret());
        lqw.like(StringUtils.isNotBlank(userOauthType.getKid()),UserOauthType::getKid ,userOauthType.getKid());
        lqw.like(StringUtils.isNotBlank(userOauthType.getTeamId()),UserOauthType::getTeamId ,userOauthType.getTeamId());
        lqw.eq(userOauthType.getStatus() != null, UserOauthType::getStatus ,userOauthType.getStatus());
        lqw.eq(userOauthType.getAdapter() != null, UserOauthType::getAdapter ,userOauthType.getAdapter());
        lqw.eq(userOauthType.getUpdatedTime() != null, UserOauthType::getUpdatedTime ,userOauthType.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserOauthType::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserOauthType::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(UserOauthType::getId);
        return userOauthTypeService.list(lqw);
    }

    /**
     * 导入 三方认证类型 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "userOauthType::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 三方认证类型 Excel", notes = "导入 三方认证类型 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), UserOauthType.class, new ReadListener<UserOauthType>() {
                @Override
                public void invoke(UserOauthType userOauthType, AnalysisContext analysisContext) {
                    userOauthTypeService.save(userOauthType);
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
