package work.soho.admin.biz.service;

import com.github.pagehelper.PageSerializable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import work.soho.admin.api.request.AdminNotificationCreateRequest;
import work.soho.admin.api.vo.AdminNotificationVo;
import work.soho.admin.biz.domain.AdminNotification;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.List;

/**
* @author i
* @description 针对表【admin_notification(管理员通知)】的数据库操作Service
* @createDate 2022-04-10 22:58:46
*/
public interface AdminNotificationService extends IService<AdminNotification> {
    /**
     * 查询通知管理列表。
     */
    PageSerializable<AdminNotificationVo> listNotifications(AdminNotification query);

    /**
     * 查询当前登录人的未读通知。
     */
    PageSerializable<AdminNotificationVo> myNotifications(String receiverType, Long receiverId);

    /**
     * 创建通知并批量写入接收人。
     */
    boolean createNotification(AdminNotificationCreateRequest request, SohoUserDetails sender);

    /**
     * 查询通知详情。
     */
    AdminNotificationVo getNotificationDetail(Long id);

    /**
     * 标记指定接收记录已读。
     */
    boolean readReceivers(String receiverType, Long receiverId, List<Long> receiverRecordIds);

    /**
     * 标记当前角色全部未读通知为已读。
     */
    boolean readAll(String receiverType, Long receiverId);

    /**
     * 统计当前角色未读通知数。
     */
    long countUnread(String receiverType, Long receiverId);

}
