package work.soho.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.soho.admin.domain.AdminUser;

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {

}
