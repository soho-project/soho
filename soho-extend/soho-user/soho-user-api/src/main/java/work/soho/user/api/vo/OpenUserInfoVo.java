package work.soho.user.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class OpenUserInfoVo {
    /**
     * 唯一标识;;frontName:唯一标识
     */
    @ApiModelProperty(value = "唯一标识")
    private String code;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickname;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 头像;;frontType:upload,uploadCount:1,ignoreInList:true
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 状态;0:禁用,1:活跃;frontType:select
     */
    @ApiModelProperty(value = "状态;0:禁用,1:活跃")
    private Integer status;

    /**
     * 年龄;;frontName:年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 性别;0:女,1:男;frontType:select,frontName:性别
     */
    @ApiModelProperty(value = "性别;0:女,1:男")
    private Integer sex;

    /**
     * 等级;;frontName:等级
     */
    @ApiModelProperty(value = "等级")
    private Integer level;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 头像;;frontType:upload,uploadCount:1
     */
    @ApiModelProperty(value = "头像")
    @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}
