package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.admin.domain.AdminNotification;
import work.soho.admin.service.AdminNotificationService;
import work.soho.admin.mapper.AdminNotificationMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【admin_notification(管理员通知)】的数据库操作Service实现
* @createDate 2022-04-10 22:58:46
*/
@Service
public class AdminNotificationServiceImpl extends ServiceImpl<AdminNotificationMapper, AdminNotification>
    implements AdminNotificationService{

}




