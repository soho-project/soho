package work.soho.quartz.biz.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import work.soho.quartz.biz.service.TestService;

@Service
@Log4j2
public class TestServiceImpl implements TestService {

    @Override
    public void test(String test) {
        log.info(test);
    }
}
