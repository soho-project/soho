package work.soho.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.api.admin.po.AdminUser;

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
