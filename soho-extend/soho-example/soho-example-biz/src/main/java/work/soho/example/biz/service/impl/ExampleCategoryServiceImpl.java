package work.soho.example.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.example.biz.domain.ExampleCategory;
import work.soho.example.biz.service.ExampleCategoryService;
import work.soho.example.biz.mapper.ExampleCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author fang
* @description 针对表【example_category(自动化样例分类表;option:id~title,tree:id~title~parent_id)】的数据库操作Service实现
* @createDate 2022-11-15 18:30:03
*/
@Service
public class ExampleCategoryServiceImpl extends ServiceImpl<ExampleCategoryMapper, ExampleCategory>
    implements ExampleCategoryService{

}




