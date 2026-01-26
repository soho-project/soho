package work.soho.user.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 用户信息Controller
 *
 * @author fang
 */
@Api(value="用户信息",tags = "用户信息")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/admin/userInfo" )
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final AdminDictApiService adminDictApiService;

    /**
     * 查询用户信息列表
     */
    @GetMapping("/list")
    @Node(value = "userInfo::list", name = "获取 用户信息 列表")
    @ApiOperation(value = "获取 用户信息 列表", notes = "获取 用户信息 列表")
    public R<PageSerializable<UserInfo>> list(UserInfo userInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userInfo.getId() != null, UserInfo::getId ,userInfo.getId());
        lqw.like(StringUtils.isNotBlank(userInfo.getUsername()),UserInfo::getUsername ,userInfo.getUsername());
        lqw.like(StringUtils.isNotBlank(userInfo.getEmail()),UserInfo::getEmail ,userInfo.getEmail());
        lqw.like(StringUtils.isNotBlank(userInfo.getPhone()),UserInfo::getPhone ,userInfo.getPhone());
        lqw.like(StringUtils.isNotBlank(userInfo.getNickname()),UserInfo::getNickname ,userInfo.getNickname());
        lqw.like(StringUtils.isNotBlank(userInfo.getPassword()),UserInfo::getPassword ,userInfo.getPassword());
        lqw.like(StringUtils.isNotBlank(userInfo.getAvatar()),UserInfo::getAvatar ,userInfo.getAvatar());
        lqw.eq(userInfo.getStatus() != null, UserInfo::getStatus ,userInfo.getStatus());
        lqw.eq(userInfo.getAge() != null, UserInfo::getAge ,userInfo.getAge());
        lqw.eq(userInfo.getSex() != null, UserInfo::getSex ,userInfo.getSex());
        lqw.eq(userInfo.getLevel() != null, UserInfo::getLevel ,userInfo.getLevel());
        lqw.eq(userInfo.getUpdatedTime() != null, UserInfo::getUpdatedTime ,userInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(UserInfo::getId);
        List<UserInfo> list = userInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "userInfo::getInfo", name = "获取 用户信息 详细信息")
    @ApiOperation(value = "获取 用户信息 详细信息", notes = "获取 用户信息 详细信息")
    public R<UserInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(userInfoService.getById(id));
    }

    /**
     * 新增用户信息
     */
    @PostMapping
    @Node(value = "userInfo::add", name = "新增 用户信息")
    @ApiOperation(value = "新增 用户信息", notes = "新增 用户信息")
    public R<Boolean> add(@RequestBody UserInfo userInfo) {
        return R.success(userInfoService.save(userInfo));
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    @Node(value = "userInfo::edit", name = "修改 用户信息")
    @ApiOperation(value = "修改 用户信息", notes = "修改 用户信息")
    public R<Boolean> edit(@RequestBody UserInfo userInfo) {
        return R.success(userInfoService.updateById(userInfo));
    }

    /**
     * 删除用户信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "userInfo::remove", name = "删除 用户信息")
    @ApiOperation(value = "删除 用户信息", notes = "删除 用户信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(userInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该用户信息 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "userInfo::options", name = "获取 用户信息 选项")
    @ApiOperation(value = "获取 用户信息 选项", notes = "获取 用户信息 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<UserInfo> list = userInfoService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(UserInfo item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getUsername());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 用户信息 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = UserInfo.class)
    @Node(value = "userInfo::exportExcel", name = "导出 用户信息 Excel")
    @ApiOperation(value = "导出 用户信息 Excel", notes = "导出 用户信息 Excel")
    public Object exportExcel(UserInfo userInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<UserInfo>();
        lqw.eq(userInfo.getId() != null, UserInfo::getId ,userInfo.getId());
        lqw.like(StringUtils.isNotBlank(userInfo.getUsername()),UserInfo::getUsername ,userInfo.getUsername());
        lqw.like(StringUtils.isNotBlank(userInfo.getEmail()),UserInfo::getEmail ,userInfo.getEmail());
        lqw.like(StringUtils.isNotBlank(userInfo.getPhone()),UserInfo::getPhone ,userInfo.getPhone());
        lqw.like(StringUtils.isNotBlank(userInfo.getNickname()),UserInfo::getNickname ,userInfo.getNickname());
        lqw.like(StringUtils.isNotBlank(userInfo.getPassword()),UserInfo::getPassword ,userInfo.getPassword());
        lqw.like(StringUtils.isNotBlank(userInfo.getAvatar()),UserInfo::getAvatar ,userInfo.getAvatar());
        lqw.eq(userInfo.getStatus() != null, UserInfo::getStatus ,userInfo.getStatus());
        lqw.eq(userInfo.getAge() != null, UserInfo::getAge ,userInfo.getAge());
        lqw.eq(userInfo.getSex() != null, UserInfo::getSex ,userInfo.getSex());
        lqw.eq(userInfo.getLevel() != null, UserInfo::getLevel ,userInfo.getLevel());
        lqw.eq(userInfo.getUpdatedTime() != null, UserInfo::getUpdatedTime ,userInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(UserInfo::getId);
        return userInfoService.list(lqw);
    }

    /**
     * 导入 用户信息 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "userInfo::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 用户信息 Excel", notes = "导入 用户信息 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), UserInfo.class, new ReadListener<UserInfo>() {
                @Override
                public void invoke(UserInfo userInfo, AnalysisContext analysisContext) {
                    userInfoService.save(userInfo);
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
     * 获取 用户信息 level 字典选项
     *
     * @return
     */
    @GetMapping("/queryLevelOptions")
    @ApiOperation(value = "获取 用户信息 level 字典选项", notes = "获取 用户信息 level 字典选项")
    public R<List<OptionVo<Integer, String>>> queryLevelOptions(){
        return R.success(adminDictApiService.getOptionsByCode("user-info-level"));
    }
}