package work.soho.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.soho.admin.mapper.HelloMapper;
import work.soho.api.admin.po.Hello;

@Service
@RequiredArgsConstructor
public class HelloService {
    @Autowired
    private final HelloMapper helloMapper;

    public Hello getById(Integer id) {
        Hello hello = new Hello();
        hello.setId(id);
        return helloMapper.getById(hello);
    }
}
