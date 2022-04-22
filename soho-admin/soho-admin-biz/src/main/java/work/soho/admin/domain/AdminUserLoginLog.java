package work.soho.admin.domain;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

/**
 * 用户登录日志
 *
 * @TableName admin_user_login_log
 */
@Data
public class AdminUserLoginLog implements Serializable {


    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 后台用户ID
     */
    @ApiModelProperty("后台用户ID")
    private Long adminUserId;

    /**
     * 客户端IP地址考虑IPv6字段适当放宽
     */
    @ApiModelProperty("客户端IP地址考虑IPv6字段适当放宽")
    private String clientIp;

    /**
     * 客户端设备信息
     */
    @ApiModelProperty("客户端设备信息")
    private String clientUserAgent;

    /**
     * 给用户发放的token
     */
    @ApiModelProperty("给用户发放的token")
    private String token;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

}
