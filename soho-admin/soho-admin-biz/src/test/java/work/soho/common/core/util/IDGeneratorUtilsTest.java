package work.soho.common.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IDGeneratorUtilsTest {
    @Test
    public void testId() {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Long id = IDGeneratorUtils.snowflake();
            if(list.contains(id)) {
                throw new RuntimeException("重复了");
            }
            list.add(id);
            System.out.println(id);
        }
    }
}