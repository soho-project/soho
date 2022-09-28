package work.soho.admin.service;

import work.soho.admin.domain.AdminContentCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author i
* @description 针对表【admin_content_category(内容分类)】的数据库操作Service
* @createDate 2022-09-03 01:14:08
*/
public interface AdminContentCategoryService extends IService<AdminContentCategory> {
    List<AdminContentCategory> getCategorysBySonId(Long id);
}
