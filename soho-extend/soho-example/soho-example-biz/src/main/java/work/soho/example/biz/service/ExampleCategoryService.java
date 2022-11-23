package work.soho.example.biz.service;

import work.soho.example.biz.domain.ExampleCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author fang
* @description 针对表【example_category(自动化样例分类表;option:id~title,tree:id~title~parent_id)】的数据库操作Service
* @createDate 2022-11-15 18:30:03
*/
public interface ExampleCategoryService extends IService<ExampleCategory> {

}
