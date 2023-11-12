package work.soho.admin.controller;

import java.time.LocalDateTime;

import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.api.admin.service.AdminDictApiService;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.admin.domain.AdminRelease;
import work.soho.admin.service.AdminReleaseService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
import work.soho.common.data.upload.utils.UploadUtils;

/**
 * 发版Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminRelease" )
public class AdminReleaseController {

    private final AdminReleaseService adminReleaseService;

    private final AdminDictApiService adminDictApiService;

    /**
     * 查询发版列表
     */
    @GetMapping("/list")
    @Node(value = "adminRelease::list", name = "发版列表")
    public R<PageSerializable<AdminRelease>> list(AdminRelease adminRelease, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AdminRelease> lqw = new LambdaQueryWrapper<AdminRelease>();
        lqw.eq(adminRelease.getId() != null, AdminRelease::getId ,adminRelease.getId());
        lqw.like(StringUtils.isNotBlank(adminRelease.getVersion()),AdminRelease::getVersion ,adminRelease.getVersion());
        lqw.like(StringUtils.isNotBlank(adminRelease.getNotes()),AdminRelease::getNotes ,adminRelease.getNotes());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AdminRelease::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AdminRelease::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(adminRelease.getUpdatedTime() != null, AdminRelease::getUpdatedTime ,adminRelease.getUpdatedTime());
        lqw.like(StringUtils.isNotBlank(adminRelease.getUrl()),AdminRelease::getUrl ,adminRelease.getUrl());
        lqw.eq(adminRelease.getPlatformType() != null, AdminRelease::getPlatformType ,adminRelease.getPlatformType());
        List<AdminRelease> list = adminReleaseService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取发版详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminRelease::getInfo", name = "发版详细信息")
    public R<AdminRelease> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminReleaseService.getById(id));
    }

    /**
     * 新增发版
     */
    @PostMapping
    @Node(value = "adminRelease::add", name = "发版新增")
    public R<Boolean> add(@RequestBody AdminRelease adminRelease) {
        adminRelease.setUpdatedTime(LocalDateTime.now());
        adminRelease.setCreatedTime(LocalDateTime.now());
        return R.success(adminReleaseService.save(adminRelease));
    }

    /**
     * 修改发版
     */
    @PutMapping
    @Node(value = "adminRelease::edit", name = "发版修改")
    public R<Boolean> edit(@RequestBody AdminRelease adminRelease) {
        adminRelease.setUpdatedTime(LocalDateTime.now());
        return R.success(adminReleaseService.updateById(adminRelease));
    }

    /**
     * 删除发版
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminRelease::remove", name = "发版删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminReleaseService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取PlatformType枚举选项
     *
     * @return
     */
    @GetMapping("/queryPlatformTypeEnumOption")
    public R<List<OptionVo<Integer, String>>> platformTypeEnumOption() {
        return R.success(adminDictApiService.getOptionsByCode("adminRelease-platformType"));
    }

    /**
     * 上传软件文件
     *
     * @param file
     * @return
     */
    @PostMapping("/upload-file")
    public Object uploadAvatar(@RequestParam(value = "file") MultipartFile file) {
        try {
            String url = UploadUtils.upload("release", file);
            return R.success(url);
        } catch (Exception ioException) {
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }
}
