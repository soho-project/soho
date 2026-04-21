package work.soho.admin.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import work.soho.admin.api.vo.DashboardIndexVo;
import work.soho.admin.api.vo.DashboardUserCardVo;
import work.soho.admin.api.vo.NumberCardVo;
import work.soho.admin.biz.controller.DashboardController;
import work.soho.admin.biz.service.DashboardService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Dashboard 控制器测试。
 */
public class DashboardControllerTest {
    private MockMvc mockMvc;

    /**
     * 初始化 MockMvc。
     */
    @Before
    public void setup() {
        DashboardService dashboardService = this::buildDashboardIndexVo;
        DashboardController dashboardController = new DashboardController(dashboardService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    /**
     * 验证首页接口返回结构。
     *
     * @throws Exception 请求异常
     */
    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/admin/admin/adminDashboard/index").contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.numbers").isArray())
                .andExpect(jsonPath("$.payload.listCards").isArray())
                .andExpect(jsonPath("$.payload.listKVCards").isArray())
                .andExpect(jsonPath("$.payload.listUserCard").isArray())
                .andExpect(jsonPath("$.payload.numbers[0].title").value("登录数"))
                .andExpect(jsonPath("$.payload.listUserCard[0].username").value("guest"))
                .andReturn();
    }

    /**
     * 构造 dashboard 返回数据。
     *
     * @return dashboard 返回对象
     */
    private DashboardIndexVo buildDashboardIndexVo() {
        DashboardIndexVo result = new DashboardIndexVo();
        result.getNumbers().add(buildNumberCard());
        result.getListUserCard().add(buildUserCard());
        result.getListCards().add(buildListCard());
        result.getListKVCards().add(buildKvCard());
        return result;
    }

    /**
     * 构造数字卡片。
     *
     * @return 数字卡片
     */
    private NumberCardVo buildNumberCard() {
        NumberCardVo card = new NumberCardVo();
        card.setTitle("登录数");
        card.setNumber(BigDecimal.TEN);
        return card;
    }

    /**
     * 构造用户卡片。
     *
     * @return 用户卡片
     */
    private DashboardUserCardVo buildUserCard() {
        DashboardUserCardVo card = new DashboardUserCardVo();
        card.setUsername("guest");
        return card;
    }

    /**
     * 构造列表卡片。
     *
     * @return 列表卡片
     */
    private List buildListCard() {
        LinkedHashMap<String, Object> row = new LinkedHashMap<>();
        row.put("标题", "文章A");
        LinkedList<LinkedHashMap<String, Object>> card = new LinkedList<>();
        card.add(row);
        return card;
    }

    /**
     * 构造键值卡片。
     *
     * @return 键值卡片
     */
    private List buildKvCard() {
        LinkedHashMap<String, Object> row = new LinkedHashMap<>();
        row.put("name", "JDK版本");
        row.put("percent", "17");
        row.put("status", 1);
        LinkedList<LinkedHashMap<String, Object>> card = new LinkedList<>();
        card.add(row);
        return card;
    }
}
