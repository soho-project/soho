package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.ContentExternalMapping;
import work.soho.content.biz.mapper.ContentExternalMappingMapper;
import work.soho.content.biz.service.ContentExternalMappingService;

/**
 * 外部内容映射服务实现
 */
@Service
public class ContentExternalMappingServiceImpl extends ServiceImpl<ContentExternalMappingMapper, ContentExternalMapping>
        implements ContentExternalMappingService {
}
