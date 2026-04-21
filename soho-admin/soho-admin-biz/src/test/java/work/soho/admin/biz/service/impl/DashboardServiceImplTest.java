package work.soho.admin.biz.service.impl;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import work.soho.admin.api.dashboard.DashboardKvCardProvider;
import work.soho.admin.api.dashboard.DashboardListCardProvider;
import work.soho.admin.api.dashboard.DashboardNumberCardProvider;
import work.soho.admin.api.dashboard.DashboardUserCardProvider;
import work.soho.admin.api.vo.DashboardIndexVo;
import work.soho.admin.api.vo.DashboardUserCardVo;
import work.soho.admin.api.vo.NumberCardVo;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Dashboard 聚合服务测试。
 */
public class DashboardServiceImplTest {

    /**
     * 清理安全上下文。
     */
    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 验证能够聚合所有 provider 结果。
     */
    @Test
    public void index_shouldAggregateAllProviderResults() {
        mockLoginUser(1L, "guest");
        DashboardNumberCardProvider numberProvider = context -> {
            assertThat(context.getLoginUserId()).isEqualTo(1L);
            return List.of(buildNumberCard("登录数", 10));
        };
        DashboardUserCardProvider userProvider = context -> List.of(buildUserCard("guest"));
        DashboardListCardProvider listProvider = context -> List.of(buildListCard("标题", "文章A"));
        DashboardKvCardProvider kvProvider = context -> List.of(buildKvCard("JDK版本", "17"));

        DashboardServiceImpl service = new DashboardServiceImpl(
                List.of(numberProvider),
                List.of(userProvider),
                List.of(listProvider),
                List.of(kvProvider)
        );

        DashboardIndexVo result = service.index();

        assertThat(result.getNumbers()).hasSize(1);
        assertThat(result.getNumbers().get(0).getTitle()).isEqualTo("登录数");
        assertThat(result.getListUserCard()).hasSize(1);
        assertThat(result.getListUserCard().get(0).getUsername()).isEqualTo("guest");
        assertThat(result.getListCards()).hasSize(1);
        assertThat(result.getListKVCards()).hasSize(1);
    }

    /**
     * 验证 provider 抛异常时不会影响其它结果。
     */
    @Test
    public void index_shouldSkipProviderWhenRuntimeExceptionThrown() {
        mockLoginUser(1L, "guest");
        DashboardNumberCardProvider brokenProvider = context -> {
            throw new IllegalStateException("boom");
        };

        DashboardServiceImpl service = new DashboardServiceImpl(
                List.of(brokenProvider),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        DashboardIndexVo result = service.index();

        assertThat(result.getNumbers()).isEmpty();
        assertThat(result.getListUserCard()).isEmpty();
        assertThat(result.getListCards()).isEmpty();
        assertThat(result.getListKVCards()).isEmpty();
    }

    /**
     * 模拟登录用户。
     *
     * @param userId 用户 ID
     * @param username 用户名
     */
    private void mockLoginUser(Long userId, String username) {
        SohoUserDetails userDetails = new SohoUserDetails();
        userDetails.setId(userId);
        userDetails.setUsername(username);
        userDetails.setPassword("123456");
        userDetails.setAuthorities(Collections.emptyList());
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken(userDetails, null, Collections.emptyList()));
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * 构造数字卡片。
     *
     * @param title 标题
     * @param number 数值
     * @return 数字卡片
     */
    private NumberCardVo buildNumberCard(String title, int number) {
        NumberCardVo card = new NumberCardVo();
        card.setTitle(title);
        card.setNumber(BigDecimal.valueOf(number));
        return card;
    }

    /**
     * 构造用户卡片。
     *
     * @param username 用户名
     * @return 用户卡片
     */
    private DashboardUserCardVo buildUserCard(String username) {
        DashboardUserCardVo card = new DashboardUserCardVo();
        card.setUsername(username);
        return card;
    }

    /**
     * 构造列表卡片。
     *
     * @param key 字段名
     * @param value 字段值
     * @return 列表卡片
     */
    private List buildListCard(String key, Object value) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<>();
        row.put(key, value);
        LinkedList<LinkedHashMap<String, Object>> card = new LinkedList<>();
        card.add(row);
        return card;
    }

    /**
     * 构造键值卡片。
     *
     * @param name 名称
     * @param value 值
     * @return 键值卡片
     */
    private List buildKvCard(String name, Object value) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<>();
        row.put("name", name);
        row.put("percent", value);
        row.put("status", 1);
        LinkedList<LinkedHashMap<String, Object>> card = new LinkedList<>();
        card.add(row);
        return card;
    }
}
