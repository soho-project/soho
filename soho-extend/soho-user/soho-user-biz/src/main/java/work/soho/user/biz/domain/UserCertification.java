package work.soho.user.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@TableName(value ="user_certification")
@Data
public class UserCertification implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 用户ID
    */
    @ExcelProperty("用户ID")
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    private Long userId;

    /**
    * 身份证号
    */
    @ExcelProperty("身份证号")
    @ApiModelProperty(value = "身份证号")
    @TableField(value = "card_id")
    private String cardId;

    /**
    * 姓名
    */
    @ExcelProperty("姓名")
    @ApiModelProperty(value = "姓名")
    @TableField(value = "name")
    private String name;

    /**
    * 认证状态
    */
    @ExcelProperty("认证状态")
    @ApiModelProperty(value = "认证状态")
    @TableField(value = "status")
    private Integer status;

    /**
    * 更新时间
    */
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 身份证正面图片
    */
    @ExcelProperty("身份证正面图片")
    @ApiModelProperty(value = "身份证正面图片")
    @TableField(value = "card_front_img")
    private String cardFrontImg;

    /**
    * 身份证背面图片
    */
    @ExcelProperty("身份证背面图片")
    @ApiModelProperty(value = "身份证背面图片")
    @TableField(value = "card_back_img")
    private String cardBackImg;

    /**
    * 发卡地
    */
    @ExcelProperty("发卡地")
    @ApiModelProperty(value = "发卡地")
    @TableField(value = "issuing_location")
    private String issuingLocation;

    /**
    * 居住地
    */
    @ExcelProperty("居住地")
    @ApiModelProperty(value = "居住地")
    @TableField(value = "card_address")
    private String cardAddress;

    /**
    * 有效期
    */
    @ExcelProperty("有效期")
    @ApiModelProperty(value = "有效期")
    @TableField(value = "period_of_validity")
    private Date periodOfValidity;

    /**
     * 认证视频
     */
    @ExcelProperty("认证视频")
    @ApiModelProperty(value = "认证视频")
    @TableField(value = "video")
    private String video;

}