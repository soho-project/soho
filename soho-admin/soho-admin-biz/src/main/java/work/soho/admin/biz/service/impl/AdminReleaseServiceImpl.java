package work.soho.admin.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.biz.domain.AdminRelease;
import work.soho.admin.biz.mapper.AdminReleaseMapper;
import work.soho.admin.biz.service.AdminReleaseService;

@RequiredArgsConstructor
@Service
public class AdminReleaseServiceImpl extends ServiceImpl<AdminReleaseMapper, AdminRelease>
    implements AdminReleaseService{

}