package work.soho.admin.provider;

import com.littlenb.snowflake.sequence.IdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.annotation.Log;
import work.soho.admin.service.HelloService;
import work.soho.api.admin.po.Hello;

@Slf4j
@Controller
@RestController
@Api(tags = "测试，产品，作用类上")
public class HelloProvider {
    @Autowired
    private HelloService helloService;

    @Autowired
    private IdGenerator idGenerator;

    @GetMapping("/hello")
    @Log("test")
    @ApiOperation(value = "获取所有的产品信息",response = String.class,httpMethod ="GET",notes = "展开后的信息提示，可以写的比较详细")
    public String hello() {
        return "Hello world";
    }

    @GetMapping("/hello-by-id")
    public Hello helloById(Integer id) {
        if (id == null) {
            id = 1;
        }
        System.out.println(id);
        log.debug(String.valueOf(id));
        log.debug("test");
        log.debug("test");
        return helloService.getById(id);
    }
}
