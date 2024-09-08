package work.soho.api.admin.service;

import work.soho.api.admin.vo.AdminUserVo;

import java.util.HashSet;

public interface AdminInfoApiService {
    /**
     * 获取管理用户详细信息
     *
     * @param id
     * @return
     */
    AdminUserVo getAdminById(Long id);

    /**
     * 获取用户资源set
     *
     * 一般用来鉴权用
     *
     * @param id
     * @return
     */
    HashSet<String> getResourceKeys(Long id);
}
