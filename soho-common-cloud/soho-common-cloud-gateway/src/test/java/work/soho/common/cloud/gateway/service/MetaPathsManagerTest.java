package work.soho.common.cloud.gateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@ContextConfiguration
//@WebAppConfiguration("src/main/resources")
@SpringBootTest
class MetaPathsManagerTest {
    @Autowired
    private MetaPathsManager metaPathsManager;

    @Test
    void getServicePaths() {
        System.out.println(metaPathsManager.getServicePaths());
        System.out.println("test");
    }
}