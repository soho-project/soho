package work.soho.admin.biz.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.soho.admin.common.security.utils.SecurityUtils;
import work.soho.admin.biz.domain.AdminNotification;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.domain.AdminUserLoginLog;
import work.soho.admin.biz.service.AdminNotificationService;
import work.soho.admin.biz.service.AdminUserLoginLogService;
import work.soho.admin.biz.service.AdminUserService;
import work.soho.admin.api.event.DashboardEvent;
import work.soho.admin.api.vo.DashboardUserCardVo;
import work.soho.admin.api.vo.NumberCardVo;

import java.math.BigDecimal;
import java.util.Calendar;

@Component
public class NumberCardEventListener {
    @Autowired
    private AdminUserLoginLogService adminUserLoginLogService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminNotificationService adminNotificationService;

    @EventListener
    public void numberCard(DashboardEvent event) {
        //后台用户总登录次数
        NumberCardVo loginTotal = new NumberCardVo();
        LambdaQueryWrapper<AdminUserLoginLog> loginLogLambdaQueryWrapper = new LambdaQueryWrapper<AdminUserLoginLog>();
        loginTotal.setNumber(BigDecimal.valueOf(adminUserLoginLogService.count(loginLogLambdaQueryWrapper)));
        loginTotal.setTitle("后台用户登录");
        loginTotal.setColor("#64ea91");
        loginTotal.setIcon("user-switch");
        event.addNumberCard(loginTotal);

        //统计后台用户数
        NumberCardVo userCount  = new NumberCardVo();
        userCount.setNumber(BigDecimal.valueOf(adminUserService.count()));
        userCount.setTitle("后台用户总数");
        userCount.setIcon("team");
        userCount.setColor("#64ea91");
        event.addNumberCard(userCount);

        //统计用户未读消息数
        NumberCardVo userMessageCount  = new NumberCardVo();
        LambdaQueryWrapper<AdminNotification> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AdminNotification::getAdminUserId, SecurityUtils.getLoginUserId())
                .eq(AdminNotification::getIsRead, 0);
        userMessageCount.setNumber(new BigDecimal(adminNotificationService.count(lambdaQueryWrapper)));
        userMessageCount.setIcon("message");
        userMessageCount.setColor("#d897eb");
        userMessageCount.setTitle("未读系统消息");
        event.addNumberCard(userMessageCount);

        //今日客户端
        NumberCardVo userClientCount = new NumberCardVo();
        LambdaQueryWrapper<AdminUserLoginLog> loginLogLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        loginLogLambdaQueryWrapper1.gt(AdminUserLoginLog::getCreatedTime, calendar.getTime());
        userClientCount.setNumber(new BigDecimal(adminUserLoginLogService.count(loginLogLambdaQueryWrapper1)));
        userClientCount.setColor("#f69899");
        userClientCount.setIcon("shopping-cart");
        userClientCount.setTitle("当日登录");
        event.addNumberCard(userClientCount);

    }

    @EventListener
    public void userInfoCards(DashboardEvent event) {
        DashboardUserCardVo dashboardUserCardVo = new DashboardUserCardVo();
        AdminUser adminUser = adminUserService.getById(SecurityUtils.getLoginUserId());
        dashboardUserCardVo.setUserId(adminUser.getId());
        dashboardUserCardVo.setUsername(adminUser.getUsername());
        dashboardUserCardVo.setAvatar(adminUser.getAvatar());
        //查询用户登录总次数
        DashboardUserCardVo.Info totalInfo = new DashboardUserCardVo.Info();
        totalInfo.setTitle("用户总登录次数");
        LambdaQueryWrapper<AdminUserLoginLog> totalLqw = new LambdaQueryWrapper<>();
        totalLqw.eq(AdminUserLoginLog::getAdminUserId, adminUser.getId());
        totalInfo.setValue(adminUserLoginLogService.count(totalLqw));
        dashboardUserCardVo.getListInfo().add(totalInfo);
        //查询用户当日登录次数
        DashboardUserCardVo.Info todayInfo = new DashboardUserCardVo.Info();
        todayInfo.setTitle("用户当日录次数");
        LambdaQueryWrapper<AdminUserLoginLog> todayLqw = new LambdaQueryWrapper<>();
        todayLqw.eq(AdminUserLoginLog::getAdminUserId, adminUser.getId());
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        todayLqw.gt(AdminUserLoginLog::getCreatedTime, c);

        todayInfo.setValue(adminUserLoginLogService.count(todayLqw));
        dashboardUserCardVo.getListInfo().add(todayInfo);

        event.getListUserCard().add(dashboardUserCardVo);
    }

}
