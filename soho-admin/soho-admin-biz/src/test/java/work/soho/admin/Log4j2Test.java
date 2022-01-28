package work.soho.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

@Slf4j
class Log4j2Test {

	@Test
	void testLog() {
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
	void testLog2() {
		log.debug("test by fang");
	}

}
