package work.soho.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"work.soho"
//		,"work.soho.pay.biz"
//		,"work.soho.user.biz"
//		,"work.soho.code.biz"
})
@MapperScan({"work.soho.admin.mapper"
		, "work.soho.approvalprocess.mapper"
		, "work.soho.common.quartz.mapper"
//		, "work.soho.pay.biz.mapper"
//		, "work.soho.code.biz.mapper"
//		, "work.soho.admin.mapper"
		, "work.soho.*.mapper"
		, "work.soho.**.mapper"
//		, "work.soho.lot.mapper"
		, "com.baomidou.mybatisplus.samples.quickstart.mapper"})
public class AdminApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class);
	}

}
