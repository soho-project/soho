package work.soho.example.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.example.biz.domain.Example;
import work.soho.example.biz.mapper.ExampleMapper;
import work.soho.example.biz.service.ExampleService;

@RequiredArgsConstructor
@Service
public class ExampleServiceImpl extends ServiceImpl<ExampleMapper, Example>
    implements ExampleService{

}