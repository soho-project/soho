package work.soho.example.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.example.biz.domain.ExampleOption;
import work.soho.example.biz.mapper.ExampleOptionMapper;
import work.soho.example.biz.service.ExampleOptionService;

@RequiredArgsConstructor
@Service
public class ExampleOptionServiceImpl extends ServiceImpl<ExampleOptionMapper, ExampleOption>
    implements ExampleOptionService{

}