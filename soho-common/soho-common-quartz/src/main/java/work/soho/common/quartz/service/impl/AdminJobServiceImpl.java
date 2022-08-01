package work.soho.common.quartz.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import work.soho.common.quartz.domain.AdminJob;
import work.soho.common.quartz.service.AdminJobService;
import work.soho.common.quartz.mapper.AdminJobMapper;
import org.springframework.stereotype.Service;
import work.soho.common.quartz.util.JobUtil;

import javax.annotation.PostConstruct;
import java.util.List;

/**
* @author i
* @description 针对表【admin_job】的数据库操作Service实现
* @createDate 2022-07-26 03:42:42
*/
@RequiredArgsConstructor
@Service
public class AdminJobServiceImpl extends ServiceImpl<AdminJobMapper, AdminJob>
    implements AdminJobService{

    private final Scheduler scheduler;

    @PostConstruct
    public void init() {
        List<AdminJob> list = getBaseMapper().selectList(Wrappers.emptyWrapper());
        for (AdminJob job:list) {
            JobUtil.buildJob(scheduler, job);
        }
    }
}




