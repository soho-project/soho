package work.soho.admin.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.biz.domain.AdminSmsTemplate;
import work.soho.admin.biz.service.AdminSmsTemplateService;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 短信模板Controller
 *
 * @author fang
 */
@Api(tags = "短信模板")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/admin/adminSmsTemplate" )
public class AdminSmsTemplateController {

    private final AdminSmsTemplateService adminSmsTemplateService;

    /**
     * 查询短信模板列表
     */
    @GetMapping("/list")
    @Node(value = "adminSmsTemplate::list", name = "短信模板;;列表")
    public R<PageSerializable<AdminSmsTemplate>> list(AdminSmsTemplate adminSmsTemplate, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AdminSmsTemplate> lqw = new LambdaQueryWrapper<AdminSmsTemplate>();
        lqw.eq(adminSmsTemplate.getId() != null, AdminSmsTemplate::getId ,adminSmsTemplate.getId());
        lqw.like(StringUtils.isNotBlank(adminSmsTemplate.getAdapterName()),AdminSmsTemplate::getAdapterName ,adminSmsTemplate.getAdapterName());
        lqw.like(StringUtils.isNotBlank(adminSmsTemplate.getName()),AdminSmsTemplate::getName ,adminSmsTemplate.getName());
        lqw.like(StringUtils.isNotBlank(adminSmsTemplate.getTitle()),AdminSmsTemplate::getTitle ,adminSmsTemplate.getTitle());
        lqw.like(StringUtils.isNotBlank(adminSmsTemplate.getTemplateCode()),AdminSmsTemplate::getTemplateCode ,adminSmsTemplate.getTemplateCode());
        lqw.like(StringUtils.isNotBlank(adminSmsTemplate.getSignName()),AdminSmsTemplate::getSignName ,adminSmsTemplate.getSignName());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AdminSmsTemplate::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AdminSmsTemplate::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(adminSmsTemplate.getUpdatedTime() != null, AdminSmsTemplate::getUpdatedTime ,adminSmsTemplate.getUpdatedTime());
        lqw.orderByDesc(AdminSmsTemplate::getId);
        List<AdminSmsTemplate> list = adminSmsTemplateService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取短信模板详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "adminSmsTemplate::getInfo", name = "短信模板;;详细信息")
    public R<AdminSmsTemplate> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminSmsTemplateService.getById(id));
    }

    /**
     * 新增短信模板
     */
    @PostMapping
    @Node(value = "adminSmsTemplate::add", name = "短信模板;;新增")
    public R<Boolean> add(@RequestBody AdminSmsTemplate adminSmsTemplate) {
        return R.success(adminSmsTemplateService.save(adminSmsTemplate));
    }

    /**
     * 修改短信模板
     */
    @PutMapping
    @Node(value = "adminSmsTemplate::edit", name = "短信模板;;修改")
    public R<Boolean> edit(@RequestBody AdminSmsTemplate adminSmsTemplate) {
        return R.success(adminSmsTemplateService.updateById(adminSmsTemplate));
    }

    /**
     * 删除短信模板
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "adminSmsTemplate::remove", name = "短信模板;;删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminSmsTemplateService.removeByIds(Arrays.asList(ids)));
    }
}