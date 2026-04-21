package work.soho.admin.api.vo;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Dashboard 聚合结果。
 */
@Data
public class DashboardIndexVo {
    /**
     * 数字卡片。
     */
    private LinkedList<NumberCardVo> numbers = new LinkedList<>();

    /**
     * 列表卡片。
     */
    private LinkedList<List> listCards = new LinkedList<>();

    /**
     * 键值卡片。
     */
    private LinkedList<List> listKVCards = new LinkedList<>();

    /**
     * 用户信息卡片。
     */
    private LinkedList<DashboardUserCardVo> listUserCard = new LinkedList<>();
}
