package work.soho.common.quartz.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Arrays;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.RequestUtil;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.quartz.domain.AdminJob;
import work.soho.common.quartz.service.AdminJobService;
import work.soho.common.quartz.util.InvokeUtil;
import work.soho.common.quartz.util.JobUtil;

/**
 * admin_jobController
 *
 * @author i
 * @date 2022-07-26 03:45:29
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminJob" )
public class AdminJobController {

    private final AdminJobService adminJobService;

    private final Scheduler scheduler;

    @GetMapping("/test")
    public String test() throws SchedulerException {
        AdminJob adminJob = adminJobService.getById(1);
        JobUtil.buildJob(scheduler, adminJob);
        return "hello";
    }

    @GetMapping("/run")
    public String run() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        InvokeUtil.invoke("work.soho.common.quartz.Hello::test(2000)");
//        DefaultListableBeanFactory
     //   SpringUtil.getBean(Class.forName("work.soho.common.quartz.Hello"));
        return "is runing";
    }

    /**
     * 查询admin_job列表
     */
    @GetMapping("/list")
    public R<PageSerializable<AdminJob>> list(AdminJob adminJob)
    {
        PageHelper.startPage(RequestUtil.getRequest());
//        startPage();
        LambdaQueryWrapper<AdminJob> lqw = new LambdaQueryWrapper<AdminJob>();

        if (adminJob.getId() != null){
            lqw.eq(AdminJob::getId ,adminJob.getId());
        }
        if (StringUtils.isNotBlank(adminJob.getName())){
            lqw.like(AdminJob::getName ,adminJob.getName());
        }
        if (adminJob.getCanConcurrency() != null){
            lqw.eq(AdminJob::getCanConcurrency ,adminJob.getCanConcurrency());
        }
        if (StringUtils.isNotBlank(adminJob.getCmd())){
            lqw.like(AdminJob::getCmd ,adminJob.getCmd());
        }
        if (adminJob.getStatus() != null){
            lqw.eq(AdminJob::getStatus ,adminJob.getStatus());
        }
        if (StringUtils.isNotBlank(adminJob.getCron())){
            lqw.like(AdminJob::getCron ,adminJob.getCron());
        }
        if (adminJob.getCreatedTime() != null){
            lqw.eq(AdminJob::getCreatedTime ,adminJob.getCreatedTime());
        }
        if (adminJob.getUpdatedTime() != null){
            lqw.eq(AdminJob::getUpdatedTime ,adminJob.getUpdatedTime());
        }
        List<AdminJob> list = adminJobService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取admin_job详细信息
     */
    @GetMapping(value = "/{id}" )
    public R<AdminJob> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminJobService.getById(id));
    }

    /**
     * 新增admin_job
     */
    @PostMapping
    public R<Boolean> add(@RequestBody AdminJob adminJob) {
        boolean result = adminJobService.save(adminJob);
        if(result) {
            JobUtil.buildJob(scheduler, adminJob);
        }
        return R.success(result);
    }

    /**
     * 修改admin_job
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminJob adminJob) {
        boolean result = adminJobService.updateById(adminJob);
        if(result) {
            JobUtil.buildJob(scheduler, adminJob);
        }
        return R.success(result);
    }

    /**
     * 删除admin_job
     */
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            AdminJob adminJob = adminJobService.getById(ids[i]);
            JobUtil.removeJob(scheduler, adminJob);
        }
        return R.success(adminJobService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("/run/{id}")
    public R<Boolean> run(@PathVariable(name = "id") Integer id) {
        AdminJob adminJob = adminJobService.getById(id);
        JobUtil.runJob(scheduler, adminJob);
        return R.success(Boolean.TRUE);
    }
}
