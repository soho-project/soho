package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.ContentMedia;
import work.soho.content.biz.mapper.ContentMediaMapper;
import work.soho.content.biz.service.ContentMediaService;

/**
 * 内容媒体服务实现
 */
@Service
public class ContentMediaServiceImpl extends ServiceImpl<ContentMediaMapper, ContentMedia>
        implements ContentMediaService {
}
