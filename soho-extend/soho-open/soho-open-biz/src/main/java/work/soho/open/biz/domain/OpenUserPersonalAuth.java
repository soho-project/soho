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
@TableName(value ="open_user_personal_auth")
public class OpenUserPersonalAuth implements Serializable {
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
    * 证件类型
    */
    @ExcelProperty("证件类型")
    @ApiModelProperty(value = "证件类型")
    @TableField(value = "id_card_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer idCardType;

    /**
    * 加密的证件号码
    */
    @ExcelProperty("加密的证件号码")
    @ApiModelProperty(value = "加密的证件号码")
    @TableField(value = "id_card_no_encrypt")
    private String idCardNoEncrypt;

    /**
    * 证件正面照URL
    */
    @ExcelProperty("证件正面照URL")
    @ApiModelProperty(value = "证件正面照URL")
    @TableField(value = "id_card_front_img")
    private String idCardFrontImg;

    /**
    * 证件反面照URL
    */
    @ExcelProperty("证件反面照URL")
    @ApiModelProperty(value = "证件反面照URL")
    @TableField(value = "id_card_back_img")
    private String idCardBackImg;

    /**
    * 真实姓名
    */
    @ExcelProperty("真实姓名")
    @ApiModelProperty(value = "真实姓名")
    @TableField(value = "real_name")
    private String realName;

    /**
    * 性别
    */
    @ExcelProperty("性别")
    @ApiModelProperty(value = "性别")
    @TableField(value = "gender")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer gender;

    /**
    * 民族
    */
    @ExcelProperty("民族")
    @ApiModelProperty(value = "民族")
    @TableField(value = "nation")
    private String nation;

    /**
    * 出生日期
    */
    @ExcelProperty("出生日期")
    @ApiModelProperty(value = "出生日期")
    @TableField(value = "birth_date")
    private LocalDate birthDate;

    /**
    * 地址
    */
    @ExcelProperty("地址")
    @ApiModelProperty(value = "地址")
    @TableField(value = "address")
    private String address;

    /**
    * 签发机关
    */
    @ExcelProperty("签发机关")
    @ApiModelProperty(value = "签发机关")
    @TableField(value = "issued_by")
    private String issuedBy;

    /**
    * 有效期开始
    */
    @ExcelProperty("有效期开始")
    @ApiModelProperty(value = "有效期开始")
    @TableField(value = "valid_date_start")
    private LocalDate validDateStart;

    /**
    * 有效期结束
    */
    @ExcelProperty("有效期结束")
    @ApiModelProperty(value = "有效期结束")
    @TableField(value = "valid_date_end")
    private LocalDate validDateEnd;

    /**
    * 人脸比对分数
    */
    @ExcelProperty("人脸比对分数")
    @ApiModelProperty(value = "人脸比对分数")
    @TableField(value = "face_compare_score")
    private BigDecimal faceCompareScore;

    /**
    * 人脸照片URL
    */
    @ExcelProperty("人脸照片URL")
    @ApiModelProperty(value = "人脸照片URL")
    @TableField(value = "face_image_url")
    private String faceImageUrl;

    /**
    * 活体检测结果
    */
    @ExcelProperty("活体检测结果")
    @ApiModelProperty(value = "活体检测结果")
    @TableField(value = "live_detect_result")
    private String liveDetectResult;

    /**
    * 认证场景：API、H5、SDK
    */
    @ExcelProperty("认证场景：API、H5、SDK")
    @ApiModelProperty(value = "认证场景：API、H5、SDK")
    @TableField(value = "auth_scene")
    private String authScene;

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

}