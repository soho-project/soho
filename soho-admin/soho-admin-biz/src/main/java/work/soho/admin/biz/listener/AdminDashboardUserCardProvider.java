package work.soho.admin.biz.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import work.soho.admin.api.dashboard.DashboardUserCardProvider;
import work.soho.admin.api.vo.DashboardBuildContext;
import work.soho.admin.api.vo.DashboardUserCardVo;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.domain.AdminUserLoginLog;
import work.soho.admin.biz.service.AdminUserLoginLogService;
import work.soho.admin.biz.service.AdminUserService;

import java.util.Collections;
import java.util.List;
import java.util.Calendar;

/**
 * 后台 Dashboard 用户卡片提供者。
 */
@Component
@Order(100)
@RequiredArgsConstructor
public class AdminDashboardUserCardProvider implements DashboardUserCardProvider {
    private final AdminUserLoginLogService adminUserLoginLogService;
    private final AdminUserService adminUserService;

    @Override
    public List<DashboardUserCardVo> provide(DashboardBuildContext context) {
        Long loginUserId = context == null ? null : context.getLoginUserId();
        if (loginUserId == null) {
            return Collections.emptyList();
        }
        AdminUser adminUser = adminUserService.getById(loginUserId);
        if (adminUser == null) {
            return Collections.emptyList();
        }
        DashboardUserCardVo dashboardUserCardVo = new DashboardUserCardVo();
        dashboardUserCardVo.setUserId(adminUser.getId());
        dashboardUserCardVo.setUsername(adminUser.getUsername());
        dashboardUserCardVo.setAvatar(adminUser.getAvatar());
        dashboardUserCardVo.getListInfo().add(buildTotalInfo(adminUser.getId()));
        dashboardUserCardVo.getListInfo().add(buildTodayInfo(adminUser.getId()));
        return Collections.singletonList(dashboardUserCardVo);
    }

    /**
     * 构建用户总登录次数信息。
     *
     * @param adminUserId 后台用户ID
     * @return 信息项
     */
    private DashboardUserCardVo.Info buildTotalInfo(Long adminUserId) {
        DashboardUserCardVo.Info totalInfo = new DashboardUserCardVo.Info();
        totalInfo.setTitle("用户总登录次数");
        LambdaQueryWrapper<AdminUserLoginLog> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(AdminUserLoginLog::getAdminUserId, adminUserId);
        totalInfo.setValue(adminUserLoginLogService.count(totalWrapper));
        return totalInfo;
    }

    /**
     * 构建用户当日登录次数信息。
     *
     * @param adminUserId 后台用户ID
     * @return 信息项
     */
    private DashboardUserCardVo.Info buildTodayInfo(Long adminUserId) {
        DashboardUserCardVo.Info todayInfo = new DashboardUserCardVo.Info();
        todayInfo.setTitle("用户当日录次数");
        LambdaQueryWrapper<AdminUserLoginLog> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(AdminUserLoginLog::getAdminUserId, adminUserId);
        todayWrapper.gt(AdminUserLoginLog::getCreatedTime, getTodayStart());
        todayInfo.setValue(adminUserLoginLogService.count(todayWrapper));
        return todayInfo;
    }

    /**
     * 获取当天起始时间。
     *
     * @return 当天起始时间
     */
    private Calendar getTodayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
