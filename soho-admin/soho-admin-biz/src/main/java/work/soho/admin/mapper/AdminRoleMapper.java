package work.soho.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.api.admin.po.AdminRole;

import java.util.List;

@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
    /**
     * 根据用户ID获取角色列表
     *
     * @return
     */
    List<AdminRole> getAdminRoleListByUserId(Integer userId);
}
