package work.soho.user.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class UserOauthEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        GITEE(1,"Gitee"),
        WECHAT_MINI_PROGRAM(2,"微信小程序"),
        WECHAT_OFFICIAL_ACCOUNT(3,"微信公众号");
        private final int id;
        private final String name;
    }
}