package work.soho.chat.biz.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.enums.ChatUserEnums;
import work.soho.test.TestApp;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class ChatUserServiceTest {
    @Autowired
    private ChatUserService chatUserService;

    @Test
    void loopCreateUser() {
        for (int i = 0; i < 1000; i++) {
            ChatUser chatUser = new ChatUser();
            chatUser.setOriginType("test");
            String username = "test0-" + i;
            chatUser.setUsername(username);
            chatUser.setNickname(chatUser.getNickname());
            chatUser.setAvatar("https://igogo-test.oss-cn-shenzhen.aliyuncs.com/user/avatar/35/2023_10_7_222926926468091904.png");
            chatUser.setPassword("$2a$10$dIvGpaEgAUIgXR8pntsLC.yhY3Nkjyjc9ulM965jdyZ4/iDsI0BGi");
            chatUser.setIntroduction("test");
            chatUser.setAddress("北京市 北京市市辖区 石景山区");
            chatUser.setAge(11);
            chatUser.setBirthday(new Date());
            chatUser.setEmail(i+"@test.com");
            chatUser.setPhone("11111" + i);
            chatUser.setLoginIp("127.0.0.1");
            chatUser.setRegisterIp("127.0.0.1");
            chatUser.setUpdatedTime(LocalDateTime.now());
            chatUser.setCreatedTime(LocalDateTime.now());
            chatUserService.save(chatUser);
        }
    }
}
