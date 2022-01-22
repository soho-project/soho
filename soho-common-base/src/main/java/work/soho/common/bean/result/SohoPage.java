package work.soho.common.bean.result;

import com.github.pagehelper.Page;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SohoPage
 * </p>
 *
 * @author livk
 * @date 2022/1/22
 */
@Data
public class SohoPage<T> {
    private static final long serialVersionUID = 1L;

    private int pageNum;

    private int pageSize;

    private final long total;

    private final List<T> list;

    private SohoPage(List<T> list) {
        this.list = list;
        if (list instanceof Page) {
            Page<T> page = (Page<T>) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.total = page.getTotal();
        } else {
            this.total = list.size();
        }
    }

    public static <T> SohoPage<T> of(List<T> list) {
        return new SohoPage<>(list);
    }
}
