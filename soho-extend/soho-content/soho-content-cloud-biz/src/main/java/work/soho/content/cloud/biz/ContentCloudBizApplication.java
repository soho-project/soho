package work.soho.content.cloud.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "work.soho")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"work.soho"})
@MapperScan({"work.soho.**.mapper"})

public class ContentCloudBizApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ContentCloudBizApplication.class, args);
    }
}
