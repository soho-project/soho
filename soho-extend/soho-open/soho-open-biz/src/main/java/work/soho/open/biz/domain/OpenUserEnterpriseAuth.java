package work.soho.open.biz.domain;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName(value ="open_user_enterprise_auth")
public class OpenUserEnterpriseAuth implements Serializable {
    /**
    * 认证记录ID
    */
    @ExcelProperty("认证记录ID")
    @ApiModelProperty(value = "认证记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 用户ID
    */
    @ExcelProperty("用户ID")
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * 企业名称
    */
    @ExcelProperty("企业名称")
    @ApiModelProperty(value = "企业名称")
    @TableField(value = "company_name")
    private String companyName;

    /**
    * 企业英文名
    */
    @ExcelProperty("企业英文名")
    @ApiModelProperty(value = "企业英文名")
    @TableField(value = "company_name_en")
    private String companyNameEn;

    /**
    * 统一社会信用代码
    */
    @ExcelProperty("统一社会信用代码")
    @ApiModelProperty(value = "统一社会信用代码")
    @TableField(value = "credit_code")
    private String creditCode;

    /**
    * 企业类型
    */
    @ExcelProperty("企业类型")
    @ApiModelProperty(value = "企业类型")
    @TableField(value = "company_type")
    private String companyType;

    /**
    * 法人姓名
    */
    @ExcelProperty("法人姓名")
    @ApiModelProperty(value = "法人姓名")
    @TableField(value = "legal_person")
    private String legalPerson;

    /**
    * 加密的法人身份证号
    */
    @ExcelProperty("加密的法人身份证号")
    @ApiModelProperty(value = "加密的法人身份证号")
    @TableField(value = "legal_person_id_card")
    private String legalPersonIdCard;

    /**
    * 注册资本（万元）
    */
    @ExcelProperty("注册资本（万元）")
    @ApiModelProperty(value = "注册资本（万元）")
    @TableField(value = "registered_capital")
    private BigDecimal registeredCapital;

    /**
    * 成立日期
    */
    @ExcelProperty("成立日期")
    @ApiModelProperty(value = "成立日期")
    @TableField(value = "establish_date")
    private LocalDate establishDate;

    /**
    * 营业期限开始
    */
    @ExcelProperty("营业期限开始")
    @ApiModelProperty(value = "营业期限开始")
    @TableField(value = "business_term_start")
    private LocalDate businessTermStart;

    /**
    * 营业期限结束
    */
    @ExcelProperty("营业期限结束")
    @ApiModelProperty(value = "营业期限结束")
    @TableField(value = "business_term_end")
    private LocalDate businessTermEnd;

    /**
    * 经营范围
    */
    @ExcelProperty("经营范围")
    @ApiModelProperty(value = "经营范围")
    @TableField(value = "business_scope")
    private String businessScope;

    /**
    * 注册地址
    */
    @ExcelProperty("注册地址")
    @ApiModelProperty(value = "注册地址")
    @TableField(value = "registered_address")
    private String registeredAddress;

    /**
    * 经营地址
    */
    @ExcelProperty("经营地址")
    @ApiModelProperty(value = "经营地址")
    @TableField(value = "business_address")
    private String businessAddress;

    /**
    * 营业执照照片URL
    */
    @ExcelProperty("营业执照照片URL")
    @ApiModelProperty(value = "营业执照照片URL")
    @TableField(value = "license_image_url")
    private String licenseImageUrl;

    /**
    * 营业执照编号
    */
    @ExcelProperty("营业执照编号")
    @ApiModelProperty(value = "营业执照编号")
    @TableField(value = "license_image_no")
    private String licenseImageNo;

    /**
    * 税务登记证URL
    */
    @ExcelProperty("税务登记证URL")
    @ApiModelProperty(value = "税务登记证URL")
    @TableField(value = "tax_certificate_image")
    private String taxCertificateImage;

    /**
    * 组织机构代码证URL
    */
    @ExcelProperty("组织机构代码证URL")
    @ApiModelProperty(value = "组织机构代码证URL")
    @TableField(value = "organization_certificate_image")
    private String organizationCertificateImage;

    /**
    * 企业状态
    */
    @ExcelProperty("企业状态")
    @ApiModelProperty(value = "企业状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * 所属行业
    */
    @ExcelProperty("所属行业")
    @ApiModelProperty(value = "所属行业")
    @TableField(value = "industry")
    private String industry;

    /**
    * 企业规模
    */
    @ExcelProperty("企业规模")
    @ApiModelProperty(value = "企业规模")
    @TableField(value = "company_size")
    private String companySize;

    /**
    * 认证渠道
    */
    @ExcelProperty("认证渠道")
    @ApiModelProperty(value = "认证渠道")
    @TableField(value = "auth_channel")
    private String authChannel;

    /**
    * 扩展信息
    */
    @ExcelProperty("扩展信息")
    @ApiModelProperty(value = "扩展信息")
    @TableField(value = "ext_info")
    private String extInfo;

    /**
    * created_time
    */
    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * updated_time
    */
    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}