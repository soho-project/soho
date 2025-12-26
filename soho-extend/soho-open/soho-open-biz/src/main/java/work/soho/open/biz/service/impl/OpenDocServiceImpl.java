package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenDoc;
import work.soho.open.biz.mapper.OpenDocMapper;
import work.soho.open.biz.service.OpenDocService;

@RequiredArgsConstructor
@Service
public class OpenDocServiceImpl extends ServiceImpl<OpenDocMapper, OpenDoc>
    implements OpenDocService{

}