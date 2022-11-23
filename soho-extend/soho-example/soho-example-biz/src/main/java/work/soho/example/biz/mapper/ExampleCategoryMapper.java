package work.soho.example.biz.mapper;

import work.soho.example.biz.domain.ExampleCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author fang
* @description 针对表【example_category(自动化样例分类表;option:id~title,tree:id~title~parent_id)】的数据库操作Mapper
* @createDate 2022-11-15 18:30:03
* @Entity work.soho.example.biz.domain.ExampleCategory
*/
public interface ExampleCategoryMapper extends BaseMapper<ExampleCategory> {

}




