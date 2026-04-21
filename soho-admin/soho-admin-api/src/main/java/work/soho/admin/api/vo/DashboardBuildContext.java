package work.soho.admin.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard 构建上下文。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardBuildContext {
    /**
     * 当前登录用户ID。
     */
    private Long loginUserId;
}
