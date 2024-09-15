package work.soho.example.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.service.AdminConfigApiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hello")
public class HelloController {
    private final AdminConfigApiService adminConfigApiService;
    @GetMapping
    public String sayHello() {
        return adminConfigApiService.getByKey("temporal_db_username", String.class);
    }
}
