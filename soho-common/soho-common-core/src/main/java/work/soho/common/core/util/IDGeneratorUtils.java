package work.soho.common.core.util;

import com.littlenb.snowflake.sequence.IdGenerator;
import com.littlenb.snowflake.support.MillisIdGeneratorFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IDGeneratorUtils {
    private static IdGenerator idGenerator;
    static {
        IDGeneratorUtils.idGenerator = new MillisIdGeneratorFactory(1643477899000L).create(IpUtils.maxLongIp() % 1024);
    }

    /**
     * 生成一个雪花ID
     *
     * @return
     */
    public static Long snowflake() {
        return idGenerator.nextId();
    }
}
