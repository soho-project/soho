package work.soho.admin.config;

import com.littlenb.snowflake.sequence.IdGenerator;
import com.littlenb.snowflake.support.MillisIdGeneratorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.soho.common.core.util.IpUtils;

@Configuration
public class SnowflakeConfiguration {
    @Bean
    public IdGenerator factory() {
        return new MillisIdGeneratorFactory(1643477899000L).create(IpUtils.maxLongIp() % 1024);
    }
}
