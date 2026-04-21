package work.soho.admin.biz.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import work.soho.admin.api.dashboard.DashboardNumberCardProvider;
import work.soho.admin.api.vo.DashboardBuildContext;
import work.soho.admin.api.vo.NumberCardVo;
import work.soho.admin.biz.domain.AdminUserLoginLog;
import work.soho.admin.biz.service.AdminNotificationService;
import work.soho.admin.biz.service.AdminUserLoginLogService;
import work.soho.admin.biz.service.AdminUserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 后台 Dashboard 数字卡片提供者。
 */
@Component
@Order(100)
@RequiredArgsConstructor
public class AdminDashboardNumberCardProvider implements DashboardNumberCardProvider {
    private static final String RECEIVER_TYPE_ADMIN = "admin";

    private final AdminUserLoginLogService adminUserLoginLogService;
    private final AdminUserService adminUserService;
    private final AdminNotificationService adminNotificationService;

    @Override
    public List<NumberCardVo> provide(DashboardBuildContext context) {
        List<NumberCardVo> result = new ArrayList<>();
        result.add(buildLoginTotalCard());
        result.add(buildUserCountCard());
        result.add(buildUnreadMessageCard(context));
        result.add(buildTodayLoginCard());
        return result;
    }

    /**
     * 构建后台用户登录总数卡片。
     *
     * @return 数字卡片
     */
    private NumberCardVo buildLoginTotalCard() {
        NumberCardVo loginTotal = new NumberCardVo();
        loginTotal.setNumber(BigDecimal.valueOf(adminUserLoginLogService.count(new LambdaQueryWrapper<>())));
        loginTotal.setTitle("后台用户登录");
        loginTotal.setColor("#64ea91");
        loginTotal.setIcon("user-switch");
        return loginTotal;
    }

    /**
     * 构建后台用户总数卡片。
     *
     * @return 数字卡片
     */
    private NumberCardVo buildUserCountCard() {
        NumberCardVo userCount = new NumberCardVo();
        userCount.setNumber(BigDecimal.valueOf(adminUserService.count()));
        userCount.setTitle("后台用户总数");
        userCount.setIcon("team");
        userCount.setColor("#64ea91");
        return userCount;
    }

    /**
     * 构建未读消息卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 数字卡片
     */
    private NumberCardVo buildUnreadMessageCard(DashboardBuildContext context) {
        NumberCardVo userMessageCount = new NumberCardVo();
        Long loginUserId = context == null ? null : context.getLoginUserId();
        long unreadCount = loginUserId == null ? 0L : adminNotificationService.countUnread(RECEIVER_TYPE_ADMIN, loginUserId);
        userMessageCount.setNumber(BigDecimal.valueOf(unreadCount));
        userMessageCount.setIcon("message");
        userMessageCount.setColor("#d897eb");
        userMessageCount.setTitle("未读系统消息");
        return userMessageCount;
    }

    /**
     * 构建当日登录卡片。
     *
     * @return 数字卡片
     */
    private NumberCardVo buildTodayLoginCard() {
        NumberCardVo todayLoginCount = new NumberCardVo();
        LambdaQueryWrapper<AdminUserLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(AdminUserLoginLog::getCreatedTime, getTodayStart());
        todayLoginCount.setNumber(BigDecimal.valueOf(adminUserLoginLogService.count(wrapper)));
        todayLoginCount.setColor("#f69899");
        todayLoginCount.setIcon("shopping-cart");
        todayLoginCount.setTitle("当日登录");
        return todayLoginCount;
    }

    /**
     * 获取当天起始时间。
     *
     * @return 当天起始时间
     */
    private Calendar getTodayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
