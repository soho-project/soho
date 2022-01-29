package work.soho.admin.configuration;

import com.littlenb.snowflake.sequence.IdGenerator;
import com.littlenb.snowflake.support.MillisIdGeneratorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeConfiguration {
    @Bean
    public IdGenerator factory() {
        return new MillisIdGeneratorFactory(1643477899000L).create(1L);
    }
}
