package work.soho.example.biz.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试")
@RequiredArgsConstructor
@RestController
@RequestMapping("/client/api/example-test" )
public class TestController {
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
