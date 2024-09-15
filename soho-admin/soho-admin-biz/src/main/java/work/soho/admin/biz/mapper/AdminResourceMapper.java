package work.soho.admin.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.soho.admin.biz.domain.AdminResource;

import java.util.List;

@Mapper
public interface AdminResourceMapper extends BaseMapper<AdminResource> {
    List<AdminResource> getByRoleIds(Integer[] roleIds);
}
