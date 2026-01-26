package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.ContentTag;
import work.soho.content.biz.mapper.ContentTagMapper;
import work.soho.content.biz.service.ContentTagService;

/**
 * 内容标签服务实现
 */
@Service
public class ContentTagServiceImpl extends ServiceImpl<ContentTagMapper, ContentTag>
        implements ContentTagService {
}
