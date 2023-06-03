package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.AdminContentCategory;
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
public class AdminContentCategoryServiceImpl extends ServiceImpl<AdminContentCategoryMapper, AdminContentCategory>
    implements AdminContentCategoryService {
    public List<AdminContentCategory> getCategorysBySonId(Long id) {
        LinkedList<AdminContentCategory> list = new LinkedList<>();
        AdminContentCategory adminContentCategory = null;

        while((adminContentCategory = getById(id)) != null) {
            list.addFirst(adminContentCategory);
            id = adminContentCategory.getParentId();
        }

        return list;
    }

}




