package work.soho.common.data.avatar.utils;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;
import work.soho.common.data.upload.utils.UploadUtils;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@TestPropertySource(locations="classpath:/application.yml")
@SpringBootTest(classes = TestCommonApplication.class)
@Log4j2
@ActiveProfiles({"local"})
class NinePatchAvatarGeneratorUtilsTest {

    @Test
    void generateNinePatchAvatar() {
        System.out.println("test by fang");
        String[] imageUrls = {
                "https://randomuser.me/api/portraits/med/men/32.jpg",
                "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg",
                "https://randomuser.me/api/portraits/med/men/32.jpg",
                "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg",
                "https://randomuser.me/api/portraits/med/men/32.jpg",
                "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg"
        };
        BufferedImage image = NinePatchAvatarGeneratorUtils.createImage(150, 3, imageUrls);
    }

    @Test
    void create() {
        String[] imageUrls = {
                "https://randomuser.me/api/portraits/med/men/32.jpg",
                "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg",
                "https://randomuser.me/api/portraits/med/men/32.jpg",
                "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg",
                "https://randomuser.me/api/portraits/med/men/32.jpg",
                "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg"
        };
        String url = UploadUtils.upload("tmp/test.png", NinePatchAvatarGeneratorUtils.create(150, 3, imageUrls));
        System.out.println(url);
    }

    @Test
    void saveImage() {
    }
}
