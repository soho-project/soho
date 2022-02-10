package work.soho.admin.interceptor;

import com.alibaba.excel.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import work.soho.admin.service.AdminResourceService;
import work.soho.admin.service.AdminRoleService;
import work.soho.admin.service.AdminUserService;
import work.soho.api.admin.po.AdminResource;
import work.soho.api.admin.po.AdminRole;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final AdminResourceService adminResourceService;

    private final AdminUserService adminUserService;

    private final AdminRoleService adminRoleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //TODO         return HandlerInterceptor.super.preHandle(request, response, handler);
        String path = request.getRequestURI();

        AdminResource adminResource = adminResourceService.getByPath(path);
        String token = request.getHeader("TOKEN");
        if(StringUtils.isEmpty(token)) {
            return false;
        }
        Integer userId = adminUserService.getUserIdOuthToken(token);
        //用户ID为1的用户为固定超级用户,无需鉴权
        if(userId == 1) {
            return true;
        }
        HashMap<Long, AdminResource> userResource = getAdminResourceList(userId);
        AdminRole adminRole = null;
        if(adminResource != null) {
            return userResource.get(adminResource.getId()) != null;
        }
        return true;
    }

    private HashMap<Long, AdminResource> getAdminResourceList(Integer userId) {
        List<AdminRole> list = adminRoleService.getRoleListByUserId(userId);
        HashMap<Long, AdminRole> roleHashMap = new HashMap<>();
        list.forEach(item -> roleHashMap.put(item.getId(), item));
        List<AdminResource> resources = adminResourceService.getListByRoleIds((Integer[]) roleHashMap.entrySet().toArray());
        HashMap<Long, AdminResource> resourceHashMap = new HashMap<>();
        resources.forEach(item -> resourceHashMap.put(item.getId(), item));
        return resourceHashMap;
    }
}
