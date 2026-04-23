package work.soho.chat.cloud.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 聊天云端业务启动入口。
 */
@EnableFeignClients(basePackages = "work.soho")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = { "work.soho" })
@MapperScan({ "work.soho.**.mapper" })
public class ChatCloudBizApplication {

    /**
     * 启动聊天云端业务模块。
     * 返回值：无。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ChatCloudBizApplication.class, args);
    }
}
