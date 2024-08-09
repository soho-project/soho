package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.domain.AdminEmailTemplate;
import work.soho.admin.service.AdminEmailTemplateService;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 邮件模板Controller
 *
 * @author fang
 */
@Api(tags = "邮件模板")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminEmailTemplate" )
public class AdminEmailTemplateController {

    private final AdminEmailTemplateService adminEmailTemplateService;

    /**
     * 查询邮件模板列表
     */
    @GetMapping("/list")
    @Node(value = "adminEmailTemplate::list", name = "邮件模板;;列表")
    public R<PageSerializable<AdminEmailTemplate>> list(AdminEmailTemplate adminEmailTemplate, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AdminEmailTemplate> lqw = new LambdaQueryWrapper<AdminEmailTemplate>();
        lqw.eq(adminEmailTemplate.getId() != null, AdminEmailTemplate::getId ,adminEmailTemplate.getId());
        lqw.like(StringUtils.isNotBlank(adminEmailTemplate.getName()),AdminEmailTemplate::getName ,adminEmailTemplate.getName());
        lqw.like(StringUtils.isNotBlank(adminEmailTemplate.getTitle()),AdminEmailTemplate::getTitle ,adminEmailTemplate.getTitle());
        lqw.like(StringUtils.isNotBlank(adminEmailTemplate.getBody()),AdminEmailTemplate::getBody ,adminEmailTemplate.getBody());
        lqw.eq(adminEmailTemplate.getUpdatedTime() != null, AdminEmailTemplate::getUpdatedTime ,adminEmailTemplate.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AdminEmailTemplate::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AdminEmailTemplate::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<AdminEmailTemplate> list = adminEmailTemplateService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取邮件模板详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminEmailTemplate::getInfo", name = "邮件模板;;详细信息")
    public R<AdminEmailTemplate> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminEmailTemplateService.getById(id));
    }

    /**
     * 新增邮件模板
     */
    @PostMapping
    @Node(value = "adminEmailTemplate::add", name = "邮件模板;;新增")
    public R<Boolean> add(@RequestBody AdminEmailTemplate adminEmailTemplate) {
        return R.success(adminEmailTemplateService.save(adminEmailTemplate));
    }

    /**
     * 修改邮件模板
     */
    @PutMapping
    @Node(value = "adminEmailTemplate::edit", name = "邮件模板;;修改")
    public R<Boolean> edit(@RequestBody AdminEmailTemplate adminEmailTemplate) {
        return R.success(adminEmailTemplateService.updateById(adminEmailTemplate));
    }

    /**
     * 删除邮件模板
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminEmailTemplate::remove", name = "邮件模板;;删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminEmailTemplateService.removeByIds(Arrays.asList(ids)));
    }
}