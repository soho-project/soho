package work.soho.groovy.cloud.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "work.soho")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"work.soho"})
@MapperScan({"work.soho.**.mapper"})
public class GroovyCloudBizApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(GroovyCloudBizApplication.class, args);
    }
}
