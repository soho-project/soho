package work.soho.api.admin.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;
import work.soho.api.admin.vo.DashboardUserCardVo;
import work.soho.api.admin.vo.NumberCardVo;

import java.util.LinkedList;
import java.util.List;

@Data
public class DashboardEvent extends ApplicationEvent {
    /**
     * 数字卡片
     */
    private LinkedList<NumberCardVo> numbers = new LinkedList<>();

    /**
     * 列表卡片
     */
    private LinkedList<List> listCards = new LinkedList<>();

    /**
     * kv数据结构
     */
    private LinkedList<List> listKVCards = new LinkedList<>();

    /**
     * 用户信息卡片
     */
    private LinkedList<DashboardUserCardVo> listUserCard = new LinkedList<>();

    public DashboardEvent(Object source) {
        super(source);
    }

    /**
     * 添加数字卡片
     *
     * @param numberCardVo
     */
    public void addNumberCard(NumberCardVo numberCardVo) {
        numbers.add(numberCardVo);
    }
}
