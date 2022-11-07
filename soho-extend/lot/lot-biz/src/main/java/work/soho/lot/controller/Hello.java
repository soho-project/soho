package work.soho.lot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/client/api/hello" )
public class Hello {
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
