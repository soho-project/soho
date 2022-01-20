package work.soho.admin.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.service.HelloService;
import work.soho.api.admin.po.Hello;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HelloProvider {

    private final HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello world";
    }

    @GetMapping("/hello-by-id")
    public Hello helloById(Integer id) {
        if (id == null) {
            id = 1;
        }
        System.out.println(id);
        log.debug(id);
        log.debug("test");
        log.debug("test");
        return helloService.getById(id);
    }
}
