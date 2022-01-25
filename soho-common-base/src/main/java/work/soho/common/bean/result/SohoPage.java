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

  /** 总记录数 */
  private int total;
  /** 每页记录数 */
  private int pageSize;
  /** 总页数 */
  private int pages;
  /** 当前页数 */
  private int pageNum;
  /** 列表数据 */
  private List<T> list;

  public SohoPage(List<T> list, int total, int pageSize, int pageNum) {
    this.list = list;
    this.total = total;
    this.pageSize = pageSize;
    this.pageNum = pageNum;
    this.pages = (int) Math.ceil((double) total / pageSize);
  }

  public SohoPage(Page<T> page) {
    this.list = page.getResult();
    this.total = (int) page.getTotal();
    this.pageSize = (int) page.getPageSize();
    this.pageNum = (int) page.getPageNum();
    this.pages = (int) page.getPages();
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
