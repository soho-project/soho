package work.soho.common.data.ip2region;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@TestPropertySource(locations="classpath:/application.yml")
@SpringBootTest(classes = TestCommonApplication.class)
@Log4j2
@ActiveProfiles({"local"})
class Ip2regionUtilTest {

    @Test
    void getCityInfo() {
        String city = Ip2regionUtil.getCityInfo("1.2.3.4");
        Assert.assertNotNull(city);

        city = Ip2regionUtil.getCityInfo("114.114.114.114");
        Assert.assertNotNull(city);

        city = Ip2regionUtil.getCityInfo("120.24.239.129");
        Assert.assertNotNull(city);

        RegionInfo regionInfo =  Ip2regionUtil.getRegionInfoByIp("120.24.239.129");
        Assert.assertTrue("中国".equals(regionInfo.getCountry()));
        Assert.assertTrue("广东省".equals(regionInfo.getProvince()));
        Assert.assertTrue("深圳市".equals(regionInfo.getCity()));
        Assert.assertTrue("阿里云".equals(regionInfo.getIsp()));

        regionInfo =  Ip2regionUtil.getRegionInfoByIp("127.0.0.1");
        System.out.println(regionInfo);
        Assert.assertTrue("".equals(regionInfo.getCountry()));
        Assert.assertTrue("".equals(regionInfo.getProvince()));
        Assert.assertTrue("内网IP".equals(regionInfo.getCity()));
        Assert.assertTrue("内网IP".equals(regionInfo.getIsp()));

        regionInfo =  Ip2regionUtil.getRegionInfoByIp("192.168.2.1");
        System.out.println(regionInfo);
        Assert.assertTrue("".equals(regionInfo.getCountry()));
        Assert.assertTrue("".equals(regionInfo.getProvince()));
        Assert.assertTrue("内网IP".equals(regionInfo.getCity()));
        Assert.assertTrue("内网IP".equals(regionInfo.getIsp()));
    }
}