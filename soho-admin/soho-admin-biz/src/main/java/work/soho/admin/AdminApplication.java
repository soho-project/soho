package work.soho.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"work.soho"})
@MapperScan({"work.soho.admin.mapper"
		, "work.soho.approvalprocess.mapper"
		, "work.soho.common.quartz.mapper"
		, "work.soho.*.mapper"
		, "com.baomidou.mybatisplus.samples.quickstart.mapper"})
public class AdminApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class);
	}

}
