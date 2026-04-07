package work.soho.admin.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import work.soho.admin.api.event.NewNotificationEvent;
import work.soho.admin.api.request.AdminNotificationCreateRequest;
import work.soho.admin.api.vo.AdminNotificationVo;
import work.soho.admin.biz.domain.AdminNotification;
import work.soho.admin.biz.domain.AdminNotificationReceiver;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.mapper.AdminNotificationMapper;
import work.soho.admin.biz.service.AdminNotificationReceiverService;
import work.soho.admin.biz.service.AdminNotificationService;
import work.soho.admin.biz.service.AdminUserService;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.service.UserApiService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通知服务实现。
 */
@Service
@RequiredArgsConstructor
public class AdminNotificationServiceImpl extends ServiceImpl<AdminNotificationMapper, AdminNotification>
        implements AdminNotificationService {
    private static final String RECEIVER_TYPE_ADMIN = "admin";
    private static final String RECEIVER_TYPE_USER = "user";
    private static final String SENDER_TYPE_ADMIN = "admin";
    private static final String SENDER_TYPE_SYSTEM = "system";
    private static final String RECEIVER_SCOPE_ALL = "all";

    private final AdminNotificationReceiverService adminNotificationReceiverService;
    private final AdminUserService adminUserService;
    private final UserApiService userApiService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 查询通知管理列表。
     */
    @Override
    public PageSerializable<AdminNotificationVo> listNotifications(AdminNotification query) {
        LambdaQueryWrapper<AdminNotification> notificationQuery = new LambdaQueryWrapper<>();
        notificationQuery.eq(query.getId() != null, AdminNotification::getId, query.getId());
        notificationQuery.like(StringUtils.isNotBlank(query.getTitle()), AdminNotification::getTitle, query.getTitle());
        notificationQuery.like(StringUtils.isNotBlank(query.getContent()), AdminNotification::getContent, query.getContent());
        notificationQuery.eq(query.getCreatedTime() != null, AdminNotification::getCreatedTime, query.getCreatedTime());
        notificationQuery.eq(query.getCreateAdminUserId() != null, AdminNotification::getSenderId, query.getCreateAdminUserId());
        appendNotificationIdsFilter(query, notificationQuery);
        notificationQuery.orderByDesc(AdminNotification::getId);

        List<AdminNotification> notifications = list(notificationQuery);
        PageSerializable<AdminNotification> pageSerializable = new PageSerializable<>(notifications);
        List<AdminNotificationVo> rows = buildManageRows(notifications);

        PageSerializable<AdminNotificationVo> result = new PageSerializable<>();
        result.setTotal(pageSerializable.getTotal());
        result.setList(rows);
        return result;
    }

    /**
     * 查询当前登录人的未读通知。
     */
    @Override
    public PageSerializable<AdminNotificationVo> myNotifications(String receiverType, Long receiverId) {
        LambdaQueryWrapper<AdminNotificationReceiver> receiverQuery = new LambdaQueryWrapper<>();
        receiverQuery.eq(AdminNotificationReceiver::getReceiverType, receiverType);
        receiverQuery.eq(AdminNotificationReceiver::getReceiverId, receiverId);
        receiverQuery.eq(AdminNotificationReceiver::getIsRead, 0);
        receiverQuery.orderByDesc(AdminNotificationReceiver::getId);
        List<AdminNotificationReceiver> receivers = adminNotificationReceiverService.list(receiverQuery);
        PageSerializable<AdminNotificationReceiver> pageSerializable = new PageSerializable<>(receivers);

        List<AdminNotificationVo> rows = buildInboxRows(receivers);
        PageSerializable<AdminNotificationVo> result = new PageSerializable<>();
        result.setTotal(pageSerializable.getTotal());
        result.setList(rows);
        return result;
    }

    /**
     * 创建通知并批量写入接收人。
     */
    @Override
    public boolean createNotification(AdminNotificationCreateRequest request, SohoUserDetails sender) {
        String receiverType = normalizeReceiverType(request.getReceiverType());
        List<Long> receiverIds = resolveReceiverIds(request, receiverType);
        if (receiverIds.isEmpty()) {
            return false;
        }

        AdminNotification notification = new AdminNotification();
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setSenderType(sender == null ? SENDER_TYPE_SYSTEM : SENDER_TYPE_ADMIN);
        notification.setSenderId(sender == null ? 0L : sender.getId());
        notification.setCreateAdminUserId(sender == null ? 0L : sender.getId());
        notification.setCreatedTime(LocalDateTime.now());
        notification.setReceiverType(receiverType);
        save(notification);

        List<AdminNotificationReceiver> receivers = new ArrayList<>();
        for (Long targetId : receiverIds) {
            AdminNotificationReceiver receiver = new AdminNotificationReceiver();
            receiver.setNotificationId(notification.getId());
            receiver.setReceiverType(receiverType);
            receiver.setReceiverId(targetId);
            receiver.setIsRead(0);
            receivers.add(receiver);
        }
        adminNotificationReceiverService.saveBatch(receivers);

        for (AdminNotificationReceiver receiver : receivers) {
            publishNotificationEvent(notification, receiver);
        }
        return true;
    }

    /**
     * 查询通知详情。
     */
    @Override
    public AdminNotificationVo getNotificationDetail(Long id) {
        AdminNotification notification = getById(id);
        if (notification == null) {
            return null;
        }
        Map<Long, String> adminNameMap = loadAdminNameMap(Collections.singleton(notification.getSenderId()));
        AdminNotificationVo vo = toManageVo(notification, loadReceiverStats(Collections.singletonList(id)).get(id), adminNameMap);
        return vo;
    }

    /**
     * 标记指定接收记录已读。
     */
    @Override
    public boolean readReceivers(String receiverType, Long receiverId, List<Long> receiverRecordIds) {
        if (receiverRecordIds == null || receiverRecordIds.isEmpty()) {
            return true;
        }
        LambdaUpdateWrapper<AdminNotificationReceiver> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(AdminNotificationReceiver::getId, receiverRecordIds);
        updateWrapper.eq(AdminNotificationReceiver::getReceiverType, receiverType);
        updateWrapper.eq(AdminNotificationReceiver::getReceiverId, receiverId);
        updateWrapper.eq(AdminNotificationReceiver::getIsRead, 0);
        updateWrapper.set(AdminNotificationReceiver::getIsRead, 1);
        updateWrapper.set(AdminNotificationReceiver::getReadTime, LocalDateTime.now());
        return adminNotificationReceiverService.update(updateWrapper);
    }

    /**
     * 标记当前角色全部未读通知为已读。
     */
    @Override
    public boolean readAll(String receiverType, Long receiverId) {
        LambdaUpdateWrapper<AdminNotificationReceiver> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdminNotificationReceiver::getReceiverType, receiverType);
        updateWrapper.eq(AdminNotificationReceiver::getReceiverId, receiverId);
        updateWrapper.eq(AdminNotificationReceiver::getIsRead, 0);
        updateWrapper.set(AdminNotificationReceiver::getIsRead, 1);
        updateWrapper.set(AdminNotificationReceiver::getReadTime, LocalDateTime.now());
        return adminNotificationReceiverService.update(updateWrapper);
    }

    /**
     * 统计当前角色未读通知数。
     */
    @Override
    public long countUnread(String receiverType, Long receiverId) {
        return adminNotificationReceiverService.count(new LambdaQueryWrapper<AdminNotificationReceiver>()
                .eq(AdminNotificationReceiver::getReceiverType, receiverType)
                .eq(AdminNotificationReceiver::getReceiverId, receiverId)
                .eq(AdminNotificationReceiver::getIsRead, 0));
    }

    /**
     * 构建管理端通知行。
     */
    private List<AdminNotificationVo> buildManageRows(List<AdminNotification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, ReceiverStats> statsMap = loadReceiverStats(notifications.stream().map(AdminNotification::getId).collect(Collectors.toList()));
        Map<Long, String> adminNameMap = loadAdminNameMap(notifications.stream()
                .map(AdminNotification::getSenderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        return notifications.stream()
                .map(item -> toManageVo(item, statsMap.get(item.getId()), adminNameMap))
                .collect(Collectors.toList());
    }

    /**
     * 构建收件箱通知行。
     */
    private List<AdminNotificationVo> buildInboxRows(List<AdminNotificationReceiver> receivers) {
        if (receivers == null || receivers.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, AdminNotification> notificationMap = loadNotificationMap(receivers.stream()
                .map(AdminNotificationReceiver::getNotificationId)
                .collect(Collectors.toList()));
        Map<Long, String> adminNameMap = loadAdminNameMap(notificationMap.values().stream()
                .map(AdminNotification::getSenderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        return receivers.stream().map(receiver -> {
            AdminNotification notification = notificationMap.get(receiver.getNotificationId());
            if (notification == null) {
                return null;
            }
            AdminNotificationVo vo = BeanUtils.copy(notification, AdminNotificationVo.class);
            vo.setId(receiver.getId());
            vo.setNotificationId(notification.getId());
            vo.setReceiverType(receiver.getReceiverType());
            vo.setReceiverId(receiver.getReceiverId());
            vo.setIsRead(receiver.getIsRead());
            if (SENDER_TYPE_ADMIN.equalsIgnoreCase(notification.getSenderType())) {
                vo.setCreateAdminUser(adminNameMap.get(notification.getSenderId()));
            } else if (SENDER_TYPE_SYSTEM.equalsIgnoreCase(notification.getSenderType())) {
                vo.setCreateAdminUser("system");
            }
            return vo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 转换为管理端通知视图。
     */
    private AdminNotificationVo toManageVo(AdminNotification notification, ReceiverStats stats, Map<Long, String> adminNameMap) {
        AdminNotificationVo vo = BeanUtils.copy(notification, AdminNotificationVo.class);
        vo.setNotificationId(notification.getId());
        vo.setId(notification.getId());
        if (stats != null) {
            vo.setReceiverType(stats.getReceiverType());
            vo.setReceiverCount(stats.getReceiverCount());
            vo.setReadCount(stats.getReadCount());
            vo.setUnreadCount(stats.getUnreadCount());
        }
        if (SENDER_TYPE_ADMIN.equalsIgnoreCase(notification.getSenderType())) {
            vo.setCreateAdminUser(adminNameMap.get(notification.getSenderId()));
        } else if (SENDER_TYPE_SYSTEM.equalsIgnoreCase(notification.getSenderType())) {
            vo.setCreateAdminUser("system");
        }
        return vo;
    }

    /**
     * 按通知维度加载接收统计。
     */
    private Map<Long, ReceiverStats> loadReceiverStats(List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<AdminNotificationReceiver> receivers = adminNotificationReceiverService.list(new LambdaQueryWrapper<AdminNotificationReceiver>()
                .in(AdminNotificationReceiver::getNotificationId, notificationIds));
        if (receivers == null || receivers.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, ReceiverStats> result = new HashMap<>();
        for (AdminNotificationReceiver receiver : receivers) {
            ReceiverStats stats = result.computeIfAbsent(receiver.getNotificationId(), key -> new ReceiverStats());
            stats.setReceiverType(receiver.getReceiverType());
            stats.setReceiverCount(stats.getReceiverCount() + 1);
            if (Integer.valueOf(1).equals(receiver.getIsRead())) {
                stats.setReadCount(stats.getReadCount() + 1);
            } else {
                stats.setUnreadCount(stats.getUnreadCount() + 1);
            }
        }
        return result;
    }

    /**
     * 预先根据接收者条件裁剪通知范围。
     */
    private void appendNotificationIdsFilter(AdminNotification query, LambdaQueryWrapper<AdminNotification> notificationQuery) {
        boolean filterByReceiverType = StringUtils.isNotBlank(query.getReceiverType());
        boolean filterByReceiverId = query.getAdminUserId() != null || query.getReceiverId() != null;
        if (!filterByReceiverType && !filterByReceiverId && query.getIsRead() == null) {
            return;
        }

        LambdaQueryWrapper<AdminNotificationReceiver> receiverQuery = new LambdaQueryWrapper<>();
        if (filterByReceiverType) {
            receiverQuery.eq(AdminNotificationReceiver::getReceiverType, query.getReceiverType());
        }
        if (query.getAdminUserId() != null) {
            receiverQuery.eq(AdminNotificationReceiver::getReceiverType, RECEIVER_TYPE_ADMIN);
            receiverQuery.eq(AdminNotificationReceiver::getReceiverId, query.getAdminUserId());
        } else if (query.getReceiverId() != null) {
            receiverQuery.eq(AdminNotificationReceiver::getReceiverId, query.getReceiverId());
        }
        if (query.getIsRead() != null) {
            receiverQuery.eq(AdminNotificationReceiver::getIsRead, query.getIsRead());
        }

        List<AdminNotificationReceiver> receivers = adminNotificationReceiverService.list(receiverQuery);
        if (receivers == null || receivers.isEmpty()) {
            notificationQuery.eq(AdminNotification::getId, -1L);
            return;
        }
        Set<Long> notificationIds = receivers.stream().map(AdminNotificationReceiver::getNotificationId).collect(Collectors.toSet());
        notificationQuery.in(AdminNotification::getId, notificationIds);
    }

    /**
     * 加载通知主表映射。
     */
    private Map<Long, AdminNotification> loadNotificationMap(List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<AdminNotification> notifications = list(new LambdaQueryWrapper<AdminNotification>()
                .in(AdminNotification::getId, notificationIds));
        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyMap();
        }
        return notifications.stream().collect(Collectors.toMap(AdminNotification::getId, Function.identity(), (a, b) -> a));
    }

    /**
     * 预加载后台用户名映射。
     */
    private Map<Long, String> loadAdminNameMap(Set<Long> adminIds) {
        if (adminIds == null || adminIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> filteredAdminIds = adminIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (filteredAdminIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<AdminUser> users = adminUserService.list(new LambdaQueryWrapper<AdminUser>().in(AdminUser::getId, filteredAdminIds));
        if (users == null || users.isEmpty()) {
            return Collections.emptyMap();
        }
        return users.stream().collect(Collectors.toMap(AdminUser::getId, AdminUser::getUsername, (a, b) -> a));
    }

    /**
     * 归一化接收者类型。
     */
    private String normalizeReceiverType(String receiverType) {
        if (RECEIVER_TYPE_USER.equalsIgnoreCase(receiverType)) {
            return RECEIVER_TYPE_USER;
        }
        return RECEIVER_TYPE_ADMIN;
    }

    /**
     * 解析创建通知时的接收人 ID 列表。
     */
    private List<Long> resolveReceiverIds(AdminNotificationCreateRequest request, String receiverType) {
        if (isSendToAll(request)) {
            return loadAllReceiverIds(receiverType);
        }

        List<Long> receiverIds = new ArrayList<>();
        if (RECEIVER_TYPE_USER.equals(receiverType)) {
            receiverIds.addAll(request.getUserIds());
            if (request.getUserId() != null) {
                receiverIds.add(request.getUserId());
            }
        } else {
            receiverIds.addAll(request.getAdminUserIds());
            if (request.getAdminUserId() != null) {
                receiverIds.add(request.getAdminUserId());
            }
        }
        return receiverIds.stream().filter(item -> item != null && item > 0).distinct().collect(Collectors.toList());
    }

    /**
     * 判断是否按当前接收类型发送给全员。
     */
    private boolean isSendToAll(AdminNotificationCreateRequest request) {
        return request != null && RECEIVER_SCOPE_ALL.equalsIgnoreCase(request.getReceiverScope());
    }

    /**
     * 按接收类型加载全量接收人。
     */
    private List<Long> loadAllReceiverIds(String receiverType) {
        if (RECEIVER_TYPE_USER.equals(receiverType)) {
            return userApiService.getAllUserIds().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return adminUserService.list(new LambdaQueryWrapper<AdminUser>()
                        .eq(AdminUser::getIsDeleted, 0))
                .stream()
                .map(AdminUser::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 发布新通知事件，按角色推送不同命名空间。
     */
    private void publishNotificationEvent(AdminNotification notification, AdminNotificationReceiver receiver) {
        AdminNotificationVo notificationVo = BeanUtils.copy(notification, AdminNotificationVo.class);
        notificationVo.setId(receiver.getId());
        notificationVo.setNotificationId(notification.getId());
        notificationVo.setReceiverType(receiver.getReceiverType());
        notificationVo.setReceiverId(receiver.getReceiverId());
        notificationVo.setIsRead(receiver.getIsRead());
        NewNotificationEvent newNotificationEvent = new NewNotificationEvent();
        newNotificationEvent.setNotification(notificationVo);
        applicationEventPublisher.publishEvent(newNotificationEvent);
    }

    /**
     * 接收统计对象。
     */
    private static class ReceiverStats {
        private String receiverType;
        private int receiverCount;
        private int readCount;
        private int unreadCount;

        public String getReceiverType() {
            return receiverType;
        }

        public void setReceiverType(String receiverType) {
            this.receiverType = receiverType;
        }

        public int getReceiverCount() {
            return receiverCount;
        }

        public void setReceiverCount(int receiverCount) {
            this.receiverCount = receiverCount;
        }

        public int getReadCount() {
            return readCount;
        }

        public void setReadCount(int readCount) {
            this.readCount = readCount;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }
    }
}
