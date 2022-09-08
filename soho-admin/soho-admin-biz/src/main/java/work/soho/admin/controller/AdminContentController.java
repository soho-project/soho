package work.soho.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.utils.SecurityUtils;
import work.soho.api.admin.request.AdminContentRequest;
import work.soho.api.admin.request.BetweenDateRequest;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.admin.domain.AdminContent;
import work.soho.admin.service.AdminContentService;
import work.soho.common.data.upload.utils.UploadUtils;

/**
 * 系统内容表Controller
 *
 * @author i
 * @date 2022-09-03 01:59:15
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminContent" )
public class AdminContentController extends BaseController {

    private final AdminContentService adminContentService;

    /**
     * 查询系统内容表列表
     */
    @GetMapping("/list")
    @Node(value = "adminContent::list", name = "系统内容表列表")
    public R<PageSerializable<AdminContent>> list(AdminContentRequest adminContentRequest)
    {
        startPage();
        LambdaQueryWrapper<AdminContent> lqw = new LambdaQueryWrapper<>();

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
    public R<String> upload(MultipartFile file) {
        String filePath = UploadUtils.upload("test", file);
        return R.success(filePath);
    }
}
