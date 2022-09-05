package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.admin.domain.AdminContent;
import work.soho.admin.service.AdminContentService;
import work.soho.admin.mapper.AdminContentMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【admin_content(系统内容表)】的数据库操作Service实现
* @createDate 2022-09-03 01:14:09
*/
@Service
public class AdminContentServiceImpl extends ServiceImpl<AdminContentMapper, AdminContent>
    implements AdminContentService{

}




