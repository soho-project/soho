package work.soho.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.soho.admin.domain.AdminRole;

import java.util.List;

@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
    /**
     * 根据用户ID获取角色列表
     *
     * @return
     */
    List<AdminRole> getAdminRoleListByUserId(Long userId);
}
