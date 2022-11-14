package work.soho.pay.biz.vo;

import lombok.Data;

@Data
public class ClientPayInfoVo {
    private Integer id;

    /**
     * 支付标题
     */
    private String title;

    /**
     *
     */
    private String name;

    /**
     * 支付封面图片
     */
    private String accountImg;

    /**
     * 支持客户端类型； 1 web  2 wap 3 app 4 微信公众号  5 微信小程序
     */
    private String clientType;

    /**
     * 平台识别名
     */
    private String platform;
}
