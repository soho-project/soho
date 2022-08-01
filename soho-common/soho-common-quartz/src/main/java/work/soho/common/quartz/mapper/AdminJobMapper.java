package work.soho.common.quartz.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.common.quartz.domain.AdminJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author i
* @description 针对表【admin_job】的数据库操作Mapper
* @createDate 2022-07-26 03:42:42
* @Entity work.soho.common.quartz.domain.AdminJob
*/
@Mapper
public interface AdminJobMapper extends BaseMapper<AdminJob> {

}




