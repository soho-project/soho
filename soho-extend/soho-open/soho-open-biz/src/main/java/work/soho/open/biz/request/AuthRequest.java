package work.soho.open.biz.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实名认证请求
 */
@Data
public class AuthRequest {
    private Integer authType;

    private Personal personal;

    private Enterprise enterprise;

    @Data
    public static class Enterprise {
        private Long id;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 企业名称
         */
        private String companyName;

        /**
         * 企业英文名
         */
        private String companyNameEn;

        /**
         * 统一社会信用代码
         */
        private String creditCode;

        /**
         * 企业类型
         */
        private String companyType;

        /**
         * 法人姓名
         */
        private String legalPerson;

        /**
         * 加密的法人身份证号
         */
        private String legalPersonIdCard;

        /**
         * 注册资本（万元）
         */
        private BigDecimal registeredCapital;

        /**
         * 成立日期
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate establishDate;

        /**
         * 营业期限开始
         */
        private LocalDate businessTermStart;

        /**
         * 营业期限结束
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate businessTermEnd;

        /**
         * 经营范围
         */
        private String businessScope;

        /**
         * 注册地址
         */
        private String registeredAddress;

        /**
         * 经营地址
         */
        private String businessAddress;

        /**
         * 营业执照照片URL
         */
        private String licenseImageUrl;

        /**
         * 营业执照编号
         */
        private String licenseImageNo;

        /**
         * 税务登记证URL
         */
        private String taxCertificateImage;

        /**
         * 组织机构代码证URL
         */
        private String organizationCertificateImage;

        /**
         * 企业状态
         */
        private Integer status;

        /**
         * 所属行业
         */
        private String industry;

        /**
         * 企业规模
         */
        private String companySize;
    }

    @Data
    public static class Personal {
        private Long id;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 证件类型
         */
        private Integer idCardType;

        /**
         * 加密的证件号码
         */
        private String idCardNoEncrypt;

        /**
         * 证件正面照URL
         */
        private String idCardFrontImg;

        /**
         * 证件反面照URL
         */
        private String idCardBackImg;

        /**
         * 真实姓名
         */
        private String realName;

        /**
         * 性别
         */
        private Integer gender;

        /**
         * 民族
         */
        private String nation;

        /**
         * 出生日期
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

        /**
         * 地址
         */
        private String address;

        /**
         * 签发机关
         */
        private String issuedBy;

        /**
         * 有效期开始
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate validDateStart;

        /**
         * 有效期结束
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate validDateEnd;

        /**
         * 人脸比对分数
         */
        private BigDecimal faceCompareScore;

        /**
         * 人脸照片URL
         */
        private String faceImageUrl;
    }
}
