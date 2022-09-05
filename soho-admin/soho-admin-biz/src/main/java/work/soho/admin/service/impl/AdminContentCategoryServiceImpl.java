package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.admin.domain.AdminContentCategory;
import work.soho.admin.service.AdminContentCategoryService;
import work.soho.admin.mapper.AdminContentCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【admin_content_category(内容分类)】的数据库操作Service实现
* @createDate 2022-09-03 01:14:08
*/
@Service
public class AdminContentCategoryServiceImpl extends ServiceImpl<AdminContentCategoryMapper, AdminContentCategory>
    implements AdminContentCategoryService{

}




