package work.soho.example.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "work.soho")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
		"work.soho"
//		,"work.soho.admin.biz"
})
//@ComponentScan(basePackages = {"work.soho", "work.soho.admin.cloud.biz"})
@MapperScan({
		 "work.soho.*.mapper"
		, "work.soho.**.mapper"
})
public class ExampleCloudBizApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExampleCloudBizApplication.class);
	}
}
