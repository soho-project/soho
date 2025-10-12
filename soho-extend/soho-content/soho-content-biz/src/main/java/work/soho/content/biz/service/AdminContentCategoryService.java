package work.soho.content.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.content.biz.domain.ContentCategory;

import java.util.List;

/**
* @author i
* @description 针对表【admin_content_category(内容分类)】的数据库操作Service
* @createDate 2022-09-03 01:14:08
*/
public interface AdminContentCategoryService extends IService<ContentCategory> {
    List<ContentCategory> getCategorysBySonId(Long id);
}
