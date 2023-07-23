package work.soho.groovy.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.groovy.biz.domain.GroovyGroup;
import work.soho.groovy.biz.mapper.GroovyGroupMapper;
import work.soho.groovy.biz.service.GroovyGroupService;

@RequiredArgsConstructor
@Service
public class GroovyGroupServiceImpl extends ServiceImpl<GroovyGroupMapper, GroovyGroup>
    implements GroovyGroupService{

}