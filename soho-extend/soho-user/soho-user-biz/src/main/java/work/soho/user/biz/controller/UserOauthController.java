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
import work.soho.user.biz.domain.UserOauth;
import work.soho.user.biz.service.UserOauthService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 用户三方认证Controller
 *
 * @author fang
 */
@Api(value="用户三方认证",tags = "用户三方认证")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/admin/userOauth" )
public class UserOauthController {

    private final UserOauthService userOauthService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询用户三方认证列表
     */
    @GetMapping("/list")
    @Node(value = "userOauth::list", name = "获取 用户三方认证 列表")
    @ApiOperation(value = "获取 用户三方认证 列表", notes = "获取 用户三方认证 列表")
    public R<PageSerializable<UserOauth>> list(UserOauth userOauth, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<UserOauth> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userOauth.getId() != null, UserOauth::getId ,userOauth.getId());
        lqw.like(StringUtils.isNotBlank(userOauth.getOpenId()),UserOauth::getOpenId ,userOauth.getOpenId());
        lqw.like(StringUtils.isNotBlank(userOauth.getUnionId()),UserOauth::getUnionId ,userOauth.getUnionId());
        lqw.eq(userOauth.getUid() != null, UserOauth::getUid ,userOauth.getUid());
        lqw.eq(userOauth.getType() != null, UserOauth::getType ,userOauth.getType());
        lqw.eq(userOauth.getUpdatedTime() != null, UserOauth::getUpdatedTime ,userOauth.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserOauth::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserOauth::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(UserOauth::getId);
        List<UserOauth> list = userOauthService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户三方认证详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "userOauth::getInfo", name = "获取 用户三方认证 详细信息")
    @ApiOperation(value = "获取 用户三方认证 详细信息", notes = "获取 用户三方认证 详细信息")
    public R<UserOauth> getInfo(@PathVariable("id" ) Long id) {
        return R.success(userOauthService.getById(id));
    }

    /**
     * 新增用户三方认证
     */
    @PostMapping
    @Node(value = "userOauth::add", name = "新增 用户三方认证")
    @ApiOperation(value = "新增 用户三方认证", notes = "新增 用户三方认证")
    public R<Boolean> add(@RequestBody UserOauth userOauth) {
        return R.success(userOauthService.save(userOauth));
    }

    /**
     * 修改用户三方认证
     */
    @PutMapping
    @Node(value = "userOauth::edit", name = "修改 用户三方认证")
    @ApiOperation(value = "修改 用户三方认证", notes = "修改 用户三方认证")
    public R<Boolean> edit(@RequestBody UserOauth userOauth) {
        return R.success(userOauthService.updateById(userOauth));
    }

    /**
     * 删除用户三方认证
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "userOauth::remove", name = "删除 用户三方认证")
    @ApiOperation(value = "删除 用户三方认证", notes = "删除 用户三方认证")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(userOauthService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 用户三方认证 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = UserOauth.class)
    @Node(value = "userOauth::exportExcel", name = "导出 用户三方认证 Excel")
    @ApiOperation(value = "导出 用户三方认证 Excel", notes = "导出 用户三方认证 Excel")
    public Object exportExcel(UserOauth userOauth, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<UserOauth> lqw = new LambdaQueryWrapper<UserOauth>();
        lqw.eq(userOauth.getId() != null, UserOauth::getId ,userOauth.getId());
        lqw.like(StringUtils.isNotBlank(userOauth.getOpenId()),UserOauth::getOpenId ,userOauth.getOpenId());
        lqw.like(StringUtils.isNotBlank(userOauth.getUnionId()),UserOauth::getUnionId ,userOauth.getUnionId());
        lqw.eq(userOauth.getUid() != null, UserOauth::getUid ,userOauth.getUid());
        lqw.eq(userOauth.getType() != null, UserOauth::getType ,userOauth.getType());
        lqw.eq(userOauth.getUpdatedTime() != null, UserOauth::getUpdatedTime ,userOauth.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserOauth::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserOauth::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(UserOauth::getId);
        return userOauthService.list(lqw);
    }

    /**
     * 导入 用户三方认证 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "userOauth::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 用户三方认证 Excel", notes = "导入 用户三方认证 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), UserOauth.class, new ReadListener<UserOauth>() {
                @Override
                public void invoke(UserOauth userOauth, AnalysisContext analysisContext) {
                    userOauthService.save(userOauth);
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