package work.soho.common.core.util;

import com.github.pagehelper.PageHelper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtils {
    /**
     * 启动分页
     */
    public void startPage() {
        int pageSize = 20;
        int pageNum = 1;
        String pageNumStr = RequestUtil.getRequest().getParameter("page");
        String pageSizeStr = RequestUtil.getRequest().getParameter("pageSize");
        if(StringUtils.isNotEmpty(pageNumStr)) {
            pageNum = Integer.parseInt(pageNumStr);
        }
        if(StringUtils.isNotEmpty(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        }

        PageHelper.startPage(pageNum, pageSize);
    }
}
