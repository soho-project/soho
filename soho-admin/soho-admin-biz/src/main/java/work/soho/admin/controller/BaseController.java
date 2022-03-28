package work.soho.admin.controller;

import com.github.pagehelper.PageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    /**
     * 启动默认参数分页
     */
    public void startPage() {
        Integer pageNum = 1;
        Integer pageSize = 20;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String pageNumString = request.getParameter("pageNum");
        String pageSizeString = request.getParameter("pageSize");
        if(!StringUtils.isEmpty(pageSizeString)) {
            pageNum = Integer.parseInt(pageNumString);
        }
        if(!StringUtils.isEmpty(pageSizeString)) {
            pageSize = Integer.parseInt(pageSizeString);
        }

        startPage(pageNum, pageSize);
    }

    /**
     * 启动分页
     *
     * @param pageNum
     * @param pageSize
     */
    public void startPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }
}
