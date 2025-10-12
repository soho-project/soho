package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.mapper.AdminContentCategoryMapper;
import work.soho.content.biz.service.AdminContentCategoryService;

import java.util.LinkedList;
import java.util.List;

/**
* @author i
* @description 针对表【admin_content_category(内容分类)】的数据库操作Service实现
* @createDate 2022-09-03 01:14:08
*/
@Service
public class AdminContentCategoryServiceImpl extends ServiceImpl<AdminContentCategoryMapper, ContentCategory>
    implements AdminContentCategoryService {
    public List<ContentCategory> getCategorysBySonId(Long id) {
        LinkedList<ContentCategory> list = new LinkedList<>();
        ContentCategory adminContentCategory = null;

        while((adminContentCategory = getById(id)) != null) {
            list.addFirst(adminContentCategory);
            id = adminContentCategory.getParentId();
        }

        return list;
    }

}




