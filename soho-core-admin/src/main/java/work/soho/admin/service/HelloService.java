package work.soho.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.soho.admin.mapper.HelloMapper;
import work.soho.api.admin.po.Hello;

@Service
public class HelloService {
    @Autowired
    private  HelloMapper helloMapper;
    public Hello getById(Integer id) {
        Hello hello = new Hello();
        hello.setId(id);
        return helloMapper.getById(hello);
    }
}
