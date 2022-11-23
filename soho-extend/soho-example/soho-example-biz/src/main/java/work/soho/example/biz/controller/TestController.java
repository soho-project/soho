package work.soho.example.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/client/api/example-test" )
public class TestController {
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
