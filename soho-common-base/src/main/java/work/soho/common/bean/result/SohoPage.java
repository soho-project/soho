package work.soho.common.bean.result;

import com.github.pagehelper.Page;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * SohoPage
 *
 * @author livk
 * @date 2022/1/22
 */
@Getter
public class SohoPage<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private int total;
    /**
     * 每页记录数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 当前页数
     */
    private int pageNum;
    /**
     * 列表数据
     */
    private List<T> list;

    @Deprecated
    public SohoPage(List<T> list, int total, int pages, int pageNum) {
        //TODO:这方法有问题吧
        this.list = list;
        this.total = total;
        this.pages = pages;
        this.pageNum = pageNum;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    public SohoPage(Page<T> page) {
        this.list = page.getResult();
        this.total = Long.valueOf(page.getTotal()).intValue();
        this.pageSize = page.getPageSize();
        this.pageNum = page.getPageNum();
        this.pages = page.getPages();
    }

    private SohoPage(List<T> list) {
        this.list = list;
        if (list instanceof Page) {
            Page<T> page = (Page<T>) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.pages = (int) page.getTotal();
        } else {
            this.total = list.size();
        }
    }

    public static <T> SohoPage<T> of(List<T> list) {
        return new SohoPage<>(list);
    }
}
