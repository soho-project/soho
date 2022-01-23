package work.soho.admin;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

@Log4j2
public class Log4j2Test {
    @Test
    public void testLog() {
        System.out.println("test by fang");
        Logger log = LogManager.getLogger(Log4j2Test.class);
        log.error("error");
        log.info("info");
        log.warn("warn");
        log.debug("debug");
        log.trace("trace");
        log.fatal("fatal");
    }

    @Test
    public void testLog2() {
        log.debug("test by fang");
    }
}
