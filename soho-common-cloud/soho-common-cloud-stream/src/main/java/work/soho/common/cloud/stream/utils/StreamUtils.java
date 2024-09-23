package work.soho.common.cloud.stream.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.Environment;
import work.soho.common.core.support.SpringContextHolder;

@UtilityClass
public class StreamUtils {
    /**
     * 获取指定数据流名称
     *
     * @param streamName
     * @return
     */
    public String getStreamName(String streamName) {
        String applicationName = SpringContextHolder.getApplicationContext().getBean(Environment.class).getProperty("spring.application.name");
        return applicationName + "-events-" + streamName;
    }
}
