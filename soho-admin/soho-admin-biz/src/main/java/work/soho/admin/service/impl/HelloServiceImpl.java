package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.admin.domain.Hello;
import work.soho.admin.service.HelloService;
import work.soho.admin.mapper.HelloMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【hello】的数据库操作Service实现
* @createDate 2022-04-16 22:41:04
*/
@Service
public class HelloServiceImpl extends ServiceImpl<HelloMapper, Hello>
    implements HelloService{

}




