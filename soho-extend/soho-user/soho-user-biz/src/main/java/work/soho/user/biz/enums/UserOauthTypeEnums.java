package work.soho.user.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class UserOauthTypeEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLED(0,"禁用"),
        ACTIVE(1,"活跃");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Adapter {
        FACEBOOK(22,"Facebook"),
        LINKEDIN(23,"领英"),
        TWITTER(24,"推特"),
        MICROSOFT(25,"Microsoft"),
        TIKTOK(27,"抖音"),
        JD(28,"京东"),
        DINGTALK_ACCOUNT(30,"钉钉账号"),
        TAOBAO(31,"淘宝"),
        MICROSOFT_CN(32,"微软中国"),
        MI(33,"小米"),
        TOUTIAO(34,"今日头条"),
        TEAMBITION(35,"Teambition"),
        RENREN(36,"人人"),
        PINTEREST(37,"Pinterest"),
        HUAWEI_V3(38,"华为V3"),
        GITLAB(41,"GitLab"),
        MEITUAN(42,"美团"),
        ELEME(43,"饿了么"),
        AMAZON(45,"Amazon"),
        SLACK(46,"Slack"),
        LINE(47,"Line"),
        AFDIAN(49,"爱发电"),
        FIGMA(50,"Figma"),
        QQ_MINI_PROGRAM(51,"QQ小程序"),
        KUJIALE(52,"酷家乐"),
        WECHAT_MP(53,"微信公众号"),
        OPEN_SOURCE_CHINA(10,"开源中国"),
        PROGRAMMERS_INN(13,"程序员客栈"),
        CSDN(14,"CSDN"),
        DINGTALK(15,"钉钉"),
        ALIBABA_CLOUD(16,"阿里云"),
        ALIPAY_PUBLIC_KEY(17,"支付宝公钥"),
        ALIPAY_CERTIFICATE(18,"支付宝证书"),
        HUAWEI(19,"华为"),
        WECHAT_MINI_PROGRAM(1,"微信小程序"),
        WECHAT_OPEN_PLATFORM(2,"微信开放平台"),
        QQ(5,"QQ"),
        SINA_WEIBO(6,"新浪微博"),
        BAIDU(7,"百度"),
        GITEE(8,"Gitee"),
        GITHUB(9,"Github"),
        LARK(20,"飞书"),
        GOOGLE(21,"Google");
        private final int id;
        private final String name;

        public static Adapter getById(Integer id) {
            for (Adapter value : values()) {
                if (value.getId() == id) {
                    return value;
                }
            }
            return null;
        }
    }

    // 根据ID获取枚举

}
