package work.soho.content.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.mapper.ContentCategoryMapper;
import work.soho.content.biz.service.ContentCategoryService;

@RequiredArgsConstructor
@Service
public class ContentCategoryServiceImpl extends ServiceImpl<ContentCategoryMapper, ContentCategory>
    implements ContentCategoryService{

}