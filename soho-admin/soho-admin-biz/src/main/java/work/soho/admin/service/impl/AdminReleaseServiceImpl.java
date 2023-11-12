package work.soho.admin.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.domain.AdminRelease;
import work.soho.admin.mapper.AdminReleaseMapper;
import work.soho.admin.service.AdminReleaseService;

@RequiredArgsConstructor
@Service
public class AdminReleaseServiceImpl extends ServiceImpl<AdminReleaseMapper, AdminRelease>
    implements AdminReleaseService{

}