package work.soho.open.biz.controller;

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
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.service.OpenUserService;

import java.util.Arrays;
import java.util.List;
/**
 * 开放平台用户Controller
 *
 * @author fang
 */
@Api(tags = "开放平台用户")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openUser" )
public class OpenUserController {

    private final OpenUserService openUserService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询开放平台用户列表
     */
    @GetMapping("/list")
    @Node(value = "openUser::list", name = "获取 开放平台用户 列表")
    public R<PageSerializable<OpenUser>> list(OpenUser openUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openUser.getId() != null, OpenUser::getId ,openUser.getId());
        lqw.eq(openUser.getUserId() != null, OpenUser::getUserId ,openUser.getUserId());
        lqw.like(StringUtils.isNotBlank(openUser.getUsername()),OpenUser::getUsername ,openUser.getUsername());
        lqw.like(StringUtils.isNotBlank(openUser.getPassword()),OpenUser::getPassword ,openUser.getPassword());
        lqw.like(StringUtils.isNotBlank(openUser.getName()),OpenUser::getName ,openUser.getName());
        lqw.eq(openUser.getType() != null, OpenUser::getType ,openUser.getType());
        lqw.like(StringUtils.isNotBlank(openUser.getContactName()),OpenUser::getContactName ,openUser.getContactName());
        lqw.like(StringUtils.isNotBlank(openUser.getContactEmail()),OpenUser::getContactEmail ,openUser.getContactEmail());
        lqw.like(StringUtils.isNotBlank(openUser.getContactPhone()),OpenUser::getContactPhone ,openUser.getContactPhone());
        lqw.eq(openUser.getUpdatedTime() != null, OpenUser::getUpdatedTime ,openUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenUser::getId);
        List<OpenUser> list = openUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取开放平台用户详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openUser::getInfo", name = "获取 开放平台用户 详细信息")
    public R<OpenUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openUserService.getById(id));
    }

    /**
     * 新增开放平台用户
     */
    @PostMapping
    @Node(value = "openUser::add", name = "新增 开放平台用户")
    public R<Boolean> add(@RequestBody OpenUser openUser) {
        return R.success(openUserService.save(openUser));
    }

    /**
     * 修改开放平台用户
     */
    @PutMapping
    @Node(value = "openUser::edit", name = "修改 开放平台用户")
    public R<Boolean> edit(@RequestBody OpenUser openUser) {
        return R.success(openUserService.updateById(openUser));
    }

    /**
     * 删除开放平台用户
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openUser::remove", name = "删除 开放平台用户")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openUserService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 开放平台用户 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenUser.class)
    @Node(value = "openUser::exportExcel", name = "导出 开放平台用户 Excel")
    public Object exportExcel(OpenUser openUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenUser> lqw = new LambdaQueryWrapper<OpenUser>();
        lqw.eq(openUser.getId() != null, OpenUser::getId ,openUser.getId());
        lqw.eq(openUser.getUserId() != null, OpenUser::getUserId ,openUser.getUserId());
        lqw.like(StringUtils.isNotBlank(openUser.getUsername()),OpenUser::getUsername ,openUser.getUsername());
        lqw.like(StringUtils.isNotBlank(openUser.getPassword()),OpenUser::getPassword ,openUser.getPassword());
        lqw.like(StringUtils.isNotBlank(openUser.getName()),OpenUser::getName ,openUser.getName());
        lqw.eq(openUser.getType() != null, OpenUser::getType ,openUser.getType());
        lqw.like(StringUtils.isNotBlank(openUser.getContactName()),OpenUser::getContactName ,openUser.getContactName());
        lqw.like(StringUtils.isNotBlank(openUser.getContactEmail()),OpenUser::getContactEmail ,openUser.getContactEmail());
        lqw.like(StringUtils.isNotBlank(openUser.getContactPhone()),OpenUser::getContactPhone ,openUser.getContactPhone());
        lqw.eq(openUser.getUpdatedTime() != null, OpenUser::getUpdatedTime ,openUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenUser::getId);
        return openUserService.list(lqw);
    }

    /**
     * 导入 开放平台用户 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openUser::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenUser.class, new ReadListener<OpenUser>() {
                @Override
                public void invoke(OpenUser openUser, AnalysisContext analysisContext) {
                    openUserService.save(openUser);
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