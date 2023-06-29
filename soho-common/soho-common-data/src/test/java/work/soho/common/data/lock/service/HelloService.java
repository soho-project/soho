package work.soho.common.data.lock.service;

import org.springframework.stereotype.Component;
import work.soho.common.data.lock.annotation.Lock;

@Component
public class HelloService {
    @Lock("'pre-key-' + #name")
    public void hello(String name) {
        System.out.println("hello............." + name);
    }
}
