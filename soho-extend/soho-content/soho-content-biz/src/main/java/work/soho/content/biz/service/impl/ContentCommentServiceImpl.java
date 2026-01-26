package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.ContentComment;
import work.soho.content.biz.mapper.ContentCommentMapper;
import work.soho.content.biz.service.ContentCommentService;

/**
 * 内容评论服务实现
 */
@Service
public class ContentCommentServiceImpl extends ServiceImpl<ContentCommentMapper, ContentComment>
        implements ContentCommentService {
}
