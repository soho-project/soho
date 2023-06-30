package work.soho.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "work.soho"
})
@MapperScan({"work.soho.admin.mapper"
        , "work.soho.*.mapper"
        , "work.soho.**.mapper"
})
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class);
    }

}
