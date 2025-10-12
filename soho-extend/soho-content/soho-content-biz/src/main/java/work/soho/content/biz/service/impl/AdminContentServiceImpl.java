package work.soho.content.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.mapper.AdminContentMapper;
import work.soho.content.biz.service.AdminContentService;

/**
* @author i
* @description 针对表【admin_content(系统内容表)】的数据库操作Service实现
* @createDate 2022-09-03 01:14:09
*/
@Service
public class AdminContentServiceImpl extends ServiceImpl<AdminContentMapper, ContentInfo>
    implements AdminContentService {

}




