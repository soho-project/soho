package work.soho.admin.cloud.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
		"work.soho"
})
@MapperScan({"work.soho.admin.mapper"
		, "work.soho.*.mapper"
		, "work.soho.**.mapper"
})
public class AdminCloudBizApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdminCloudBizApplication.class);
	}
}
