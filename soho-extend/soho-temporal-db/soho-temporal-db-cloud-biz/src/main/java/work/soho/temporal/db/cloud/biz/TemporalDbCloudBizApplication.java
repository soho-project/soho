package work.soho.temporal.db.cloud.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 时序数据库云端业务启动入口。
 */
@EnableFeignClients(basePackages = "work.soho")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = { "work.soho" })
@MapperScan({ "work.soho.**.mapper" })
public class TemporalDbCloudBizApplication {

    /**
     * 启动时序数据库云端业务模块。
     * 返回值：无。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TemporalDbCloudBizApplication.class, args);
    }
}
