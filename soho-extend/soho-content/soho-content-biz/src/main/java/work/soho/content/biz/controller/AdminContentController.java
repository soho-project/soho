package work.soho.content.biz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.admin.api.annotation.Node;
import work.soho.admin.api.request.AdminContentRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.content.biz.domain.AdminContent;
import work.soho.content.biz.service.AdminContentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * 系统内容表Controller
 *
 * @author i
 * @date 2022-09-03 01:59:15
 */
@Api(tags = "系统内容表API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/content/admin/adminContent" )
public class AdminContentController {

    private final AdminContentService adminContentService;

    /**
     * 查询系统内容表列表
     */
    @GetMapping("/list")
    @Node(value = "adminContent::list", name = "系统内容表列表")
    public R<PageSerializable<AdminContent>> list(AdminContentRequest adminContentRequest)
    {
        LambdaQueryWrapper<AdminContent> lqw = new LambdaQueryWrapper<>();
        PageUtils.startPage();
        lqw.between(adminContentRequest.getStartTime()!=null,AdminContent::getCreatedTime, adminContentRequest.getStartTime(), adminContentRequest.getEndTime());
        lqw.like(adminContentRequest.getTitle()!=null, AdminContent::getTitle, adminContentRequest.getTitle());
        lqw.eq(adminContentRequest.getCategoryId()!=null, AdminContent::getCategoryId, adminContentRequest.getCategoryId());

        List<AdminContent> list = adminContentService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取系统内容表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminContent::getInfo", name = "系统内容表详细信息")
    public R<AdminContent> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminContentService.getById(id));
    }

    /**
     * 新增系统内容表
     */
    @PostMapping
    @Node(value = "adminContent::add", name = "系统内容表新增")
    public R<Boolean> add(@RequestBody AdminContent adminContent) {
        adminContent.setCreatedTime(LocalDateTime.now());
        adminContent.setUpdatedTime(LocalDateTime.now());
        adminContent.setUserId(SecurityUtils.getLoginUserId());
        return R.success(adminContentService.save(adminContent));
    }

    /**
     * 修改系统内容表
     */
    @PutMapping
    @Node(value = "adminContent::edit", name = "系统内容表修改")
    public R<Boolean> edit(@RequestBody AdminContent adminContent) {
        adminContent.setUpdatedTime(LocalDateTime.now());
        return R.success(adminContentService.updateById(adminContent));
    }

    /**
     * 删除系统内容表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminContent::remove", name = "系统内容表删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminContentService.removeByIds(Arrays.asList(ids)));
    }

    @PostMapping("upload")
    @Node(value = "adminContent::upload", name = "附件上传")
    public R<String> upload(@RequestParam(value = "thumbnail")MultipartFile file) {
        String filePath = UploadUtils.upload("test", file);
        return R.success(filePath);
    }
}
