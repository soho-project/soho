package work.soho.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.api.admin.po.Hello;

@Mapper
public interface HelloMapper {
    Hello getById(Hello hello);
}
