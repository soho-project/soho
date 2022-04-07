package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.admin.domain.AdminConfig;
import work.soho.admin.service.AdminConfigService;
import work.soho.admin.mapper.AdminConfigMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【admin_config】的数据库操作Service实现
* @createDate 2022-04-05 23:01:25
*/
@Service
public class AdminConfigServiceImpl extends ServiceImpl<AdminConfigMapper, AdminConfig>
    implements AdminConfigService{

}




