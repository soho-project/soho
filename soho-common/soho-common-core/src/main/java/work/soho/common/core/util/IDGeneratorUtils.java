package work.soho.common.core.util;

import com.littlenb.snowflake.sequence.IdGenerator;
import com.littlenb.snowflake.support.MillisIdGeneratorFactory;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class IDGeneratorUtils {

    private static final IdGenerator idGenerator;

    static {
        idGenerator = new MillisIdGeneratorFactory(1643477899000L)
                .create(IpUtils.maxLongIp() % 1024);
    }

    /**
     * 生成一个雪花ID
     */
    public static Long snowflake() {
        return idGenerator.nextId();
    }

    /**
     * 生成标准 UUID（36位，包含-）
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成简化 UUID（32位，不包含-，常用于数据库主键）
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
