package work.soho.content.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.mapper.ContentInfoMapper;
import work.soho.content.biz.service.ContentInfoService;

@RequiredArgsConstructor
@Service
public class ContentInfoServiceImpl extends ServiceImpl<ContentInfoMapper, ContentInfo>
    implements ContentInfoService{

}