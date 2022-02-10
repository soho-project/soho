package work.soho.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.api.admin.po.AdminResource;

import java.util.List;

@Mapper
public interface AdminResourceMapper extends BaseMapper<AdminResource> {
    AdminResource getByKey(String key);

    AdminResource getByPath(String path);

//    List<AdminResource> getByIds(Integer[] ids);

    List<AdminResource> getByRoleIds(Integer[] roleIds);
}
