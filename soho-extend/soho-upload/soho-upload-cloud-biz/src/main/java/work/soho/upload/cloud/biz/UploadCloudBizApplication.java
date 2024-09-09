package work.soho.upload.cloud.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "work.soho")
@SpringBootApplication(scanBasePackages = { "work.soho" })
@MapperScan({"work.soho.**.mapper"})
public class UploadCloudBizApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(UploadCloudBizApplication.class, args);
    }
}
