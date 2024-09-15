package work.soho.admin.biz;

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
public class AdminApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class);
	}

}
