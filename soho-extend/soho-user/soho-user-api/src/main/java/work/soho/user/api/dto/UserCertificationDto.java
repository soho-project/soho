package work.soho.user.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserCertificationDto {
    /**
     * id
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 身份证号
     */
    private String cardId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 认证状态
     */
    private Integer status;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 身份证正面图片
     */
    private String cardFrontImg;

    /**
     * 身份证背面图片
     */
    private String cardBackImg;

    /**
     * 发卡地
     */
    private String issuingLocation;

    /**
     * 居住地
     */
    private String cardAddress;

    /**
     * 有效期
     */
    private Date periodOfValidity;

    /**
     * 认证视频
     */
    private String video;
}
