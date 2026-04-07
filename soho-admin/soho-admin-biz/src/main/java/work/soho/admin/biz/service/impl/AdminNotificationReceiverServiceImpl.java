package work.soho.admin.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.admin.biz.domain.AdminNotificationReceiver;
import work.soho.admin.biz.mapper.AdminNotificationReceiverMapper;
import work.soho.admin.biz.service.AdminNotificationReceiverService;

/**
 * 通知接收人服务实现。
 */
@Service
public class AdminNotificationReceiverServiceImpl
        extends ServiceImpl<AdminNotificationReceiverMapper, AdminNotificationReceiver>
        implements AdminNotificationReceiverService {
}
