package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.ContentTagRelation;
import work.soho.content.biz.mapper.ContentTagRelationMapper;
import work.soho.content.biz.service.ContentTagRelationService;

/**
 * 内容标签关联服务实现
 */
@Service
public class ContentTagRelationServiceImpl extends ServiceImpl<ContentTagRelationMapper, ContentTagRelation>
        implements ContentTagRelationService {
}
