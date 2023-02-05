package work.soho.example.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopeMetadata;
import work.soho.example.biz.domain.Example;
import work.soho.example.biz.service.ExampleService;
import work.soho.example.biz.mapper.ExampleMapper;
import org.springframework.stereotype.Service;

/**
* @author fang
* @description 针对表【example(自动化样例表)】的数据库操作Service实现
* @createDate 2022-11-15 18:30:03
*/
@Service()
@Scope()
public class ExampleServiceImpl extends ServiceImpl<ExampleMapper, Example>
    implements ExampleService{

}




